
package com.mogujie.tt.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.mogujie.tt.R;
import com.mogujie.tt.biz.MessageNotifyCenter;
import com.mogujie.tt.cache.biz.CacheHub;
import com.mogujie.tt.config.HandlerConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.conn.NetStateDispach;
import com.mogujie.tt.widget.NaviTabButton;

public class MainActivity extends FragmentActivity {
    private static Handler uiHandler = null;// 处理界面消息
    
    private Fragment[] mFragments;
    private NaviTabButton[] mTabButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tt_activity_main);

        initTab();
        initFragment();
        setFragmentIndicator(0);

        initHandler();
        registEvents();
    }

    public static Handler getUiHandler() {
        return uiHandler;
    }
    
    private void initFragment() {
        mFragments = new Fragment[4];
        mFragments[0] = getSupportFragmentManager().findFragmentById(
                R.id.fragment_chat);
        mFragments[1] = getSupportFragmentManager().findFragmentById(
                R.id.fragment_contact);
        mFragments[2] = getSupportFragmentManager().findFragmentById(
                R.id.fragment_internal);
        mFragments[3] = getSupportFragmentManager().findFragmentById(
                R.id.fragment_my);
    }

    private void initTab() {
        mTabButtons = new NaviTabButton[4];

        mTabButtons[0] = (NaviTabButton) findViewById(R.id.tabbutton_chat);
        mTabButtons[1] = (NaviTabButton) findViewById(R.id.tabbutton_contact);
        mTabButtons[2] = (NaviTabButton) findViewById(R.id.tabbutton_internal);
        mTabButtons[3] = (NaviTabButton) findViewById(R.id.tabbutton_my);

        mTabButtons[0].setTitle(getString(R.string.main_chat));
        mTabButtons[0].setIndex(0);
        mTabButtons[0].setSelectedImage(getResources().getDrawable(
                R.drawable.tt_tab_chat_sel));
        mTabButtons[0].setUnselectedImage(getResources().getDrawable(
                R.drawable.tt_tab_chat_nor));

        mTabButtons[1].setTitle(getString(R.string.main_contact));
        mTabButtons[1].setIndex(1);
        mTabButtons[1].setSelectedImage(getResources().getDrawable(
                R.drawable.tt_tab_contact_sel));
        mTabButtons[1].setUnselectedImage(getResources().getDrawable(
                R.drawable.tt_tab_contact_nor));

        mTabButtons[2].setTitle(getString(R.string.main_innernet));
        mTabButtons[2].setIndex(2);
        mTabButtons[2].setSelectedImage(getResources().getDrawable(
                R.drawable.tt_tab_innernet_sel));
        mTabButtons[2].setUnselectedImage(getResources().getDrawable(
                R.drawable.tt_tab_innernet_nor));

        mTabButtons[3].setTitle(getString(R.string.main_me_tab));
        mTabButtons[3].setIndex(3);
        mTabButtons[3].setSelectedImage(getResources().getDrawable(
                R.drawable.tt_tab_me_sel));
        mTabButtons[3].setUnselectedImage(getResources().getDrawable(
                R.drawable.tt_tab_me_nor));
    }

    public void setFragmentIndicator(int which) {
        getSupportFragmentManager().beginTransaction().hide(mFragments[0])
                .hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3])
                .show(mFragments[which]).commit();

        mTabButtons[0].setSelectedButton(false);
        mTabButtons[1].setSelectedButton(false);
        mTabButtons[2].setSelectedButton(false);
        mTabButtons[3].setSelectedButton(false);

        mTabButtons[which].setSelectedButton(true);
    }
    
    @SuppressLint("HandlerLeak")
    private void initHandler() {
        uiHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HandlerConstant.HANDLER_CONTACTS_NEW_MESSAGE_COME:
                        showUnreadMessageCount();
                        break;
                    default:
                        break;
                }
            }
        };
    }
   
    private void showUnreadMessageCount() {
        mTabButtons[0].setUnreadNotify(CacheHub.getInstance().getUnreadCount());
    }
    
    /**
     * @Description 注册事件
     */
    private void registEvents() {
        NetStateDispach.getInstance().register(this.getClass(), uiHandler);
        // 未读消息通知
        MessageNotifyCenter.getInstance().register(SysConstant.EVENT_UNREAD_MSG, uiHandler,
                HandlerConstant.HANDLER_CONTACTS_NEW_MESSAGE_COME);
    }

    /**
     * @Description 取消事件注册
     */
    private void unRegistEvents() {
        MessageNotifyCenter.getInstance().unregister(SysConstant.EVENT_UNREAD_MSG, getUiHandler(),
                HandlerConstant.HANDLER_CONTACTS_NEW_MESSAGE_COME);
        return;
    }

    public void btnChatClick(View v) {
        setFragmentIndicator(0);
    }

    public void btnContactClick(View v) {
        setFragmentIndicator(1);
    }

    public void btnInternalClick(View v) {
        setFragmentIndicator(2);
    }

    public void btnMyClick(View v) {
        setFragmentIndicator(3);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unRegistEvents();
        super.onDestroy();
    }
    
    
}
