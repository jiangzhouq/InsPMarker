package com.qjizho.inspmarker.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.activity.FeedsActivity;
import com.qjizho.inspmarker.activity.MyActivity;
import com.qjizho.inspmarker.activity.PersonActivity;
import com.qjizho.inspmarker.helper.InsImage;
import com.qjizho.inspmarker.helper.RecentImageViewHolder;
import com.qjizho.inspmarker.helper.UserProfileHeaderInfo;
import com.qjizho.inspmarker.service.InsHttpRequestService;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.list.ListPageInfo;
import in.srain.cube.views.list.PagedListViewDataAdapter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by qjizho on 15-7-13.
 */
public class SmallViewFragment extends MyFragment{
    private static int sGirdImageSize = 0;
    private ImageLoader mImageLoader;
    private PtrFrameLayout ptrFrameLayout;
    private ArrayList<InsImage> picUrls = new ArrayList<InsImage>();
    private String mId ;
    private String mToken;
    private PagedListViewDataAdapter<InsImage> nAdapter;
    private ListPageInfo<InsImage> mInfos = new ListPageInfo<InsImage>(36);
    private GridViewWithHeaderAndFooter mGridView;
    private String mPagination;
    LoadMoreGridViewContainer loadMoreContainer;
    private int mPosition = 0;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private String mRequestUrl = "";
    private String mUserId = "";
    private CubeImageView mHeaderProfilePic;
    private TextView mHeaderFullName;
    private TextView mHeaderCountMedia;
    private TextView mHeaderCountFollowers;
    private TextView mHeaderCountFollowing;
    private TextView mHeaderName;
    private TextView mHeaderBio;
    private TextView mHeaderWebsite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(inflater, viewGroup, bundle);
        final View view = inflater.inflate(R.layout.fragment_gridview, null);
        LocalDisplay.init(getActivity());
        sGirdImageSize = (LocalDisplay.SCREEN_WIDTH_PIXELS) / 3 ;
        mImageLoader = ImageLoaderFactory.create(getActivity());
        ptrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.load_more_grid_view_ptr_frame);
        mGridView = (GridViewWithHeaderAndFooter) view.findViewById(R.id.load_more_grid_view);
        loadMoreContainer = (LoadMoreGridViewContainer) view.findViewById(R.id.load_more_grid_view_container);
        mFragmentManager = getActivity().getFragmentManager();


        if(getActivity() instanceof FeedsActivity){
            mRequestUrl = InsHttpRequestService.GET_USERS_SELF_FEED;
        }else{
            mRequestUrl = InsHttpRequestService.GET_USERS_USERID_MEDIA_RECENT;
            mUserId = getArguments().getString("user_id");

            // header place holder
            View headerMarginView = inflater.inflate(R.layout.user_profile_header_layout, null);
            headerMarginView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LocalDisplay.dp2px(80)));
            mHeaderProfilePic = (CubeImageView)headerMarginView.findViewById(R.id.header_profile_pic);
            mHeaderFullName = (TextView)headerMarginView.findViewById(R.id.header_full_name);
            mHeaderCountMedia = (TextView)headerMarginView.findViewById(R.id.header_posts);
            mHeaderCountFollowers = (TextView)headerMarginView.findViewById(R.id.header_followers);
            mHeaderCountFollowing = (TextView)headerMarginView.findViewById(R.id.header_following);
            mHeaderName = (TextView)headerMarginView.findViewById(R.id.header_name);
            mHeaderBio = (TextView)headerMarginView.findViewById(R.id.header_bio);
            mHeaderWebsite = (TextView)headerMarginView.findViewById(R.id.header_website);
            mGridView.addHeaderView(headerMarginView);
        }
        return view;
    }

    @Override
    public void onFreshData(ListPageInfo listPageInfo) {
        mInfos = listPageInfo;
        Log.d("qiqi", "mInfos new : " + mInfos.getListLength());
        nAdapter.setListPageInfo(mInfos);
        handler.sendEmptyMessage(0);
    }

    public void onFreshUserInfo(UserProfileHeaderInfo userProfileHeaderInfo){
        mHeaderProfilePic.loadImage(mImageLoader, userProfileHeaderInfo.mProfilePic);
        mHeaderFullName.setText(userProfileHeaderInfo.mFullName);
        mHeaderCountMedia.setText(userProfileHeaderInfo.mCountsMedia);
        mHeaderCountFollowers.setText(userProfileHeaderInfo.mCountsFollows);
        mHeaderCountFollowing.setText(userProfileHeaderInfo.mCountsFollowing);
        mHeaderName.setText(userProfileHeaderInfo.mUserName);
        mHeaderBio.setText(userProfileHeaderInfo.mBio);
        mHeaderWebsite.setText(userProfileHeaderInfo.mId);
    }

    public void updatePosition( int position){
        int plusPosition;
        if(getActivity() instanceof PersonActivity){
            plusPosition = 9;
        }else{
            plusPosition = 12;
        }

        int firstPosition  = mGridView.getFirstVisiblePosition();
        if(position >= firstPosition && position < plusPosition + firstPosition)
            return;
        mGridView.smoothScrollToPositionFromTop(position -6 , 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("qiqi", "SmallViewFragment onResume.");
        ptrFrameLayout.setLoadingMinTime(1000);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                ((MyActivity) getActivity()).onAskServiceFor(mRequestUrl, InsHttpRequestService.REQUEST_REFRESH, mUserId, null);
                if(getActivity() instanceof PersonActivity)
                    ((MyActivity) getActivity()).onAskServiceFor(InsHttpRequestService.GET_USERS_USERID, InsHttpRequestService.REQUEST_REFRESH, mUserId, null);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mGridView, header);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                Bundle bundle = new Bundle();
                bundle.putInt("position", (int) id);
                Log.d("qiqi", "Clicked Pos:" + id);
                LargeViewFragment largeViewFragment = new LargeViewFragment();
                largeViewFragment.setArguments(bundle);
                mFragmentTransaction.add(R.id.frag, largeViewFragment, "LargeViewFragment");
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();

//                getContext().pushFragmentToBackStack(LargeViewFragment.class, obj);
            }
        });

        loadMoreContainer.setAutoLoadMore(true);
        // binding view and data
        nAdapter = new PagedListViewDataAdapter<InsImage>();
        nAdapter.setViewHolderClass(this, RecentImageViewHolder.class, mImageLoader);
        nAdapter.setListPageInfo(mInfos);
        mInfos.prepareForNextPage();
        mGridView.setAdapter(nAdapter);
        loadMoreContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                Log.d("qiqi", "Start load more");
                ((MyActivity) getActivity()).onAskServiceFor(mRequestUrl,InsHttpRequestService.REQUEST_LOADMORE, null, null);
//                mInfos.prepareForNextPage();
            }
        });
        loadMoreContainer.loadMoreFinish(false, mInfos.hasMore() );

        // the following are default settings
//        mPtrFrame.setResistance(1.7f);
//        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
//        mPtrFrame.setDurationToClose(200);
//        mPtrFrame.setDurationToCloseHeader(1000);
//        // default is false
//        mPtrFrame.setPullToRefresh(false);
//        // default is true
//        mPtrFrame.setKeepHeaderWhenRefresh(true);
        ptrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrameLayout.autoRefresh(false);
            }
        }, 150);
        // updateData();
    }



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:

                    nAdapter.notifyDataSetChanged();
                    ptrFrameLayout.refreshComplete();
                    loadMoreContainer.loadMoreFinish(mInfos.getDataList().isEmpty(), mInfos.hasMore());
                    mInfos.prepareForNextPage();
                    break;
            }
            super.handleMessage(msg);
        }
    };




}
