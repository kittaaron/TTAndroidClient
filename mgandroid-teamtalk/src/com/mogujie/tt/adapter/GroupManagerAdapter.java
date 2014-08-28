
package com.mogujie.tt.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mogujie.tt.R;
import com.mogujie.tt.entity.GroupManagerEntity;

public class GroupManagerAdapter extends BaseAdapter {
    private ArrayList<GroupManagerEntity> dataList;
    private Context context = null;
    private boolean removeState = false;// 用于控制是否是删除状态
    private GroupHolder holder = null;

    public GroupManagerAdapter(Context c, ArrayList<GroupManagerEntity> data) {
        this.dataList = data;
        this.context = c;
        // 添加按钮
        dataList.add(new GroupManagerEntity(R.drawable.tt_group_manager_add_user, null));
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        dataList.remove(position);
        this.notifyDataSetChanged();
    }

    public void add() {
        dataList.add(getAddFriendPosition(), new GroupManagerEntity(
                R.drawable.tt_default_user_portrait_corner,
                "姓名"));
        this.notifyDataSetChanged();
    }

    public void add(GroupManagerEntity user) {
        dataList.add(getAddFriendPosition(), new GroupManagerEntity(
                R.drawable.tt_default_user_portrait_corner,user.getName()));
        this.notifyDataSetChanged();
    }

    public int getAddFriendPosition() {
        return dataList.size() - 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tt_group_manage_grid_item, null);
        }
        initHolder(convertView);

        setHolder(dataList.get(position));

        return convertView;
    }

    private void setHolder(GroupManagerEntity item) {
        if (null != item && null != holder) {
            holder.imageView.setAdjustViewBounds(false);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imageView.setImageResource(item.getHeadId());
            holder.userTitle.setText(item.getName());
            holder.imageView.setVisibility(View.VISIBLE);
            holder.userTitle.setVisibility(View.VISIBLE);
            if (removeState) {
                holder.deleteImg.setVisibility(View.VISIBLE);
                if (item.getHeadId() == R.drawable.tt_group_manager_add_user) {
                    holder.imageView.setVisibility(View.INVISIBLE);
                    holder.userTitle.setVisibility(View.INVISIBLE);
                    holder.deleteImg.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.deleteImg.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initHolder(View convertView) {
        holder = new GroupHolder();
        holder.imageView = (ImageView) convertView
                .findViewById(R.id.grid_item_image);
        holder.userTitle = (TextView) convertView
                .findViewById(R.id.group_manager_user_title);
        holder.deleteImg = convertView.findViewById(R.id.deleteLayout);
    }

    final class GroupHolder {
        ImageView imageView;
        TextView userTitle;
        View deleteImg;
    }

    public void setRemoveState(boolean remove) {
        removeState = remove;
    }

    public boolean getRemoveState() {
        return removeState;
    }
}
