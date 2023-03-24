package com.shopnolive.shopnolive.ppal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.ppal.callbacks.MessageListClick;
import com.shopnolive.shopnolive.model.MessageListModel;
import com.shopnolive.shopnolive.ppal.model.FollowListModel;
import com.shopnolive.shopnolive.ppal.util.Common;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Context context;
    private String tag;
    private List<?> itemList;
    private MessageListClick messageListClick;

    public StoryAdapter(Context context, String tag, MessageListClick messageListClick) {
        this.context = context;
        this.tag = tag;
        this.messageListClick = messageListClick;
        if (tag.equals("story")){
            itemList = new ArrayList<FollowListModel.MyFollower>();

        }else if (tag.equals("msg")){
            itemList=new ArrayList<MessageListModel>();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        if (tag.equals("story")){
            view = inflater.inflate(R.layout.story_layout,parent,false);

        }else if (tag.equals("msg")){
            view=inflater.inflate(R.layout.msg_list_layout,parent,false);

        }
        assert view != null;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (tag.equals("story")){
            FollowListModel.MyFollower model = (FollowListModel.MyFollower) itemList.get(position);
            Glide.with(context).load(Common.BASE_URL_MAIN+model.getImage()).into(holder.imageViewStory);
            holder.textViewUserName.setText(model.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    messageListClick.onStoryClick(model);
                }
            });
        }else if (tag.equals("msg")){

            MessageListModel model = (MessageListModel) itemList.get(position);

            Glide.with(context).load(model.getImg()).into(holder.imageViewMsg);
            holder.userName.setText(model.getUser_name());
            holder.textViewDesc.setText(model.getDesc());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    messageListClick.onMessageListClick(model);
                }
            });


        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(List<?> list){
        itemList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageViewStory;
        TextView textViewUserName;
        ImageView imageViewLiveIndicator;

        CircleImageView imageViewMsg;
        TextView userName,textViewDesc;
        ImageView imageViewMsgStatus,imageViewCallButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewStory=itemView.findViewById(R.id.img_story);
            textViewUserName=itemView.findViewById(R.id.tv_user_name_l);
            imageViewLiveIndicator=itemView.findViewById(R.id.image_view_live_indicator);

            imageViewMsg=itemView.findViewById(R.id.img_msg);
            userName=itemView.findViewById(R.id.tv_user_name);
            textViewDesc=itemView.findViewById(R.id.tv_desc);
            imageViewMsgStatus=itemView.findViewById(R.id.image_msg_status);
            imageViewCallButton=itemView.findViewById(R.id.img_call_button);
        }
    }
}
