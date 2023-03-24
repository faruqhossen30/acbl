package com.shopnolive.shopnolive.ppal.callbacks;

import com.shopnolive.shopnolive.model.MessageListModel;
import com.shopnolive.shopnolive.ppal.model.FollowListModel;

public interface MessageListClick {
    void onStoryClick(FollowListModel.MyFollower storyModel);
    void onMessageListClick(MessageListModel messageListModel);
}
