
package com.mogujie.tt.packet;

import java.util.Queue;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.entity.MessageInfo;
import com.mogujie.tt.log.Logger;
import com.mogujie.tt.packet.base.Packet;
import com.mogujie.tt.packet.biz.ConfirmRecvMsgPacket;
import com.mogujie.tt.packet.biz.ConfirmUnReadMsgInfoPacket;
import com.mogujie.tt.packet.biz.FriendStatusNotifyPacket;
import com.mogujie.tt.packet.biz.HeartBeatPacket;
import com.mogujie.tt.packet.biz.KickOffPacket;
import com.mogujie.tt.packet.biz.LoginPacket;
import com.mogujie.tt.packet.biz.MessagePacket;
import com.mogujie.tt.packet.biz.MsgServerPacket;
import com.mogujie.tt.packet.biz.QueryUserOnlineStatusPacket;
import com.mogujie.tt.packet.biz.QueryUsersInfoPacket;
import com.mogujie.tt.packet.biz.RecentContactPacket;
import com.mogujie.tt.packet.biz.RecvMessagePacket;
import com.mogujie.tt.packet.biz.UnReadMsgCountPacket;
import com.mogujie.tt.packet.biz.UnReadMsgInfoPacket;

/**
 * 一个工厂类，所有的{@link Packet}的初始化都应该由这个类来进行. 根据ServiceID和CommandID。
 * 
 * @author dolphinWang
 */
public class PacketDistinguisher {

    private PacketDistinguisher() {
    }

    @SuppressWarnings("unchecked")
    /**
     *  packet工厂
     *  serviceID: SID
     *  commandID: CID
     *  param:     构造packet的参数，具体看每个packet的构造函数
     *  bSendPacket: 是否是用于发送的packet，因为有些packet既可以发送也可以接收
     * */
    public static Packet make(int serviceID, int commandID, Object[] param,
            boolean bSendPacket) {
        Packet packet = null;

        try {
            switch (serviceID) {
                case ProtocolConstant.SID_LOGIN: {
                    switch (commandID) {
                        case ProtocolConstant.CID_LOGIN_REQ_MSGSERVER:
                            packet = new MsgServerPacket();
                            break;
                        case ProtocolConstant.CID_LOGIN_REQ_USERLOGIN:
                            if (param != null && param.length >= 5) {
                                String name = (String) param[0];
                                String password = (String) param[1];
                                int _online_status = (Integer) param[2];
                                int _client_type = (Integer) param[3];
                                String _client_version = (String) param[4];
                                if (name != null && password != null)
                                    packet = new LoginPacket(name, password,
                                            _online_status, _client_type, _client_version);
                            }
                            break;
                        case ProtocolConstant.CID_LOGIN_KICK_USER:
                            packet = new KickOffPacket();
                            break;
                        default:
                            break;
                    }
                }
                    break;

                case ProtocolConstant.SID_BUDDY_LIST: {
                    switch (commandID) {
                        case ProtocolConstant.CID_REQUEST_RECNET_CONTACT:
                            packet = new RecentContactPacket();
                            break;
                        case ProtocolConstant.CID_GET_USER_INFO_REQUEST:
                            if (param != null && param.length >= 1) {
                                Queue<String> _userIdList = (Queue<String>) (param[0]);
                                if (_userIdList != null && _userIdList.size() > 0) {
                                    packet = new QueryUsersInfoPacket(_userIdList);
                                }
                            }
                            break;
                        case ProtocolConstant.CID_QUERY_USER_ONLINE_STATUS_REQUEST:
                            if (param != null && param.length >= 1) {
                                String _user_id = (String) param[0];
                                if (_user_id != null)
                                    packet = new QueryUserOnlineStatusPacket(_user_id);
                            }
                            break;
                        case ProtocolConstant.CID_CONTACT_FRIEND_STATUS_NOTIYF:
                            packet = new FriendStatusNotifyPacket();
                            break;
                        default:
                            break;
                    }
                }
                    break;

                case ProtocolConstant.SID_MSG: {
                    switch (commandID) {
                        case ProtocolConstant.CID_MSG_DATA:
                            if (bSendPacket) {
                                if (param != null && param.length >= 1) {
                                    MessageInfo info = (MessageInfo) (param[0]);
                                    if (info != null)
                                        packet = new MessagePacket(info);
                                }
                            } else {
                                packet = new RecvMessagePacket();
                            }

                            break;
                        case ProtocolConstant.CID_MSG_DATA_ACK:
                            if (param != null && param.length >= 2) {
                                int _seq_no = (Integer) (param[0]);
                                String _from_id = (String) (param[1]);
                                if (_from_id != null)
                                    packet = new ConfirmRecvMsgPacket(_seq_no, _from_id);
                            }
                            break;
                        case ProtocolConstant.CID_MSG_READ_ACK:
                            if (param != null && param.length >= 1) {
                                String _from_id = (String) (param[0]);
                                if (_from_id != null)
                                    packet = new ConfirmUnReadMsgInfoPacket(_from_id);
                            }
                            break;
                        case ProtocolConstant.CID_MSG_UNREAD_CNT_REQUEST:
                            packet = new UnReadMsgCountPacket();
                            break;
                        case ProtocolConstant.CID_MSG_UNREAD_MSG_REUQEST:
                            if (param != null && param.length >= 1) {
                                String _from_id = (String) (param[0]);
                                if (_from_id != null) {
                                    packet = new UnReadMsgInfoPacket(_from_id);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                    break;

                case ProtocolConstant.SID_DEFAULT: {
                    switch (commandID) {
                        case ProtocolConstant.CID_HEART_BEAT:
                            packet = new HeartBeatPacket();
                            break;
                        default:
                            break;
                    }
                }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            Logger.getLogger(PacketDistinguisher.class).e(e.getMessage());
        }

        return packet;
    }
}
