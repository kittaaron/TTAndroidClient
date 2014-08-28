
package com.mogujie.tt.packet.biz;

import java.util.LinkedList;
import java.util.Queue;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.entity.User;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.base.DataBuffer;
import com.mogujie.tt.packet.base.Header;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.utils.SequenceNumberMaker;

public class QueryUsersInfoPacket extends Packet {

    private Logger logger = Logger.getLogger(QueryUsersInfoPacket.class);

    public QueryUsersInfoPacket(Queue<String> _userIdList) {

        mRequest = new QueryUsersInfoRequest(_userIdList);
        setNeedMonitor(true);
    }

    @Override
    public DataBuffer encode() {

        Queue<String> userlist = ((QueryUsersInfoRequest) mRequest).getUserIdList();

        DataBuffer bodyBuffer = new DataBuffer();
        int user_cnt = userlist.size();
        if (user_cnt <= 0)
            return null;
        bodyBuffer.writeInt(user_cnt);

        while (null != userlist.peek()) {
            String user = userlist.poll();
            bodyBuffer.writeString(user);
        }

        int bodyLength = bodyBuffer.readableBytes();
        Header RequestQueryUsersInfoHeader = mRequest.getHeader();
        RequestQueryUsersInfoHeader.setLength(SysConstant.PROTOCOL_HEADER_LENGTH + bodyLength);
        DataBuffer headerBuffer = RequestQueryUsersInfoHeader.encode();

        DataBuffer buffer = new DataBuffer(headerBuffer.readableBytes() + bodyLength);
        buffer.writeDataBuffer(headerBuffer);
        buffer.writeDataBuffer(bodyBuffer);
        return buffer;

    }

    @Override
    public void decode(DataBuffer buffer) {

        if (null == buffer)
            return;

        try {
            QueryUsersInfoResponse res = new QueryUsersInfoResponse();

            Header ResponseQueryInfoHeader = new Header();
            ResponseQueryInfoHeader.decode(buffer);
            res.setHeader(ResponseQueryInfoHeader);

            if (ResponseQueryInfoHeader.getServiceId() != ProtocolConstant.SID_BUDDY_LIST
                    ||
                    ResponseQueryInfoHeader.getCommandId() != ProtocolConstant.CID_GET_USER_INFO_RESPONSE)
                return;

            int user_cnt = buffer.readInt();
            res.setUser_cnt(user_cnt);

            for (int i = 0; i < user_cnt; i++) {

                User user = new User();
                user.setUserId(buffer.readString(buffer.readInt()));
                user.setName(buffer.readString(buffer.readInt()));
                user.setNickName(buffer.readString(buffer.readInt()));
                user.setAvatarUrl(buffer.readString(buffer.readInt()));
                user.setTitle(buffer.readString(buffer.readInt()));
                user.setPosition(buffer.readString(buffer.readInt()));
                user.setRoleStatus(buffer.readInt());
                user.setSex(buffer.readInt());
                user.setDepartId(buffer.readString(buffer.readInt()));
                user.setJobNum(buffer.readInt());
                user.setTelphone(buffer.readString(buffer.readInt()));
                user.setEmail(buffer.readString(buffer.readInt()));

                res.addUser(user);
            }

            mResponse = res;
        } catch (Exception e) {
            logger.e(e.getMessage());
        }

    }

    public static class QueryUsersInfoRequest extends Request {
        private Queue<String> userIdList;

        public QueryUsersInfoRequest(Queue<String> _userIdList) {
            userIdList = _userIdList;

            Header queryusersinfoHeader = new Header();
            queryusersinfoHeader.setVersion((short) SysConstant.PROTOCOL_VERSION);
            queryusersinfoHeader.setFlag((short) SysConstant.PROTOCOL_FLAG);
            queryusersinfoHeader.setServiceId(ProtocolConstant.SID_BUDDY_LIST);
            queryusersinfoHeader.setCommandId(ProtocolConstant.CID_GET_USER_INFO_REQUEST);
            queryusersinfoHeader.setError((short) SysConstant.PROTOCOL_ERROR);
            short seqNo = SequenceNumberMaker.getInstance().make();
            queryusersinfoHeader.setReserved(seqNo);

            // content length 过长 放在encode里面设置
            setHeader(queryusersinfoHeader);
        }

        public Queue<String> getUserIdList() {
            return userIdList;
        }

        public void setUserIdList(Queue<String> userIdList) {
            this.userIdList = userIdList;
        }
    }

    public static class QueryUsersInfoResponse extends Response {

        private Queue<User> userList = new LinkedList<User>();
        private int user_cnt;

        public QueryUsersInfoResponse() {

        }

        public void addUser(User user) {
            userList.add(user);
        }

        public Queue<User> getUserList() {
            return userList;
        }

        public void setUserList(Queue<User> userList) {
            this.userList = userList;
        }

        public int getUser_cnt() {
            return user_cnt;
        }

        public void setUser_cnt(int user_cnt) {
            this.user_cnt = user_cnt;
        }

    }
}
