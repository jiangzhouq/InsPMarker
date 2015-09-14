package com.qjizho.inspmarker.helper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qjizho.inspmarker.R;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.list.ViewHolderBase;

/**
 * Created by qjizho on 15-7-22.
 */
public class RecentImageViewHolder extends ViewHolderBase<InsImage>{

    private CubeImageView mImageView;
    private ImageLoader mImageloader;

    public RecentImageViewHolder(ImageLoader imageLoader){
        mImageloader = imageLoader;
    }
    @Override
    public View createView(LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.with_grid_view_item_image_list_grid,null);
        mImageView = (CubeImageView) view.findViewById(R.id.with_grid_view_item_image);
        LinearLayout.LayoutParams lyp = new LinearLayout.LayoutParams(LocalDisplay.SCREEN_WIDTH_PIXELS/3,LocalDisplay.SCREEN_WIDTH_PIXELS/3);
        mImageView.setLayoutParams(lyp);
        return view;
    }

    @Override
    public void showData(int position, InsImage insImage) {
        Log.d("qiqi", "show image :" + insImage.mThumbnail);
        if(insImage.mLowResolution.equals("12321")){
            mImageView.loadImage(mImageloader, "http://jiangzhoumigs-jiangzhoumigs.stor.sinaapp.com/original/download.png", LocalDisplay.SCREEN_WIDTH_PIXELS/3);
        }else{
            mImageView.loadImage(mImageloader, insImage.mThumbnail, LocalDisplay.SCREEN_WIDTH_PIXELS/3);
        }
    }
}
