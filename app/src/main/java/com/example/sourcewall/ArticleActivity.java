package com.example.sourcewall;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sourcewall.adapters.ArticleDetailAdapter;
import com.example.sourcewall.commonview.LListView;
import com.example.sourcewall.connection.ResultObject;
import com.example.sourcewall.connection.api.ArticleAPI;
import com.example.sourcewall.model.AceModel;
import com.example.sourcewall.model.Article;
import com.example.sourcewall.model.SimpleComment;
import com.example.sourcewall.util.Consts;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class ArticleActivity extends BaseActivity implements LListView.OnRefreshListener {

    LListView listView;
    ArticleDetailAdapter adapter;
    Article article;
    LoaderTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        article = (Article) getIntent().getSerializableExtra(Consts.Extra_Article);
        listView = (LListView) findViewById(R.id.list_detail);
        adapter = new ArticleDetailAdapter(this);
        listView.setAdapter(adapter);
        listView.setCanPullToRefresh(false);
        listView.setCanPullToLoadMore(true);
        listView.setOnRefreshListener(this);
        loadData(0);
    }

    private void loadData(int offset) {
        if (offset < 0) {
            offset = 0;
        }
        cancelPotentialTask();
        task = new LoaderTask();
        task.execute(offset);
    }

    private void cancelPotentialTask() {
        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
            task.cancel(true);
            listView.doneOperation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartRefresh() {
        loadData(0);
    }

    @Override
    public void onStartLoadMore() {
        loadData(adapter.getCount() - 1);
    }

    class LoaderTask extends AsyncTask<Integer, Integer, ResultObject> {
        int offset;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ResultObject doInBackground(Integer... params) {
            offset = params[0];
            ArrayList<AceModel> models = new ArrayList<AceModel>();
            ResultObject resultObject = new ResultObject();
            try {
                if (offset > 0) {
                    models.addAll(ArticleAPI.getArticleComments(article.getId(), offset));
                } else {
                    //同时取了热门回帖，但是在这里没有显示 TODO
                    Article detailArticle = ArticleAPI.getArticleDetailByID(article.getId());
                    ArrayList<SimpleComment> simpleComments = ArticleAPI.getArticleComments(article.getId(), 0);
                    article.setContent(detailArticle.getContent());
                    models.add(article);
                    models.addAll(simpleComments);
                }
                resultObject.result = models;
                resultObject.ok = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultObject;
        }

        @Override
        protected void onPostExecute(ResultObject result) {
            if (!isCancelled()) {
                if (result.ok) {
                    ArrayList<AceModel> ars = (ArrayList<AceModel>) result.result;
                    if (offset > 0) {
                        //Load More
                        if (ars.size() > 0) {
                            adapter.addAll(ars);
                        } else {
                            //no data loaded
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        //Refresh
                        if (ars.size() > 0) {
                            adapter.setList(ars);
                        } else {
                            //no data loaded,不清除，保留旧数据
                        }
                        adapter.notifyDataSetInvalidated();
                    }
                } else {
                    // load error
                }
                listView.doneOperation();
            }
        }
    }
}
