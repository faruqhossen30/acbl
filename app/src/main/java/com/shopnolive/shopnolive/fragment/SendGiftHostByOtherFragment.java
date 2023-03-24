package com.shopnolive.shopnolive.fragment;

import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.joinUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.levelUpNumber;
import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.shopnolive.shopnolive.model.GiftListData;
import com.shopnolive.shopnolive.model.ResultMsg;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SendGiftHostByOtherFragment extends Fragment implements GiftListAdapter.GiftItemClickListener {

    private int sendCoinNumber = 0;
    private int commission = 0;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ImageView gift5, gift10, gift20, gift50, gift100, gift500, gift1000, gift2000;
    Button sendGiftButton;
    Button rechargeButton;
    TextView userCoinShow;
    RecyclerView recyclerView;
    GiftListAdapter giftListAdapter;

    public SendGiftHostByOtherFragment() {
        // Required empty public constructor
    }

    public static SendGiftHostByOtherFragment newInstance(String param1, String param2) {
        SendGiftHostByOtherFragment fragment = new SendGiftHostByOtherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    LinearLayout gift05, gift010, gift020, gift050, gift0100, gift0500, gift01000, gift02000;

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

        viewModel = ViewModelProviders.of(getActivity()).get(LiveUserViewModel.class);

        viewModelProfile = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 4, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);

        try {
            if (userInfo.getPresentCoinBalance() != null)
                userCoinShow.setText(String.valueOf(userInfo.getPresentCoinBalance()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        liveDataObserver();



        rechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        /*gift5.setOnClickListener(view12 -> {

            sendCoinNumber = 5;
            gift05.setBackgroundColor(getResources().getColor(R.color.greenOp));
            gift010.setBackgroundColor(getResources().getColor(R.color.white));
            gift020.setBackgroundColor(getResources().getColor(R.color.white));
            gift050.setBackgroundColor(getResources().getColor(R.color.white));
            gift0100.setBackgroundColor(getResources().getColor(R.color.white));
            gift0500.setBackgroundColor(getResources().getColor(R.color.white));
            gift01000.setBackgroundColor(getResources().getColor(R.color.white));
            gift02000.setBackgroundColor(getResources().getColor(R.color.white));

            sendGiftButton.setTextColor(getResources().getColor(R.color.white));
            //sendCoin(5);

        });

        gift10.setOnClickListener(view14 -> {

            sendCoinNumber = 10;
            gift05.setBackgroundColor(getResources().getColor(R.color.white));
            gift010.setBackgroundColor(getResources().getColor(R.color.greenOp));
            gift020.setBackgroundColor(getResources().getColor(R.color.white));
            gift050.setBackgroundColor(getResources().getColor(R.color.white));
            gift0100.setBackgroundColor(getResources().getColor(R.color.white));
            gift0500.setBackgroundColor(getResources().getColor(R.color.white));
            gift01000.setBackgroundColor(getResources().getColor(R.color.white));
            gift02000.setBackgroundColor(getResources().getColor(R.color.white));

            sendGiftButton.setTextColor(getResources().getColor(R.color.white));
            //sendCoin(5);

        });

        gift20.setOnClickListener(view15 -> {

            sendCoinNumber = 20;
            gift05.setBackgroundColor(getResources().getColor(R.color.white));
            gift010.setBackgroundColor(getResources().getColor(R.color.white));
            gift020.setBackgroundColor(getResources().getColor(R.color.greenOp));
            gift050.setBackgroundColor(getResources().getColor(R.color.white));
            gift0100.setBackgroundColor(getResources().getColor(R.color.white));
            gift0500.setBackgroundColor(getResources().getColor(R.color.white));
            gift01000.setBackgroundColor(getResources().getColor(R.color.white));
            gift02000.setBackgroundColor(getResources().getColor(R.color.white));

            sendGiftButton.setTextColor(getResources().getColor(R.color.white));
            //sendCoin(5);

        });

        gift50.setOnClickListener(view16 -> {

            sendCoinNumber = 50;
            gift05.setBackgroundColor(getResources().getColor(R.color.white));
            gift010.setBackgroundColor(getResources().getColor(R.color.white));
            gift020.setBackgroundColor(getResources().getColor(R.color.white));

            gift050.setBackgroundColor(getResources().getColor(R.color.greenOp));
            gift0100.setBackgroundColor(getResources().getColor(R.color.white));
            gift0500.setBackgroundColor(getResources().getColor(R.color.white));
            gift01000.setBackgroundColor(getResources().getColor(R.color.white));
            gift02000.setBackgroundColor(getResources().getColor(R.color.white));

            sendGiftButton.setTextColor(getResources().getColor(R.color.white));
            //sendCoin(5);

        });

        gift100.setOnClickListener(view17 -> {

            sendCoinNumber = 100;
            gift05.setBackgroundColor(getResources().getColor(R.color.white));
            gift010.setBackgroundColor(getResources().getColor(R.color.white));
            gift020.setBackgroundColor(getResources().getColor(R.color.white));
            gift050.setBackgroundColor(getResources().getColor(R.color.white));

            gift0100.setBackgroundColor(getResources().getColor(R.color.greenOp));
            gift0500.setBackgroundColor(getResources().getColor(R.color.white));
            gift01000.setBackgroundColor(getResources().getColor(R.color.white));
            gift02000.setBackgroundColor(getResources().getColor(R.color.white));

            sendGiftButton.setTextColor(getResources().getColor(R.color.white));
            //sendCoin(5);

        });

        gift500.setOnClickListener(view18 -> {

            sendCoinNumber = 500;
            gift05.setBackgroundColor(getResources().getColor(R.color.white));
            gift010.setBackgroundColor(getResources().getColor(R.color.white));
            gift020.setBackgroundColor(getResources().getColor(R.color.white));
            gift050.setBackgroundColor(getResources().getColor(R.color.white));
            gift0100.setBackgroundColor(getResources().getColor(R.color.white));

            gift0500.setBackgroundColor(getResources().getColor(R.color.greenOp));
            gift01000.setBackgroundColor(getResources().getColor(R.color.white));
            gift02000.setBackgroundColor(getResources().getColor(R.color.white));


            sendGiftButton.setTextColor(getResources().getColor(R.color.white));
            //sendCoin(5);

        });

        gift1000.setOnClickListener(view19 -> {

            sendCoinNumber = 1000;
            gift05.setBackgroundColor(getResources().getColor(R.color.white));
            gift010.setBackgroundColor(getResources().getColor(R.color.white));
            gift020.setBackgroundColor(getResources().getColor(R.color.white));
            gift050.setBackgroundColor(getResources().getColor(R.color.white));
            gift0100.setBackgroundColor(getResources().getColor(R.color.white));
            gift0500.setBackgroundColor(getResources().getColor(R.color.white));

            gift01000.setBackgroundColor(getResources().getColor(R.color.greenOp));
            gift02000.setBackgroundColor(getResources().getColor(R.color.white));

            sendGiftButton.setTextColor(getResources().getColor(R.color.white));
            //sendCoin(5);

        });

        gift2000.setOnClickListener(view13 -> {

            sendCoinNumber = 2000;

            gift05.setBackgroundColor(getResources().getColor(R.color.white));
            gift010.setBackgroundColor(getResources().getColor(R.color.white));
            gift020.setBackgroundColor(getResources().getColor(R.color.white));
            gift050.setBackgroundColor(getResources().getColor(R.color.white));
            gift0100.setBackgroundColor(getResources().getColor(R.color.white));
            gift0500.setBackgroundColor(getResources().getColor(R.color.white));
            gift01000.setBackgroundColor(getResources().getColor(R.color.white));

            gift02000.setBackgroundColor(getResources().getColor(R.color.greenOp));
            sendGiftButton.setTextColor(getResources().getColor(R.color.white));

            //  sendCoin(sendCoinNumber);

        });*/

        sendGiftButton.setOnClickListener(v -> {
            if (sendCoinNumber != 0) {
                sendCoin(sendCoinNumber);
            } else {
                Toast.makeText(requireContext(), "Select a gift to send", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showCoinSend() {

        String unicKey = myRef.push().getKey();
        //Comment comment = new Comment(userInfo.getId(), joinUserInfo.getName(), (String) joinUserInfo.getImage(),/*userInfo.getName()+*/"<i> :- send " + String.valueOf(sendCoinNumber) + "\uD83D\uDC8E" + " gift to " + joinUserInfo.getName() + "</i>");

        //myRef.child(userInfo.getId()).child("commend").child(unicKey).setValue(comment);
        // getComment();

    }

    private void rechargeAmount() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View alertView = layoutInflater.inflate(R.layout.diamond_recharge_layout, null);

        Button rechargeButton1 = alertView.findViewById(R.id.recharge_button1);
        TextView diamondBuyDialog = alertView.findViewById(R.id.userDiamondShowBuyDialog);

        diamondBuyDialog.setText(String.valueOf(userInfo.getPresentCoinBalance()));


        rechargeButton1.setOnClickListener(v -> {

            PaymentBottomSheetFragment paymentBottomSheet = new PaymentBottomSheetFragment();
            paymentBottomSheet.show(getFragmentManager(), "Payment Bottom Sheet");

        });

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(alertView).create();
        alertDialog.show();

    }


    void sendCoin(int coin) {

        int mainCoin = Integer.valueOf(userInfo.getPresentCoinBalance());
        int levelCoin = Integer.valueOf(joinUserInfo.getMainCoinBalance()) + coin;


        int level = 0;

        if (mainCoin > coin) {

            showCoinSend();

            //for(int leve levelUpNumber)
            for (int i = 0; i < levelUpNumber.length; i++) {
                if (levelUpNumber.length - 1 > i)
                    if (levelUpNumber[i + 1] > levelCoin && levelCoin > levelUpNumber[i]) {
                        level = i + 1;

                        Call<String> levels = api.levelUpdate(joinUserInfo.getId(), level);

                        levels.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.body() != null) {
                                    Toast.makeText(getActivity(), "Level up", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                        break;
                    }
            }


            Call<String> share = api.shareCoin(userInfo.getId(), joinUserInfo.getId(), coin);
            Call<ResultMsg> history = api.historyAdded(userInfo.getId(), joinUserInfo.getId(), coin);


            share.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.body() != null) {


                        userInfo.setPresentCoinBalance(String.valueOf(mainCoin - coin));


                        int diamond = Integer.parseInt(joinUserInfo.getPresentCoinBalance()) + coin;


                        joinUserInfo.setPresentCoinBalance(String.valueOf(diamond));

                        setDiamond(Integer.parseInt(joinUserInfo.getPresentCoinBalance()), coin);

                        userCoinShow.setText(userInfo.getPresentCoinBalance());

                        history.enqueue(new Callback<ResultMsg>() {
                            @Override
                            public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                                if (response.body() != null) {

                                }
                            }

                            @Override
                            public void onFailure(Call<ResultMsg> call, Throwable t) {

                            }
                        });


                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });


        } else {
            Toast.makeText(getActivity(), "You have no available Diamond", Toast.LENGTH_SHORT).show();
        }
    }

    private void liveDataObserver() {
        viewModel.getAllUsers().observe(getViewLifecycleOwner(), allUserResponses -> {

           /* if (allUserResponses != null) {
                List<com.bdlive.devboyz.model.profile.Response> userDetails = allUserResponses.getResponse();

                allUserInfo.clear();
                allUserInfo = userDetails;
            }*/

        });

        viewModel.getAllGiftList().observe(getViewLifecycleOwner(), giftListItems -> {
            giftListAdapter = new GiftListAdapter(giftListItems, this);
            recyclerView.setAdapter(giftListAdapter);
        });

    }


    public void setDiamond(int s, int coin) {
        Activity activity = getActivity();
        if (activity instanceof LiveVideoBroadcasterActivity) {
            ((LiveVideoBroadcasterActivity) activity).setDiamond(s, coin);
        }
    }

    @Override
    public void onClick(GiftListData giftItem, ConstraintLayout giftBgLayout) {
        giftBgLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        sendCoinNumber = giftItem.getDiamond();
        commission = giftItem.getComission();
    }
}