
package com.mogujie.tt.conn;

import com.mogujie.tt.config.HandlerConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.socket.MoGuSocket;
import com.mogujie.tt.socket.SocketStateManager;
import com.mogujie.tt.timer.HeartBeatProcessor;
import com.mogujie.tt.timer.TimerHelper;

public class StateManager {

    private static StateManager pInstance = null;
    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger(StateManager.class);

    private TimerHelper thHeartBeat = new TimerHelper(30 * 1000,
            new HeartBeatProcessor());

    private NetStateDispach nsInstance = NetStateDispach.getInstance();
    private NetStateManager nsmInstance = NetStateManager.getInstance();
    private SocketStateManager ssmInstance = SocketStateManager.getInstance();

    public static StateManager getInstance()
    {
        if (null == pInstance)
        {
            pInstance = new StateManager();
        }
        return pInstance;
    }

    public boolean isOnline()
    {
        return nsmInstance.isOnline() && ssmInstance.isOnline();
    }

    public void notifyNetState(boolean bState) {
        if (!bState) {
            thHeartBeat.stopTimer();
            ReconnectManager.getInstance().setOnRecconnecting(false);
            ReconnectManager.getInstance().setLogining(false);
            nsInstance.dispachMsg(HandlerConstant.HANDLER_NET_STATE_DISCONNECTED);
            resetSockets();
        }
        else if (bState && ssmInstance.isOnline()) {
            thHeartBeat.startTimer(true);
            nsInstance.dispachMsg(HandlerConstant.HANDLER_NET_STATE_CONNECTED);
        }
        else if (bState) {
            ReconnectManager.getInstance().resetConnectCount();
        }
    }

    public void notifySocketState(boolean bState) {
        if (!bState) {
            thHeartBeat.stopTimer();
            ReconnectManager.getInstance().setOnRecconnecting(false);
            ReconnectManager.getInstance().setLogining(false);
            nsInstance.dispachMsg(HandlerConstant.HANDLER_NET_STATE_DISCONNECTED);
            resetSockets();
        }
        else if (bState && nsmInstance.isOnline()) {
            thHeartBeat.startTimer(true);
            nsInstance.dispachMsg(HandlerConstant.HANDLER_NET_STATE_CONNECTED);
        }
    }

    public void startTimer() {
        thHeartBeat.startTimer(true);
    }

    public void stopTimer() {
        thHeartBeat.stopTimer();
    }

    public void resetSockets() {
        MoGuSocket socketLogin = ConnectionStore.getInstance().get(
                SysConstant.CONNECT_LOGIN_SERVER);
        if (null != socketLogin && null != socketLogin.getChannel())
        {
            socketLogin.close();
        }
        ConnectionStore.getInstance().remove(SysConstant.CONNECT_LOGIN_SERVER);

        MoGuSocket socketMsg = ConnectionStore.getInstance().get(SysConstant.CONNECT_MSG_SERVER);
        if (null != socketMsg && null != socketMsg.getChannel())
        {
            socketMsg.close();
        }
        ConnectionStore.getInstance().remove(SysConstant.CONNECT_MSG_SERVER);
    }
}
