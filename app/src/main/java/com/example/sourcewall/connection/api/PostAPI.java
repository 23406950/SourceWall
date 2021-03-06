package com.example.sourcewall.connection.api;

import android.text.TextUtils;

import com.example.sourcewall.connection.HttpFetcher;
import com.example.sourcewall.connection.ResultObject;
import com.example.sourcewall.model.AceModel;
import com.example.sourcewall.model.Post;
import com.example.sourcewall.model.PrepareData;
import com.example.sourcewall.model.SubItem;
import com.example.sourcewall.model.UComment;
import com.example.sourcewall.util.MDUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class PostAPI extends APIBase {

    public PostAPI() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 返回所有我加入的小组
     *
     * @return ResultObject，resultObject.result是ArrayList[SubItem]
     */
    public static ResultObject getAllMyGroups() {
        ResultObject resultObject = new ResultObject();
        String pageUrl = "http://m.guokr.com/group/i/" + UserAPI.getUserID() + "/joined/";
        ArrayList<SubItem> subItems = new ArrayList<>();
        int numPages;
        try {
            String firstPage = HttpFetcher.get(pageUrl).toString();
            Document doc1 = Jsoup.parse(firstPage);
            Elements lis = doc1.getElementsByClass("group-list").get(0).getElementsByTag("li");
            numPages = Integer.valueOf(doc1.getElementsByClass("page-num").text().replaceAll("1/", ""));
            //第一页
            for (int i = 0; i < lis.size(); i++) {
                Element element = lis.get(i).getElementsByTag("a").get(0);
                String groupUrl = element.attr("href");//
                String groupID = groupUrl.replaceAll("^\\D+(\\d+)\\D*", "$1");
                String groupName = element.getElementsByTag("b").text();
                SubItem subItem = new SubItem(SubItem.Section_Post, SubItem.Type_Single_Channel, groupName, groupID);
                subItems.add(subItem);
                //String groupIcon = element.getElementsByTag("img").get(0).attr("src").replaceAll("\\?\\S*$", "");
                //String groupMembers = element.getElementsByClass("group-member").get(0).text();
            }
            if (numPages > 1) {
                for (int j = 2; j <= numPages; j++) {
                    Thread.sleep(100);
                    String url = pageUrl + "?page=" + j;
                    Document pageDoc = Jsoup.parse(HttpFetcher.get(url).toString());
                    Elements pageLis = pageDoc.getElementsByClass("group-list").get(0).getElementsByTag("li");
                    for (int i = 0; i < pageLis.size(); i++) {
                        Element element = pageLis.get(i).getElementsByTag("a").get(0);
                        String groupUrl = element.attr("href");//
                        String groupID = groupUrl.replaceAll("^\\D+(\\d+)\\D*", "$1");
                        String groupName = element.getElementsByTag("b").text();
                        SubItem subItem = new SubItem(SubItem.Section_Post, SubItem.Type_Single_Channel, groupName, groupID);
                        subItems.add(subItem);
                    }
                }
            }
            resultObject.ok = true;
            resultObject.result = subItems;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultObject;
    }

    /**
     * @param url
     * @return
     */
    private static ArrayList<SubItem> getMyGroupInOnePage(String url) throws Exception {
        ArrayList<SubItem> subItems = new ArrayList<>();
        Document doc1 = Jsoup.parse(HttpFetcher.get(url).toString());
        Elements lis = doc1.getElementsByClass("group-list").get(0).getElementsByTag("li");
        for (int i = 0; i < lis.size(); i++) {
            Element element = lis.get(i).getElementsByTag("a").get(0);
            String groupUrl = element.attr("href");//
            String groupID = groupUrl.replaceAll("^\\D+(\\d+)\\D*", "$1");
            String groupName = element.getElementsByTag("b").text();
            SubItem subItem = new SubItem(SubItem.Section_Post, SubItem.Type_Single_Channel, groupName, groupID);
            subItems.add(subItem);
        }
        return subItems;
    }

    /**
     * 解析getMyGroupRecentRepliesPosts和getMyGroupHotPosts传过来的url
     * resultObject.result是ArrayList[Post] list
     *
     * @param url
     * @return
     * @throws IOException
     */
    private static ResultObject getMyGroupPostListFromMobileUrl(String url) {
        ResultObject resultObject = new ResultObject();
        try {
            ArrayList<Post> list = new ArrayList<Post>();
            String html = HttpFetcher.get(url).toString();
            Document doc = Jsoup.parse(html);
            Elements elements = doc.getElementsByClass("post-list");
            if (elements.size() == 1) {
                Elements postlist = elements.get(0).getElementsByTag("li");
                for (Iterator<Element> iterator = postlist.iterator(); iterator.hasNext(); ) {
                    Post item = new Post();
                    Element element = iterator.next();
                    Element link = element.getElementsByClass("post").get(0);
                    String postTitle = link.getElementsByTag("h4").get(0).text();
                    String postUrl = link.attr("href");
                    String postImageUrl = "";
                    String postAuthor = "";//没有Author名……
                    String postGroup = element.getElementsByClass("post-author").get(0).text();//没错，post-author是小组名……
                    Elements children = element.getElementsByClass("post-info-right").get(0).children();
                    int postLike = Integer.valueOf(children.get(0).text().replaceAll("\\D*", ""));
                    int postComm = Integer.valueOf(children.get(1).text().replaceAll("\\D*", ""));
                    item.setTitle(postTitle);
                    item.setUrl(postUrl);
                    item.setId(postUrl.replaceAll("\\?\\S*$", "").replaceAll("\\D+", ""));
                    item.setTitleImageUrl(postImageUrl);
                    item.setAuthor(postAuthor);
                    item.setGroupName(postGroup);
                    item.setLikeNum(postLike);
                    item.setReplyNum(postComm);
                    list.add(item);
                }
                resultObject.ok = true;
                resultObject.result = list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultObject;
    }

    /**
     * 返回《我的小组》最新回复的主题列表，解析html获得
     *
     * @param pageNo
     * @return
     * @throws IOException
     */
    public static ResultObject getMyGroupRecentRepliesPosts(int pageNo) {
        //TODO
        String url = "http://m.guokr.com/group/user/recent_replies/?page=" + pageNo;
        return getMyGroupPostListFromMobileUrl(url);
    }

    /**
     * 返回《我的小组》热门主题列表，解析html获得
     *
     * @param pageNo
     * @return
     * @throws IOException
     */
    public static ResultObject getMyGroupHotPosts(int pageNo) {
        //TODO
        String url = "http://m.guokr.com/group/user/hot_posts/?page=" + pageNo;
        return getMyGroupPostListFromMobileUrl(url);
    }

    /**
     * 返回《我的小组》最新主题列表，解析html获得，果壳移动版没有这个方式
     *
     * @param pageNo
     * @return
     * @throws IOException
     */
    public static ResultObject getMyGroupRecentPosts(int pageNo) throws IOException, Exception {
        //TODO
        ResultObject resultObject = new ResultObject();
        ArrayList<Post> list = new ArrayList<Post>();
        String url = "http://www.guokr.com/group/user/recent_posts/?page=" + pageNo;
        String html = HttpFetcher.get(url).toString();
        Document doc = Jsoup.parse(html);
        return resultObject;
    }


    /**
     * 获得小组热贴，解析html获得（与登录无关）
     *
     * @param pageNo，要获取的页码
     * @return 帖子列表
     * @throws java.io.IOException
     */
    public static ResultObject getGroupHotPostListFromMobileUrl(int pageNo) {
        ResultObject resultObject = new ResultObject();
        try {
            ArrayList<Post> list = new ArrayList<Post>();
            String url = "http://m.guokr.com/group/hot_posts/?page=" + pageNo;
            String html = HttpFetcher.get(url).toString();
            Document doc = Jsoup.parse(html);
            Elements elements = doc.getElementsByClass("post-index-list");
            if (elements.size() == 1) {
                Elements postlist = elements.get(0).getElementsByTag("li");
                for (Iterator<Element> iterator = postlist.iterator(); iterator.hasNext(); ) {
                    Post item = new Post();
                    Element element = (Element) iterator.next();
                    Element link = element.getElementsByClass("post").get(0);
                    String postTitle = link.text();
                    String postUrl = link.attr("href");
                    String postImageUrl = "";
                    if (link.getElementsByClass("post-img").size() > 0) {
                        String bgimg = link.getElementsByClass("post-img").get(0).attr("style")
                                .replace("background-image:url(", "");
                        int idx = bgimg.indexOf("?");
                        if (idx == -1) {
                            idx = bgimg.length();
                        }
                        postImageUrl = bgimg.substring(0, idx);
                    }
                    String postAuthor = "";
                    String postGroup = "";
                    String[] ang = element.getElementsByClass("post-info-content").get(0).text()
                            .split(" 发表于 ");
                    postAuthor = ang[0];
                    postGroup = ang[1];
                    int postLike = Integer.valueOf(element.getElementsByClass("like-num").get(0)
                            .text());
                    int postComment = Integer.valueOf(element.getElementsByClass("post-reply-num")
                            .get(0).text().replaceAll(" 回应$", ""));
                    item.setTitle(postTitle);
                    item.setUrl(postUrl);
                    item.setId(postUrl.replaceAll("\\?\\S*$", "").replaceAll("\\D+", ""));
                    item.setTitleImageUrl(postImageUrl);
                    item.setAuthor(postAuthor);
                    item.setGroupName(postGroup);
                    item.setLikeNum(postLike);
                    item.setReplyNum(postComment);
                    list.add(item);
                }
                resultObject.ok = true;
                resultObject.result = list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultObject;
    }

    /**
     * 根据小组id获得帖子列表，json格式
     *
     * @param id     小组id
     * @param offset 从第几个帖子开始取
     * @return
     * @throws java.io.IOException
     */
    public static ResultObject getGroupPostListByJsonUrl(String id, int offset) {
        ResultObject resultObject = new ResultObject();
        try {
            String url = "http://apis.guokr.com/group/post.json?retrieve_type=by_group&group_id=" + id
                    + "&limit=20&offset=" + offset;
            ArrayList<Post> list = new ArrayList<Post>();
            String jString = HttpFetcher.get(url).toString();
            JSONObject jss = new JSONObject(jString);
            boolean ok = jss.getBoolean("ok");
            if (ok) {
                JSONArray articles = jss.getJSONArray("result");
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject jo = articles.getJSONObject(i);
                    Post post = new Post();
                    post.setId(getJsonString(jo, "id"));
                    post.setGroupName(jo.getJSONObject("group").getString("name"));
                    post.setTitle(getJsonString(jo, "title"));
                    post.setUrl(getJsonString(jo, "url"));
                    post.setAuthor(jo.getJSONObject("author").getString("nickname"));
                    post.setAuthorID(jo.getJSONObject("author").getString("url")
                            .replaceAll("\\D+", ""));
                    post.setAuthorAvatarUrl(jo.getJSONObject("author").getJSONObject("avatar")
                            .getString("large").replaceAll("\\?\\S*$", ""));
                    post.setDate(parseDate(getJsonString(jo, "date_created")));
                    post.setReplyNum(getJsonInt(jo, "replies_count"));
                    post.setContent(getJsonString(jo, "html"));
                    // 无法获取赞的数量
                    // 无法获取titleImageUrl
                    list.add(post);
                }
                resultObject.ok = true;
                resultObject.result = list;
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultObject;
    }

    /**
     * 根据帖子id解析帖子详细内容，解析html获得
     *
     * @param id，帖子id
     * @return
     * @throws java.io.IOException
     */
    public static ResultObject getPostDetailByIDFromMobileUrl(String id) {
        return getPostDetailByPostMobileUrl("http://m.guokr.com/post/" + id + "/");
    }

    /**
     * 仅限第一页，根据帖子地址解析帖子详细内容，解析html获得
     *
     * @param url
     */
    public static ResultObject getPostDetailByPostMobileUrl(String url) {
        // 手机页面无法取得评论数，最好是从点击时带过来。TODO
        ResultObject resultObject = new ResultObject();
        try {
            Post detail = new Post();
            String html = HttpFetcher.get(url).toString();
            Document doc = Jsoup.parse(html);
            String postID = url.replaceAll("\\?\\S*$", "").replaceAll("\\D+", "");
            String groupID = doc.getElementsByClass("crumbs").get(0).getElementsByTag("a")
                    .attr("href").replaceAll("\\D+", "");
            String groupName = doc.getElementsByClass("crumbs").get(0).getElementsByTag("a").text();
            Element mainElement = doc.getElementById("contentMain");
            String authorAvatarUrl = mainElement.getElementsByClass("author-avatar").get(0)
                    .getElementsByTag("img").attr("src").replaceAll("\\?\\S*$", "");
            String title = mainElement.getElementsByClass("title").text();
            String author = mainElement.select(".author").select(".gfl").text();
            String authorID = mainElement.select(".author").select(".gfl").attr("href")
                    .replaceAll("\\D+", "");
            String date = mainElement.getElementsByTag("time").text();
            String content = mainElement.getElementById("postContent").outerHtml();
            int likeNum = Integer.valueOf(mainElement.getElementsByClass("like-num").get(0).text());
            detail.setGroupID(groupID);
            detail.setGroupName(groupName);
            detail.setAuthor(author);
            detail.setAuthorAvatarUrl(authorAvatarUrl);
            detail.setAuthorID(authorID);
            detail.setId(postID);
            detail.setTitle(title);
            detail.setDate(date);
            detail.setContent(content);
            detail.setLikeNum(likeNum);
            resultObject.ok = true;
            resultObject.result = detail;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultObject;
    }

    /**
     * 使用Json解析方式获得帖子评论列表
     * resultObject.result是ArrayList[UComment]
     *
     * @param id
     * @param offset
     * @return
     * @throws java.io.IOException
     * @throws org.json.JSONException
     */
    public static ResultObject getPostCommentsFromJsonUrl(String id, int offset) {
        ResultObject resultObject = new ResultObject();
        try {
            ArrayList<UComment> list = new ArrayList<UComment>();
            String url = "http://apis.guokr.com/group/post_reply.json?retrieve_type=by_post&post_id="
                    + id + "&limit=20&offset=" + offset;
            String jString = HttpFetcher.get(url).toString();
            JSONObject jss = new JSONObject(jString);
            boolean ok = jss.getBoolean("ok");
            if (ok) {
                JSONArray comments = jss.getJSONArray("result");
                for (int i = 0; i < comments.length(); i++) {
                    JSONObject jo = comments.getJSONObject(i);
                    UComment comment = new UComment();
                    comment.setID(getJsonString(jo, "id"));
                    comment.setAuthor(getJsonObject(jo, "author").getString("nickname"));
                    comment.setAuthorID(getJsonObject(jo, "author").getString("url")
                            .replaceAll("\\D+", ""));
                    comment.setAuthorAvatarUrl(getJsonObject(jo, "author").getJSONObject("avatar")
                            .getString("large").replaceAll("\\?\\S*$", ""));
                    comment.setDate(parseDate(getJsonString(jo, "date_created")));
                    comment.setLikeNum(getJsonInt(jo, "likings_count"));
                    String html = getJsonString(jo, "html");
                    comment.setContent(getJsonString(jo, "html"));
//                    comment.setSimpleHtml(TextHtmlHelper.correctLinkPaths(Html.fromHtml(html)));
                    comment.setFloor((offset + i + 1) + "楼");
                    comment.setHostID(jo.getJSONObject("post").getString("id"));
                    list.add(comment);
                }
                resultObject.ok = true;
                resultObject.result = list;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultObject;
    }

    /**
     * 返回第一页数据，包括Post与第一页的评论列表
     * resultObject.result是ArrayList[AceModel]
     *
     * @param id
     * @return
     */
    public static ResultObject getPostFirstPage(String id) {
        ResultObject resultObject = new ResultObject();
        ArrayList<AceModel> aceModels = new ArrayList<>();
        ResultObject articleResult = getPostDetailByIDFromMobileUrl(id);
        if (articleResult.ok) {
            ResultObject commentsResult = getPostCommentsFromJsonUrl(id, 0);
            if (commentsResult.ok) {
                Post post = (Post) articleResult.result;
                ArrayList<UComment> simpleComments = (ArrayList<UComment>) commentsResult.result;
                aceModels.add(post);
                aceModels.addAll(simpleComments);
                resultObject.ok = true;
                resultObject.result = aceModels;
            }
        }
        return resultObject;
    }

    /**
     * 使用Html解析方式获得帖子评论列表
     * 用不着，可删
     *
     * @param id
     * @param pageNo
     * @return
     */
    public static ArrayList<UComment> getPostCommentsFromHtmlUrl(String id, int pageNo) {
        ArrayList<UComment> list = new ArrayList<UComment>();
        String url = "http://m.guokr.com/post/" + id + "/?page=" + pageNo;
        try {
            String html = HttpFetcher.get(url).toString();
            Document doc = Jsoup.parse(html);
            String postID = url.replaceAll("\\?\\S*$", "").replaceAll("\\D+", "");
            Elements elements = doc.getElementsByClass("group-comments");
            if (elements.size() == 1) {
                return extractPostComments(elements.get(0), postID);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 解析出html中的帖子评论列表
     * 返回值直接是列表就够了
     *
     * @param element
     * @param postID
     * @return
     */
    private static ArrayList<UComment> extractPostComments(Element element, String postID) throws Exception {
        ArrayList<UComment> list = new ArrayList<UComment>();
        Elements commentList = element.getElementsByClass("comment");
        for (int i = 0; i < commentList.size(); i++) {
            UComment comment = new UComment();
            Element liElement = commentList.get(i);
            String commentID = liElement.id();
            String commentAuthorAvatarUrl = liElement.getElementsByClass("cmt-author-img")
                    .get(0).getElementsByTag("img").attr("src").replaceAll("\\?\\S*$", "");
            String commentAuthor = liElement.getElementsByClass("cmt-author").text();
            String commentAuthorID = liElement.getElementsByClass("cmt-author-img")
                    .attr("href").replaceAll("\\D+", "");
            String commentDate = liElement.getElementsByClass("cmt-time").text();
            String commentFloor = liElement.getElementsByClass("cmt-info-txt-left").text()
                    .replaceAll("^" + commentAuthor, "").replaceAll(commentDate + "$", "")
                    .replaceAll(" ", "");
            String commentContent = liElement.getElementsByClass("cmt-main").outerHtml();
            int likes = Integer.valueOf(liElement.getElementsByClass("cmt-like-num").text());
            comment.setAuthorAvatarUrl(commentAuthorAvatarUrl);
            comment.setAuthorID(commentAuthorID);
            comment.setAuthor(commentAuthor);
            comment.setDate(commentDate);
            comment.setID(commentID);
            comment.setContent(commentContent);
            comment.setFloor(commentFloor);
            comment.setHostID(postID);
            comment.setLikeNum(likes);
            list.add(comment);
        }
        return list;
    }

    /**
     * 赞一个帖子
     *
     * @param postID
     * @return
     */
    public static ResultObject likePost(String postID) {
        String url = "http://www.guokr.com/apis/group/post_liking.json";
        ResultObject resultObject = new ResultObject();
        try {
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("post_id", postID));
            pairs.add(new BasicNameValuePair("access_token", UserAPI.getToken()));
            String result = HttpFetcher.post(url, pairs).toString();
            JSONObject object = new JSONObject(result);
            if (getJsonBoolean(object, "ok")) {
                resultObject.ok = true;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return resultObject;
    }

    /**
     * 回复一个帖子，使用json请求，所以格式简单
     * 回复一个评论不过是在回复帖子的时候@了这个人而已
     *
     * @param id
     * @param content
     * @return ResultObject.result is the reply_id if ok;
     */
    public static ResultObject replyPost(String id, String content) {
        ResultObject resultObject = new ResultObject();
        try {
            String url = "http://apis.guokr.com/group/post_reply.json";
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("post_id", id));
            pairs.add(new BasicNameValuePair("content", content));
            pairs.add(new BasicNameValuePair("access_token", UserAPI.getToken()));
            String result = HttpFetcher.post(url, pairs).toString();
            JSONObject object = new JSONObject(result);
            if (getJsonBoolean(object, "ok")) {
                JSONObject resultJson = getJsonObject(object, "result");
                String replyID = getJsonString(resultJson, "id");
                resultObject.ok = true;
                resultObject.result = replyID;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultObject;
    }

    /**
     * 赞一个评论
     *
     * @param id
     * @return
     */
    public static ResultObject likeComment(String id) {
        String url = "http://www.guokr.com/apis/group/post_reply_liking.json";
        ResultObject resultObject = new ResultObject();
        try {
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("reply_id", id));
            pairs.add(new BasicNameValuePair("access_token", UserAPI.getToken()));
            String result = HttpFetcher.post(url, pairs).toString();
            JSONObject object = new JSONObject(result);
            if (getJsonBoolean(object, "ok")) {
                resultObject.ok = true;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return resultObject;
    }

    /**
     * 获取发帖所需的csrf和topic列表
     * resultObject.result是PostPrepareData
     *
     * @param group_id
     * @return
     */
    public static ResultObject getPostPrepareData(String group_id) {
        ResultObject resultObject = new ResultObject();
        try {
            String url = "http://www.guokr.com/group/" + group_id + "/post/edit/";
            String html = HttpFetcher.get(url).toString();
            Document doc = Jsoup.parse(html);
            Element selects = doc.getElementById("topic");
            ArrayList<BasicNameValuePair> pairs = new ArrayList<>();
            String csrf = doc.getElementById("csrf_token").attr("value");
            if (selects != null) {
                Elements elements = selects.getElementsByTag("option");
                if (elements != null && elements.size() > 0) {
                    for (int i = 0; i < elements.size(); i++) {
                        Element topic = elements.get(i);
                        String name = topic.text();
                        String value = topic.attr("value");
                        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                            pairs.add(new BasicNameValuePair(name, value));
                        }
                    }
                }
            }
            if (!TextUtils.isEmpty(csrf)) {
                PrepareData prepareData = new PrepareData();
                prepareData.setCsrf(csrf);
                prepareData.setPairs(pairs);
                resultObject.ok = true;
                resultObject.result = prepareData;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultObject;
    }

    /**
     * 发帖
     *
     * @param group_id
     * @param csrf
     * @param title
     * @param body     html格式
     * @param topic
     * @return
     */
    public static ResultObject publishPost(String group_id, String csrf, String title, String body, String topic) {
        ResultObject resultObject = new ResultObject();
        String url = "http://www.guokr.com/group/" + group_id + "/post/edit/";
        try {
            ResultObject mdResult = MDUtil.parseMarkdownByGitHub(body);
            if (mdResult.ok) {
                //使用github接口转换成html
                String htmlBody = (String) mdResult.result;
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("csrf_token", csrf));
                pairs.add(new BasicNameValuePair("title", title));
                pairs.add(new BasicNameValuePair("topic", topic));
                pairs.add(new BasicNameValuePair("body", htmlBody));
                pairs.add(new BasicNameValuePair("captcha", ""));
                pairs.add(new BasicNameValuePair("share_opts", "activity"));

                ResultObject result = HttpFetcher.post(url, pairs);
                if (result.statusCode == 302 && testPublishResult(result.toString())) {
                    resultObject.ok = true;
                    resultObject.result = result.toString();
                }
            } else {
                //转换失败……
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultObject;
    }

    private static boolean testPublishResult(String res) {
        try {
            Document doc = Jsoup.parse(res);
            String href = doc.getElementsByTag("a").attr("href");
            return href.matches("/post/\\d+/");
        } catch (Exception e) {
            return false;
        }
    }

}
