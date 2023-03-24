package com.shopnolive.shopnolive.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.model.GiftListData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GiftListAdapter extends RecyclerView.Adapter<GiftListAdapter.GiftViewHolder> {

    List<GiftListData> giftList;
    GiftItemClickListener giftItemClickListener;
    int selectedItemPos = -1;
    int lastItemSelectedPos = -1;

    public GiftListAdapter(List<GiftListData> giftList, GiftItemClickListener giftItemClickListener) {
        this.giftList = giftList;
        this.giftItemClickListener = giftItemClickListener;
    }

    @NonNull
    @Override
    public GiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GiftViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gift_item, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull GiftViewHolder holder, int position) {
        GiftListData giftItem = giftList.get(position);
        Picasso.get().load(giftItem.getImageUrl()).placeholder(R.drawable.user1).into(holder.giftImage);
        holder.giftPoint.setText("" + giftItem.getDiamond());

        if (position == selectedItemPos) {
            holder.giftBgLayout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.background_gift_item_selected));
        } else {
            holder.giftBgLayout.setBackground(holder.itemView.getResources().getDrawable(R.drawable.background_gift_item));
        }
    }

    @Override
    public int getItemCount() {
        return giftList.size();
    }

    class GiftViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout giftBgLayout;
        ImageView giftImage;
        TextView giftPoint;

        public GiftViewHolder(@NonNull View itemView) {
            super(itemView);

            giftBgLayout = itemView.findViewById(R.id.layout_gift_background);
            giftImage = itemView.findViewById(R.id.iv_gift_item);
            giftPoint = itemView.findViewById(R.id.tv_gift_item);


            itemView.setOnClickListener(v -> {
                selectedItemPos = getAdapterPosition();
                if (lastItemSelectedPos == -1)
                    lastItemSelectedPos = selectedItemPos;
                else {
                    notifyItemChanged(lastItemSelectedPos);
                    lastItemSelectedPos = selectedItemPos;
                }
                notifyItemChanged(selectedItemPos);
                giftItemClickListener.onClick(giftList.get(getAdapterPosition()), giftBgLayout);
            });
        }
    }

    public interface GiftItemClickListener {
        void onClick(GiftListData giftItem, ConstraintLayout giftBgLayout);
    }
}
