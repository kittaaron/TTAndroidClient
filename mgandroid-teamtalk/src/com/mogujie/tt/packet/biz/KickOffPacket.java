
package com.mogujie.tt.packet.biz;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.conn.ReconnectManager;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;

public class KickOffPacket extends Packet {

    private Logger logger = Logger.getLogger(KickOffPacket.class);

    public KickOffPacket() {
        mRequest = null;
        setNeedMonitor(false);
    }

    @Override
    public DataBuffer encode() {
        return null;
    }

    @Override
    public void decode(DataBuffer buffer) {
        if (null == buffer)
            return;
        try {
            KickOffNotify res = new KickOffNotify();

            Header ResponseKickOffNotifyHeader = new Header();
            ResponseKickOffNotifyHeader.decode(buffer);
            res.setHeader(ResponseKickOffNotifyHeader);

            if (ResponseKickOffNotifyHeader.getServiceId() != ProtocolConstant.SID_LOGIN
                    ||
                    ResponseKickOffNotifyHeader.getCommandId() != ProtocolConstant.CID_LOGIN_KICK_USER)
                return;

            int len = buffer.readInt();
            res.setUser_id(buffer.readString(len));
            res.setReason(buffer.readInt());
            ReconnectManager.getInstance().setKickOff(true);
            mResponse = res;
        } catch (Exception e) {
            logger.e(e.getMessage());
        }
        ReconnectManager.getInstance().setKickOff(true);
    }

    public static class KickOffRequest extends Request {
        public KickOffRequest() {
        }
    }

    public static class KickOffNotify extends Response {

        private String user_id;
        private int reason;

        public KickOffNotify() {

        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getReason() {
            return reason;
        }

        public void setReason(int reason) {
            this.reason = reason;
        }
    }
}
