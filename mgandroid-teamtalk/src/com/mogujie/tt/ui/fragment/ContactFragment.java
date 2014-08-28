
package com.mogujie.tt.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mogujie.tt.R;
import com.mogujie.tt.adapter.ContactAllAdapter;
import com.mogujie.tt.adapter.ContactBaseAdapter;
import com.mogujie.tt.adapter.ContactDepartmentAdapter;
import com.mogujie.tt.config.HandlerConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.entity.ContactSortEntity;
import com.mogujie.tt.entity.GroupManagerEntity;
import com.mogujie.tt.ui.base.TTBaseFragment;
import com.mogujie.tt.utils.CharacterParser;
import com.mogujie.tt.utils.SortComparator;
import com.mogujie.tt.widget.SearchEditText;
import com.mogujie.tt.widget.SortSideBar;
import com.mogujie.tt.widget.SortSideBar.OnTouchingLetterChangedListener;

public class ContactFragment extends TTBaseFragment implements OnTouchingLetterChangedListener,
        OnItemClickListener {
    private View curView = null;
    private static Handler uiHandler = null;
    private ListView allContactListView;
    private ListView departmentContactListView;
    private SortSideBar sortSideBar;
    private TextView dialog;
    private ContactAllAdapter allAdapter;
    private ContactDepartmentAdapter departmentAdapter;
    private SearchEditText searchEditText;

    private CharacterParser characterParser;
    private List<ContactSortEntity> SourceDateList;

    private SortComparator sortComparator;
    private int curTabIndex = 0;
    private boolean chooseMode = false;// 用于控制通讯录是否用于添加群组联系人

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initHandler();

        setChooseMode(getActivity().getIntent());
    }

    private void setChooseMode(Intent data) {
        if (null != data) {
            chooseMode = data.getBooleanExtra(SysConstant.CHOOSE_CONTACT,
                    false);
        } else {
            chooseMode = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (null != curView) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_contact, topContentView);

        initRes();

        return curView;
    }

    /**
     * @Description 初始化界面资源
     */
    private void initRes() {
        // 设置顶部标题栏
        showContactTopBar();
        if (chooseMode) {
            setTopLeftButton(R.drawable.tt_top_back);
            setTopLeftText(getActivity().getString(R.string.top_left_back));
            topLeftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }

        sortSideBar = (SortSideBar) curView.findViewById(R.id.sidrbar);
        dialog = (TextView) curView.findViewById(R.id.dialog);
        sortSideBar.setTextView(dialog);
        sortSideBar.setOnTouchingLetterChangedListener(this);

        allContactListView = (ListView) curView.findViewById(R.id.all_contact_list);
        allContactListView.setOnItemClickListener(this);
        departmentContactListView = (ListView) curView.findViewById(R.id.department_contact_list);
        departmentContactListView.setOnItemClickListener(this);

        characterParser = CharacterParser.getInstance();
        sortComparator = new SortComparator();

        SourceDateList = filledData(getResources().getStringArray(R.array.data));
        Collections.sort(SourceDateList, sortComparator);
        allAdapter = new ContactAllAdapter(getActivity(), SourceDateList);
        departmentAdapter = new ContactDepartmentAdapter(getActivity(), SourceDateList);

        allContactListView.setAdapter(allAdapter);
        departmentContactListView.setAdapter(departmentAdapter);

        searchEditText = (SearchEditText) curView.findViewById(R.id.filter_edit);

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 为ListView填充数据
     * 
     * @param date
     * @return
     */
    private List<ContactSortEntity> filledData(String[] date) {
        List<ContactSortEntity> mSortList = new ArrayList<ContactSortEntity>();

        for (int i = 0; i < date.length; i++) {
            ContactSortEntity sortModel = new ContactSortEntity();
            sortModel.setName(date[i]);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * 
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<ContactSortEntity> filterDateList = new ArrayList<ContactSortEntity>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (ContactSortEntity sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.toUpperCase().indexOf(
                        filterStr.toString().toUpperCase()) != -1
                        || characterParser.getSelling(name).toUpperCase()
                                .startsWith(filterStr.toString().toUpperCase())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, sortComparator);
        getCurAdapter().updateListView(filterDateList);
    }

    private ContactBaseAdapter getCurAdapter() {
        if (0 == curTabIndex) {
            return allAdapter;
        } else {
            return departmentAdapter;
        }
    }

    private ListView getCurListView() {
        if (0 == curTabIndex) {
            return allContactListView;
        } else {
            return departmentContactListView;
        }
    }

    public static Handler getHandler() {
        return uiHandler;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initHandler() {
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HandlerConstant.HANDLER_CHANGE_CONTACT_TAB:
                        if (null != msg.obj) {
                            curTabIndex = (Integer) msg.obj;
                            if (0 == curTabIndex) {
                                allContactListView.setVisibility(View.VISIBLE);
                                departmentContactListView.setVisibility(View.GONE);
                            } else {
                                departmentContactListView.setVisibility(View.VISIBLE);
                                allContactListView.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
            }
        };
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = getCurAdapter().getPositionForSection(s.charAt(0));
        if (position != -1) {
            getCurListView().setSelection(position);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (chooseMode) {
            GroupManagerEntity user = new GroupManagerEntity(R.drawable.tt_group_manager_add_user,
                    ((ContactSortEntity) getCurAdapter().getItem(position)).getName());
            Bundle bundle = new Bundle();
            bundle.putSerializable(SysConstant.OBJECT_PARAM, user);
            getActivity().setResult(Activity.RESULT_OK, new Intent().putExtras(bundle));
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(),
                    ((ContactSortEntity) getCurAdapter().getItem(position)).getName(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
