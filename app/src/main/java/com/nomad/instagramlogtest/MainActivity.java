package com.nomad.instagramlogtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nomad.instagramlogin.InstaLogin;
import com.nomad.instagramlogin.Keys;

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

public class MainActivity extends ActionBarActivity {
    Button login;
    TextView stat;
    private static final int sGirdImageSize = (LocalDisplay.SCREEN_WIDTH_PIXELS - LocalDisplay.dp2px(12 + 12 + 10)) / 2;
    private ImageLoader mImageLoader;
    private PtrClassicFrameLayout mPtrFrame;
    GridView gridListView;
    private ArrayList<String> picUrls = new ArrayList<String>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    gridListView.setAdapter(new GridViewAdapter());
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageLoader = ImageLoaderFactory.create(this);
        gridListView = (GridView) findViewById(R.id.rotate_header_grid_view);
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_grid_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                InstaLogin instaLogin = new InstaLogin(MainActivity.this,
                        "c4d946f3dc8a43699aeb7c57b5cbc12d",
                        "6aba840c8c984aadbae55bad66c5eab3",
                        "https://loggedinbaby");
                instaLogin.login();
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


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Keys.LOGIN_REQ) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                stat.setText("Fullname : "+bundle.getString(InstaLogin.FULLNAME)+"\n"+
                "UserName : "+bundle.getString(InstaLogin.USERNAME)+"\n"+
                        "id : "+bundle.getString(InstaLogin.ID)+"\n"+
                        "pICTURE : "+bundle.getString(InstaLogin.PROFILE_PIC)+"\n"+
                        "access_token : "+bundle.getString(InstaLogin.ACCESS_TOKEN)+"\n"+
                        "bıo : "+bundle.getString(InstaLogin.BIO)+"\n");
                AsyncHttpClient client = new AsyncHttpClient();
                String str = "https://api.instagram.com/v1/users/self/feed";
                RequestParams params = new RequestParams();
                params.add("count","10");
                params.add("access_token",bundle.getString(InstaLogin.ACCESS_TOKEN));
                client.get(str,params,new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("qiqi","code:" + statusCode );

                        try {
                            JSONObject obj = new JSONObject(new String(responseBody));
                            JSONArray dataArray = obj.getJSONArray("data");
                            for(int i = 0; i < dataArray.length(); i++){
                                JSONObject dataObj = dataArray.getJSONObject(i);
                                JSONObject imageObj = dataObj.getJSONObject("images");
                                JSONObject lowPObj = imageObj.getJSONObject("low_resolution");
                                picUrls.add(lowPObj.getString("url"));
//                                JSONObject thumbnailPObj = imageObj.getJSONObject("thumbnail");
//                                JSONObject standardPObj = imageObj.getJSONObject("standard_resolution");

                            }
                        }catch (Exception e){
                            Log.d("qiqi","error:" + e.toString());
                        }
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        }
    }

    class ViewHolder {
        CubeImageView img;
    }
    private class GridViewAdapter extends BaseAdapter{

        private LayoutInflater mInflater;

        public GridViewAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.with_grid_view_item_image_list_grid,null);
                holder = new ViewHolder();
                holder.img = (CubeImageView) convertView.findViewById(R.id.with_grid_view_item_image);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.img.loadImage(mImageLoader, picUrls.get(position));
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }

    private static int counter = 0;
    public static int stringNumbers(String str)
    {
        if (str.indexOf("type")==-1)
        {
            return 0;
        }
        else if(str.indexOf("type") != -1)
        {
            counter++;
            stringNumbers(str.substring(str.indexOf("type")+4));
            return counter;
        }
        return 0;
    }
}
