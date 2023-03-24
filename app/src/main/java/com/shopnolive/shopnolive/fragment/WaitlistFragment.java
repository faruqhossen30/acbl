package com.shopnolive.shopnolive.fragment;

import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.playId;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.waitingInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.shopnolive.shopnolive.adapter.LiveRequestAdapter;
import com.shopnolive.shopnolive.liveVideoPlayer.LiveVideoPlayerActivity;
import com.shopnolive.shopnolive.model.LiveRequest;
import com.shopnolive.shopnolive.ui.live.LiveBroadcastActivity;
import com.shopnolive.shopnolive.ui.live.PlayBroadcastActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WaitlistFragment extends Fragment {

    private RecyclerView liveViewShow;

    public WaitlistFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_waitlist, container, false);

        liveViewShow = view.findViewById(R.id.liveWaitingShowRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false
        );
        liveViewShow.setLayoutManager(linearLayoutManager);
//        LiveViewUserAdapter LVA = new LiveViewUserAdapter(getActivity(), new ArrayList<>());
        LiveRequestAdapter LVA = new LiveRequestAdapter(getActivity(), new ArrayList<>());

        //  liveViewShow.smoothScrollToPosition(list.size());
        liveViewShow.setAdapter(LVA);

        liveViewShow.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            v.onTouchEvent(event);
            return true;
        });

        getJoinUser();

        return view;
    }


    public void getJoinUser() {

        String requestId;
        if (waitingInfo.equals("other")) {
            requestId = playId;
        } else {
            requestId = userInfo.getId();
        }


        myRef.child(/*userInfo.getId()*/requestId).child("join").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                List<LiveRequest> list = new ArrayList<>();
                List<LiveRequest> playList = new ArrayList<>();
                LiveRequest value = null;

                if (dataSnapshot != null)
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        try {
                            value = d.getValue(LiveRequest.class);
                        } catch (Exception ignored) {
                        }
                        assert value != null;
                        if (value.getType() != null)

                            try {
                                if (!value.getType().equals("online")) {
                                    if (value.getType().equals("waiting")) {
                                        list.add(value);
                                    } else if (value.getType().equals("out") && value.getId().equals(userInfo.getId())) {
                                        kickOutUser();
                                    }
                                } else {

                                    showViews();
                                    playList.add(value);

                                    //  value.getId();
                                    if (value.getId().equals(userInfo.getId())) {
                                        Log.d("StartLive", "startLive Play : online method 2  ok");
                                        acceptLive();
                                    }

                                }
                            } catch (Exception ignored) {
                            }
                    }

                if (playList.size() > 0) {
                    updatePlaylist(playList);
                } else {
                    singlePlay();
                }

                LiveRequestAdapter LVA = new LiveRequestAdapter(getActivity(), list);
                //  liveViewShow.smoothScrollToPosition(list.size());
                liveViewShow.setAdapter(LVA);

                totalWaitingCall(list.size());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException());
            }
        });

    }

    private void kickOutUser() {
        Activity activity = getActivity();
        if (activity instanceof PlayBroadcastActivity) {
            ((PlayBroadcastActivity) activity).kickOut();
        }
    }

    private void totalWaitingCall(int size) {
        Activity activity = getActivity();
        if (activity instanceof LiveBroadcastActivity) {
            ((LiveBroadcastActivity) activity).totalCalls(size);
        } else if (activity instanceof PlayBroadcastActivity) {
            ((PlayBroadcastActivity) activity).totalCalls(size);
        }
    }

    private void singlePlay() {
        Activity activity = getActivity();
        if (activity instanceof LiveVideoPlayerActivity) {
            ((LiveVideoPlayerActivity) activity).hideLayouts();
        }
    }

    private void updatePlaylist(List<LiveRequest> playList) {
        Activity activity = getActivity();

        if (activity instanceof LiveVideoPlayerActivity) {

            List<String> list;

//            if (!list.contains(requestId)) {
//                int i = 0;
//                while (i < ((LiveVideoPlayerActivity) activity).livePeoples.length) {
//                    if (((LiveVideoPlayerActivity) activity).livePeoples[i] == null) {
//                        ((LiveVideoPlayerActivity) activity).livePeoples[i] = requestId;
//                        break;
//                    }
//                    i++;
//                }
//            }

            for (LiveRequest user : playList) {

                list = Arrays.asList(((LiveVideoPlayerActivity) activity).livePeoples);

                if (!list.contains(user.getId()) && !user.getId().equals(userInfo.getId())) {
                    int j = 0;
                    while (j < ((LiveVideoPlayerActivity) activity).livePeoples.length) {
                        if (((LiveVideoPlayerActivity) activity).livePeoples[j] == null) {
                            ((LiveVideoPlayerActivity) activity).livePeoples[j] = user.getId();
                            ((LiveVideoPlayerActivity) activity).updateDetails(user, j);
                            break;
                        }
                        j++;
                    }
                }
            }

            ((LiveVideoPlayerActivity) activity).liveChecker(playList);
        }
    }

    private void showViews() {

        Activity activity = getActivity();

        if (activity instanceof LiveVideoPlayerActivity) {

            if (((LiveVideoPlayerActivity) activity).showLayouts != 3) {
                Log.i("ysawijaaw", "showViews");
                ((LiveVideoPlayerActivity) activity).showLayouts = 1;
            }


        }
    }


    private void acceptLive() {

        Activity activity = getActivity();

        Log.d("StartLive", "startLive Play : method 2");

        if (activity instanceof PlayBroadcastActivity) {
            Log.d("StartLive", "startLive Play : method work 2");
            ((PlayBroadcastActivity) activity).startLive();
        }
    }

}
