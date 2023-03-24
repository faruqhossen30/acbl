package com.shopnolive.shopnolive.adapter;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.waitingInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.model.LiveRequest;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LiveRequestAdapter extends RecyclerView.Adapter<LiveRequestAdapter.ViewHolder> {

    private List<LiveRequest> liveRequests;
    private Context userContext;
    boolean followResult = false;

    public LiveRequestAdapter(Context context, List<LiveRequest> models) {
        this.userContext = context;
        this.liveRequests = models;
    }

    @NonNull
    @Override
    public LiveRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveRequestAdapter.ViewHolder holder, int position) {

        // holder.image.setImageResource(chatHeadModels.get(position).get());
        //  Log.d("datatesting", "onBindViewHolder: "+liveRequests.get(position).getName());

        LiveRequest request = liveRequests.get(position);

        ///holder.coinTotal.setText(request.get());
        try {
            holder.name.setText(request.getName());
            if (request.getImage() != null)
                Picasso.get().load(BASE_URL + request.getImage()).placeholder(R.drawable.user1).into(holder.image);
        } catch (Exception e) {

        }

        try {
            if (waitingInfo.equals("other")) {
                holder.followBTN.setVisibility(View.GONE);
                holder.disconnectBtn.setVisibility(View.GONE);

            } else {

                if (request.getType().equals("online")) {
                    holder.followBTN.setVisibility(View.GONE);
                } else {
//                    holder.followBTN.setText("Accept");
                }
//                holder.followBTN.setTextColor(userContext.getResources().getColor(R.color.white));
                holder.followBTN.setBackground(userContext.getResources().getDrawable(R.drawable.my_button));
            }

        } catch (Exception e) {
        }
//        followUser(holder);

        holder.disconnectBtn.setOnClickListener(v -> {
            if (request.getType().equals("online")) {
                request.setType("out");
                myRef.child(userInfo.getId()).child("join").child(request.getUn_id()).setValue(request);
            } else {
                myRef.child(userInfo.getId()).child("join").child(request.getUn_id()).removeValue();
            }
        });

        holder.followBTN.setOnClickListener(v -> {

            //LiveRequest rType;
            try {

                if (request.getType().equals("online")) {
                    // LiveRequest liveRequest = new LiveRequest(userInfo.getId(), request.getUn_id(), userInfo.getName(), userInfo.getImage(), "waiting");
                    //Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), messages);

                    request.setType("waiting");
                    //myRef.child(allUserInfo.get(userPosition).getId()).child("join").child(unicKeyLiveView).setValue(liveRequest);

                    myRef.child(userInfo.getId()).child("join").child(request.getUn_id()).setValue(request);
                } else {
                    //rType = new LiveRequest("online");
                    request.setType("online");
                    myRef.child(userInfo.getId()).child("join").child(request.getUn_id()).setValue(request);
                }

                //  notifyDataSetChanged();

            } catch (Exception e) {
            }
        });


    }

    private void setOnlineBackground(ViewHolder holder) {
        holder.followBTN.setBackground(userContext.getResources().getDrawable(R.drawable.plus));
    }

    private void setWaitingBackground(ViewHolder holder) {
        holder.followBTN.setBackground(userContext.getResources().getDrawable(R.drawable.ic_min));
        //follow.setBackground(getResources().getDrawable(R.drawable.ic_min));
    }

    @Override
    public int getItemCount() {
        if (liveRequests == null)
            return 0;
        return liveRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        ImageButton followBTN, disconnectBtn;
        TextView coinTotal, name;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.liveIV);
            followBTN = itemView.findViewById(R.id.liveUserBTN);
            disconnectBtn = itemView.findViewById(R.id.disconnectBTN);
            name = itemView.findViewById(R.id.liveUserName);
            coinTotal = itemView.findViewById(R.id.liveUserCoin);
            //message = itemView.findViewById(R.id.adminOrUserMessageShow);
        }
    }
}