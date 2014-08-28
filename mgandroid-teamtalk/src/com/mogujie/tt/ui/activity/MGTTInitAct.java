
package com.mogujie.tt.ui.activity;

import android.content.Context;

import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;

import android.os.Message;

import android.text.TextUtils;

import android.view.KeyEvent;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.mogujie.tt.R;
import com.mogujie.tt.app.IMEntrance;
import com.mogujie.tt.config.HandlerConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.conn.LoginManager;
import com.mogujie.tt.conn.NetStateDispach;
import com.mogujie.tt.ui.base.TTBaseActivity;
import com.mogujie.tt.utils.NetworkUtil;

public class MGTTInitAct extends TTBaseActivity {

    protected static Handler uiHandler = null;

    private EditText mNameView;

    private EditText mPasswordView;

    @SuppressWarnings("unused")
    private View mLoginFormView;

    private View mLoginStatusView;

    public static Context instance = null;

    public static Handler getUiHandler() {

        return uiHandler;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        IMEntrance.getInstance().setContext(MGTTInitAct.this);

        initHandler();

        setContentView(R.layout.tt_activity_login);

        instance = this;

        mNameView = (EditText) findViewById(R.id.name);
        mNameView.setText("nana");

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText("123456");

        mPasswordView

                .setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView textView, int id,

                            KeyEvent keyEvent) {

                        if (id == R.id.login || id == EditorInfo.IME_NULL) {

                            attemptLogin();

                            return true;

                        }

                        return false;

                    }

                });

        mLoginFormView = findViewById(R.id.login_form);

        mLoginStatusView = findViewById(R.id.login_status);

        findViewById(R.id.sign_in_button).setOnClickListener(

                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (NetworkUtil.isNetWorkAvalible(MGTTInitAct.this)) {

                            attemptLogin();

                        } else {
                            mPasswordView.setError(getString(R.string.invalid_network));
                            mPasswordView.requestFocus();
                        }

                    }

                });
    }

    public void attemptLogin() {

        // mNameView.setError(null);
        //
        // mPasswordView.setError(null);

        String mName = mNameView.getText().toString();

        String mPassword = mPasswordView.getText().toString();

        boolean cancel = false;

        View focusView = null;

        if (TextUtils.isEmpty(mPassword)) {

            mPasswordView.setError(getString(R.string.error_field_required));

            focusView = mPasswordView;

            cancel = true;

        } else if (mPassword.length() < 4) {

            mPasswordView.setError(getString(R.string.error_invalid_password));

            focusView = mPasswordView;

            cancel = true;

        }

        if (TextUtils.isEmpty(mName)) {

            mNameView.setError(getString(R.string.error_field_required));

            focusView = mNameView;

            cancel = true;

        }

        if (cancel) {

            focusView.requestFocus();

        } else {

            showProgress(true);
            mPasswordView.setFocusable(false);
            mNameView.setFocusable(false);
            IMEntrance.getInstance().initTask(this, mName, mPassword);

        }

    }

    private void showProgress(final boolean show) {
        if (show) {
            mLoginStatusView.setVisibility(View.VISIBLE);
        } else {
            mLoginStatusView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            MGTTInitAct.this.finish();

            return true;

        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void initHandler() {

        uiHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);

                switch (msg.what) {
                    case HandlerConstant.HANDLER_LOGIN_MSG_SERVER:
                        onLoginSuccess();
                        break;
                    case HandlerConstant.HANDLER_LOGIN_MSG_SERVER_FAILED:
                        onLoginFailed(getString(R.string.login_failed));
                        break;
                    case HandlerConstant.HANDLER_LOGIN_MSG_SERVER_TIMEOUT:
                        onLoginFailed(getString(R.string.login_timeout));
                        break;
                    default:
                        onLoginFailed(getString(R.string.error_incorrect_user));
                        break;
                }
            }
        };
    }

    private void onLoginFailed(String tip) {
        mPasswordView.setError(tip);
        mPasswordView.requestFocus();
        mLoginStatusView.setVisibility(View.GONE);
    }

    private void onLoginSuccess() {
        if (LoginManager.getInstance().isLogined()) {
            Intent i = new Intent();

            i.setAction(SysConstant.START_SERVICE_ACTION);

            MGTTInitAct.this.sendBroadcast(i);

            Intent intent = new Intent(MGTTInitAct.this, MainActivity.class);

            startActivity(intent);

            MGTTInitAct.this.finish();
        } else {
            onLoginFailed(getString(R.string.error_incorrect_user));
        }

    }

    @Override
    protected void onStop() {

        NetStateDispach.getInstance().unregister(MGTTInitAct.class);

        super.onStop();

    }

}
