
package com.mogujie.tt.packet.biz;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.utils.SequenceNumberMaker;

public class QueryUserOnlineStatusPacket extends Packet {

    private Logger logger = Logger.getLogger(QueryUserOnlineStatusPacket.class);

    public QueryUserOnlineStatusPacket(String _user_id) {

        mRequest = new QueryUserOnlineStatusRequest(_user_id);
        setNeedMonitor(true);
    }

    @Override
    public DataBuffer encode() {

        Header QueryUserOnlineStatusRequest = mRequest.getHeader();
        DataBuffer headerBuffer = QueryUserOnlineStatusRequest.encode();

        String _user_id = ((QueryUserOnlineStatusRequest) mRequest)
                .getUser_id();

        DataBuffer buffer = new DataBuffer(headerBuffer.readableBytes() + 4
                + _user_id.length());
        buffer.writeDataBuffer(headerBuffer);
        buffer.writeString(_user_id);

        return buffer;
    }

    @Override
    public void decode(DataBuffer buffer) {

        if (null == buffer)
            return;

        try {
            QueryUserOnlineStatusResponse res = new QueryUserOnlineStatusResponse();

            Header ResponseQueryUserOnlineStatusHeader = new Header();
            ResponseQueryUserOnlineStatusHeader.decode(buffer);
            res.setHeader(ResponseQueryUserOnlineStatusHeader);

            if (ResponseQueryUserOnlineStatusHeader.getServiceId() != ProtocolConstant.SID_BUDDY_LIST
                    || ResponseQueryUserOnlineStatusHeader.getCommandId() != ProtocolConstant.CID_QUERY_USER_ONLINE_STATUS_RESPONSE)
                return;

            int user_id_len = buffer.readInt();
            res.setUser_id(buffer.readString(user_id_len));
            res.setOnline_status(buffer.readInt());

            mResponse = res;
        } catch (Exception e) {
            logger.e(e.getMessage());
        }

    }

    public static class QueryUserOnlineStatusRequest extends Request {
        private String user_id;

        public QueryUserOnlineStatusRequest(String _user_id) {
            user_id = _user_id;

            Header QueryUserOnlineStatusRequestHeader = new Header();

            QueryUserOnlineStatusRequestHeader
                    .setVersion((short) SysConstant.PROTOCOL_VERSION);
            QueryUserOnlineStatusRequestHeader
                    .setFlag((short) SysConstant.PROTOCOL_FLAG);
            QueryUserOnlineStatusRequestHeader
                    .setServiceId(ProtocolConstant.SID_BUDDY_LIST);
            QueryUserOnlineStatusRequestHeader
                    .setCommandId(ProtocolConstant.CID_QUERY_USER_ONLINE_STATUS_REQUEST);
            QueryUserOnlineStatusRequestHeader
                    .setError((short) SysConstant.PROTOCOL_ERROR);
            short seqNo = SequenceNumberMaker.getInstance().make();
            QueryUserOnlineStatusRequestHeader.setReserved(seqNo);
            int contentLength = 4 + user_id.length();
            QueryUserOnlineStatusRequestHeader
                    .setLength(SysConstant.PROTOCOL_HEADER_LENGTH
                            + contentLength);

            setHeader(QueryUserOnlineStatusRequestHeader);

        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }

    public static class QueryUserOnlineStatusResponse extends Response {

        private String user_id;
        private int online_status;

        public QueryUserOnlineStatusResponse() {

        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getOnline_status() {
            return online_status;
        }

        public void setOnline_status(int online_status) {
            this.online_status = online_status;
        }
    }
}
