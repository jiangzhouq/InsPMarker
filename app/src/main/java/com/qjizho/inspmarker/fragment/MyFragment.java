package com.qjizho.inspmarker.fragment;

import android.app.Fragment;

import in.srain.cube.views.list.ListPageInfo;

/**
 * Created by qjizho on 15-9-15.
 */
public abstract class MyFragment extends Fragment{
    public abstract void onFreshData(ListPageInfo listPageInfo);
}
