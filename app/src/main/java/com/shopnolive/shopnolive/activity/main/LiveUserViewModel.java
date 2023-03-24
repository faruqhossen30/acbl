package com.shopnolive.shopnolive.activity.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.shopnolive.shopnolive.model.GiftListData;
import com.shopnolive.shopnolive.model.UserListResponse;
import com.shopnolive.shopnolive.model.profile.IsLiveUser;
import com.shopnolive.shopnolive.model.user.UserDetails;

import java.util.List;

public class LiveUserViewModel extends AndroidViewModel {


    LiveUserRepository liveUserRepository;

    public LiveUserViewModel(@NonNull Application application) {
        super(application);

        liveUserRepository = LiveUserRepository.getInstance();
        // liveData = liveUserRepository.
        //liveUserRepository = ;

    }


    public LiveData<UserDetails> getAllUsers() {
        return liveUserRepository.getAllUsers();
    }


    public LiveData<List<IsLiveUser>> getLiveUsers() {
        return liveUserRepository.getLiveUser();
    }

    public LiveData<List<GiftListData>> getAllGiftList() {
        return liveUserRepository.getAllGiftList();
    }

    public LiveData<UserListResponse> getStreamingNow() {
        return liveUserRepository.getStreamingList();
    }
}
