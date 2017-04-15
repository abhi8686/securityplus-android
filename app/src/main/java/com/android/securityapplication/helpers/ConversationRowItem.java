package com.android.securityapplication.helpers;

import android.content.Context;
import android.view.View;

/**
 * Created by rashmi on 29/11/16.
 */

public class ConversationRowItem {
    String imageUrl;
    Context mContext;
    String name, heading, time, from;
    boolean isOnline;
    View.OnClickListener itemListener;

    public ConversationRowItem(Context context)
    {
        this.mContext = context;
    }
    public void setName(String text){name = text;}
    public void setHeading(String text){ heading = text;}
    public void setTime(String text){time=text;}
    public void setImage(String url){imageUrl=url;}
    public void setOnline(boolean val){isOnline=val;}
    public void setFrom(String val){from=val;}
    public void setItemListener(View.OnClickListener l){itemListener =l;}
    public String getName(){return name;}
    public String getHeading(){return heading;}
    public String getTime(){return time;}
    public String getImage(){return imageUrl;}
    public boolean getOnline(){return isOnline;}
    public String getFrom(){return from;}
    public View.OnClickListener getItemListener(){return itemListener;}
}
