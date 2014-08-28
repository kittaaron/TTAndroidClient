
package com.mogujie.tt.packet.biz;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.entity.MessageInfo;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.utils.SequenceNumberMaker;

public class MessagePacket extends Packet {

    private Logger logger = Logger.getLogger(MessagePacket.class);

    public MessagePacket(MessageInfo msgInfo) {
        mRequest = new SendMessageRequest(msgInfo);
        setNeedMonitor(true);
    }

    @Override
    public DataBuffer encode() {

        DataBuffer bodyBuffer = new DataBuffer();
        MessageInfo info = ((SendMessageRequest) mRequest).getMsgInfo();
        if (null == info)
            return null;
        bodyBuffer.writeInt(info.getMsgId());
        bodyBuffer.writeString(info.getMsgFromUserId());
        bodyBuffer.writeString(info.getTargetId());
        bodyBuffer.writeInt(info.getCreated());
        bodyBuffer.writeByte(info.getMsgType());
        bodyBuffer.writeByte(info.getMsgRenderType());
        byte[] bytes = null;
        if (info.getMsgType() == SysConstant.MESSAGE_TYPE_TELETEXT)
        {
            bytes = info.getMsgContent().getBytes();
        }
        else if (info.getMsgType() == SysConstant.MESSAGE_TYPE_AUDIO)
        {
            bytes = info.getAudioContent();
        }
        else
        {
            bytes = "".getBytes();
        }
        int contentLength = bytes.length;
        bodyBuffer.writeInt(contentLength);
        bodyBuffer.writeBytes(bytes);
        bodyBuffer.writeString(info.getMsgAttachContent());

        Header RequestSendMessageHeader = mRequest.getHeader();
        RequestSendMessageHeader.setLength(SysConstant.PROTOCOL_HEADER_LENGTH
                + bodyBuffer.readableBytes());
        DataBuffer headerBuffer = RequestSendMessageHeader.encode();

        DataBuffer buffer = new DataBuffer(headerBuffer.readableBytes()
                + bodyBuffer.readableBytes());
        buffer.writeDataBuffer(headerBuffer);
        buffer.writeDataBuffer(bodyBuffer);

        return buffer;

    }

    @Override
    public void decode(DataBuffer buffer) {
        if (null == buffer)
            return;
        try {
            SendMessageAckResponse res = new SendMessageAckResponse();

            Header SendMessageAckResponseHeader = new Header();
            SendMessageAckResponseHeader.decode(buffer);
            res.setHeader(SendMessageAckResponseHeader);

            if (SendMessageAckResponseHeader.getServiceId() != ProtocolConstant.SID_MSG
                    ||
                    SendMessageAckResponseHeader.getCommandId() != ProtocolConstant.CID_MSG_DATA_ACK)
                return;

            res.setSeqNo(buffer.readInt());
            int len = buffer.readInt();
            res.setFrom_id(buffer.readString(len));

            mResponse = res;
        } catch (Exception e) {
            logger.e(e.getMessage());
        }

    }

    public static class SendMessageRequest extends Request {

        private MessageInfo MsgInfo;

        public SendMessageRequest(MessageInfo msgInfo) {
            MsgInfo = msgInfo;

            Header messageHeader = new Header();

            messageHeader.setVersion((short) SysConstant.PROTOCOL_VERSION);
            messageHeader.setFlag((short) SysConstant.PROTOCOL_FLAG);
            messageHeader.setServiceId(ProtocolConstant.SID_MSG);
            messageHeader.setCommandId(ProtocolConstant.CID_MSG_DATA);
            messageHeader.setError((short) SysConstant.PROTOCOL_ERROR);
            short seqNo = SequenceNumberMaker.getInstance().make();
            messageHeader.setReserved(seqNo);
            // 这里content内容比较多，在encode里面设置
            setHeader(messageHeader);
        }

        public MessageInfo getMsgInfo() {
            return MsgInfo;
        }

        public void setMsgInfo(MessageInfo msgInfo) {
            this.MsgInfo = msgInfo;
        }
    }

    public static class SendMessageAckResponse extends Response {
        private int seqNo;
        private String from_id;

        public SendMessageAckResponse() {

        }

        public int getSeqNo() {
            return seqNo;
        }

        public void setSeqNo(int seqNo) {
            this.seqNo = seqNo;
        }

        public String getFrom_id() {
            return from_id;
        }

        public void setFrom_id(String from_id) {
            this.from_id = from_id;
        }

    }
}
