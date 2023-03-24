package com.shopnolive.shopnolive.adapter;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.utils.Variable.userPosition;
import static com.shopnolive.shopnolive.utils.Variable.selectedUserId;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.shopnolive.shopnolive.ui.live.PlayBroadcastActivity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserStoryAdapter extends RecyclerView.Adapter<UserStoryAdapter.MyViewHolder> {
    private final Context mContext;
    private final List<ProfileData> mData;

    public UserStoryAdapter(Context mContext, List<ProfileData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override

    public UserStoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.user_day, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserStoryAdapter.MyViewHolder holder, int position) {

        ProfileData data = mData.get(holder.getAdapterPosition());

        if (data != null) {
            holder.userName.setText(data.getName());
            holder.views.setText("");
            Picasso.get().load(BASE_URL + data.getImage()).placeholder(R.drawable.app_icon).into(holder.story);
            Picasso.get().load(BASE_URL + data.getImage()).placeholder(R.drawable.user1).into(holder.userImage);
        }

        holder.story.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PlayBroadcastActivity.class);
            intent.putExtra("position", holder.getAdapterPosition());
            intent.putExtra("id", Objects.requireNonNull(data).getId());
            intent.putExtra("waiting", "other");
            intent.putExtra("name", data.getName());

            //  intent.putExtra("waiting", "other" );
            userPosition = holder.getAdapterPosition();
            selectedUserId = Objects.requireNonNull(data).getId();

            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView userName;
        TextView views;
        ImageView story;

        public MyViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_day_image);
            userName = itemView.findViewById(R.id.user_day_name);
            views = itemView.findViewById(R.id.views);
            story = itemView.findViewById(R.id.story);
        }
    }
}
