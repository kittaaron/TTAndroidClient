
package com.mogujie.tt.conn;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.mogujie.tt.app.IMEntrance;
import com.mogujie.tt.cache.biz.CacheHub;
import com.mogujie.tt.config.HandlerConstant;
import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.entity.User;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.PacketDistinguisher;
import com.mogujie.tt.packet.PacketSendMonitor;
import com.mogujie.tt.packet.SocketMessageQueue;
import com.mogujie.tt.packet.WaitingListMonitor;
import com.mogujie.tt.packet.action.ActionCallback;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.packet.biz.LoginPacket;
import com.mogujie.tt.packet.biz.LoginPacket.LoginResponse;
import com.mogujie.tt.packet.biz.MsgServerPacket.MsgServerResponse;
import com.mogujie.tt.socket.MoGuSocket;
import com.mogujie.tt.socket.SocketStateManager;
import com.mogujie.tt.task.TaskManager;
import com.mogujie.tt.task.biz.PushActionToQueueTask;
import com.mogujie.tt.ui.activity.MGTTInitAct;
import com.mogujie.tt.ui.activity.MessageActivity;

public class LoginManager {

    private String strMsgServIp1 = "";

    private String strMsgServIp2 = "";

    private int m_nMsgServPort;

    private boolean needRequset = true;

    private String strLoginServIp1 = ProtocolConstant.LOGIN_IP1;

    private String strLoginServIp2 = ProtocolConstant.LOGIN_IP2;

    private int nLoginServPort = ProtocolConstant.LOGIN_PORT;

    public static boolean userReady = false;

    private MoGuSocket socketMsg1, socketMsg2;

    private Handler loginHandler;

    private Logger logger = Logger.getLogger(LoginManager.class);

    private MoGuSocket socketLogin1 = null;

    private MoGuSocket socketLogin2 = null;

    public static Context context = null;

    private StateManager smInstance = StateManager.getInstance();

    private ReconnectManager rmInstance = ReconnectManager.getInstance();

    //private TokenManager tmInstance = TokenManager.getInstance();

    private SocketStateManager ssmInstance = SocketStateManager.getInstance();

    WaitingListMonitor waitingListMoniotr = new WaitingListMonitor(

            SysConstant.WAITING_LIST_MONITOR_INTERVAL);

    PacketSendMonitor packetSendMonitor = new PacketSendMonitor(

            SysConstant.DEFAULT_PACKET_SEND_MONTOR_INTERVAL);

    private static Lock lockLoginSock = new ReentrantLock();

    private static Lock lockMsgSock = new ReentrantLock();

    private String mUserName = "";

    private String mPassWord = "";

    private boolean bLogined = false;

    private LoginManager() {

        try {

            initHandler();

        } catch (Exception e) {

            e.printStackTrace();

            logger.e(e.getStackTrace().toString());

        }

    }

    private static class SingletonHolder {

        static LoginManager instance = new LoginManager();

    }

    public static LoginManager getInstance() {

        return SingletonHolder.instance;

    }

    public void doConnectMsgServ(Context mContext) {

        context = mContext;

        try {

            if (null != socketLogin1 && null != socketLogin1.getChannel())

            {

                socketLogin1.close();

            }

            if (null != socketLogin2 && null != socketLogin2.getChannel())

            {

                socketLogin2.close();

            }

            if (null != socketMsg1 && null != socketMsg1.getChannel())

            {

                socketMsg1.close();

            }

            if (null != socketMsg2 && null != socketMsg2.getChannel())

            {

                socketMsg2.close();

            }

        } catch (Exception e) {

            logger.e(e.getMessage());

        }

        if (!TextUtils.isEmpty(strMsgServIp1))

        {

            socketMsg1 = new MoGuSocket(strMsgServIp1, m_nMsgServPort);

            socketMsg1.start();

        }

        if (!TextUtils.isEmpty(strMsgServIp2) && null != strMsgServIp1

                && !strMsgServIp1.equals(strMsgServIp2))

        {

            socketMsg2 = new MoGuSocket(strMsgServIp2, m_nMsgServPort);

            socketMsg2.start();

        }

    }

    public void doLogin(Context mContext, String name, String password) {

        logger.d("doLogin");

        rmInstance.setLogining(true);

        context = mContext;

        mUserName = name;

        mPassWord = password;

        startConnectLoginServer();

    }

    public void doReconect(Context mContext) {

        logger.d("doReconect");

        context = mContext;

        bLogined = false;

        // 清理socket Queue

        SocketMessageQueue.getInstance().clearActionQueue();

        MoGuSocket socketLogin = ConnectionStore.getInstance().get(

                SysConstant.CONNECT_LOGIN_SERVER);

        if (null != socketLogin) {

            if (null != socketLogin.getChannel()) {

                socketLogin.close();

            }

            socketLogin = null;

        }

        MoGuSocket socketMsg = ConnectionStore.getInstance().get(SysConstant.CONNECT_MSG_SERVER);

        if (null != socketMsg)

        {

            if (null != socketMsg.getChannel())

            {

                socketMsg.close();

            }

            socketMsg = null;

        }

        smInstance.resetSockets();

        if (!"".equals(mUserName) && !"".equals(mPassWord)) {

            IMEntrance.getInstance().initTask(context, mUserName, mPassWord);

        }

    }

    public Handler getHandler() {

        return loginHandler;

    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {

        loginHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);

                switch (msg.what) {

                    case HandlerConstant.HANDLER_CONNECT_SUCESS:

                        String clientAddr = (String) msg.obj;

                        if ((clientAddr.contains(String

                                .valueOf(nLoginServPort))

                                && clientAddr.contains(strLoginServIp1)) ||

                                (clientAddr.contains(String

                                        .valueOf(nLoginServPort))

                                && clientAddr.contains(strLoginServIp2))) {
                            try {
                                lockLoginSock.lock();

                                requestMsgServer(clientAddr);

                            } catch (Exception e) {

                                logger.e(e.getMessage());

                            } finally {
                                lockLoginSock.unlock();
                            }

                        } else {
                            try {
                                lockMsgSock.lock();

                                connectMsgServer(clientAddr);

                            } catch (Exception e) {

                                logger.e(e.getMessage());

                            } finally {
                                lockMsgSock.unlock();
                            }

                        }

                        logger.d("HANDLER_CONNECT_SUCESS");

                        break;

                    case ProtocolConstant.SID_LOGIN * 1000

                            + ProtocolConstant.CID_LOGIN_RES_MSGSERVER:

                        onRequestMsgServer(msg.obj);

                        logger.d("REQUEST_MESSAGE_SERVER_SUCCESS");

                        break;

                    case HandlerConstant.REQUEST_MESSAGE_SERVER_FAILED:

                        // mbLogining = false;

                        rmInstance.setOnRecconnecting(false);

                        rmInstance.setLogining(false);

                        logger.d("请求MsgServer地址失败");

                        break;

                    case HandlerConstant.REQUEST_LOGIN_SUCCESS:

                    case ProtocolConstant.SID_LOGIN * 1000

                            + ProtocolConstant.CID_LOGIN_RES_USERLOGIN:

                        onConnectMsgServer(msg.obj);

                        logger.d("REQUEST_LOGIN_SUCCESS");

                        break;

                    case HandlerConstant.REQUEST_LOGIN_FAILED:

                        // 将socket State状态设置为

                        rmInstance.setOnRecconnecting(false);

                        rmInstance.setLogining(false);

                        // 失败有可能是token失效导致的

                        // 把之前的im token清空，重新换取token后登录

                       // tmInstance.setIMToken("");

                        // IMEntrance.getInstance()

                        // .initTask(context,

                        // CacheHub.getInstance().getLoginUserId());

                        logger.d("登陆消息服务器失败");

                        break;

                }

            }

        };

    }

    private void responseSuccessCallback(Packet packet) {

        if (null == packet || null == loginHandler)

            return;

        int serviceId = packet.getResponse().getHeader().getServiceId();

        int commandId = packet.getResponse().getHeader().getCommandId();

        Message msg = new Message();

        msg.what = serviceId * 1000 + commandId;

        msg.obj = packet;

        loginHandler.sendMessage(msg);

    }

    private void connectMsgServer(String address) {

        MoGuSocket socketLogin = ConnectionStore.getInstance().get(

                SysConstant.CONNECT_LOGIN_SERVER);

        if (null != socketLogin && null != socketLogin.getChannel())

        {

            socketLogin.close();

            ConnectionStore.getInstance().remove(SysConstant.CONNECT_LOGIN_SERVER);

        }

        MoGuSocket socketMsg = ConnectionStore.getInstance().get(SysConstant.CONNECT_MSG_SERVER);

        if (null == socketMsg) {

            if (address.contains(strMsgServIp1)) {

                socketMsg = socketMsg1;

            } else {

                socketMsg = socketMsg2;

            }

            ConnectionStore.getInstance().put(SysConstant.CONNECT_MSG_SERVER,

                    socketMsg);

        } else {

            if (address.contains(strMsgServIp1)) {

                socketMsg1.close();

            } else {

                socketMsg2.close();

            }

            logger.d("socketMsg has already exsits");

            return;

        }

        ssmInstance.setState(true);

        Object[] obj = new Object[5];

        obj[0] = mUserName;

        obj[1] = mPassWord;

        obj[2] = ProtocolConstant.ON_LINE;

        obj[3] = ProtocolConstant.CLIENT_TYPE;

        obj[4] = ProtocolConstant.CLIENT_VERSION;

        Packet packet = PacketDistinguisher.make(ProtocolConstant.SID_LOGIN,

                ProtocolConstant.CID_LOGIN_REQ_USERLOGIN, obj, true);

        ActionCallback callback = new ActionCallback() {

            @Override
            public void onSuccess(Packet packet) {

                responseSuccessCallback(packet);

            }

            @Override
            public void onTimeout(Packet packet) {
                Handler loginHandler = MGTTInitAct.getUiHandler();
                if (null != loginHandler) {
                    loginHandler.sendEmptyMessage(HandlerConstant.HANDLER_LOGIN_MSG_SERVER_FAILED);
                }

                rmInstance.setOnRecconnecting(false);

                rmInstance.setLogining(false);

            }

            @Override
            public void onFaild(Packet packet) {

                Handler loginHandler = MGTTInitAct.getUiHandler();
                if (null != loginHandler) {
                    loginHandler.sendEmptyMessage(HandlerConstant.HANDLER_LOGIN_MSG_SERVER_TIMEOUT);
                }

                rmInstance.setOnRecconnecting(false);

                rmInstance.setLogining(false);

            }

        };

        PushActionToQueueTask task = new PushActionToQueueTask(packet, callback);

        TaskManager.getInstance().trigger(task);

    }

    private void onConnectMsgServer(Object obj) {
        rmInstance.setOnRecconnecting(false);
        rmInstance.resetConnectCount();

        rmInstance.setLogining(false);

        LoginPacket packet = (LoginPacket) obj;

        int res = ((LoginResponse) (packet.getResponse())).getResult();

        if (res == 0)

        {
            LoginResponse loginRes = (LoginResponse) packet.getResponse();
            User user = loginRes.getUser();
            user.setName(mUserName);
            CacheHub.getInstance().setLoginUser(user);

            bLogined = true;

        } else {

            bLogined = false;

            logger.e("登陆msg_server失败");

           // TokenManager.getInstance().resetAll();

        }

        Handler handler = MessageActivity.getMsgHandler();
        if (null != handler) {
            handler.sendEmptyMessage(HandlerConstant.HANDLER_LOGIN_MSG_SERVER);
        }
        Handler loginHandler = MGTTInitAct.getUiHandler();
        if (null != loginHandler) {
            loginHandler.sendEmptyMessage(HandlerConstant.HANDLER_LOGIN_MSG_SERVER);
        }

    }

    public void startConnectLoginServer() {

        socketLogin1 = new MoGuSocket(strLoginServIp1, nLoginServPort);

        socketLogin1.start();

        socketLogin2 = new MoGuSocket(strLoginServIp2, nLoginServPort);

        socketLogin2.start();

    }

    private void requestMsgServer(String address) {

        MoGuSocket socketLogin = ConnectionStore.getInstance().get(

                SysConstant.CONNECT_LOGIN_SERVER);

        if (null == socketLogin) {

            if (address.contains(strLoginServIp1)) {

                socketLogin = socketLogin1;

            } else {

                socketLogin = socketLogin2;

            }

            ConnectionStore.getInstance().put(SysConstant.CONNECT_LOGIN_SERVER,

                    socketLogin);

        } else {

            if (address.contains(strLoginServIp1)) {

                socketLogin1.close();

            } else {

                socketLogin2.close();

            }

            logger.d("socketLogin has already exsits");

            return;

        }

        // 连接成功，启动两个扫描线程

        if (null != waitingListMoniotr) {

            waitingListMoniotr.start();

        }

        if (null != packetSendMonitor) {

            packetSendMonitor.start();

        }

        Packet packet = PacketDistinguisher.make(ProtocolConstant.SID_LOGIN,

                ProtocolConstant.CID_LOGIN_REQ_MSGSERVER, null, true);

        ActionCallback callback = new ActionCallback() {

            @Override
            public void onSuccess(Packet packet) {
                responseSuccessCallback(packet);

            }

            @Override
            public void onTimeout(Packet packet) {
                rmInstance.setOnRecconnecting(false);

                rmInstance.setLogining(false);

            }

            @Override
            public void onFaild(Packet packet) {

                rmInstance.setOnRecconnecting(false);

                rmInstance.setLogining(false);

            }

        };

        PushActionToQueueTask task = new PushActionToQueueTask(packet, callback);

        TaskManager.getInstance().trigger(task);

    }

    private void onRequestMsgServer(Object obj) {

        Packet packet = (Packet) obj;

        MsgServerResponse response = (MsgServerResponse) packet.getResponse();

        strMsgServIp1 = response.getStrIp1();

        strMsgServIp2 = response.getStrIp2();

        m_nMsgServPort = response.getPort();

        doConnectMsgServ(context);

    }

    // @Override
    // public void callback(Object result) {
    //
    // User user = (User) result;
    //
    // if (null != user) {
    //
    // CacheHub.getInstance().setLoginUser(user);
    //
    // tmInstance.setDao(user.getDao());
    //
    // Logger.getLogger(LoginManager.class).d("认证成功");
    //
    // rmInstance.setLogining(true);
    //
    // // nFrom = SysConstant.FROM_IM_LOGIN;
    //
    // // 发广播启动IMService
    //
    // Intent i = new Intent();
    //
    // i.setAction(SysConstant.START_SERVICE_ACTION);
    //
    // context.sendBroadcast(i);
    //
    // startConnectLoginServer();
    //
    // } else {
    //
    // rmInstance.setOnRecconnecting(false);
    //
    // rmInstance.setLogining(false);
    //
    // Logger.getLogger(LoginManager.class).d("认证失败");
    //
    // }
    //
    // }

    public static void getUserInfo(Queue<String> userIdList, ActionCallback callback) {

        Object[] obj = new Object[1];

        obj[0] = userIdList;

        Packet packet = PacketDistinguisher.make(ProtocolConstant.SID_BUDDY_LIST,

                ProtocolConstant.CID_GET_USER_INFO_REQUEST, obj, true);

        PushActionToQueueTask task = new PushActionToQueueTask(packet, callback);

        TaskManager.getInstance().trigger(task);

    }

    public void setLoginIp1(String strIp1) {

        this.strLoginServIp1 = strIp1;

    }

    public String getLoginIp1() {

        return strLoginServIp1;

    }

    public void setLoginIp2(String strIp2) {

        this.strLoginServIp2 = strIp2;

    }

    public String getLoginIp2() {

        return strLoginServIp2;

    }

    public void setLoginPort(int nPort) {

        this.nLoginServPort = nPort;

    }

    public int getLoginPort() {

        return nLoginServPort;

    }

    public void ClearMessageQueue() {

        SocketMessageQueue.getInstance().clear();

    }

    public boolean isLogined() {

        return bLogined;

    }

    public boolean isFirstLogin() {
        boolean isFirst = needRequset;
        needRequset = false;
        return isFirst;
    }
}
