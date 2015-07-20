package com.qjizho.inspmarker.activity;

import android.view.LayoutInflater;
import android.view.View;

import com.qjizho.inspmarker.R;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.views.list.ViewHolderBase;

public class ImageListItemMiddleImageViewHolder extends ViewHolderBase<ImageListItem> {

    private CubeImageView mImageView;
    private ImageLoader mImageLoader;

    public ImageListItemMiddleImageViewHolder(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    @Override
    public View createView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.with_grid_view_item_image_list_grid, null);
        mImageView = (CubeImageView) view.findViewById(R.id.with_grid_view_item_image);

//        LinearLayout.LayoutParams lyp = new LinearLayout.LayoutParams(ImageSize.sGirdImageSize, ImageSize.sGirdImageSize);
//        mImageView.setLayoutParams(lyp);
        return view;
    }

    @Override
    public void showData(int position, ImageListItem itemData) {
        mImageView.loadImage(mImageLoader, itemData.picUrl);
    }
}