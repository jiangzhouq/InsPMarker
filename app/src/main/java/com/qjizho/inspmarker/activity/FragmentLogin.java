package com.qjizho.inspmarker.activity;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nomad.instagramlogin.InstaLogin;
import com.nomad.instagramlogin.Keys;
import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.db.Account;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by qjizho on 15-7-13.
 */
public class FragmentLogin extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btn = (Button) getActivity().findViewById(R.id.login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstaLogin instaLogin = new InstaLogin(getActivity(),
                        "c4d946f3dc8a43699aeb7c57b5cbc12d",
                        "6aba840c8c984aadbae55bad66c5eab3",
                        "https://loggedinbaby");
                instaLogin.login();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Keys.LOGIN_REQ) {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK) {
                Bundle bundle = data.getExtras();
                if(!bundle.getString(InstaLogin.ACCESS_TOKEN).isEmpty()){
                    insertAccount(bundle);
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.frag, new FragmentGridview());
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
        value.put(Account.COLUMN_ACCESS_TOKEN, bundle.getString(InstaLogin.ACCESS_TOKEN));
        value.put(Account.COLUMN_ACTIVED, 1);
        getActivity().getContentResolver().insert(Account.CONTENT_URI_ACCOUNT,value);
    }
}
