
package com.mogujie.tt.entity;

import java.util.Comparator;
import java.util.Date;

import android.text.TextUtils;

import com.mogujie.tt.R;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.utils.DateUtil;

/**
 * @author seishuchen
 */
@SuppressWarnings("rawtypes")
public class RecentInfo implements Comparator {

    // protected String selfUserId; // 当前用户id

    protected String avatar; // 最近联系人头像

    private int defaultAvatar = R.id.contact_portrait; // 最近联系人默认头像

    protected String uname; // 最近联系人姓名

    protected String userId; // 最近联系人的用户id

    private byte msgType; // 最近一条消息类型

    private int displayType = SysConstant.DISPLAY_TYPE_TEXT; // 消息展示类型

    private String lastContent; // 与最近联系人聊天的最近一条消息内容

    private long lasttime; // 与最近联系人聊天的最近一次时间

    private int unReadCount = 0; // 未读消息计数

    private Date date;

    private String nickname;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // public String getSelfUserId() {
    // return selfUserId;
    // }
    //
    // public void setSelfUserId(String selfUserId) {
    // this.selfUserId = selfUserId;
    // }

    public String getUserName() {
        return uname;
    }

    public void setUserName(String uname) {
        this.uname = uname;
    }

    public String getUserAvatar() {
        if (TextUtils.isEmpty(avatar) || avatar.trim().length() == 0) {
            return null;
            // return SysConstant.DETAULT_PORTRAIT_URL;
        }
        return avatar;
    }

    public void setUserAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getDefaultAvatar() {
        return defaultAvatar;
    }

    public void setDefaultAvatar(int defaultAvatar) {
        this.defaultAvatar = defaultAvatar;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgRenderType) {
        this.msgType = msgRenderType;
    }

    public int getDisplayType() {
        return displayType;
    }

    public void setDisplayType(int displayType) {
        this.displayType = displayType;
    }

    public String getLastContent() {
        return lastContent;
    }

    public void setLastContent(String lastContent) {
        this.lastContent = lastContent;
    }

    public long getLastTime() {
        return lasttime;
    }

    public String getLastTimeString() {
        if (0L == lasttime) {
            return " ";
        }
        date = new Date(lasttime);
        return DateUtil.getTimeDisplay(date);
    }

    public void setLastTime(long lasttime) {
        this.lasttime = lasttime;
    }

    public void setLastTime(Date date) {
        this.lasttime = date.getTime();
    }

    /*
     * 未读消息计数加1
     */
    public int incUnreadCount() {
        unReadCount += 1;
        return unReadCount;
    }

    /*
     * 未读消息计数减1
     */
    public int decUnreadCount() {
        unReadCount -= 1;
        return unReadCount;
    }

    public int getUnreadCount() {
        return unReadCount;
    }

    public String getUnreadCountString() {
        if (99 < unReadCount) {
            return "99+";
        }
        return unReadCount + "";
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    @Override
    public int compare(Object o1, Object o2) {
        RecentInfo recentInfo1 = (RecentInfo) o1;
        RecentInfo recentInfo2 = (RecentInfo) o2;

        // //特殊占位符判断，特殊占位符必需放在最后
        // if(SysConstant.MOGU_SPACE_HODER.equals(recentInfo1.getUserName())){
        // return -1;
        // }
        //
        // //特殊占位符判断，特殊占位符必需放在最后
        // if(SysConstant.MOGU_SPACE_HODER.equals(recentInfo2.getUserName())){
        // return 1;
        // }

        // 先按最近联系时间排序
        if (recentInfo1.getLastTime() < recentInfo2.getLastTime()) {
            return 1;
        }
        if (recentInfo1.getLastTime() > recentInfo2.getLastTime()) {
            return -1;
        }

        // // 再按userId排序
        // if (recentInfo1.getUserId().compareTo(recentInfo2.getUserId()) < 0) {
        // return 1;
        // }
        // if (recentInfo1.getUserId().compareTo(recentInfo2.getUserId()) > 0) {
        // return -1;
        // }
        return 0;
    }

    public void setNickName(String NickName) {
        nickname = NickName;
    }

    public String getNickName() {
        return nickname;
    }

}
