
package com.mogujie.tt.packet.biz;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.utils.SequenceNumberMaker;

public class ConfirmUnReadMsgInfoPacket extends Packet {

    public ConfirmUnReadMsgInfoPacket(String _from_id) {

        mRequest = new ConfirmUnReadMsgInfoRequest(_from_id);
        setNeedMonitor(false);
    }

    @Override
    public DataBuffer encode() {

        Header RequestConfirmHeader = mRequest.getHeader();
        DataBuffer headerBuffer = RequestConfirmHeader.encode();

        String _from_id = ((ConfirmUnReadMsgInfoRequest) mRequest).getFrom_id();
        if (null == _from_id)
            return null;
        DataBuffer buffer = new DataBuffer(headerBuffer.readableBytes() + 4 + _from_id.length());
        buffer.writeDataBuffer(headerBuffer);
        buffer.writeString(_from_id);

        return buffer;

    }

    @Override
    public void decode(DataBuffer buffer) {
    }

    public static class ConfirmUnReadMsgInfoRequest extends Request {

        private String from_id;

        public ConfirmUnReadMsgInfoRequest(String _from_id) {
            from_id = _from_id;

            Header confirmUnReadMsgInfoHeader = new Header();

            confirmUnReadMsgInfoHeader.setVersion((short) SysConstant.PROTOCOL_VERSION);
            confirmUnReadMsgInfoHeader.setFlag((short) SysConstant.PROTOCOL_FLAG);
            confirmUnReadMsgInfoHeader.setServiceId(ProtocolConstant.SID_MSG);
            confirmUnReadMsgInfoHeader.setCommandId(ProtocolConstant.CID_MSG_READ_ACK);
            confirmUnReadMsgInfoHeader.setError((short) SysConstant.PROTOCOL_ERROR);
            short seqNo = SequenceNumberMaker.getInstance().make();
            confirmUnReadMsgInfoHeader.setReserved(seqNo);
            int contentLength = 4 + from_id.length();
            confirmUnReadMsgInfoHeader.setLength(SysConstant.PROTOCOL_HEADER_LENGTH
                    + contentLength);

            setHeader(confirmUnReadMsgInfoHeader);
        }

        public String getFrom_id() {
            return from_id;
        }

        public void setFrom_id(String from_id) {
            this.from_id = from_id;
        }

    }

    public static class ConfirmUnReadMagInfoResponse extends Response {

    }
}
