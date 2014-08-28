
package com.mogujie.tt.packet.biz;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.entity.RecentInfo;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.utils.SequenceNumberMaker;

public class RecentContactPacket extends Packet {

    private Logger logger = Logger.getLogger(RecentContactPacket.class);

    public RecentContactPacket() {
        mRequest = new RecentContactRequest();
        setNeedMonitor(true);
    }

    @Override
    public DataBuffer encode() {
        Header recentContactHeader = mRequest.getHeader();
        DataBuffer headerBuffer = recentContactHeader.encode();

        DataBuffer buffer = new DataBuffer(headerBuffer.readableBytes() + 4);
        buffer.writeDataBuffer(headerBuffer);

        int request_type = 0;
        buffer.writeInt(request_type);

        return buffer;
    }

    @Override
    public void decode(DataBuffer buffer) {
        if (null == buffer)
            return;

        try {
            RecentContactResponse res = new RecentContactResponse();

            Header ResponseRecentContactHeader = new Header();
            ResponseRecentContactHeader.decode(buffer);
            res.setHeader(ResponseRecentContactHeader);

            if (ResponseRecentContactHeader.getServiceId() != ProtocolConstant.SID_BUDDY_LIST
                    || ResponseRecentContactHeader.getCommandId() != ProtocolConstant.CID_CONTACT_RECENT_RESPONSE)
                return;

            int nFriendCnt = buffer.readInt();

            res.setFriend_cnt(nFriendCnt);

            for (int i = 0; i < nFriendCnt; i++) {
                RecentInfo info = new RecentInfo();
                int id_len = buffer.readInt();
                String userId = buffer.readString(id_len);
                info.setUserId(userId);
                info.setLastTime(Long.valueOf(buffer.readInt()));
                res.addRecentInfo(info);
            }

            mResponse = res;
        } catch (Exception e) {
            logger.equals(e.getMessage());
        }
    }

    public static class RecentContactRequest extends Request {

        public RecentContactRequest() {

            Header recentcontactHeader = new Header();

            recentcontactHeader
                    .setVersion((short) SysConstant.PROTOCOL_VERSION);
            recentcontactHeader.setFlag((short) SysConstant.PROTOCOL_FLAG);
            recentcontactHeader.setServiceId(ProtocolConstant.SID_BUDDY_LIST);
            recentcontactHeader
                    .setCommandId(ProtocolConstant.CID_REQUEST_RECNET_CONTACT);
            recentcontactHeader.setError((short) SysConstant.PROTOCOL_ERROR);
            short seqNo = SequenceNumberMaker.getInstance().make();
            recentcontactHeader.setReserved(seqNo);
            int contentLength = 4;
            recentcontactHeader.setLength(SysConstant.PROTOCOL_HEADER_LENGTH
                    + contentLength);

            setHeader(recentcontactHeader);
        }
    }

    public static class RecentContactResponse extends Response {
        private int friend_cnt;
        private List<RecentInfo> recentInfos = new LinkedList<RecentInfo>();

        public RecentContactResponse() {

        }

        public void addRecentInfo(RecentInfo info) {
            recentInfos.add(info);
        }

        public int getFriend_cnt() {
            return friend_cnt;
        }

        public void setFriend_cnt(int friend_cnt) {
            this.friend_cnt = friend_cnt;
        }

        public List<RecentInfo> getRecentInfos() {
            return recentInfos;
        }

        public Queue<String> getRecentIdList() {
            Queue<String> list = new ArrayBlockingQueue<String>(recentInfos.size());
            for (RecentInfo recent : recentInfos) {
                list.add(recent.getUserId());
            }
            return list;
        }

        public void setRecentInfos(List<RecentInfo> recentInfos) {
            this.recentInfos = recentInfos;
        }

    }
}
