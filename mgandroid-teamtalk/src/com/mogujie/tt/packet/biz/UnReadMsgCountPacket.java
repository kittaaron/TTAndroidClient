
package com.mogujie.tt.packet.biz;

import java.util.LinkedList;
import java.util.List;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.entity.UnReadMsgCountInfo;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.utils.SequenceNumberMaker;

/**
 * yugui 2014-05-04
 */
public class UnReadMsgCountPacket extends Packet {

    private Logger logger = Logger.getLogger(UnReadMsgCountPacket.class);

    public UnReadMsgCountPacket() {
        mRequest = new UnReadMsgCountRequest();
        setNeedMonitor(true);
    }

    @Override
    public DataBuffer encode() {

        Header unReadMsgCountHeader = mRequest.getHeader();
        DataBuffer headerBuffer = unReadMsgCountHeader.encode();
        // 这个协议没有body
        return headerBuffer;
    }

    @Override
    public void decode(DataBuffer buffer) {

        if (null == buffer)
            return;

        try {
            UnReadMsgCountResponse res = new UnReadMsgCountResponse();

            Header ResponseUnReadMsgCountHeader = new Header();
            ResponseUnReadMsgCountHeader.decode(buffer);
            res.setHeader(ResponseUnReadMsgCountHeader);

            if (ResponseUnReadMsgCountHeader.getServiceId() != ProtocolConstant.SID_MSG
                    ||
                    ResponseUnReadMsgCountHeader.getCommandId() != ProtocolConstant.CID_MSG_UNREAD_CNT_RESPONSE)
                return;

            int itemCount = buffer.readInt();
            res.setCounter_cnt(itemCount);
            for (int i = 0; i < itemCount; i++) {
                UnReadMsgCountInfo info = new UnReadMsgCountInfo();
                int len = buffer.readInt();
                info.setFromUserId(buffer.readString(len));
                info.setUnReadCount(buffer.readInt());
                res.addInfo(info);
            }

            mResponse = res;
        } catch (Exception e) {
            logger.e(e.getMessage());
        }

    }

    public static class UnReadMsgCountRequest extends Request {
        public UnReadMsgCountRequest() {

            Header recentcontactHeader = new Header();
            recentcontactHeader
                    .setVersion((short) SysConstant.PROTOCOL_VERSION);
            recentcontactHeader.setFlag((short) SysConstant.PROTOCOL_FLAG);
            recentcontactHeader.setServiceId(ProtocolConstant.SID_MSG);
            recentcontactHeader
                    .setCommandId(ProtocolConstant.CID_MSG_UNREAD_CNT_REQUEST);
            recentcontactHeader.setError((short) SysConstant.PROTOCOL_ERROR);
            short seqNo = SequenceNumberMaker.getInstance().make();
            recentcontactHeader.setReserved(seqNo);
            int contentLength = 0;
            recentcontactHeader.setLength(SysConstant.PROTOCOL_HEADER_LENGTH
                    + contentLength);

            setHeader(recentcontactHeader);
        }
    }

    public static class UnReadMsgCountResponse extends Response {
        private int counter_cnt;
        private List<UnReadMsgCountInfo> unReadMsgCountList = new LinkedList<UnReadMsgCountInfo>();

        public UnReadMsgCountResponse() {

        }

        public void addInfo(UnReadMsgCountInfo info) {
            unReadMsgCountList.add(info);
        }

        public int getCounter_cnt() {
            return counter_cnt;
        }

        public void setCounter_cnt(int counter_cnt) {
            this.counter_cnt = counter_cnt;
        }

        public List<UnReadMsgCountInfo> getUnReadMsgCountList() {
            return unReadMsgCountList;
        }

        public void setUnReadMsgCountList(List<UnReadMsgCountInfo> unReadMsgCountList) {
            this.unReadMsgCountList = unReadMsgCountList;
        }

    }
}
