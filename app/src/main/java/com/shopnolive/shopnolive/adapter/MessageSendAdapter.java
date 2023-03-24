package com.shopnolive.shopnolive.adapter;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.playId;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.userProfileFollow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel;
import com.shopnolive.shopnolive.listener.UserItemClickListener;
import com.shopnolive.shopnolive.model.Comment;
import com.shopnolive.shopnolive.model.DeleteResponse;
import com.shopnolive.shopnolive.model.ResultMsg;
import com.shopnolive.shopnolive.model.profile.Follower;
import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageSendAdapter extends RecyclerView.Adapter<MessageSendAdapter.ViewHolder> {

    private List<Comment> comments;
    private Activity userContext;
    private ProfileViewModel profileViewModel;
    boolean followResult = false;
    int typeCode = 0;
    private UserItemClickListener userItemClickListener;


    public MessageSendAdapter(Activity context, int typeCode, UserItemClickListener userItemClickListener) {
        this.userContext = context;
        this.typeCode = typeCode;
        this.userItemClickListener = userItemClickListener;
        this.profileViewModel = ViewModelProviders.of((FragmentActivity) userContext).get(ProfileViewModel.class);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addAllComments(List<Comment> models) {
        this.comments = models;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageSendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageSendAdapter.ViewHolder holder, int position) {

        Comment comment = comments.get(position);
        // holder.image.setImageResource(chatHeadModels.get(position).get());

        /*if (!comment.giftImage.equals("none")) {
            Picasso.get().load(comment.giftImage).into(holder.giftImage);
            holder.giftImage.setVisibility(View.VISIBLE);
        } else {
            holder.giftImage.setVisibility(View.GONE);
        }*/

        try {
            holder.tvName.setText(Html.fromHtml(comment.getName()));
            if (!comment.getLevel().equals("null")) {
                holder.tvLevel.setText(comment.getLevel());
            } else {
                holder.tvLevel.setText("0");
            }
            if (comment.getType().equals("reaction")) {
                holder.rectImage.setVisibility(View.VISIBLE);
                holder.tvComment.setVisibility(View.GONE);
                switch (comment.getComment()) {
                    case "like":
                        holder.rectImage.setImageResource(R.drawable.ic_like);
                        break;
                    case "love":
                        holder.rectImage.setImageResource(R.drawable.ic_heart);
                        break;
                    case "haha":
                        holder.rectImage.setImageResource(R.drawable.ic_happy);
                        break;
                    case "wow":
                        holder.rectImage.setImageResource(R.drawable.ic_surprise);
                        break;
                    case "sad":
                        holder.rectImage.setImageResource(R.drawable.ic_sad);
                        break;
                    case "angry":
                        holder.rectImage.setImageResource(R.drawable.ic_angry);
                        break;
                }
            } else {
                holder.rectImage.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(comment.getComment())) {
                    holder.tvComment.setVisibility(View.VISIBLE);
                    holder.tvComment.setText(Html.fromHtml(comment.getComment()));
                } else {
                    holder.tvComment.setVisibility(View.GONE);
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        holder.itemView.setOnClickListener(view -> {
            userItemClickListener.onUserItemClicked(comment.id);
            /*showDialog(userContext,comment)*/
        });
    }

    private void showDialog(Context userContext, Comment comment) {
        AlertDialog.Builder a_dialog = new AlertDialog.Builder(userContext, R.style.MaterialDialogSheet);
        View details = LayoutInflater.from(userContext).inflate(R.layout.dialog_user_history, null);

        TextView userName = details.findViewById(R.id.userNameDetailsForHistory);
        TextView userLevel = details.findViewById(R.id.userLevelInDialog);
        TextView userId = (TextView) details.findViewById(R.id.userIdForHistory);
        TextView userDiamond = (TextView) details.findViewById(R.id.userDiamondForHistory);
        Button btnBlock = details.findViewById(R.id.btn_block_dialog);
        CircleImageView imageView = details.findViewById(R.id.detailsProfileImage);
        ImageView followIV = details.findViewById(R.id.userFollowForHistory);
        Button report = details.findViewById(R.id.btn_report_dialog);

        a_dialog.setView(details);

        Dialog dialog = a_dialog.create();
        dialog.show();

        // User Details
        profileViewModel.getProfileByMessId(comment.getId()).observe((LifecycleOwner) userContext, profileData -> {

            followUser(profileData, followIV);

            if (!profileData.getId().equals(userInfo.getId()) && typeCode == 1) {
                btnBlock.setVisibility(View.VISIBLE);
            } else {
                btnBlock.setVisibility(View.GONE);
            }

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
                //userLevel.setText(info.getUserLevel());
            } catch (Exception e) {
            }

            try {
                userDiamond.setText("\uD83D\uDC8E " + profileData.getPresentCoinBalance());
            } catch (Exception e) {
            }


            if (profileData.getImage() != null) {
                Picasso.get().load(BASE_URL + profileData.getImage()).placeholder(R.drawable.user1).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.user1);
            }

            followIV.setOnClickListener(view1 -> followUserResponse(profileData, followIV));

            btnBlock.setOnClickListener(v -> {
                myRef.child(playId).child("view").child(profileData.getId()).child("status").setValue("blocked");
                blockUser(Integer.parseInt(profileData.getId()), btnBlock);
                //Toast.makeText(userContext, "ok block", Toast.LENGTH_SHORT).show();
            });

            //report user
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(userContext, "report", Toast.LENGTH_SHORT).show();

                }
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

    private void blockUser(int userid, Button btnBlock) {
        Call<DeleteResponse> blockUser = api.blockSpecificUser(userid, 24);
        blockUser.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                DeleteResponse deleteResponse = response.body();
                if (deleteResponse != null) {
                    if (deleteResponse.isSuccess()) {
                        Toast.makeText(userContext, "User successfully blocked", Toast.LENGTH_SHORT).show();
                        btnBlock.setEnabled(false);
                        btnBlock.setText("Blocked");
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

    @Override
    public int getItemCount() {
        if (comments == null)
            return 0;
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView rectImage;
        TextView tvName, tvComment, tvLevel;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.liveUserShowCommentName);
            tvComment = itemView.findViewById(R.id.adminOrUserMessageShow);
            tvLevel = itemView.findViewById(R.id.user_leve_comment_item);
            rectImage = itemView.findViewById(R.id.iv_comment_react);
        }
    }
}
