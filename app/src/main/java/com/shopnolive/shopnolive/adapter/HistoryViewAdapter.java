package com.shopnolive.shopnolive.adapter;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.userProfileFollow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel;
import com.shopnolive.shopnolive.model.ResultMsg;
import com.shopnolive.shopnolive.model.gift.GiftHistoryItem;
import com.shopnolive.shopnolive.model.profile.Follower;
import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class HistoryViewAdapter extends RecyclerView.Adapter<HistoryViewAdapter.ViewHolder> {

    List<GiftHistoryItem> userHistories;
    private final Context userContext;
    private final ProfileViewModel profileViewModel;
    boolean followResult = false;

    public HistoryViewAdapter(Context context, List<GiftHistoryItem> models) {
        this.userContext = context;
        this.userHistories = models;
        this.profileViewModel = ViewModelProviders.of((FragmentActivity) userContext).get(ProfileViewModel.class);
    }

    @NonNull
    @Override
    public HistoryViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewAdapter.ViewHolder holder, int position) {
        GiftHistoryItem info = userHistories.get(position);
        try {
            if (info.getImage() != null) {
                Picasso.get().load(BASE_URL + info.getImage()).placeholder(R.drawable.user1).into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.user1);
            }

            holder.diamond.setText(Html.fromHtml("\uD83D\uDC8E" + info.getCoin()));
            holder.name.setText(Html.fromHtml(info.getName()));
            holder.level.setText(String.valueOf(info.getUserLevel()));
        } catch (Exception ignored) {
        }

        holder.itemView.setOnClickListener(view -> {

            AlertDialog.Builder a_dialog = new AlertDialog.Builder(userContext, R.style.MaterialDialogSheet);
            View details = LayoutInflater.from(userContext).inflate(R.layout.dialog_user_history, null);

            TextView userName = details.findViewById(R.id.userNameDetailsForHistory);
            TextView userLevel = details.findViewById(R.id.userLevelInDialog);
            TextView userId = details.findViewById(R.id.userIdForHistory);
            TextView userDiamond = details.findViewById(R.id.userDiamondForHistory);
            CircleImageView imageView = details.findViewById(R.id.detailsProfileImage);
            ImageView followIV = details.findViewById(R.id.userFollowForHistory);

            a_dialog.setView(details);

            Dialog dialog = a_dialog.create();
            dialog.show();


            profileViewModel.getProfileByMessId(info.getSenderId()).observe((LifecycleOwner) userContext, profileData -> {

                followUser(profileData, followIV);

                try {
                    userName.setText(profileData.getName());
                } catch (Exception e) {
                }


                try {
                    userLevel.setText("" + profileData.getUserLevel());
                } catch (Exception e) {
                }


                try {
                    userId.setText("id - " + profileData.getId());
                    //                    userLevel.setText(info.getUserLevel());
                } catch (Exception e) {
                }

                try {
                    userDiamond.setText("\uD83D\uDC8E " + profileData.getPresentCoinBalance());
                } catch (Exception e) {
                }


                try {
                    Picasso.get().load(BASE_URL + profileData.getImage()).placeholder(R.drawable.user1).into(imageView);
                } catch (Exception e) {
                }
                //  Toast.makeText(userContext, "Check view", Toast.LENGTH_SHORT).show();


                followIV.setOnClickListener(view1 -> followUserResponse(profileData, followIV));

            });

        });
    }


    private void followUserResponse(ProfileData info, ImageView followIV) {

        if (followResult) {

            Call<ResultMsg> unFollow = api.unFollowUser(userInfo.getId(), info.getId());

            unFollow.enqueue(new Callback<ResultMsg>() {
                @Override
                public void onResponse(Call<ResultMsg> call, retrofit2.Response<ResultMsg> response) {

                    if (response.body() != null) {
                        followResult = false;
                        setUnFollow(followIV);
                        getObserver();
                    }
                }

                @Override
                public void onFailure(Call<ResultMsg> call, Throwable t) {

                }
            });

        } else {

            Call<ResultMsg> follow = api.followUser(userInfo.getId(), info.getId());

            follow.enqueue(new Callback<ResultMsg>() {
                @Override
                public void onResponse(Call<ResultMsg> call, retrofit2.Response<ResultMsg> response) {

                    if (response.body() != null) {
                        followResult = true;
                        setFollow(followIV);
                        getObserver();
                    }
                }

                @Override
                public void onFailure(Call<ResultMsg> call, Throwable t) {

                }
            });
        }
    }

    private void getObserver() {
        profileViewModel.getProfile().observe((LifecycleOwner) userContext, profileData -> {
            // userInfo =  loginRespons;
            notifyDataSetChanged();

        });
    }


    private void followUser(ProfileData id, ImageView followIV) {
        int checkFollow = 0;

        try {

            if (userProfileFollow != null)
                for (Follower f : userProfileFollow) {
                    Log.d("followUser", "followUser: " + f.getFollowersId());
                    Log.d("followUser", "followUser: " + id.getId());

                    if (f.getFollowersId().equals(id.getId())) {
                        checkFollow++;
                        break;
                    }
                }

        } catch (Exception e) {

        }
        if (checkFollow != 0) {
            followResult = true;
            setFollow(followIV);
        } else {
            setUnFollow(followIV);
        }

    }

    private void setUnFollow(ImageView imageView) {
        imageView.setBackground(userContext.getResources().getDrawable(R.drawable.plus));
    }

    private void setFollow(ImageView imageView) {
        imageView.setBackground(userContext.getResources().getDrawable(R.drawable.ic_min));
        //follow.setBackground(getResources().getDrawable(R.drawable.ic_min));
    }

    @Override
    public int getItemCount() {
        if (userHistories == null)
            return 0;
        return userHistories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name, level;
        Button diamond;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.historyViewModelIV);
            name = itemView.findViewById(R.id.historyUserNameModel);
            diamond = itemView.findViewById(R.id.historyDiamondModelBTN);
            level = itemView.findViewById(R.id.user_level_gift_history);
        }
    }
}
