package com.qjizho.inspmarker.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.nomad.instagramlogin.InstaLogin;
import com.nomad.instagramlogin.Keys;
import com.qjizho.inspmarker.Constant.Constants;
import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.db.Account;
import com.qjizho.inspmarker.fragment.AccountManageView;
import com.qjizho.inspmarker.fragment.LargeViewFragment;
import com.qjizho.inspmarker.fragment.SmallViewFragment;
import com.qjizho.inspmarker.helper.InsImage;
import com.qjizho.inspmarker.helper.UserProfileHeaderInfo;
import com.qjizho.inspmarker.service.InsHttpRequestService;

import in.srain.cube.views.list.ListPageInfo;

/*
This is the main activity to show feeded accounts' pics.
It has two show mode : SmallViewFragment | LargeViewFragment
 */
public class FeedsActivity extends MyActivity {
    private static final int PROFILE_SETTING = 99;
    private static final int PROFILE_MANAGER = 100;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private ListPageInfo<InsImage> mInfos = new ListPageInfo<InsImage>(36);
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;
    private InsHttpRequestService.InsHttpBinder mInsHttpBinder;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInsHttpBinder = (InsHttpRequestService.InsHttpBinder)service;
            mInsHttpBinder.getService().setOnReturnListener(new InsHttpRequestService.OnReturnListener() {
                @Override
                public void onReturnForSelfFeed(ListPageInfo listPageInfo) {
                    mInfos = listPageInfo;
                    Log.d("qiqi", "Receive Self Feed count:" + mInfos.getListLength());
                    SmallViewFragment smallViewFragment =  (SmallViewFragment)mFragmentManager.findFragmentByTag("SmallViewFragment");
                    LargeViewFragment largeViewFragment = (LargeViewFragment)mFragmentManager.findFragmentByTag("LargeViewFragment");
                    if(null != smallViewFragment){
                        smallViewFragment.onFreshData(listPageInfo);
                    }
                    if(null != largeViewFragment){
                        largeViewFragment.onFreshData(listPageInfo);
                    }
                }

                @Override
                public void onReturnForRecentMedia(ListPageInfo listPageInfo) {
                }

                @Override
                public void onReturnForUserInfo(UserProfileHeaderInfo userProfileHeaderInfo) {

                }

            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void updateSmallViewFragmentPosition(int position){
        Fragment curFragment = mFragmentManager.findFragmentById(R.id.frag);
        ((SmallViewFragment) curFragment).updatePosition(position);
    }

    @Override
    public void onAskServiceFor(String url, int action, String x0, String x1) {
        mInsHttpBinder.startHttpRequest(url, action, x0 , x1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getFragmentManager();
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles()
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (!(profile instanceof IDrawerItem)) {
                            return false;
                        }
                        if (((IDrawerItem) profile).getIdentifier() == PROFILE_SETTING) {
                            InstaLogin instaLogin = new InstaLogin(FeedsActivity.this, Constants.INSLOGIN_CLIENT_ID, Constants.INSLOGIN_CLIENT_SECRET, Constants.INSLOGIN_CLIENT_CALLBACKURL);
                            instaLogin.login();
                        } else if (((IDrawerItem) profile).getIdentifier() == PROFILE_MANAGER) {
                            goToFragment(new AccountManageView(), null, true);
                        } else {
                            Log.d("qiqi", "item clicked:" + ((IDrawerItem) profile).getIdentifier());
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
//                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_home_feed).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName(R.string.menu_posts).withIcon(FontAwesome.Icon.faw_file).withIdentifier(2).withCheckable(false),
                        new PrimaryDrawerItem().withName(R.string.menu_following).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(3).withCheckable(false),
                        new PrimaryDrawerItem().withName(R.string.menu_followers).withIcon(FontAwesome.Icon.faw_user).withIdentifier(4).withCheckable(false),
                        new PrimaryDrawerItem().withName(R.string.menu_liked_post).withIcon(FontAwesome.Icon.faw_star).withIdentifier(5).withCheckable(false)
                ) // add the items we want to use with our Drawer
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(false)
                .build();

        loadAccounts();
        updateActivedFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, InsHttpRequestService.class);
//        intent.setAction("com.qjizho.inspmarker.service.InsHttpRequestService");
        bindService(intent, conn , Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }

    public void goToFragment(Fragment fragment, Bundle args, boolean addToBackStack){
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        mFragmentTransaction.replace(R.id.frag, fragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

    public void loadAccounts(){
        while(headerResult.getProfiles().size() > 0){
            headerResult.removeProfile(0);
        }
        headerResult.addProfile(new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBarSize().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_SETTING), 0);
        headerResult.addProfile(new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(PROFILE_MANAGER), 1);

        Cursor cur = getContentResolver().query(Account.CONTENT_URI_ACCOUNTS,null,null,null,null);
        while(cur.moveToNext()){
            Log.d("qiqi","profile pic:" + cur.getString(Account.NUM_PROFILE_PICTURE));
            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName(cur.getString(Account.NUM_USERNAME)).withEmail(cur.getString(Account.NUM_FULL_NAME)).withIcon(Uri.parse(cur.getString(Account.NUM_PROFILE_PICTURE))).withIdentifier(Integer.parseInt(cur.getString(Account.NUM_COLUMN_ID)));
            headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
        }
        cur.close();
        Cursor activedCur = getContentResolver().query(Account.CONTENT_URI_ACCOUNTS,null,"actived=1",null,null);
        if(activedCur.getCount() > 0){
            activedCur.moveToFirst();
            headerResult.setActiveProfile(Integer.parseInt(activedCur.getString(Account.NUM_COLUMN_ID)));
        }
        activedCur.close();
        if(headerResult.isSelectionListShown()){
            headerResult.toggleSelectionList(this);
        }

    }

    public void updateActivedFragment(){
        Cursor activedCur = getContentResolver().query(Account.CONTENT_URI_ACCOUNTS,null,"actived=1",null,null);
        if(activedCur.getCount() > 0){
            activedCur.moveToFirst();
            Bundle bundle = new Bundle();
            bundle.putString("id", activedCur.getString(Account.NUM_ACCOUNT_ID));
            bundle.putString("token", activedCur.getString(Account.NUM_ACCESS_TOKEN));
            Fragment fragment = new SmallViewFragment();
            fragment.setArguments(bundle);
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            mFragmentTransaction.add(R.id.frag, fragment, "SmallViewFragment");
            mFragmentTransaction.commit();
        }
        activedCur.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Keys.LOGIN_REQ) {
            // Make sure the request was successful
            if (resultCode == this.RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (!bundle.getString(InstaLogin.ACCESS_TOKEN).isEmpty()) {
//                    Log.d("qiqi", "" + data.getExtras().getString(InstaLogin.FULLNAME));
                    insertAccount(bundle);
                    Bundle fgBundle = new Bundle();
                    fgBundle.putString("id", bundle.getString(InstaLogin.ID));
                    fgBundle.putString("token", bundle.getString(InstaLogin.ACCESS_TOKEN));

//                    FollowsGridView gridFragment = new FollowsGridView();
//                    gridFragment.setArguments(fgBundle);
                    Log.d("qiqi", "start gridview with id :" + bundle.getString(InstaLogin.ID) + " token:" + bundle.getString(InstaLogin.ACCESS_TOKEN));
                    loadAccounts();
//                    getActivity().getFragmentManager().beginTransaction().replace(R.id.frag, gridFragment).commit();
                }
            }
        }

    }
    private void insertAccount(Bundle bundle){
        ContentValues updateValue = new ContentValues();
        updateValue.put(Account.COLUMN_ACTIVED, 0);
        getContentResolver().update(Account.CONTENT_URI_ACCOUNTS,updateValue,"actived=1",null);

        ContentValues value = new ContentValues();
        value.put(Account.COLUMN_ACCOUNT_ID, bundle.getString(InstaLogin.ID));
        value.put(Account.COLUMN_USERNAME, bundle.getString(InstaLogin.USERNAME));
        value.put(Account.COLUMN_FULL_NAME, bundle.getString(InstaLogin.FULLNAME));
        value.put(Account.COLUMN_BIO, bundle.getString(InstaLogin.BIO));
        value.put(Account.COLUMN_PROFILE_PICTURE, bundle.getString(InstaLogin.PROFILE_PIC));
        value.put(Account.COLUMN_ACCESS_TOKEN, bundle.getString(InstaLogin.ACCESS_TOKEN));
        value.put(Account.COLUMN_ACTIVED, 1);
        Cursor cur = getContentResolver().query(Account.CONTENT_URI_ACCOUNTS, null, "_id=" + bundle.getString(InstaLogin.ID), null, null);
        if(cur.getCount() > 0){
            Log.d("qiqi", " already in:" + bundle.getString(InstaLogin.ID));
        }else{
            ContentValues noActived = new ContentValues();
            noActived.put(Account.COLUMN_ACTIVED, 0);
            getContentResolver().update(Account.CONTENT_URI_ACCOUNTS, noActived, "actived=1", null);
            Uri uri = getContentResolver().insert(Account.CONTENT_URI_ACCOUNTS, value);
            Log.d("qiqi", uri.toString());
        }
    }

    private void changeActived(int activeId){
        ContentValues updateValue = new ContentValues();
        updateValue.put(Account.COLUMN_ACTIVED, 0);
        getContentResolver().update(Account.CONTENT_URI_ACCOUNTS, updateValue, "actived=1", null);
        ContentValues activeValue = new ContentValues();
        activeValue.put(Account.COLUMN_ACTIVED,1);
        getContentResolver().update(Account.CONTENT_URI_ACCOUNTS, activeValue, "_id="+activeId, null);
    }


}
