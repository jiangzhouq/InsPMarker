package com.qjizho.inspmarker.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.activity.FeedsActivity;
import com.qjizho.inspmarker.activity.MyActivity;
import com.qjizho.inspmarker.activity.PersonActivity;
import com.qjizho.inspmarker.db.Account;
import com.qjizho.inspmarker.helper.InsImage;
import com.qjizho.inspmarker.helper.JazzyViewPager;
import com.qjizho.inspmarker.helper.ListPageInfoWithPosition;
import com.qjizho.inspmarker.helper.OutlineContainer;
import com.qjizho.inspmarker.helper.Utils;
import com.qjizho.inspmarker.service.InsHttpRequestService;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.list.ListPageInfo;
import in.srain.cube.views.list.PagedListViewDataAdapter;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;

/**
 * Created by qjizho on 15-7-13.
 */
public class LargeViewFragment extends MyFragment{
    private static int sGirdImageSize = 0;
    private ImageLoader mImageLoader;
    JazzyAdapter mAdapter;
    private ArrayList<InsImage> picUrls = new ArrayList<InsImage>();
    private ListPageInfo<InsImage> mInfos = new ListPageInfo<InsImage>(36);
    private String mPagination;
    LoadMoreGridViewContainer loadMoreContainer;
    private JazzyViewPager jazzyViewPager;
    private String mRequestUrl = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(inflater, viewGroup, bundle);
        final View view = inflater.inflate(R.layout.fragment_jazzy, null);
        LocalDisplay.init(getActivity());
        sGirdImageSize = (LocalDisplay.SCREEN_WIDTH_PIXELS) / 3 ;
        mImageLoader = ImageLoaderFactory.create(getActivity());
        jazzyViewPager = (JazzyViewPager) view.findViewById(R.id.jazzy_pager);
        String[] effects = this.getResources().getStringArray(R.array.jazzy_effects);
        jazzyViewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.valueOf(effects[0]));
        jazzyViewPager.setPageMargin(30);
        if(getActivity() instanceof FeedsActivity){
            mRequestUrl = InsHttpRequestService.GET_USERS_SELF_FEED;
        }else{
            mRequestUrl = InsHttpRequestService.GET_USERS_USERID_MEDIA_RECENT;
        }
        return view;
    }
    @Override
    public void onFreshData(ListPageInfo listPageInfo) {
//        mInfos = new ListPageInfo<InsImage>(36);
//        mInfos.updateListInfo(listPageInfo.getDataList(), true);
//        mInfos.getDataList().remove(mInfos.getListLength() -1);
        mInfos = listPageInfo;
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        int position = getArguments().getInt("position");
        ((MyActivity)getActivity()).onAskServiceFor(mRequestUrl, InsHttpRequestService.REQUEST_HOLD, null, null);
        if(mAdapter == null){
            mAdapter = new JazzyAdapter();
            jazzyViewPager.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }


        jazzyViewPager.setCurrentItem(position);
        // updateData();
        jazzyViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("qiqi", "current position:" + position);
                if (position == mInfos.getDataList().size() - 1) {
                    ((MyActivity)getActivity()).onAskServiceFor(mRequestUrl, InsHttpRequestService.REQUEST_LOADMORE, null, null);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDetach() {
        ((FeedsActivity)getActivity()).updateSmallViewFragmentPosition(jazzyViewPager.getCurrentItem());

        super.onDetach();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private class JazzyAdapter extends PagerAdapter{
        private LayoutInflater mInflater;
        JazzyAdapter(){
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mInflater.inflate(R.layout.jazzy_image,null);
            CubeImageView cubeImageView = (CubeImageView)view.findViewById(R.id.with_grid_view_item_image);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LocalDisplay.SCREEN_WIDTH_PIXELS,LocalDisplay.SCREEN_WIDTH_PIXELS);
            cubeImageView.setLayoutParams(lp);
            cubeImageView.loadImage(mImageLoader, mInfos.getDataList().get(position).mStandardResolution);
            CubeImageView mUserProfilePic = (CubeImageView)view.findViewById(R.id.user_profile_pic);
            mUserProfilePic.loadImage(mImageLoader, mInfos.getDataList().get(position).mProfilePciture);
            mUserProfilePic.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final int mPos = position;
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra("user_id", mInfos.getDataList().get(mPos).mUserId);
                    startActivity(intent);
                }
            });
            TextView text = (TextView)view.findViewById(R.id.message);
            text.setText(mInfos.getDataList().get(position).mCaption);
//            CubeImageView image = new CubeImageView(getContext());
//            image.loadImage(mImageLoader, mInfos.getDataList().get(position));
            container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            jazzyViewPager.setObjectForPosition(view, position);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(jazzyViewPager.findViewFromObject(position));
        }

        @Override
        public int getCount() {
            return mInfos.getListLength();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            if (view instanceof OutlineContainer) {
                return ((OutlineContainer) view).getChildAt(0) == obj;
            } else {
                return view == obj;
            }
        }
    }

}
