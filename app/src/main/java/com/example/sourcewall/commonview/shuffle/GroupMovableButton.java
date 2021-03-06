package com.example.sourcewall.commonview.shuffle;

import android.content.Context;

import com.example.sourcewall.db.gen.MyGroup;

/**
 * Created by NashLegend on 2015/1/20 0020
 */
public class GroupMovableButton extends MovableButton<MyGroup> {

    public GroupMovableButton(Context context) {
        super(context);
    }

    @Override
    public MovableButton clone() {
        MovableButton button = new GroupMovableButton(getContext());
        LayoutParams params = new LayoutParams(ShuffleDesk.buttonWidth, ShuffleDesk.buttonHeight);
        button.setLayoutParams(params);
        button.setSection(section);
        return button;
    }

    @Override
    public MyGroup getSection() {
        section.setOrder(getIndex());
        section.setSelected(selected);
        return section;
    }

    @Override
    public void setSection(MyGroup section) {
        this.section = section;
        setTitle(section.getName());
        setSelected(section.getSelected());
    }
}
