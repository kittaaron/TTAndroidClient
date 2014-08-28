
package com.mogujie.tt.packet.biz;

import java.util.LinkedList;
import java.util.List;

import com.mogujie.tt.cache.biz.CacheHub;
import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.entity.MessageInfo;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.PacketDistinguisher;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.task.TaskManager;
import com.mogujie.tt.task.biz.PushActionToQueueTask;
import com.mogujie.tt.utils.MessageSplitResult;
import com.mogujie.tt.utils.SequenceNumberMaker;

public class UnReadMsgInfoPacket extends Packet {

    private Logger logger = Logger.getLogger(UnReadMsgInfoPacket.class);

    public UnReadMsgInfoPacket(String _from_id) {
        mRequest = new UnReadMsgInfoRequest(_from_id);
        setNeedMonitor(true);
    }

    @Override
    public DataBuffer encode() {

        Header unReadMsgInfoHeader = mRequest.getHeader();
        DataBuffer headerBuffer = unReadMsgInfoHeader.encode();

        String _from_idString = ((UnReadMsgInfoRequest) mRequest).getFrom_id();
        int _from_id_length = _from_idString.length();

        DataBuffer buffer = new DataBuffer(headerBuffer.readableBytes() + 4
                + _from_id_length);
        buffer.writeDataBuffer(headerBuffer);
        buffer.writeString(_from_idString);

        return buffer;
    }

    @Override
    public void decode(DataBuffer buffer) {

        if (null == buffer)
            return;

        try {
            UnReadMsgInfoResponse res = new UnReadMsgInfoResponse();

            Header ResponseUnReadMsgInfoHeader = new Header();
            ResponseUnReadMsgInfoHeader.decode(buffer);
            res.setHeader(ResponseUnReadMsgInfoHeader);

            if (ResponseUnReadMsgInfoHeader.getServiceId() != ProtocolConstant.SID_MSG
                    || ResponseUnReadMsgInfoHeader.getCommandId() != ProtocolConstant.CID_MSG_UNREAD_MSG_RESPONSE)
                return;

            int other_user_id_len = buffer.readInt();
            String _other_user_id = buffer.readString(other_user_id_len);
            res.setOther_user_id(_other_user_id);

            int itemCount = buffer.readInt();
            res.setMsg_count(itemCount);

            List<MessageInfo> infoList = new LinkedList<MessageInfo>();
            List<byte[]> contentList = new LinkedList<byte[]>();

            for (int i = 0; i < itemCount; i++) {
                Logger.getLogger(UnReadMsgInfoPacket.class).d("收到一条未读信息：");
                MessageInfo info = new MessageInfo();
                int msg_from_user_id_len = buffer.readInt();
                info.setMsgFromUserId(buffer.readString(msg_from_user_id_len));

                info.setTargetId(CacheHub.getInstance().getLoginUserId()); // 未读消息接收对象肯定是自己哇
                info.setMsgLoadState(SysConstant.MESSAGE_STATE_UNLOAD);
                info.setMsgReadStatus(SysConstant.MESSAGE_UNREAD);

                int msg_from_name_len = buffer.readInt();
                info.setMsgFromName(buffer.readString(msg_from_name_len));
                Logger.getLogger(UnReadMsgInfoPacket.class).d(
                        "来自：" + info.getMsgFromName());
                int msg_from_user_unick_len = buffer.readInt();
                info.setMsgFromUserNick(buffer
                        .readString(msg_from_user_unick_len));

                int msg_from_user_avatar_len = buffer.readInt();
                info.setMsgFromUserAvatar(buffer
                        .readString(msg_from_user_avatar_len));

                int msg_create_time = buffer.readInt();
                info.setMsgCreateTime(msg_create_time);

                byte msg_type = buffer.readByte();
                info.setMsgType(msg_type);

                int msg_content_len = buffer.readInt();

                byte[] content = buffer.readBytes(msg_content_len);

                infoList.add(0, info);
                contentList.add(0, content);
            }

            for (int i = 0; i < itemCount; i++) {
                MessageInfo info = infoList.remove(0);
                byte[] content = contentList.remove(0);
                MessageSplitResult msr = new MessageSplitResult(info, content);
                msr.decode();
                res.addInfo(msr);
            }

            // 立即给server一个消息收到回复
            Object[] objs = new Object[1];
            objs[0] = _other_user_id;
            Packet packet = PacketDistinguisher.make(ProtocolConstant.SID_MSG,
                    ProtocolConstant.CID_MSG_READ_ACK, objs, true);
            PushActionToQueueTask task = new PushActionToQueueTask(packet, null);
            TaskManager.getInstance().trigger(task);

            mResponse = res;
        } catch (Exception e) {
            logger.e(e.getMessage());
        }

    }

    public static class UnReadMsgInfoRequest extends Request {
        private String from_id;

        public UnReadMsgInfoRequest(String _from_id) {
            from_id = _from_id;

            Header unreadmsginfoHeader = new Header();
            unreadmsginfoHeader
                    .setVersion((short) SysConstant.PROTOCOL_VERSION);
            unreadmsginfoHeader.setFlag((short) SysConstant.PROTOCOL_FLAG);
            unreadmsginfoHeader.setServiceId(ProtocolConstant.SID_MSG);
            unreadmsginfoHeader
                    .setCommandId(ProtocolConstant.CID_MSG_UNREAD_MSG_REUQEST);
            unreadmsginfoHeader.setError((short) SysConstant.PROTOCOL_ERROR);
            short seqNo = SequenceNumberMaker.getInstance().make();
            unreadmsginfoHeader.setReserved(seqNo);
            int contentLength = 4 + from_id.length();
            unreadmsginfoHeader.setLength(SysConstant.PROTOCOL_HEADER_LENGTH
                    + contentLength);

            setHeader(unreadmsginfoHeader);

        }

        public String getFrom_id() {
            return from_id;
        }

        public void setFrom_id(String from_id) {
            this.from_id = from_id;
        }
    }

    public static class UnReadMsgInfoResponse extends Response {
        private String other_user_id;
        private int msg_count;
        private List<MessageSplitResult> unReadMsgInfoList = new LinkedList<MessageSplitResult>();

        public UnReadMsgInfoResponse() {

        }

        public void addInfo(MessageSplitResult info) {
            unReadMsgInfoList.add(info);
        }

        public String getOther_user_id() {
            return other_user_id;
        }

        public void setOther_user_id(String other_user_id) {
            this.other_user_id = other_user_id;
        }

        public int getMsg_count() {
            return msg_count;
        }

        public void setMsg_count(int msg_count) {
            this.msg_count = msg_count;
        }

        public List<MessageSplitResult> getUnReadMsgInfoList() {
            return unReadMsgInfoList;
        }

        public void setUnReadMsgInfoList(List<MessageSplitResult> unReadMsgInfoList) {
            this.unReadMsgInfoList = unReadMsgInfoList;
        }

    }
}
