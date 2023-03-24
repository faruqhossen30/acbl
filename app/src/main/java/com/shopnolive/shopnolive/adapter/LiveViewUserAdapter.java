package com.shopnolive.shopnolive.adapter;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.allUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.userPosition;
import static com.shopnolive.shopnolive.utils.Variable.userProfileFollow;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.model.ResultMsg;
import com.shopnolive.shopnolive.model.profile.Follower;
import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;


public class LiveViewUserAdapter extends RecyclerView.Adapter<LiveViewUserAdapter.ViewHolder> {

    private List<ProfileData> chatHeadModels;
    private Context userContext;
    boolean followResult = false;

    public LiveViewUserAdapter(Context context, List<ProfileData> models) {
        this.userContext = context;
        this.chatHeadModels = models;
    }

    @NonNull
    @Override
    public LiveViewUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveViewUserAdapter.ViewHolder holder, int position) {

        // holder.image.setImageResource(chatHeadModels.get(position).get());
        ProfileData head = chatHeadModels.get(position);
        try {
            holder.name.setText(head.getName());
            holder.coinTotal.setText(head.getPresentCoinBalance());
        } catch (Exception e) {
        }

        try {
            if (head.getImage() != null)
                Picasso.get().load(BASE_URL + head.getImage()).placeholder(R.drawable.user1).into(holder.profileImage);

        } catch (Exception e) {
        }

        try {

            if (userInfo.getId().equals(head.getId())) {
                holder.followBTN.setVisibility(View.INVISIBLE);
            }

            followUser(holder);

        } catch (Exception e) {
        }
        holder.followBTN.setOnClickListener(v -> {

            if (followResult) {

                Call<ResultMsg> unFollow = api.unFollowUser(userInfo.getId(), allUserInfo.get(userPosition).getId());

                unFollow.enqueue(new Callback<ResultMsg>() {
                    @Override
                    public void onResponse(Call<ResultMsg> call, retrofit2.Response<ResultMsg> response) {

                        if (response.body() != null) {
                            followResult = false;
                            setUnFollow(holder);
                            //chatHeadModels.get(position).get
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultMsg> call, Throwable t) {

                    }
                });
            } else {
                Call<ResultMsg> follow = api.followUser(userInfo.getId(), allUserInfo.get(userPosition).getId());

                follow.enqueue(new Callback<ResultMsg>() {
                    @Override
                    public void onResponse(Call<ResultMsg> call, retrofit2.Response<ResultMsg> response) {

                        if (response.body() != null) {
                            followResult = true;
                            setFollow(holder);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultMsg> call, Throwable t) {

                    }
                });
            }
        });


    }


    private void followUser(ViewHolder holder) {
        //Log.d("followUser", "followUser: "+userProfileFollow.getId());

        int checkFollow = 0;
        try {

            if (userProfileFollow != null)
                for (Follower f : userProfileFollow) {
                    Log.d("followUser", "followUser: " + f.getFollowersId());
                    Log.d("followUser", "followUser: " + allUserInfo.get(userPosition).getId());

                    if (f.getFollowersId().equals(allUserInfo.get(userPosition).getId())) {
                        checkFollow++;
                        break;
                    }
                }

        } catch (Exception e) {

        }
        if (checkFollow != 0) {
            followResult = true;
            setFollow(holder);
        } else {
            setUnFollow(holder);
        }

    }


    private void setUnFollow(ViewHolder holder) {
        holder.followBTN.setImageDrawable(userContext.getResources().getDrawable(R.drawable.plus));
    }

    private void setFollow(ViewHolder holder) {
        holder.followBTN.setImageDrawable(userContext.getResources().getDrawable(R.drawable.ic_min));
    }

    @Override
    public int getItemCount() {
        if (chatHeadModels == null)
            return 0;
        return chatHeadModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        ImageView followBTN;
        TextView coinTotal, name;

        public ViewHolder(View itemView) {
            super(itemView);
            followBTN = itemView.findViewById(R.id.viewUserBTN);
            name = itemView.findViewById(R.id.viewUserName);
            coinTotal = itemView.findViewById(R.id.viewUserCoin);
            profileImage = itemView.findViewById(R.id.adminOrUserMessageIcon);
        }
    }
}
