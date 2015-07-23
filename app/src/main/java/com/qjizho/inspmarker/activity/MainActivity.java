package com.qjizho.inspmarker.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.db.Account;
import com.qjizho.inspmarker.fragment.FollowsGridView;
import com.qjizho.inspmarker.fragment.FragmentLogin;

import in.srain.cube.mints.base.MintsBaseActivity;

public class MainActivity extends MintsBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Cursor cur = getContentResolver().query(Account.CONTENT_URI_ACCOUNTS,null,"actived=1",null,null);
        if(cur.getCount() > 0){
            cur.moveToFirst();
            Bundle bundle = new Bundle();
            bundle.putString("id",cur.getString(Account.NUM_ACCOUNT_ID));
            bundle.putString("token", cur.getString(Account.NUM_ACCESS_TOKEN));
            FollowsGridView gridFragment = new FollowsGridView();
            gridFragment.setArguments(bundle);
            pushFragmentToBackStack(FollowsGridView.class, bundle);
//            getFragmentManager().beginTransaction().add(R.id.frag, gridFragment).commit();
        }else{
//            getFragmentManager().beginTransaction().add(R.id.frag, new FragmentLogin()).commit();
            pushFragmentToBackStack(FragmentLogin.class,null);
        }

    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.frag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("qiqi","activity onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }
}
