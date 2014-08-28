
package com.mogujie.tt.packet.biz;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;

public class FriendStatusNotifyPacket extends Packet {

    private Logger logger = Logger.getLogger(FriendStatusNotifyPacket.class);

    public FriendStatusNotifyPacket() {
        mRequest = new FriendStatusNotifyRequest();
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
            FriendStatusNotifyResponse res = new FriendStatusNotifyResponse();

            Header ResponseFriendStatusNotifyHeader = new Header();
            ResponseFriendStatusNotifyHeader.decode(buffer);
            res.setHeader(ResponseFriendStatusNotifyHeader);

            if (ResponseFriendStatusNotifyHeader.getServiceId() != ProtocolConstant.SID_BUDDY_LIST
                    ||
                    ResponseFriendStatusNotifyHeader.getCommandId() != ProtocolConstant.CID_CONTACT_FRIEND_STATUS_NOTIYF)
                return;

            int len = buffer.readInt();
            res.setUser_id(buffer.readString(len));
            res.setUser_status(buffer.readInt());

            mResponse = res;
        } catch (Exception e) {
            logger.e(e.getMessage());
        }

    }

    public static class FriendStatusNotifyRequest extends Request {
    }

    public static class FriendStatusNotifyResponse extends Response {

        private String user_id;
        private int user_status;

        public FriendStatusNotifyResponse() {

        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getUser_status() {
            return user_status;
        }

        public void setUser_status(int user_status) {
            this.user_status = user_status;
        }

    }
}
