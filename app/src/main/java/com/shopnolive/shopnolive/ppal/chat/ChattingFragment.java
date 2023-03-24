package com.shopnolive.shopnolive.ppal.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.ppal.callbacks.MessageListClick;
import com.shopnolive.shopnolive.ppal.adapter.StoryAdapter;
import com.shopnolive.shopnolive.model.MessageListModel;
import com.shopnolive.shopnolive.ppal.model.FollowListModel;
import com.shopnolive.shopnolive.ppal.retrofit.ApiClient;
import com.shopnolive.shopnolive.ppal.retrofit.ApiInterface;
import com.shopnolive.shopnolive.ppal.util.Common;
import com.shopnolive.shopnolive.ppal.util.MyHelper;
import com.shopnolive.shopnolive.ppal.util.PrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingFragment extends Fragment implements MessageListClick {


    public ChattingFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerViewChatList,recyclerViewStory;
    private StoryAdapter storyAdapter;
    private PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        prefManager=new PrefManager(requireContext());
        //find xml
        recyclerViewChatList=view.findViewById(R.id.recycler_view_chat_list);
        recyclerViewStory=view.findViewById(R.id.recy_story);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get follow list
        getFollowList();


        //adapter msg
        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        recyclerViewChatList.setHasFixedSize(true);
        storyAdapter = new StoryAdapter(requireContext(),"msg",ChattingFragment.this);
        storyAdapter.addData(MyHelper.getInstance().get_message_list());
        recyclerViewChatList.setAdapter(storyAdapter);
    }

    private void getFollowList() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<FollowListModel> call = apiInterface.get_my_follower_list(prefManager.get_val(Common.USER_ID));
        call.enqueue(new Callback<FollowListModel>() {
            @Override
            public void onResponse(Call<FollowListModel> call, Response<FollowListModel> response) {
                if (response.isSuccessful() && response.body()!=null){
                    FollowListModel model = response.body();
                    if (model.getSuccess()){
                        List<FollowListModel.MyFollower> myFollowerList = model.getData().getMyFollowerList();
                        //init story adapter
                        recyclerViewStory.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
                        recyclerViewStory.setHasFixedSize(true);
                        storyAdapter = new StoryAdapter(requireContext(),"story",ChattingFragment.this);
                        storyAdapter.addData(myFollowerList);
                        recyclerViewStory.setAdapter(storyAdapter);

                    }else {
                        Toast.makeText(requireContext(), ""+ response, Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<FollowListModel> call, Throwable t) {
                Toast.makeText(requireContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onStoryClick(FollowListModel.MyFollower storyModel) {
        Toast.makeText(requireContext(), ""+storyModel.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageListClick(MessageListModel messageListModel) {
        //Toast.makeText(requireContext(), ""+messageListModel.getUser_name(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(requireContext(), ChattingActivity.class));

    }
}