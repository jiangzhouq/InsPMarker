package com.qjizho.inspmarker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.srain.cube.mints.base.TitleBaseFragment;

/**
 * Created by qjizho on 15-8-7.
 */
public class MyTitleBaseFragment extends TitleBaseFragment{
    @Override
    protected View createView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        mTitleHeaderBar.setVisibility(View.GONE);
        return null;
    }
}
