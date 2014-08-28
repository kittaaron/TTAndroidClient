
package com.mogujie.tt.packet.biz;

import com.mogujie.tt.cache.biz.CacheHub;
import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.entity.MessageInfo;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.PacketDistinguisher;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.task.TaskManager;
import com.mogujie.tt.task.biz.PushActionToQueueTask;
import com.mogujie.tt.utils.MessageSplitResult;

public class RecvMessagePacket extends Packet {

    private Logger logger = Logger.getLogger(RecvMessagePacket.class);

    public RecvMessagePacket() {
        mRequest = new RecvMessageRequest();
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
            RecvMesageResponse res = new RecvMesageResponse();

            Header ResponseRecvMesageHeader = new Header();
            ResponseRecvMesageHeader.decode(buffer);
            res.setHeader(ResponseRecvMesageHeader);

            if (ResponseRecvMesageHeader.getServiceId() != ProtocolConstant.SID_MSG ||
                    ResponseRecvMesageHeader.getCommandId() != ProtocolConstant.CID_MSG_DATA)
                return;

            MessageInfo msgInfo = new MessageInfo();

            int msgReqNo = buffer.readInt();
            int fromIdLen = buffer.readInt();
            String fromId = buffer.readString(fromIdLen);
            int targetIdLen = buffer.readInt();
            String targetId = buffer.readString(targetIdLen);

            if (!targetId.equals(CacheHub.getInstance().getLoginUserId())) {
                return;
            }
            int createTime = buffer.readInt();
            byte msgType = buffer.readByte();
            byte msgRenderType = buffer.readByte();
            int msgLen = buffer.readInt();
            byte[] byteContent = buffer.readBytes(msgLen);

            int msgAttachLen = buffer.readInt();
            String msgAttachContent = buffer.readString(msgAttachLen);
            msgInfo.setMsgId(msgReqNo);
            msgInfo.setMsgFromUserId(fromId);
            msgInfo.setTargetId(targetId);
            msgInfo.setMsgCreateTime(createTime);
            msgInfo.setMsgType(msgType);
            msgInfo.setMsgRenderType(msgRenderType);
            msgInfo.setMsgAttachContent(msgAttachContent);

            MessageSplitResult msr = new MessageSplitResult(msgInfo, byteContent);
            msr.decode();
            res.setMsgSpliteResult(msr);

            // 立即给server一个消息收到回复
            Object[] objs = new Object[2];
            objs[0] = msgInfo.getMsgId();
            objs[1] = msgInfo.getMsgFromUserId();
            Packet packet = PacketDistinguisher.make(ProtocolConstant.SID_MSG,
                    ProtocolConstant.CID_MSG_DATA_ACK, objs, true);
            PushActionToQueueTask task = new PushActionToQueueTask(packet, null);
            TaskManager.getInstance().trigger(task);

            // 立即给server回复一个已读
            Object[] objReadAck = new Object[1];
            objReadAck[0] = msgInfo.getMsgFromUserId();
            Packet packetReadAck = PacketDistinguisher.make(ProtocolConstant.SID_MSG,
                    ProtocolConstant.CID_MSG_READ_ACK, objReadAck, false);
            PushActionToQueueTask taskReadAck = new PushActionToQueueTask(packetReadAck, null);
            TaskManager.getInstance().trigger(taskReadAck);

            mResponse = res;

        } catch (Exception e) {
            logger.e(e.getMessage());
        }

    }

    public static class RecvMessageRequest extends Request {

    }

    public static class RecvMesageResponse extends Response {

        private MessageSplitResult msgSpliteResult;

        public RecvMesageResponse() {

        }

        public MessageSplitResult getMsgSpliteResult() {
            return msgSpliteResult;
        }

        public void setMsgSpliteResult(MessageSplitResult msgSpliteResult) {
            this.msgSpliteResult = msgSpliteResult;
        }

    }
}
