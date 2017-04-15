package com.android.securityapplication;

import android.view.View;

/**
 * Created by rashmiagarwal on 14/04/17.
 */
public class RowItem {
    String name, id;
    View.OnClickListener itemListener;

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public View.OnClickListener getItemListener() {
        return itemListener;
    }

    public void setItemListener(View.OnClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public void setName(String name) {
        this.name = name;
    }
}
