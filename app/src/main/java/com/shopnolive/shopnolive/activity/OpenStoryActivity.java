//this is the class for the activity_open_story.xml

package com.shopnolive.shopnolive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.fragment.LiveFragment;
import com.shopnolive.shopnolive.fragment.GiftHistoryFragment;
import com.shopnolive.shopnolive.fragment.SendGiftFragment;
import com.shopnolive.shopnolive.fragment.StreamersViewFragment;
import com.shopnolive.shopnolive.fragment.WaitlistFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpenStoryActivity extends AppCompatActivity {

    LinearLayout layout;
    CardView topbar;
    CircleImageView openStoryImage;
    TextView name;

    BottomSheetBehavior joinBottomSheetBehavior, giftBottomSheetBehavior;

    Button join;
    ImageView gift;
    View joinBottomSheet, giftBottomSheet;

    ViewPager viewPagerJoin, viewPagerGift;
    TabLayout tabLayoutJoin, tabLayoutGift;

    StreamersViewFragment streamersViewFragment;
    WaitlistFragment waitlistFragment;
    LiveFragment liveFragment;
    SendGiftFragment sendGift;
    GiftHistoryFragment profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_story);

        layout = findViewById(R.id.layout);
        topbar = findViewById(R.id.top_bar);
        openStoryImage = findViewById(R.id.open_story_image);
        name = findViewById(R.id.open_story_name);
        join = findViewById(R.id.join);
        gift = findViewById(R.id.gift);
        viewPagerJoin = findViewById(R.id.view_pager_join);

        tabLayoutJoin = findViewById(R.id.tab_layout_join);

        viewPagerGift = findViewById(R.id.view_pager_gift);
        tabLayoutGift = findViewById(R.id.tab_layout_gift);

        streamersViewFragment = new StreamersViewFragment();
        waitlistFragment = new WaitlistFragment();
        liveFragment = new LiveFragment();
        sendGift = new SendGiftFragment();
        profile = new GiftHistoryFragment();

        tabLayoutJoin.setupWithViewPager(viewPagerJoin);
        tabLayoutGift.setupWithViewPager(viewPagerGift);

        Intent intent = getIntent();
        int image = intent.getExtras().getInt("story");
        String openStoryName = intent.getExtras().getString("name");

        layout.setBackgroundResource(image);
        openStoryImage.setImageResource(image);
        name.setText(openStoryName);

        //joinBottomSheet = findViewById(R.id.bottom_sheet_join);
        joinBottomSheetBehavior = BottomSheetBehavior.from(joinBottomSheet);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        giftBottomSheet = findViewById(R.id.bottom_sheet_gift);
        giftBottomSheetBehavior = BottomSheetBehavior.from(giftBottomSheet);

        gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragments(streamersViewFragment, "STREAMERS");
        viewPagerAdapter.addFragments(waitlistFragment, "WATCHLIST");
        viewPagerAdapter.addFragments(liveFragment, "VIEW");
        viewPagerJoin.setAdapter(viewPagerAdapter);

        ViewPagerAdapter2 viewPagerAdapter2 = new ViewPagerAdapter2(getSupportFragmentManager(), 0);
        viewPagerAdapter2.addFragmentsGift(sendGift, "Send Gifts");
        viewPagerAdapter2.addFragmentsGift(profile, "Profile");
        viewPagerGift.setAdapter(viewPagerAdapter2);

        tabLayoutGift.getTabAt(0).setIcon(R.drawable.gift);
        tabLayoutGift.getTabAt(1).setIcon(R.drawable.person2);


    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentsJoin = new ArrayList<>();
        List<String> fragmentsTitleJoin = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        void addFragments(Fragment fragment, String title) {
            fragmentsJoin.add(fragment);
            fragmentsTitleJoin.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentsJoin.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsJoin.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitleJoin.get(position);
        }
    }

    private class ViewPagerAdapter2 extends FragmentPagerAdapter {

        List<Fragment> fragmentsGift = new ArrayList<>();
        List<String> fragmentsTitleGift = new ArrayList<>();

        public ViewPagerAdapter2(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        void addFragmentsGift(Fragment fragment, String title) {
            fragmentsGift.add(fragment);
            fragmentsTitleGift.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentsGift.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsGift.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitleGift.get(position);
        }
    }

}
