package com.shopnolive.shopnolive.adapter;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.model.GiftHistory;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GiftHistoryAdapter extends RecyclerView.Adapter<GiftHistoryAdapter.GiftHistoryHolder> {

    List<GiftHistory> giftHistories;

    public GiftHistoryAdapter(List<GiftHistory> giftHistories) {
        this.giftHistories = giftHistories;
    }

    @NonNull
    @Override
    public GiftHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GiftHistoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gift_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GiftHistoryHolder holder, int position) {
        GiftHistory giftHistory = giftHistories.get(position);
        Picasso.get().load(BASE_URL + giftHistory.getUserImageUrl()).placeholder(R.drawable.user1).into(holder.userImage);
        Picasso.get().load(giftHistory.getGiftStickerUrl()).placeholder(R.drawable.user1).into(holder.giftImage);

        holder.username.setText(giftHistory.getUsername());
        holder.giftPoint.setText(giftHistory.getGiftCoin());
        holder.userLevel.setText(giftHistory.getUserLevel());
    }

    @Override
    public int getItemCount() {
        return giftHistories.size();
    }

    static class GiftHistoryHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        ImageView giftImage;
        TextView username,userLevel;
        TextView giftPoint;

        public GiftHistoryHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.user_image_gift_history);
            giftImage = itemView.findViewById(R.id.gift_sticker);
            username = itemView.findViewById(R.id.user_name_gift_history);
            giftPoint = itemView.findViewById(R.id.coin_gift_history);
            userLevel = itemView.findViewById(R.id.user_level_gift_item);
        }
    }
}
