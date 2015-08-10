package com.qjizho.inspmarker.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nomad.instagramlogin.InstaLogin;
import com.nomad.instagramlogin.Keys;
import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.db.Account;

import in.srain.cube.mints.base.TitleBaseFragment;

/**
 * Created by qjizho on 15-7-13.
 */
public class FragmentLogin extends MyTitleBaseFragment {


    @Override
    protected View createView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.createView(layoutInflater, viewGroup, bundle);
        setHeaderTitle("Dot View Demo");
        final View view = layoutInflater.inflate(R.layout.fragment_login,null);
        Button btn = (Button) view.findViewById(R.id.login);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InstaLogin instaLogin = new InstaLogin(FragmentLogin.this,
//                        "c4d946f3dc8a43699aeb7c57b5cbc12d",
//                        "6aba840c8c984aadbae55bad66c5eab3",
//                        "https://loggedinbaby");

//                instaLogin.login();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("qiqi", "onActivityResult");
        if (requestCode == Keys.LOGIN_REQ) {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK) {
                Bundle bundle = data.getExtras();
                if(!bundle.getString(InstaLogin.ACCESS_TOKEN).isEmpty()){
//                    Log.d("qiqi", "" + data.getExtras().getString(InstaLogin.FULLNAME));
                    insertAccount(bundle);
                    Bundle fgBundle = new Bundle();
                    fgBundle.putString("id", bundle.getString(InstaLogin.ID));
                    fgBundle.putString("token", bundle.getString(InstaLogin.ACCESS_TOKEN));

//                    FollowsGridView gridFragment = new FollowsGridView();
//                    gridFragment.setArguments(fgBundle);
                    Log.d("qiqi","start gridview with id :" + bundle.getString(InstaLogin.ID) + " token:" + bundle.getString(InstaLogin.ACCESS_TOKEN));
                    getContext().pushFragmentToBackStack(FeedGridView.class, fgBundle);
//                    getActivity().getFragmentManager().beginTransaction().replace(R.id.frag, gridFragment).commit();
                }
            }
        }
    }
    private void insertAccount(Bundle bundle){
        ContentValues value = new ContentValues();
        value.put(Account.COLUMN_ACCOUNT_ID, bundle.getString(InstaLogin.ID));
        value.put(Account.COLUMN_USERNAME, bundle.getString(InstaLogin.USERNAME));
        value.put(Account.COLUMN_FULL_NAME, bundle.getString(InstaLogin.FULLNAME));
        value.put(Account.COLUMN_BIO, bundle.getString(InstaLogin.BIO));
        value.put(Account.COLUMN_PROFILE_PICTURE, bundle.getString(InstaLogin.PROFILE_PIC));
        value.put(Account.COLUMN_ACCESS_TOKEN, bundle.getString(InstaLogin.ACCESS_TOKEN));
        value.put(Account.COLUMN_ACTIVED, 1);
        Cursor cur = getActivity().getContentResolver().query(Account.CONTENT_URI_ACCOUNTS, null, "account_id=" + bundle.getString(InstaLogin.ID), null, null);
        if(cur.getCount() > 0){
            Log.d("qiqi", " already in:" + bundle.getString(InstaLogin.ID));
        }else{
            ContentValues noActived = new ContentValues();
            noActived.put(Account.COLUMN_ACTIVED, 0);
            getActivity().getContentResolver().update(Account.CONTENT_URI_ACCOUNTS,noActived,"actived=1",null);
            Uri uri = getActivity().getContentResolver().insert(Account.CONTENT_URI_ACCOUNTS, value);
            Log.d("qiqi", uri.toString());
        }
    }
}
