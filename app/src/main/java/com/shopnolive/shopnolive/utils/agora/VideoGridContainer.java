package com.shopnolive.shopnolive.utils.agora;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.listener.BroadcasterItemClickListener;
import com.shopnolive.shopnolive.ui.main.HomeActivity;
import com.shopnolive.shopnolive.utils.Variable;
import com.shopnolive.shopnolive.utils.agora.stats.StatsData;
import com.shopnolive.shopnolive.utils.agora.stats.StatsManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoGridContainer extends RelativeLayout implements Runnable {
    private static final int MAX_USER = 4;
    private static final int STATS_REFRESH_INTERVAL = 2000;
    private static final int STAT_LEFT_MARGIN = 34;
    private static final int STAT_TEXT_SIZE = 10;
    private static final int STAT_TEXT_SIZE_NAME = 16;

    private final SparseArray<ViewGroup> mUserViewList = new SparseArray<>(MAX_USER);
    private final List<Integer> mUidList = new ArrayList<>(MAX_USER);
    private final List<Integer> mUidList2 = new ArrayList<>(MAX_USER);
    private StatsManager mStatsManager;
    private Handler mHandler;
    private int mStatMarginBottom;
    private View viewMute;
    private BroadcasterItemClickListener broadcasterItemClickListener;

    public VideoGridContainer(Context context) {
        super(context);
        init();
    }

    public VideoGridContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoGridContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.background_live_section);
        mStatMarginBottom = getResources().getDimensionPixelSize(
                R.dimen.live_stat_margin_bottom);
        mHandler = new Handler(getContext().getMainLooper());
    }

    public void setStatsManager(StatsManager manager) {
        mStatsManager = manager;
    }

    public void setListener(BroadcasterItemClickListener broadcasterItemClickListener) {
        this.broadcasterItemClickListener = broadcasterItemClickListener;
    }

    public void addUserVideoSurface(int uid, SurfaceView surface, boolean isLocal, String name) {
        if (surface == null) {
            return;
        }

        int id = -1;
        if (isLocal) {
            if (mUidList.contains(0)) {
                mUidList.remove((Integer) 0);
                mUserViewList.remove(0);
            }

            if (mUidList.size() == MAX_USER) {
                mUidList.remove(0);
                mUserViewList.remove(0);
            }
            id = 0;
        } else {
            if (mUidList.contains(uid)) {
                mUidList.remove((Integer) uid);
                mUserViewList.remove(uid);
            }

            if (mUidList.size() < MAX_USER) {
                id = uid;
            }
        }

        if (id == 0) mUidList.add(0, uid);
        else mUidList.add(uid);

        if (id != -1) {
            mUserViewList.append(uid, createVideoView(surface, id));

            if (mStatsManager != null) {
                mStatsManager.addUserStats(uid, isLocal);
                if (mStatsManager.isEnabled()) {
                    mHandler.removeCallbacks(this);
                    mHandler.postDelayed(this, STATS_REFRESH_INTERVAL);
                }
            }

            requestGridLayout();
        }
    }

    public void addUserVideoSurface(int uid, View surface, boolean isLocal) {
        if (surface == null) {
            return;
        }

        int id = -1;
        if (isLocal) {
            if (mUidList.contains(0)) {
                mUidList.remove((Integer) 0);
                mUserViewList.remove(0);
            }

            if (mUidList.size() == MAX_USER) {
                mUidList.remove(0);
                mUserViewList.remove(0);
            }
            id = 0;
        } else {
            if (mUidList.contains(uid)) {
                mUidList.remove((Integer) uid);
                mUserViewList.remove(uid);
            }

            if (mUidList.size() < MAX_USER) {
                id = uid;
            }
        }

        if (id == 0) mUidList.add(0, uid);
        else mUidList.add(uid);

        if (id != -1) {
            mUserViewList.append(uid, createVideoView(surface, uid));

            if (mStatsManager != null) {
                mStatsManager.addUserStats(uid, isLocal);
                if (mStatsManager.isEnabled()) {
                    mHandler.removeCallbacks(this);
                    mHandler.postDelayed(this, STATS_REFRESH_INTERVAL);
                }
            }

            requestGridLayout();
        }
    }

    private ViewGroup createVideoView(View surface, final int uid) {
        RelativeLayout layout = new RelativeLayout(getContext());

        layout.setId(surface.hashCode());

        RelativeLayout.LayoutParams videoLayoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        layout.addView(surface, videoLayoutParams);

        TextView text = new TextView(getContext());
        text.setId(layout.hashCode());
        RelativeLayout.LayoutParams textParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        textParams.bottomMargin = mStatMarginBottom;
        textParams.leftMargin = STAT_LEFT_MARGIN;
        text.setTextColor(Color.WHITE);
        text.setTextSize(STAT_TEXT_SIZE);

        layout.addView(text, textParams);

        surface.setOnClickListener(v -> broadcasterItemClickListener.onClick(uid, "" + uid));

        return layout;
    }

    private ViewGroup createVideoView(SurfaceView surface, final int uid) {
        RelativeLayout layout = new RelativeLayout(getContext());

        layout.setId(surface.hashCode());

        RelativeLayout.LayoutParams videoLayoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        layout.addView(surface, videoLayoutParams);

        TextView text = new TextView(getContext());
        text.setId(layout.hashCode());
        RelativeLayout.LayoutParams textParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        textParams.bottomMargin = 5;
        textParams.leftMargin = 20;
        text.setTextColor(Color.WHITE);
        text.setTextSize(STAT_TEXT_SIZE_NAME);


        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_broadcaster_info, null);
        TextView tvName = view.findViewById(R.id.tv_broadcaster_name);
        TextView tvDiamond = view.findViewById(R.id.tv_broadcaster_diamond);
        CircleImageView ivProfile = view.findViewById(R.id.iv_broadcaster_profile);

        if (uid < 2) {
            tvName.setText(Variable.userInfo.getName());
            tvDiamond.setText(Variable.userInfo.getPresentCoinBalance());
            Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(ivProfile);
        } else {
            HomeActivity.setBroadCasterInfo(uid, tvName, tvDiamond, ivProfile);
        }

        layout.addView(view, textParams);

        view.setOnClickListener(v -> {
            int userId;
            if (uid <= 1) userId = Integer.parseInt(Variable.userInfo.getId());
            else userId = uid;

            broadcasterItemClickListener.onInfoClicked(userId, tvName.getText().toString());

            if (uid < 2) {
                tvName.setText(Variable.userInfo.getName());
                tvDiamond.setText(Variable.userInfo.getPresentCoinBalance());
                Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(ivProfile);
            } else {
                HomeActivity.setBroadCasterInfo(uid, tvName, tvDiamond, ivProfile);
            }
        });

        surface.setOnClickListener(v -> {
            int userId;
            if (uid <= 1) userId = Integer.parseInt(Variable.userInfo.getId());
            else userId = uid;
            broadcasterItemClickListener.onClick(userId, tvName.getText().toString());

            if (uid < 2) {
                tvName.setText(Variable.userInfo.getName());
                tvDiamond.setText(Variable.userInfo.getPresentCoinBalance());
                Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(ivProfile);
            } else {
                HomeActivity.setBroadCasterInfo(uid, tvName, tvDiamond, ivProfile);
            }
        });
        return layout;
    }

    public void liveFinish(Activity activity, int uid, boolean isLocal) {
        if (isLocal && mUidList.contains(0)) {
            mUidList.remove((Integer) 0);
            mUserViewList.remove(0);
        } else if (mUidList.contains(uid)) {
            mUidList.remove((Integer) uid);
            mUserViewList.remove(uid);
        }


        mStatsManager.removeUserStats(uid);
        requestGridLayout();

        if (getChildCount() == 0) {
            mHandler.removeCallbacks(this);
        }
    }

    //tag0==finish, tag2==mute
    public void removeUserVideo(final Activity activity, int tag, final int uid, boolean isLocal) {
        if (isLocal && mUidList.contains(0)) {
            mUidList.remove((Integer) 0);
            mUserViewList.remove(0);
        } else if (mUidList.contains(uid)) {
            mUidList.remove((Integer) uid);
            mUserViewList.remove(uid);

        }

        mStatsManager.removeUserStats(uid);
        requestGridLayout();
        /*if (tag == 2) {
            for (int i = 0; i < mUidList.size(); i++) {
                //custom view remote
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                viewMute = inflater.inflate(R.layout.m_layout, null);
                viewMute.bringToFront();
                CircleImageView imageView = viewMute.findViewById(R.id.imgProPic);
                TextView textView = viewMute.findViewById(R.id.name);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user1));
                textView.setText("" + uid);
                addUserVideoSurface(uid, viewMute, false);
            }
        } else if (tag == 0) {
            requestGridLayout();
        }*/


        if (getChildCount() == 0) {
            mHandler.removeCallbacks(this);
        }

    }

    private void requestGridLayout() {
        removeAllViews();
        layout(mUidList.size());
    }

    private void layout(int size) {
        RelativeLayout.LayoutParams[] params = getParams(size);
        for (int i = 0; i < size; i++) {
            addView(mUserViewList.get(mUidList.get(i)), params[i]);
        }
    }

    private RelativeLayout.LayoutParams[] getParams(int size) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        RelativeLayout.LayoutParams[] array =
                new RelativeLayout.LayoutParams[size];

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                array[0] = new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                array[0].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                array[0].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else if (i == 1) {
                array[1] = new LayoutParams(width / 2, (int) (height / 2.5));
                array[0].height = array[1].height;
                array[0].width = array[1].width;
                array[0].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                array[1].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(0)).getId());
                array[1].addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                array[0].setMargins(2, 2, 2, 2);
                array[1].setMargins(2, 2, 2, 2);
            } else if (i == 2) {
                array[i] = new RelativeLayout.LayoutParams(width / 2, (int) (height / 3.1));
                array[0].height = array[i].height;
                array[1].height = array[i].height;
                array[i].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(0)).getId());
                array[i].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                array[i].setMargins(2, 2, 2, 2);
            } else if (i == 3) {
                array[i] = new RelativeLayout.LayoutParams(width / 2, (int) (height / 3.1));
                /*//array[0].width = width / 2;
                array[1].addRule(RelativeLayout.BELOW, 0);
                array[1].addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                array[1].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(0)).getId());
                array[1].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                array[2].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                array[2].addRule(RelativeLayout.RIGHT_OF, 0);
                array[2].addRule(RelativeLayout.ALIGN_TOP, 0);
                array[2].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(0)).getId());
                array[3].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(1)).getId());
                array[3].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(2)).getId());*/

                array[i] = new RelativeLayout.LayoutParams(width / 2, (int) (height / 3.1));
                //array[i].width = array[i].width;
                array[i].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(1)).getId());
                array[i].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(2)).getId());
                array[i].setMargins(2, 2, 2, 2);
            }
        }

        return array;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAllVideo();
    }

    private void clearAllVideo() {
        removeAllViews();
        mUserViewList.clear();
        mUidList.clear();
        mHandler.removeCallbacks(this);
    }

    @Override
    public void run() {
        if (mStatsManager != null && mStatsManager.isEnabled()) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                RelativeLayout layout = (RelativeLayout) getChildAt(i);
                TextView text = layout.findViewById(layout.hashCode());
                if (text != null) {
                    StatsData data = mStatsManager.getStatsData(mUidList.get(i));
                    String info = data != null ? data.toString() : null;
                    if (info != null) text.setText(info);
                }
            }

            mHandler.postDelayed(this, STATS_REFRESH_INTERVAL);
        }
    }
}
