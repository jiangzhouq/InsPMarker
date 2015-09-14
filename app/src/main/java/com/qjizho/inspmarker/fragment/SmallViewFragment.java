package com.qjizho.inspmarker.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.activity.FeedsActivity;
import com.qjizho.inspmarker.app.InsPMarkerApplication;
import com.qjizho.inspmarker.helper.InsImage;
import com.qjizho.inspmarker.helper.ListPageInfoWithPosition;
import com.qjizho.inspmarker.helper.RecentImageViewHolder;
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
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.list.ListPageInfo;
import in.srain.cube.views.list.PagedListViewDataAdapter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreUIHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by qjizho on 15-7-13.
 */
public class SmallViewFragment extends Fragment{
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
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        return view;
    }

    public void onFreshData(ListPageInfo listPageInfo){
        mInfos = listPageInfo;
        nAdapter.setListPageInfo(mInfos);
        handler.sendEmptyMessage(0);
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("qiqi", "SmallViewFragment onResume.");
        ptrFrameLayout.setLoadingMinTime(1000);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ((FeedsActivity) getActivity()).askServiceFor(InsHttpRequestService.GET_USERS_SELF_FEED, null, null);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mGridView, header);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListPageInfoWithPosition obj = new ListPageInfoWithPosition();
                obj.mInfos = mInfos;
                obj.mPosition = (int) id;
                obj.mPagination = mPagination;
                LargeViewFragment largeViewFragment = new LargeViewFragment();


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
                ((FeedsActivity) getActivity()).askServiceFor(InsHttpRequestService.GET_USERS_SELF_FEED, null, null);
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

    public void onUpdateData(Object data){
        ListPageInfoWithPosition obj = (ListPageInfoWithPosition)data;
        mPosition = obj.mPosition == 0 ? mPosition : obj.mPosition;
        mInfos = obj.mInfos;
        mInfos.getDataList().remove(mInfos.getListLength() - 1);
        mPagination = obj.mPagination;
        Log.d("qiqi","get position:" + mPosition + " get list:" + mInfos.getDataList().size());

        nAdapter.notifyDataSetChanged();

    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:

////                    LargeViewFragment largeViewFragment = (LargeViewFragment)getContext().getSupportFragmentManager().findFragmentByTag(LargeViewFragment.class.toString());
//                    if(largeViewFragment != null){
//                        ListPageInfoWithPosition obj = new ListPageInfoWithPosition();
//                        obj.mInfos = mInfos;
//                        obj.mPagination = mPagination;
//                        largeViewFragment.onEnter(obj);
//                        largeViewFragment.onResume();
//                    }
                    Log.d("qiqi", "" + mInfos.getListLength());
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
