package com.android.securityapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.securityapplication.R;
import com.squareup.picasso.Picasso;
import com.android.securityapplication.helpers.Message;
import java.util.ArrayList;

import static android.view.View.GONE;


public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {
    private ArrayList<Message> messages = new ArrayList<Message>();
//    private TextView CHAT_TXT, chat_date;
//    private ImageView chat_image;
//    private ImageView left_image, right_image;
    Context CTX;
    private int userId;
    private int SELF = 786;

    public ThreadAdapter(Context context, ArrayList<Message> messages, int userId){
        this.userId = userId;
        this.messages = messages;
        this.CTX = context;
    }

    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        Message message = messages.get(position);

        //If its owner  id is  equals to the logged in user id
//        if (message.getUsersId() == userId) {
//            //Returning self
//            return SELF;
//        }
        if(message.getApp().equals("Agent")) {
            if (message.getFrom().equals("Agent")) {
                return SELF;
            }
        }else{
            if (message.getFrom().equals("User")) {
                return SELF;
            }
        }
        //else returning position
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread_other, parent, false);
        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.textViewTime.setText(message.getSentAt());

        if(message.getImage() == "" || message.getImage() == null ){
            Picasso.with(CTX).load(R.drawable.default_profile).into(holder.user_image);
        }else {
            Picasso.with(CTX).load(message.getImage()).into(holder.user_image);
        }
        Log.e("MESSAGES",message.getMessage());

        if(message.getType().equals("text")){
            if(holder.textViewMessage.getVisibility()==GONE)
                holder.textViewMessage.setVisibility(View.VISIBLE);
            holder.textViewMessage.setText(message.getMessage());
            holder.chat_image.setVisibility(GONE);
        }else if(message.getType().equals("image")){
            holder.textViewMessage.setVisibility(GONE);
            if(holder.chat_image.getVisibility()==GONE)
                holder.chat_image.setVisibility(View.VISIBLE);
            Picasso.with(CTX).load(message.getMessage()).into(holder.chat_image);
            final String imageUrl = message.getMessage();
            holder.chat_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri imageUri = Uri.parse(imageUrl);
                    Intent imageIntent = new Intent(Intent.ACTION_VIEW);
                    imageIntent.setDataAndType(imageUri, "image/*");
                    imageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    CTX.startActivity(imageIntent);
                }
            });
        }else {
            holder.textViewMessage.setVisibility(GONE);
            if(holder.chat_image.getVisibility()==GONE)
                holder.chat_image.setVisibility(View.VISIBLE);
            final String[] latlng = message.getMessage().split(",");
            String url = "http://maps.google.com/maps/api/staticmap?center=" + latlng[0] + "," + latlng[1] + "&zoom=15&size=200x200&sensor=false";
            Picasso.with(CTX).load(url).into(holder.chat_image);
            holder.chat_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri mapUri = Uri.parse("geo:"+latlng[0]+","+latlng[1]+"?q="+latlng[0]+","+latlng[1]+"(Location)");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    CTX.startActivity(mapIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
        public TextView textViewTime;
        public ImageView chat_image, user_image;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.singleMessage);
            textViewTime = (TextView) itemView.findViewById(R.id.time);
            user_image = (ImageView) itemView.findViewById(R.id.image);
            chat_image = (ImageView) itemView.findViewById(R.id.singleMessageImage);
        }
    }
}
