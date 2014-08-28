
package com.mogujie.tt.packet.biz;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.utils.SequenceNumberMaker;

public class ConfirmRecvMsgPacket extends Packet {

    public ConfirmRecvMsgPacket(int _seq_no, String _from_id) {
        mRequest = new ConfirmRecvMsgRequest(_seq_no, _from_id);
        setNeedMonitor(false);
    }

    @Override
    public DataBuffer encode() {
        Header RequestConfirmHeader = mRequest.getHeader();
        DataBuffer headerBuffer = RequestConfirmHeader.encode();
        int readable = headerBuffer.readableBytes();

        int _seq_no = ((ConfirmRecvMsgRequest) mRequest).getSeq_no();
        String _from_id = ((ConfirmRecvMsgRequest) mRequest).getFrom_id();
        if (null == _from_id)
            return null;

        DataBuffer buffer = new DataBuffer(readable + 4 + 4 + _from_id.length());
        buffer.writeDataBuffer(headerBuffer);
        buffer.writeInt(_seq_no);
        buffer.writeString(_from_id);

        return buffer;

    }

    @Override
    public void decode(DataBuffer buffer) {

    }

    public static class ConfirmRecvMsgRequest extends Request {

        private int seq_no;
        private String from_id;

        public ConfirmRecvMsgRequest(int _seq_no, String _from_id) {
            seq_no = _seq_no;
            from_id = _from_id;

            Header confirmRecvMsgInfoHeader = new Header();
            confirmRecvMsgInfoHeader.setVersion((short) SysConstant.PROTOCOL_VERSION);
            confirmRecvMsgInfoHeader.setFlag((short) SysConstant.PROTOCOL_FLAG);
            confirmRecvMsgInfoHeader.setServiceId(ProtocolConstant.SID_MSG);
            confirmRecvMsgInfoHeader.setCommandId(ProtocolConstant.CID_MSG_DATA_ACK);
            confirmRecvMsgInfoHeader.setError((short) SysConstant.PROTOCOL_ERROR);
            short seqNo = SequenceNumberMaker.getInstance().make();
            confirmRecvMsgInfoHeader.setReserved(seqNo);
            int contentLength = 4 + 4 + from_id.length();
            confirmRecvMsgInfoHeader.setLength(SysConstant.PROTOCOL_HEADER_LENGTH
                    + contentLength);
            setHeader(confirmRecvMsgInfoHeader);
        }

        public int getSeq_no() {
            return seq_no;
        }

        public void setSeq_no(int seq_no) {
            this.seq_no = seq_no;
        }

        public String getFrom_id() {
            return from_id;
        }

        public void setFrom_id(String from_id) {
            this.from_id = from_id;
        }

    }

    public static class ConfirmRecvMsgResponse extends Response {

    }
}
