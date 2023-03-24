package com.shopnolive.shopnolive.fragment;

import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.playId;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.viewUserList;
import static com.shopnolive.shopnolive.utils.Variable.waitingInfo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.adapter.LiveViewUserAdapter;
import com.shopnolive.shopnolive.model.profile.ProfileData;

import java.util.ArrayList;
import java.util.List;

public class StreamersViewFragment extends Fragment {

    private RecyclerView liveViewShow;


    public StreamersViewFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_streamers, container, false);
        liveViewShow = view.findViewById(R.id.liveViewUserShowRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false
        );

        liveViewShow.setLayoutManager(linearLayoutManager);
        LiveViewUserAdapter LVA = new LiveViewUserAdapter(getActivity(), new ArrayList<>());
        //  liveViewShow.smoothScrollToPosition(list.size());
        liveViewShow.setAdapter(LVA);

        liveViewShow.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            v.onTouchEvent(event);
            return true;
        });

        getViewUser();
        return view;
    }


    public void getViewUser() {


        // Read from the database

        String requestId = null;
        if (waitingInfo.equals("other")) {
            requestId = playId;
        } else {
            requestId = userInfo.getId();
        }


        myRef.child(requestId).child("view").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                List<ProfileData> list = new ArrayList<>();

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    //    List<Comment> list = new ArrayList<>();
                    ProfileData value = d.getValue(ProfileData.class);
                    //   Log.d("firebase", "Value is: " + value.getId());
                    list.add(value);

                }
                viewUserList = list;


                LiveViewUserAdapter LVA = new LiveViewUserAdapter(getActivity(), list);
                //  liveViewShow.smoothScrollToPosition(list.size());
                liveViewShow.setAdapter(LVA);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException());
            }
        });

    }

}
