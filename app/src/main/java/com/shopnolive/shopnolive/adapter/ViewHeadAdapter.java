// this is the adapter for the round chat heads type elements that shows live users
//this layout was implemented using recycle view that scrolls horizontally

package com.shopnolive.shopnolive.adapter;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.playId;
import static com.shopnolive.shopnolive.utils.Variable.pref;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.userProfileFollow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel;
import com.shopnolive.shopnolive.listener.UserItemClickListener;
import com.shopnolive.shopnolive.model.DeleteResponse;
import com.shopnolive.shopnolive.model.ReportModel;
import com.shopnolive.shopnolive.model.ResultMsg;
import com.shopnolive.shopnolive.model.profile.Follower;
import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.shopnolive.shopnolive.ppal.util.PrefManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewHeadAdapter extends RecyclerView.Adapter<ViewHeadAdapter.ViewHolder> {

    List<ProfileData> chatHeadModels;
    private final Context userContext;
    private final ProfileViewModel profileViewModel;
    boolean followResult = false;
    private final PrefManager prefManager;
    private final UserItemClickListener userItemClickListener;


    public ViewHeadAdapter(Context context, List<ProfileData> userLives, UserItemClickListener userItemClickListener) {
        this.userContext = context;
        this.chatHeadModels = userLives;
        this.userItemClickListener = userItemClickListener;
        this.profileViewModel = ViewModelProviders.of((FragmentActivity) userContext).get(ProfileViewModel.class);
        prefManager = new PrefManager(userContext);
    }

    @NonNull
    @Override
    public ViewHeadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_heads, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHeadAdapter.ViewHolder holder, int position) {

        ProfileData viewUser = chatHeadModels.get(holder.getAdapterPosition());

        try {
            if (viewUser.getImage() != null)
                Picasso.get().load(BASE_URL + viewUser.getImage()).placeholder(R.drawable.user1).into(holder.image);
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.itemView.setOnClickListener(v -> {
            //ProfileData info = chatHeadModels.get(holder.getAdapterPosition());
            //showDialog(info);
            userItemClickListener.onUserItemClicked(viewUser.id);
            Log.d("onBindViewHolder", "onBindViewHolder: " + viewUser.id);
        });

    }

    private void showDialog(ProfileData info) {


        AlertDialog.Builder a_dialog = new AlertDialog.Builder(userContext, R.style.MaterialDialogSheet);
        View details = LayoutInflater.from(userContext).inflate(R.layout.dialog_user_history, null);

        TextView userName = details.findViewById(R.id.userNameDetailsForHistory);
        TextView userLevel = details.findViewById(R.id.userLevelInDialog);
        TextView userId = details.findViewById(R.id.userIdForHistory);
        TextView userDiamond = details.findViewById(R.id.userDiamondForHistory);
        CircleImageView imageView = details.findViewById(R.id.detailsProfileImage);
        ImageView followIV = details.findViewById(R.id.userFollowForHistory);
        Button block = details.findViewById(R.id.btn_block_dialog);
        Button report = details.findViewById(R.id.btn_report_dialog);


        a_dialog.setView(details);

        Dialog dialog = a_dialog.create();
        dialog.show();

        // User Details
        profileViewModel.getProfileByMessId(info.getId()).observe((LifecycleOwner) userContext, new Observer<ProfileData>() {
            @Override
            public void onChanged(ProfileData profileData) {

                if (prefManager.get_val("block_" + profileData.getId()).equals("block")) {
                    block.setText("Blocked");
                    block.setEnabled(false);

                } else {
                    block.setText("Block");
                    block.setEnabled(true);
                }

                followUser(profileData, followIV);

                try {
                    userName.setText(profileData.getName());
                } catch (Exception e) {
                }


                try {
                    userLevel.setText(String.valueOf(profileData.getUserLevel()));
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
                    e.printStackTrace();

                }
                //  Toast.makeText(userContext, "Check view", Toast.LENGTH_SHORT).show();

                followIV.setOnClickListener(view -> followUserResponse(profileData, followIV));

                block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myRef.child(playId).child("view").child(profileData.getId()).child("status").setValue("blocked");
                        blockUser(Integer.parseInt(profileData.getId()), block);
                    }
                });
                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        reportUser(Integer.parseInt(profileData.getId()), pref.getString("myId", ""));
                    }
                });

            }
        });
    }

    private void reportUser(int userId, String myId) {
        Call<ReportModel> reportUser = api.reportUser(String.valueOf(userId), myId);
        reportUser.enqueue(new Callback<ReportModel>() {
            @Override
            public void onResponse(Call<ReportModel> call, Response<ReportModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(userContext, "report done!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(userContext, "response not success", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ReportModel> call, Throwable t) {
                Toast.makeText(userContext, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void blockUser(int userid, Button block) {
        Call<DeleteResponse> blockUser = api.blockSpecificUser(userid, 24);
        blockUser.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                DeleteResponse deleteResponse = response.body();
                if (deleteResponse != null) {
                    if (deleteResponse.isSuccess()) {
                        Toast.makeText(userContext, "User successfully blocked", Toast.LENGTH_SHORT).show();
                        block.setEnabled(false);
                        block.setText("Blocked");
                        prefManager.set_val("block_" + userid, "block");
                    } else {
                        Toast.makeText(userContext, "Failed to block", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(userContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(userContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Todo : Complete the follow work

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
        if (chatHeadModels == null)
            return 0;
        return chatHeadModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        //  TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.view_user_image);
            //   name = itemView.findViewById(R.id.user_name);
        }
    }

}
