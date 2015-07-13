package com.qjizho.inspmarker.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nomad.instagramlogin.InstaLogin;
import com.qjizho.inspmarker.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by qjizho on 15-7-13.
 */
public class FragmentGridview extends Fragment{
    private static int sGirdImageSize = 0;
    private ImageLoader mImageLoader;
    private PtrClassicFrameLayout mPtrFrame;
    GridView gridListView;
    GridViewAdapter mAdapter;
    private ArrayList<String> picUrls = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gridview, container, false);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    mPtrFrame.refreshComplete();
                    mAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalDisplay.init(getActivity());
        sGirdImageSize = (LocalDisplay.SCREEN_WIDTH_PIXELS) / 3 ;
        mImageLoader = ImageLoaderFactory.create(getActivity());
        gridListView = (GridView) getActivity().findViewById(R.id.rotate_header_grid_view);
        mAdapter = new GridViewAdapter();
        gridListView.setAdapter(mAdapter);
        mPtrFrame = (PtrClassicFrameLayout) getActivity().findViewById(R.id.rotate_header_grid_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                picUrls.clear();
                AsyncHttpClient client = new AsyncHttpClient();
                String str = "https://api.instagram.com/v1/users/self/feed";
                RequestParams params = new RequestParams();
                params.add("count", "18");
                params.add("access_token", "");
                client.get(str, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("qiqi", "code:" + statusCode);

                        try {
                            JSONObject obj = new JSONObject(new String(responseBody));
                            JSONArray dataArray = obj.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObj = dataArray.getJSONObject(i);
                                JSONObject imageObj = dataObj.getJSONObject("images");
                                JSONObject lowPObj = imageObj.getJSONObject("low_resolution");
                                picUrls.add(lowPObj.getString("url"));
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

                    }
                });
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                // mPtrFrame.autoRefresh();
            }
        }, 100);
        // updateData();
    }

    class ViewHolder {
        CubeImageView img;
    }
    private class GridViewAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public GridViewAdapter() {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("qiqi", "get position:" + position + " url:" + picUrls.get(position));
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
