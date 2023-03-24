package com.shopnolive.shopnolive.fragment;

import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;

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
import com.shopnolive.shopnolive.adapter.LiveViewUserAdapter;
import com.shopnolive.shopnolive.liveVideoPlayer.LiveVideoBroadcasterActivity;
import com.shopnolive.shopnolive.model.LiveRequest;

import java.util.ArrayList;
import java.util.List;

public class LiveFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView liveShow;
//    private int lastLive = 0;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LiveFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LiveFragment newInstance(String param1, String param2) {
        LiveFragment fragment = new LiveFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_views, container, false);

        liveShow = view.findViewById(R.id.liveShowRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false
        );
        liveShow.setLayoutManager(linearLayoutManager);
        LiveViewUserAdapter LVA = new LiveViewUserAdapter(getActivity(), new ArrayList<>());
        liveShow.setAdapter(LVA);
        getJoinUser();
        return view;

    }


    public void getJoinUser() {
        myRef.child(userInfo.getId()).child("join").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<LiveRequest> list = new ArrayList<>();

//                if (!dataSnapshot.exists()) {
//                    setLivePlay(null);
//                }

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    //    List<Comment> list = new ArrayList<>();
                    try {

                        LiveRequest value = d.getValue(LiveRequest.class);

                        try {


                            if (value.getType().equals("online")) {
                                list.add(value);
                            }
                        } catch (NullPointerException e) {

                        }
                    } catch (NullPointerException e) {

                    }
                }

                Log.i("ysawijaaw", "onDataChange: size "+ list.size());

                if (list.size() > 0) {
                    setLivePlay(list);
                }else{
                    setLivePlay(null);
                    Log.i("ysawijaaw", "onDataChange: nulled");
                }

                LiveRequestAdapter LVA = new LiveRequestAdapter(getActivity(), list);
                liveShow.setAdapter(LVA);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }


    private void setLivePlay(List<LiveRequest> id) {

        Activity activity = getActivity();
        if (activity instanceof LiveVideoBroadcasterActivity) {
            ((LiveVideoBroadcasterActivity) activity).setLivePlay(id);
        }


    }

}
