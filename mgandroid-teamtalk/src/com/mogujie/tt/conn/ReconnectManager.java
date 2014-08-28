
package com.mogujie.tt.conn;

import java.util.Random;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.mogujie.tt.app.IMEntrance;
import com.mogujie.tt.config.HandlerConstant;
import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.MessageDispatchCenter;
import com.mogujie.tt.socket.SocketStateManager;
import com.mogujie.tt.ui.activity.MessageActivity;
import com.mogujie.tt.utils.NetworkUtil;

public class ReconnectManager extends Thread {

    volatile boolean mbRuning = true;
    private Context context = null;

    // private StateManager smInstance = StateManager.getInstance();
    private SocketStateManager ssmInstance = SocketStateManager.getInstance();
    private NetStateManager nsmInstance = NetStateManager.getInstance();

    private Logger logger = Logger.getLogger(ReconnectManager.class);
    private int mnReconnectCount = 0;
    private int mlastRecvPack = 0;
    /*
     * 这个标志主要是为了防止在重连的过程中再次发起重连
     */
    private boolean mbOnReconnecting = false;
    /*
     * 这个标志主要是为了防止在连接Login server的时候也发起重连
     */
    private boolean mbLogining = true;
    /*
     * 当主客killTask的时候设置暂停重连,设置这个变量主要是为了解决账户切换时候重连的问题
     */
    private boolean mbPause = false;
    /*
     * 当用户被踢下线的时候，不再重连
     */
    private boolean mbKickOff = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case ProtocolConstant.SID_LOGIN * 1000 + ProtocolConstant.CID_LOGIN_KICK_USER:
                    mbKickOff = true;
                    break;
            }
            setLastRecvTime((int) (System.currentTimeMillis() / 1000));
        }
    };

    private ReconnectManager() {
        MessageDispatchCenter.getInstance().registerAllInterest(handler);
    }

    private static class SingletonHolder {
        static ReconnectManager instance = new ReconnectManager();
    }

    public static ReconnectManager getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public void run() {
        try {
            while (mbRuning) {
                if (null == IMEntrance.getInstance().getContext()
                        || !NetworkUtil.isNetWorkAvalible(IMEntrance.getInstance().getContext())) {
                    Random random = new Random();
                    long nSleepTime = (Math.abs(random.nextInt()) % 10 + 1) * 1000;
                    Thread.sleep(nSleepTime);
                    continue;
                }
                if (ssmInstance.isOnline() && nsmInstance.isOnline() && !mbOnReconnecting
                        && !checkHeartBeat())
                {
                    ssmInstance.setState(false);
                    logger.e("long time no receive heartbeat");
                }
                if (!mbPause && !mbLogining && !ssmInstance.isOnline() && nsmInstance.isOnline() &&
                        !mbKickOff && !mbOnReconnecting /*
                                                         * && mnReconnectCount <
                                                         * SysConstant
                                                         * .MAX_RECONNECT_COUNT
                                                         */)
                {
                    mbOnReconnecting = true;
                    long nSleep = 0;
                    if (mnReconnectCount == 0)
                    {
                        int max = 10;
                        int min = 1;

                        Random random = new Random();
                        nSleep = Math.abs(random.nextInt(max) % (max - min + 1) + min);

                    }
                    else if (mnReconnectCount <= 10)
                    {
                        nSleep = (long) Math.pow(2, mnReconnectCount) * 1000;
                        if (nSleep > 16 * 1000) {
                            nSleep = 16 * 1000;
                        }
                    }
                    else {
                        nSleep = 1024;
                    }
                    Thread.sleep(nSleep);
                    LoginManager.getInstance().doReconect(context);
                    mnReconnectCount++;
                } else {
                    Thread.sleep(5 * 1000);
                }
            }
        } catch (Exception e)
        {
            mbOnReconnecting = false;
            mbLogining = false;
            logger.e(e.getMessage());
        }
    }

    private boolean checkHeartBeat() {
        int currentTime = (int) (System.currentTimeMillis() / 1000);

        if (mlastRecvPack != 0 && currentTime - mlastRecvPack > SysConstant.MAX_HEART_BEAT_TIME)
        {
            return false;
        }
        return true;
    }

    public void startReconnctManager(Context context)
    {
        this.context = context;
        mbRuning = true;
        start();
    }

    public void stopReconnctManager()
    {
        logger.d("stopReconnectManager");
        mbRuning = false;
    }

    public void setOnRecconnecting(boolean bOnReconnecting) {
        this.mbOnReconnecting = bOnReconnecting;
    }

    public void setPause(boolean bPause) {
        mbPause = bPause;
    }

    public void resetConnectCount()
    {
        mnReconnectCount = 0;
    }

    public void setLastRecvTime(int lastRecvPack) {
        mlastRecvPack = lastRecvPack;
    }

    public boolean isLogining() {
        return mbLogining;
    }

    public void setLogining(boolean bLogining) {
        mbLogining = bLogining;
    }

    public void setKickOff(boolean kickOff) {
        this.mbKickOff = kickOff;
        if (kickOff) {
            Handler handler = MessageActivity.getMsgHandler();
            if (null != handler) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.HANDLER_LOGIN_KICK;
                msg.obj = null;
                handler.sendMessage(msg);
            }
        }
    }

    public boolean isKickOff() {
        return this.mbKickOff;
    }
}
