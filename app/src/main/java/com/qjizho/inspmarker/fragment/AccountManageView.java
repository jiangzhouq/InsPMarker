package com.qjizho.inspmarker.fragment;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nomad.instagramlogin.InstaLogin;
import com.qjizho.inspmarker.R;
import com.qjizho.inspmarker.activity.MainActivity;
import com.qjizho.inspmarker.db.Account;

import java.util.List;
import java.util.Map;

import in.srain.cube.app.CubeFragment;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;

/**
 * Created by qjizho on 15-7-13.
 */
public class AccountManageView extends MyTitleBaseFragment{
    private ImageLoader mImageLoader;
    private MyAdapter myAdapter;
    private AccountsChangeListener mAccountsChangeListener;
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {

        super.createView(inflater, viewGroup, bundle);
        final View view = inflater.inflate(R.layout.account_manager, null);
        mImageLoader = ImageLoaderFactory.create(getActivity());
        mAccountsChangeListener = (AccountsChangeListener) getActivity();
        ImageButton accountAddBtn = (ImageButton) view.findViewById(R.id.account_plus);
        accountAddBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                InstaLogin instaLogin = new InstaLogin(getContext(),
                        "c4d946f3dc8a43699aeb7c57b5cbc12d",
                        "6aba840c8c984aadbae55bad66c5eab3",
                        "https://loggedinbaby");

                instaLogin.login();
            }
        });
        SwipeMenuListView swipeListView = (SwipeMenuListView) view.findViewById(R.id.example_lv_list);


        myAdapter = new MyAdapter();
        swipeListView.setAdapter(myAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
//                SwipeMenuItem openItem = new SwipeMenuItem(
//                        getContext());
//                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                        0xCE)));
//                // set item width
//                openItem.setWidth(dp2px(90));
//                // set item title
//                openItem.setTitle("Open");
//                // set item title fontsize
//                openItem.setTitleSize(18);
//                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        swipeListView.setMenuCreator(creator);
        // step 2. listener item click event
        swipeListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                ApplicationInfo item = mAppList.get(position);
                switch (index) {
                    case 0:
                        // open
//                        open(item);
                        deleteAccount(position);
                        break;
//                    case 1:
                        // delete
//                        mAppList.remove(position);
//                        mAdapter.notifyDataSetChanged();
//                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        swipeListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        // other setting
//		listView.setCloseInterpolator(new BounceInterpolator());
        swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView accountName = (TextView) view.findViewById(R.id.account_name);

                pushFragment(accountName.getText().toString());
            }
        });
        // test item long click
        swipeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
//                Toast.makeText(getContext(), position + " long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return view;
    }

    private void pushFragment(String name){

        Cursor cur = getContext().getContentResolver().query(Account.CONTENT_URI_ACCOUNTS, null, Account.COLUMN_USERNAME + "='" + name + "'", null, null);
        if(cur.moveToFirst()){
            Bundle fgBundle = new Bundle();
            changeActived(cur.getString(Account.NUM_COLUMN_ID));
            fgBundle.putString("id", cur.getString(Account.NUM_ACCOUNT_ID));
            fgBundle.putString("token", cur.getString(Account.NUM_ACCESS_TOKEN));

            android.support.v4.app.FragmentManager fm = getContext().getSupportFragmentManager();
            CubeFragment fragment = (CubeFragment) fm.findFragmentByTag(FeedGridView.class.toString());
            fragment.onEnter(fgBundle);
            fragment.onResume();
            getContext().goToFragment(FeedGridView.class, fgBundle);
            
//            fragment.onEnter(fgBundle);
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.show(fragment);
//            ft.commitAllowingStateLoss();

//            getContext().goToFragment(FeedGridView.class, fgBundle);
//            getContext().popTopFragment(fgBundle);
//            getContext().popTopFragment(null);
//            getContext().popToRoot(null);
        }
    }

    private void changeActived(String activeId){
        ContentValues updateValue = new ContentValues();
        updateValue.put(Account.COLUMN_ACTIVED, 0);
        getContext().getContentResolver().update(Account.CONTENT_URI_ACCOUNTS, updateValue, "actived=1", null);
        ContentValues activeValue = new ContentValues();
        activeValue.put(Account.COLUMN_ACTIVED, 1);
        getContext().getContentResolver().update(Account.CONTENT_URI_ACCOUNTS, activeValue, "_id="+activeId, null);
    }


    public class ViewHolder {
        CubeImageView img;
        TextView text;
    }
    class MyAdapter extends BaseAdapter{
        Cursor cur;
        MyAdapter(){

        }
        @Override
        public int getCount() {
            cur = getContext().getContentResolver().query(Account.CONTENT_URI_ACCOUNTS,null,null,null,null);
            return cur.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            cur.moveToPosition(position);
            ViewHolder holder = new ViewHolder();
            if(convertView == null){
                convertView = getContext().getLayoutInflater().inflate(R.layout.account_listview_item,null,false);
                holder.img = (CubeImageView) convertView.findViewById(R.id.account_pic);
                holder.text = (TextView) convertView.findViewById(R.id.account_name);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.img.loadImage(mImageLoader,cur.getString(cur.getColumnIndex(Account.COLUMN_PROFILE_PICTURE)));
            holder.text.setText(cur.getString(cur.getColumnIndex(Account.COLUMN_USERNAME)));
            return convertView;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    private void deleteAccount(int position){
        Cursor cur = getContext().getContentResolver().query(Account.CONTENT_URI_ACCOUNTS, null, null, null, null);
        cur.moveToPosition(position);
        getContext().getContentResolver().delete(Account.CONTENT_URI_ACCOUNTS, Account.COLUMN_USERNAME + "='" + cur.getString(Account.NUM_USERNAME) + "'", null);
        cur = getContext().getContentResolver().query(Account.CONTENT_URI_ACCOUNTS, null, "actived='1'", null, null);
        if(!cur.moveToNext()){
            cur = getContext().getContentResolver().query(Account.CONTENT_URI_ACCOUNTS, null, null, null, null);
            if(cur.moveToNext()){
                String activeId = cur.getString(Account.NUM_COLUMN_ID);
                ContentValues updateValue = new ContentValues();
                ContentValues activeValue = new ContentValues();
                activeValue.put(Account.COLUMN_ACTIVED, 1);
                getContext().getContentResolver().update(Account.CONTENT_URI_ACCOUNTS, activeValue, "_id="+activeId, null);
            }
        }
        myAdapter.notifyDataSetChanged();
        mAccountsChangeListener.onAccountsChanged();
    }
    public interface AccountsChangeListener{
        public void onAccountsChanged();
    }
}
