package com.shopnolive.shopnolive.activity.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.shopnolive.shopnolive.model.user.MyProfile;

public class ProfileViewModel extends AndroidViewModel {

    ProfileRepository repository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = ProfileRepository.getInstance();
    }

    public LiveData<MyProfile> getProfile() {
        return repository.getMyProfileData();
    }

    public LiveData<ProfileData> getProfileById(String id) {
        return repository.getProfilesById(id);
    }

    public LiveData<ProfileData> getProfileByMessId(String id) {
        return repository.getProfileByMessIds(id);
    }

}
