package com.qjizho.inspmarker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.helper.JazzyViewPager;
import com.qjizho.inspmarker.helper.ListPageInfoWithPosition;
import com.qjizho.inspmarker.helper.OutlineContainer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.mints.base.TitleBaseFragment;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.list.ListPageInfo;
import in.srain.cube.views.list.PagedListViewDataAdapter;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;

/**
 * Created by qjizho on 15-7-13.
 */
public class JazzyImageView extends TitleBaseFragment{
    private static int sGirdImageSize = 0;
    private ImageLoader mImageLoader;
    JazzyAdapter mAdapter;
    private ArrayList<String> picUrls = new ArrayList<String>();
    private int mPosition ;
    private PagedListViewDataAdapter<String> nAdapter;
    private ListPageInfo<String> mInfos = new ListPageInfo<String>(36);
    private String mPagination;
    LoadMoreGridViewContainer loadMoreContainer;
    private JazzyViewPager jazzyViewPager;
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {

        final View view = inflater.inflate(R.layout.fragment_jazzy, null);
        LocalDisplay.init(getActivity());
        ListPageInfoWithPosition obj = (ListPageInfoWithPosition)mDataIn;
        mPosition = obj.mPosition;
        mInfos = obj.mInfos;
        mPagination = obj.mPagination;
        Log.d("qiqi","get position:" + mPosition + " get list:" + mInfos.getDataList().size());
        sGirdImageSize = (LocalDisplay.SCREEN_WIDTH_PIXELS) / 3 ;
        mImageLoader = ImageLoaderFactory.create(getActivity());
//        gridListView = (GridView) getActivity().findViewById(R.id.rotate_header_grid_view);
//        mAdapter = new GridViewAdapter();
//        gridListView.setAdapter(mAdapter);
        jazzyViewPager = (JazzyViewPager) view.findViewById(R.id.jazzy_pager);
        String[] effects = this.getResources().getStringArray(R.array.jazzy_effects);
        jazzyViewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.valueOf(effects[0]));
        jazzyViewPager.setPageMargin(30);
        mAdapter = new JazzyAdapter();
        jazzyViewPager.setAdapter(mAdapter);
        jazzyViewPager.setCurrentItem(mPosition);
        // updateData();
        jazzyViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("qiqi","current position:" + position);
                if(position == mInfos.getDataList().size() -1 ){
                    if(!mPagination.isEmpty()){
                        startRequest(mPagination);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    private void startRequest(String url){

        picUrls.clear();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.get(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("qiqi", "code:" + statusCode);
                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    Log.d("qiqi","obj:"+obj.toString());
                    JSONObject pObj = obj.getJSONObject("pagination");
                    mPagination = pObj.isNull("next_url") ? "":pObj.getString("next_url");
                    Log.d("qiqi","mPagination:"+mPagination);
                    JSONArray dataArray = obj.getJSONArray("data");
                    Log.d("qiqi","Count:" + dataArray.length());
//                            Log.d("qiqi", new String(responseBody).toString());
                    for (int i = 0; i < dataArray.length(); i++) {
//                                JSONObject dataObj = dataArray.getJSONObject(i);
//                                JSONObject imageObj = dataObj.getJSONObject("images");
//                                JSONObject lowPObj = imageObj.getJSONObject("low_resolution");
//                                Log.d("qiqi",dataArray.getJSONObject(i).getString("id"));
                        picUrls.add(dataArray.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
//                                JSONObject thumbnailPObj = imageObj.getJSONObject("thumbnail");
//                                JSONObject standardPObj = imageObj.getJSONObject("standard_resolution");

                    }

                } catch (Exception e) {
                    Log.d("qiqi", "error:" + e.toString());
                }
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("qiqi","statusCode:" + statusCode);
            }
        });
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Log.d("qiqi", "Before, mInfos.length:" + mInfos.getListLength());
                    Log.d("qiqi", "Add count:" + picUrls.size());

                    mInfos.updateListInfo(picUrls, !mPagination.isEmpty());
                    Log.d("qiqi", "Then, mInfos.length:" + mInfos.getListLength());
                    mAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    class ViewHolder {
        CubeImageView img;
    }
    private class JazzyAdapter extends PagerAdapter{
        private LayoutInflater mInflater;
        JazzyAdapter(){
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            View view = mInflater.inflate(R.layout.with_grid_view_item_image_list_grid,container,false);
//            CubeImageView cubeImageView = (CubeImageView)view.findViewById(R.id.with_grid_view_item_image);
//            cubeImageView.loadImage(mImageLoader, mInfos.getDataList().get(position));
            CubeImageView image = new CubeImageView(getContext());
            image.loadImage(mImageLoader, mInfos.getDataList().get(position));
            container.addView(image, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            jazzyViewPager.setObjectForPosition(image, position);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(jazzyViewPager.findViewFromObject(position));
        }

        @Override
        public int getCount() {
            return mInfos.getDataList().size();
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
    private class GridViewAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public GridViewAdapter() {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.with_grid_view_item_image_list_grid,null);
                holder = new ViewHolder();
                holder.img = (CubeImageView) convertView.findViewById(R.id.with_grid_view_item_image);
                holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.item);
                LinearLayout.LayoutParams lyp = new LinearLayout.LayoutParams(sGirdImageSize, sGirdImageSize);
                itemLayout.setLayoutParams(lyp);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.img.loadImage(mImageLoader, picUrls.get(position));
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return picUrls.get(position);
        }

        @Override
        public int getCount() {
            return picUrls.size();
        }
    }

}
