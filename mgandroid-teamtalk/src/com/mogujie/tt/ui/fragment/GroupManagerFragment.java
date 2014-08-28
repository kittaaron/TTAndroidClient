
package com.mogujie.tt.ui.fragment;

import java.util.ArrayList;

import com.mogujie.tt.R;
import com.mogujie.tt.adapter.GroupManagerAdapter;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.entity.GroupManagerEntity;
import com.mogujie.tt.ui.activity.ContactFragmentActivity;
import com.mogujie.tt.ui.base.TTBaseFragment;
import com.mogujie.tt.widget.GroupManagerGridView;
import com.mogujie.tt.widget.GroupManagerGridView.OnTouchBlankPositionListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class GroupManagerFragment extends TTBaseFragment implements OnTouchBlankPositionListener {
    private Integer[] mThumbIds = {
            R.drawable.tt_default_user_portrait_corner, R.drawable.tt_default_user_portrait_corner,
            R.drawable.tt_default_user_portrait_corner, R.drawable.tt_default_user_portrait_corner
    };
    private GroupManagerGridView gridView = null;
    private static GroupManagerAdapter adapter = null;
    private View curView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (null != curView) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_group_manage, topContentView);

        initRes();

        return curView;
    }

    /**
     * @Description 初始化资源
     */
    private void initRes() {
        // 设置标题栏
        setTopTitle("蘑菇街IM(108)");
        setTopLeftButton(R.drawable.tt_top_back);
        setTopLeftText(getActivity().getString(R.string.top_left_back));
        topLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        // 设置其它页面信息
        gridView = (GroupManagerGridView) curView.findViewById(R.id.group_manager_grid);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉点击时的黄色背影
        // 绑定数据源
        ArrayList<GroupManagerEntity> dataList = new ArrayList<GroupManagerEntity>();
        for (int i = 0; i < mThumbIds.length; i++) {
            dataList.add(new GroupManagerEntity(mThumbIds[i], "姓名" + i));
        }

        adapter = new GroupManagerAdapter(getActivity(), dataList);
        gridView.setAdapter(adapter);

        // 点击空白地方时处理
        ((GroupManagerGridView) gridView).setOnTouchBlankPositionListener(this);

        gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                    int position, long id) {
                if (position < adapter.getCount() - 1 && adapter.getCount() > 3) {
                    adapter.setRemoveState(true);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                    long id) {
                if (adapter.getRemoveState()) {
                    if (adapter.getCount() > 3) {
                        adapter.remove(position);
                    }
                    if (adapter.getCount() <= 3) {
                        adapter.setRemoveState(false);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (position == adapter.getAddFriendPosition()) {
                        Intent intent=new Intent(getActivity(),ContactFragmentActivity.class);
                        intent.putExtra(SysConstant.CHOOSE_CONTACT, true);
                        startActivityForResult(intent, SysConstant.GROUP_MANAGER_ADD_RESULT);
                        //adapter.add();
                    }
                }
            }
        });
    }
    
    public static void addUser(GroupManagerEntity user){
        adapter.add(user);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initHandler() {
    }

    @Override
    public boolean onTouchBlankPosition() {
        adapter.setRemoveState(false);
        adapter.notifyDataSetChanged();
        return false;
    }
}
