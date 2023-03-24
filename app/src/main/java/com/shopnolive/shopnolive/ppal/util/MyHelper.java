package com.shopnolive.shopnolive.ppal.util;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.model.MessageListModel;
import com.shopnolive.shopnolive.model.StoryModel;

import java.util.ArrayList;
import java.util.List;

public class MyHelper {
    private static MyHelper instance = null;

    public MyHelper() {

    }

    public static MyHelper getInstance() {
        if (instance == null){
            return new MyHelper();
        }
        return instance;
    }

    public List<StoryModel> getStoryList(){
        List<StoryModel> list = new ArrayList<>();
        list.add(new StoryModel(R.drawable.demo_story,1,"user name"));
        list.add(new StoryModel(R.drawable.demo_story,2,"user name"));
        list.add(new StoryModel(R.drawable.demo_story,3,"user name"));
        list.add(new StoryModel(R.drawable.demo_story,4,"user name"));
        list.add(new StoryModel(R.drawable.demo_story,5,"user name"));
        list.add(new StoryModel(R.drawable.demo_story,6,"user name"));
        list.add(new StoryModel(R.drawable.demo_story,7,"user name"));
        list.add(new StoryModel(R.drawable.demo_story,8,"user name"));
        list.add(new StoryModel(R.drawable.demo_story,9,"user name"));
        list.add(new StoryModel(R.drawable.demo_story,10,"user name"));
        return list;
    }

    public List<MessageListModel> get_message_list(){
        List<MessageListModel> list = new ArrayList<>();
        list.add(new MessageListModel("user name",1,"last msg",R.drawable.demo_story));
        list.add(new MessageListModel("user name",2,"last msg",R.drawable.demo_story));
        list.add(new MessageListModel("user name",3,"last msg",R.drawable.demo_story));
        list.add(new MessageListModel("user name",4,"last msg",R.drawable.demo_story));
        list.add(new MessageListModel("user name",5,"last msg",R.drawable.demo_story));
        return list;
    }
}
