package com.shopnolive.shopnolive.adapter;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.utils.Variable.liveUserPosition;
import static com.shopnolive.shopnolive.utils.Variable.selectedUserId;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.liveVideoPlayer.LiveVideoPlayerActivity;
import com.shopnolive.shopnolive.model.profile.IsLiveUser;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatHeadAdapter extends RecyclerView.Adapter<ChatHeadAdapter.ViewHolder> {

    List<IsLiveUser> chatHeadModels;
    private final Context userContext;

    public ChatHeadAdapter(Context context, List<IsLiveUser> userLives) {
        this.userContext = context;
        this.chatHeadModels = userLives;
    }

    @NonNull
    @Override
    public ChatHeadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_heads, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IsLiveUser liveUser = chatHeadModels.get(holder.getAdapterPosition());

        if (liveUser != null) {
            Picasso.get().load(BASE_URL + liveUser.getImage()).placeholder(R.drawable.user1).into(holder.image);

            if (liveUser.getIsLive().equals("live")) {
                holder.name.setText(liveUser.getName());
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(userContext, LiveVideoPlayerActivity.class);
            // intent.putExtra("story", mData.get(position).get());
            if (liveUser != null) {
                intent.putExtra("name", liveUser.getName());
                intent.putExtra("id", liveUser.getFollowersId());
            }
            liveUserPosition = holder.getAdapterPosition();
            selectedUserId = Objects.requireNonNull(liveUser).getId();
            userContext.startActivity(intent);
            Log.d("SelectedUserId", "Selected userId: " + selectedUserId);
        });

    }


    @Override
    public int getItemCount() {
        if (chatHeadModels == null)
            return 0;
        return chatHeadModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.user_image);
            name = itemView.findViewById(R.id.user_name);
        }
    }
}
