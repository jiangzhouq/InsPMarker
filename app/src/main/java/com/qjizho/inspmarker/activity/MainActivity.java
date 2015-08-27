package com.qjizho.inspmarker.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.nomad.instagramlogin.InstaLogin;
import com.nomad.instagramlogin.Keys;
import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.db.Account;
import com.qjizho.inspmarker.fragment.AccountManageView;
import com.qjizho.inspmarker.fragment.FeedGridView;
import com.qjizho.inspmarker.fragment.FollowsGridView;
import com.qjizho.inspmarker.fragment.FragmentLogin;

import in.srain.cube.app.CubeFragment;
import in.srain.cube.mints.base.MintsBaseActivity;

public class MainActivity extends MintsBaseActivity {
    private static final int PROFILE_SETTING = 99;
    private static final int PROFILE_MANAGER = 100;
    private AccountHeader headerResult = null;
    private Drawer result = null;

    @Override
    protected void onResume() {
//        popToRoot(null);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final IProfile profile = new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460");
//        final IProfile profile2 = new ProfileDrawerItem().withName("Bernat Borras").withEmail("alorma@github.com").withIcon(Uri.parse("https://avatars3.githubusercontent.com/u/887462?v=3&s=460"));
//        final IProfile profile3 = new ProfileDrawerItem().withName("Max Muster").withEmail("max.mustermann@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460");
//        final IProfile profile4 = new ProfileDrawerItem().withName("Felix House").withEmail("felix.house@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460");
//        final IProfile profile5 = new ProfileDrawerItem().withName("Mr. X").withEmail("mister.x.super@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460").withIdentifier(4);
//        final IProfile profile6 = new ProfileDrawerItem().withName("Batman").withEmail("batman@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460");


        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
//                        profile,
//                        profile2,
//                        profile3,
//                        profile4,
//                        profile5,
//                        profile6,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if(! (profile instanceof IDrawerItem) ){
                            return false;
                        }
                        if (((IDrawerItem) profile).getIdentifier() == PROFILE_SETTING) {
                            InstaLogin instaLogin = new InstaLogin(MainActivity.this,
                                    "c4d946f3dc8a43699aeb7c57b5cbc12d",
                                    "6aba840c8c984aadbae55bad66c5eab3",
                                    "https://loggedinbaby");

                            instaLogin.login();
//                            pushFragmentToBackStack(FragmentLogin.class,null);
//                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman").withEmail("batman@gmail.com").withIcon(getResources().getDrawable(R.mipmap.ic_launcher));
//                            if (headerResult.getProfiles() != null) {
//                                //we know that there are 2 setting elements. set the new profile above them ;)
//                                headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
//                            } else {
//                                headerResult.addProfiles(newProfile);
//                            }
                        }else if(((IDrawerItem) profile).getIdentifier() == PROFILE_MANAGER){
//                            popTopFragment(null);
                            pushFragmentToBackStack(AccountManageView.class,null);
                        }
                        else{
                            Log.d("qiqi", "item clicked:" + ((IDrawerItem) profile).getIdentifier());
                            pushFragment(((IDrawerItem) profile).getIdentifier());
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
//        Cursor cur = getContentResolver().query(Account.CONTENT_URI_ACCOUNTS,null,"actived=1",null,null);
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
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_simple_fragment_drawer).withIcon(GoogleMaterial.Icon.gmd_style).withIdentifier(6).withCheckable(false),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_embedded_drawer_dualpane).withIcon(GoogleMaterial.Icon.gmd_battery_charging_full).withIdentifier(7).withCheckable(false),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_fullscreen_drawer).withIcon(GoogleMaterial.Icon.gmd_style).withIdentifier(8).withCheckable(false),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom_container_drawer).withIcon(GoogleMaterial.Icon.gmd_my_location).withIdentifier(9).withCheckable(false),
//                        new PrimaryDrawerItem().withName(R.string.drawer_with_menu).withIcon(GoogleMaterial.Icon.gmd_list).withIdentifier(10).withCheckable(false),
//                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
//                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_github).withIdentifier(20).withCheckable(false),
//                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(GoogleMaterial.Icon.gmd_format_color_fill).withIdentifier(11).withTag("Bullhorn"),
//                        new DividerDrawerItem(),
//                        new SwitchDrawerItem().withName("Switch").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Switch2").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new ToggleDrawerItem().withName("Toggle").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener)
                ) // add the items we want to use with our Drawer
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(false)
                .build();
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelectionByIdentifier(11, false);

            //set the active profile
//            headerResult.setActiveProfile();
        }
        loadAccounts();
    }

    private void loadAccounts(){
        while(headerResult.getProfiles().size() > 0){
            headerResult.removeProfile(0);
        }
        headerResult.addProfile(new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBarSize().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_SETTING), 0);
        headerResult.addProfile(new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(PROFILE_MANAGER),1);

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
            Bundle bundle = new Bundle();
            bundle.putString("id", activedCur.getString(Account.NUM_ACCOUNT_ID));
            bundle.putString("token", activedCur.getString(Account.NUM_ACCESS_TOKEN));
            FollowsGridView gridFragment = new FollowsGridView();
            gridFragment.setArguments(bundle);
            headerResult.setActiveProfile(Integer.parseInt(activedCur.getString(Account.NUM_COLUMN_ID)));

            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            CubeFragment fragment = (CubeFragment) fm.findFragmentByTag(FeedGridView.class.toString());
            if(fragment != null){
                fragment.onEnter(bundle);
                fragment.onResume();
//                goToFragment(FeedGridView.class, bundle);
            }else{
                pushFragmentToBackStack(FeedGridView.class, bundle);
            }



        }
        activedCur.close();


    }
    private void pushFragment(int colid){
        changeActived(colid);
        Cursor cur = getContentResolver().query(Account.CONTENT_URI_ACCOUNTS, null, "_id=" + colid, null, null);
        if(cur.getCount() > 0){
            cur.moveToFirst();
            Bundle fgBundle = new Bundle();
            fgBundle.putString("id", cur.getString(Account.NUM_ACCOUNT_ID));
            fgBundle.putString("token", cur.getString(Account.NUM_ACCESS_TOKEN));
//            popTopFragment(null);
            mCurrentFragment.onEnter(fgBundle);
            mCurrentFragment.onResume();
//            popTopFragment(null);
        }
    }
    @Override
    protected int getFragmentContainerId() {
        return R.id.frag;
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

    @Override
    protected String getCloseWarning() {
        return "Tap again!";
    }
}
