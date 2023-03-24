package com.shopnolive.shopnolive.fragment;

import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.allUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.playId;
import static com.shopnolive.shopnolive.utils.Variable.selectedUserId;
import static com.shopnolive.shopnolive.utils.Variable.selectedUserName;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.activity.main.LiveUserViewModel;
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel;
import com.shopnolive.shopnolive.adapter.GiftListAdapter;
import com.shopnolive.shopnolive.liveVideoPlayer.LiveVideoBroadcasterActivity;
import com.shopnolive.shopnolive.liveVideoPlayer.LiveVideoPlayerActivity;
import com.shopnolive.shopnolive.model.Comment;
import com.shopnolive.shopnolive.model.GiftHistory;
import com.shopnolive.shopnolive.model.GiftListData;
import com.shopnolive.shopnolive.model.GiftResponse;
import com.shopnolive.shopnolive.model.profile.ProfileData;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendGiftFragment extends Fragment implements GiftListAdapter.GiftItemClickListener {

    private int sendCoinNumber = 0;
    private int commission = 0;

    private String giftImage = "none";

    Button sendGiftButton;
    Button rechargeButton;
    TextView userCoinShow;
    TextView diamondBuyDialog;

    RecyclerView recyclerView;
    GiftListAdapter giftListAdapter;

    ProfileData receiverProfile;

    private int userCoin = 0;

    public SendGiftFragment() {
        // Required empty public constructor
    }

    LiveUserViewModel viewModel;
    ProfileViewModel viewModelProfile;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_send_gift, container, false);
        sendGiftButton = view.findViewById(R.id.send_gift_button_live_user);
        rechargeButton = view.findViewById(R.id.rechargeCoin);
        userCoinShow = view.findViewById(R.id.userDiamondShowLiveActivity);
        recyclerView = view.findViewById(R.id.recyclerview_gift_list);

        viewModel = ViewModelProviders.of(requireActivity()).get(LiveUserViewModel.class);

        viewModelProfile = ViewModelProviders.of(requireActivity()).get(ProfileViewModel.class);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 4, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);

        liveDataObserver();



        rechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        sendGiftButton.setOnClickListener(v -> {

            if (sendCoinNumber != 0) {
                sendGiftButton.setEnabled(false);
                sendCoin(sendCoinNumber, commission);
            } else {
                Toast.makeText(requireContext(), "Select a gift to send", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showCoinSend(Comment comment, GiftHistory giftHistory, String giftId) {

        myRef.child(playId).child("comment").child(giftId).setValue(comment);
        //getComment();

        myRef.child(playId).child("histories").child(giftId).setValue(giftHistory);


    }

    private void rechargeAmount() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View alertView = layoutInflater.inflate(R.layout.diamond_recharge_layout, null);

        TextView rechargeButton1 = alertView.findViewById(R.id.recharge_button1);
        diamondBuyDialog = alertView.findViewById(R.id.userDiamondShowBuyDialog);

        diamondBuyDialog.setText(String.valueOf(userInfo.getPresentCoinBalance()));


        rechargeButton1.setOnClickListener(v -> {
            PaymentBottomSheetFragment paymentBottomSheet = new PaymentBottomSheetFragment();
            paymentBottomSheet.show(getParentFragmentManager(), "Payment Bottom Sheet");
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(alertView).create();
        alertDialog.show();

    }


    private void sendCoin(int coin, int commission) {

        if (userCoin > coin) {
            String giftId = myRef.push().getKey();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), "<i> :- send " + sendCoinNumber + "\uD83D\uDC8E" + " gift to " + selectedUserName + "</i>", String.valueOf(userInfo.getUserLevel()), "gift");
            GiftHistory giftHistory = new GiftHistory(userInfo.getName(), (String) userInfo.getImage(), String.valueOf(userInfo.getUserLevel()), String.valueOf(sendCoinNumber), giftImage, giftId);

            Call<GiftResponse> share = api.giftCoin(Integer.parseInt(userInfo.getId()), Integer.parseInt(selectedUserId), coin, commission);

            share.enqueue(new Callback<GiftResponse>() {
                @Override
                public void onResponse(Call<GiftResponse> call, Response<GiftResponse> response) {
                    assert response.body() != null;

                    sendGiftButton.setEnabled(true);

                    if (response.body().isSuccess()) {
                        userCoin -= coin;
                        userCoinShow.setText(String.valueOf(userCoin));

                        int diamond = Integer.parseInt(receiverProfile.getPresentCoinBalance()) + (coin - commission);

                        receiverProfile.setPresentCoinBalance(String.valueOf(diamond));
                        setDiamond(Integer.parseInt(receiverProfile.getPresentCoinBalance()), (coin - commission));

                        showCoinSend(comment, giftHistory, giftId);

                        Toast.makeText(requireContext(), "Gift send Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to send gift", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GiftResponse> call, Throwable t) {
                    sendGiftButton.setEnabled(true);
                }
            });

        } else {
            sendGiftButton.setEnabled(true);
            Toast.makeText(getActivity(), "You have no available Diamond", Toast.LENGTH_SHORT).show();
        }
    }

    private void liveDataObserver() {
        viewModel.getAllUsers().observe(getViewLifecycleOwner(), allUserResponses -> {

            if (allUserResponses != null) {
                List<ProfileData> userDetails = allUserResponses.getResponse();
//                allUserInfo.clear();
                allUserInfo = userDetails;
            }

        });


        viewModelProfile.getProfileById(selectedUserId).observe(getViewLifecycleOwner(), profileData -> {
            if (profileData != null) {
                receiverProfile = profileData;
            } else {
                Log.i("ysawijaaw", "liveDataObserver: profile data null");
            }
        });

        viewModel.getAllGiftList().observe(getViewLifecycleOwner(), giftListItems -> {
            Collections.sort(giftListItems, (t0, t1) -> t0.getDiamond() - t1.getDiamond());
            giftListAdapter = new GiftListAdapter(giftListItems, this);
            recyclerView.setAdapter(giftListAdapter);
        });
    }


    public void setDiamond(int s, int coin) {
        Activity activity = getActivity();
        if (activity instanceof LiveVideoPlayerActivity) {
            ((LiveVideoPlayerActivity) activity).setDiamond(s, coin);
        } else if (activity instanceof LiveVideoBroadcasterActivity) {
            //((LiveVideoBroadcasterActivity) activity).hideGift();
        }
    }


    @Override
    public void onClick(GiftListData giftItem, ConstraintLayout giftBgLayout) {
        sendCoinNumber = giftItem.getDiamond();
        commission = giftItem.getComission();
        giftImage = giftItem.getImageUrl();
    }

    @Override
    public void onResume() {
        super.onResume();

        sendCoinNumber = 0;

        try {
            if (userInfo.getPresentCoinBalance() != null) {
                userCoinShow.setText(String.valueOf(userInfo.getPresentCoinBalance()));
                userCoin = Integer.parseInt(userInfo.getPresentCoinBalance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        sendCoinNumber = 0;
    }
}