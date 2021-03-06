package com.example.sourcewall.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.sourcewall.commonview.TTextView;
import com.example.sourcewall.R;
import com.example.sourcewall.SimpleReplyActivity;
import com.example.sourcewall.model.Question;
import com.example.sourcewall.util.Consts;
import com.example.sourcewall.util.SharedUtil;
import com.example.sourcewall.util.TextHtmlHelper;

/**
 * Created by NashLegend on 2014/9/18 0018
 */
public class QuestionView extends AceView<Question> {
    private Question question;
    private TextView titleView;
    private TextView authorView;
    private TextView dateView;
    private TTextView contentView;
    private View layoutComments;
    private TextView commentNumView;
    private TextHtmlHelper htmlHelper;

    public QuestionView(Context context) {
        super(context);
        if (SharedUtil.readBoolean(Consts.Key_Is_Night_Mode, false)) {
            setBackgroundColor(getContext().getResources().getColor(R.color.page_background_night));
        } else {
            setBackgroundColor(getContext().getResources().getColor(R.color.page_background));
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_question_view, this);
        htmlHelper = new TextHtmlHelper(context);
        titleView = (TextView) findViewById(R.id.text_title);
        authorView = (TextView) findViewById(R.id.text_author);
        dateView = (TextView) findViewById(R.id.text_date);
        contentView = (TTextView) findViewById(R.id.web_content);
        commentNumView = (TextView) findViewById(R.id.text_replies_num);
        layoutComments = findViewById(R.id.layout_comment);
        layoutComments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SimpleReplyActivity.class);
                intent.putExtra(Consts.Extra_Ace_Model, question);
                getContext().startActivity(intent);
            }
        });
    }

    public QuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuestionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setData(Question model) {
        if (question == null) {
            question = model;
            commentNumView.setText(question.getCommentNum() + "");
            titleView.setText(question.getTitle());
            authorView.setText(question.getAuthor());
            dateView.setText(question.getDate());
            htmlHelper.load(contentView, question.getContent());
        }
    }

    @Override
    public Question getData() {
        return question;
    }
}
