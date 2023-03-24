//this class is for profile fragment

package com.shopnolive.shopnolive.fragment;


import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.playId;
import static com.shopnolive.shopnolive.utils.Variable.selectedUserId;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.adapter.HistoryViewAdapter;
import com.shopnolive.shopnolive.model.DeleteResponse;
import com.shopnolive.shopnolive.model.LiveRequest;
import com.shopnolive.shopnolive.model.gift.GiftHistoryItem;
import com.shopnolive.shopnolive.model.gift.GiftHistoryResponse;
import com.shopnolive.shopnolive.ui.live.LiveBroadcastActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GiftHistoryFragment extends Fragment {


    private RecyclerView recyclerView;

    public GiftHistoryFragment() {
        // Required empty public constructor

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gift_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_gift_history);
        Button disconnectBtn = view.findViewById(R.id.btn_disconnect_profile);
        Button blockBtn = view.findViewById(R.id.btn_block_profile);

        if (getActivity() instanceof LiveBroadcastActivity) {
            disconnectBtn.setVisibility(View.VISIBLE);
            blockBtn.setVisibility(View.VISIBLE);
        }


        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false));

        recyclerView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            v.onTouchEvent(event);
            return true;
        });

        disconnectBtn.setOnClickListener(v -> {
            kickOut();
        });

        blockBtn.setOnClickListener(v -> {
            blockUser();
            blockUser(Integer.parseInt(selectedUserId));
        });

        updateHistory();

        return view;

    }

    private void blockUser(int userid) {
        Call<DeleteResponse> blockUser = api.blockSpecificUser(userid, 24);
        blockUser.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                DeleteResponse deleteResponse = response.body();
                if (deleteResponse != null) {
                    if (deleteResponse.isSuccess()) {
                        Toast.makeText(getContext(), "User successfully blocked", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to block", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void kickOut() {
        myRef.child(playId).child("join").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    LiveRequest value = data.getValue(LiveRequest.class);

                    if (value.getId().equals(selectedUserId)) {
                        value.setType("out");
                        myRef.child(userInfo.getId()).child("join").child(value.getUn_id()).setValue(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void blockUser() {
        myRef.child(playId).child("view").child(selectedUserId).child("status").setValue("blocked");
    }

    public void updateHistory() {
        Call<GiftHistoryResponse> calls = api.getGiftHistory(selectedUserId);


        calls.enqueue(new Callback<GiftHistoryResponse>() {
            @Override
            public void onResponse(Call<GiftHistoryResponse> call, Response<GiftHistoryResponse> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    List<GiftHistoryItem> userDetails = response.body().getData();

                    HistoryViewAdapter userStoryAdapter = new HistoryViewAdapter(requireContext(), userDetails);
                    recyclerView.setAdapter(userStoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<GiftHistoryResponse> call, Throwable t) {

            }
        });
    }
}
