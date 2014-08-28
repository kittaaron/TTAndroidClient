
package com.mogujie.tt.adapter;

import java.text.ParseException;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mogujie.tt.R;
import com.mogujie.tt.biz.ContactHelper;
import com.mogujie.tt.entity.RecentInfo;
import com.mogujie.tt.entity.User;
import com.mogujie.tt.log.Logger;
import com.mogujie.widget.imageview.MGWebImageView;

/**
 * 
 * @Description 联系人列表适配器
 */
@SuppressLint("ResourceAsColor")
public class ChatAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private static Logger logger = Logger.getLogger(ChatAdapter.class);

    public ChatAdapter(Context context) throws ParseException {
        this.mInflater = LayoutInflater.from(context);
    }

    /*
     * 获得当前选中的联系人用户信息
     */
    public User pullUser(int position) {
        RecentInfo item = getItem(position); // 当前单击的联系人
        if (null == item) {
            return null;
        }
        User targetUser = new User(); // 设置选中的联系人信息
        targetUser.setUserId(item.getUserId());
        targetUser.setName(item.getUserName());
        targetUser.setNickName(item.getNickName());
        targetUser.setAvatarUrl(item.getUserAvatar());
        return targetUser; // 返回选中的联系人
    }

    @Override
    public int getCount() {
        if (null == ContactHelper.getRecentInfoList()) {
            return 0;
        } else {
            return ContactHelper.getRecentInfoList().size();
        }
    }

    @Override
    public RecentInfo getItem(int position) {
        if (position >= getCount() || position < 0) {
            return null;
        }
        return ContactHelper.getRecentInfoList().get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position >= getCount() && getCount() > 0) {
            return getCount() - 1;
        } else if (position < 0) {
            return 0;
        }
        return position;
    }

    public final class ContactViewHolder {
        public MGWebImageView avatar;
        public TextView uname;
        public TextView lastContent;
        public TextView lastTime;
        public TextView msgCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            // 设置holder信息
            ContactViewHolder holder = null;
            if (null == convertView && null != mInflater) {
                convertView = mInflater.inflate(R.layout.tt_item_chat, null);
                if (null != convertView) {
                    holder = new ContactViewHolder();
                    holder.avatar = (MGWebImageView) convertView
                            .findViewById(R.id.contact_portrait);
                    holder.uname = (TextView) convertView.findViewById(R.id.shop_name);
                    holder.lastContent = (TextView) convertView
                            .findViewById(R.id.message_body);
                    holder.lastTime = (TextView) convertView
                            .findViewById(R.id.message_time);
                    holder.msgCount = (TextView) convertView
                            .findViewById(R.id.message_count_notify);
                    convertView.setTag(holder);
                }

            } else {

                holder = (ContactViewHolder) convertView.getTag();
            }
            if (null == holder) {
                return null;
            }
            // 获取信息
            List<RecentInfo> recentInfoList = ContactHelper.getSortedRecentInfoList();
            String avatarUrl = null;
            String userName = "";
            String lastContent = "";
            String lastTime = "";
            int backgroundResource = 0;
            int unReadCount = 0;
            if (null != recentInfoList && recentInfoList.size() != 0
                    && position < recentInfoList.size() && position >= 0) {
                userName = recentInfoList.get(position).getUserName();
                lastContent = recentInfoList.get(position).getLastContent();
                lastTime = recentInfoList.get(position)
                        .getLastTimeString();
                if (10 > recentInfoList.get(position).getUnreadCount()) {
                    backgroundResource = R.drawable.tt_message_notify_single;
                } else {
                    backgroundResource = R.drawable.tt_message_notify_double;
                }
                unReadCount = recentInfoList.get(position).getUnreadCount();
                avatarUrl = recentInfoList.get(position).getUserAvatar();
            }

            // 设置未读消息计数
            if (unReadCount > 0) {
                holder.msgCount
                        .setBackgroundResource(backgroundResource);
                holder.msgCount.setVisibility(View.VISIBLE);
                holder.msgCount.setText(String.valueOf(unReadCount));
            } else {
                holder.msgCount.setVisibility(View.GONE);
            }
            // 设置头像
            if (null == avatarUrl||!avatarUrl.contains("http")) {
                holder.avatar
                        .setImageResource(R.drawable.tt_default_user_portrait_corner);
            } else {
                holder.avatar.setImageUrlNeedFit(avatarUrl);
            }
            // 设置其它信息
            holder.uname.setText(userName);
            holder.lastContent.setText(lastContent);
            holder.lastTime.setText(lastTime);

            this.notifyDataSetChanged();
            return convertView;
        } catch (Exception e) {
            logger.e(e.getMessage());
            return null;
        }
    }
}
