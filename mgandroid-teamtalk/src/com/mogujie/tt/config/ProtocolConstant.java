
package com.mogujie.tt.config;

public class ProtocolConstant {

    public static final int USER_MSG_TYPE = 1;

    public static final int IM_PDU_VERSION = 3;

    // SERVICE_ID
    public static final int SID_LOGIN = 0x0001;
    public static final int SID_BUDDY_LIST = 0x0002;
    public static final int SID_MSG = 0x0003;
    public static final int SID_SWITCH_SERVER = 0x0004;
    public static final int SID_GROUP = 0x0005;
    public static final int SID_FILE = 0x0006;
    public static final int SID_OTHER = 0x0007;
    public static final int SID_DEFAULT = 0x0007;

    // LOGIN IP,PORT
    public static final String LOGIN_IP1 = "122.225.68.125";
    public static final String LOGIN_IP2 = "101.68.218.125";
    public static final int LOGIN_PORT = 9008;

    // COMMAND_ID FOR LOGIN
    public static final int CID_LOGIN_REQ_MSGSERVER = 1;
    public static final int CID_LOGIN_RES_MSGSERVER = 2;
    public static final int CID_LOGIN_REQ_USERLOGIN = 3;
    public static final int CID_LOGIN_RES_USERLOGIN = 4;
    public static final int CID_LOGIN_REQ_LOGINOUT = 5;
    public static final int CID_LOGIN_RES_LOGINOUT = 6;
    public static final int CID_LOGIN_KICK_USER = 7;
    
    //CONTACT
    public static final int CID_REQUEST_RECNET_CONTACT = 1;
    
    public static final int CID_GET_USER_INFO_REQUEST = 11;// 请求用户信息
    
    public static final int CID_GET_USER_INFO_RESPONSE = 10;// 获取用户信息
    
    
    public static final int ON_LINE = 1;
    // COMMAND_ID FOR MSG
    public static final int CID_MSG_DATA = 1;
    public static final int CID_MSG_DATA_ACK = 2;
    public static final int CID_MSG_READ_ACK = 3;
    public static final int CID_MSG_TIME_REQUEST = 5;
    public static final int CID_MSG_TIME_RESPONSE = 6;
    public static final int CID_MSG_UNREAD_CNT_REQUEST = 7;
    public static final int CID_MSG_UNREAD_CNT_RESPONSE = 8;
    public static final int CID_MSG_UNREAD_MSG_REUQEST = 9;
    public static final int CID_MSG_HISTORY_MSG_REQUEST = 10;
    // public static final int CID_MSG_LIST_RESPONSE = 11;
    public static final int CID_MSG_HISTORY_SERVICE_MSG_REQUEST = 12;
    public static final int CID_MSG_HISTORY_SERVICE_MSG_RESPONSE = 13;

    public static final int CID_MSG_UNREAD_MSG_RESPONSE = 14;
    public static final int CID_MSG_HISTORY_MSG_RESPONSE = 15;

    public static final int CID_SHOP_MEMBER_RESPONSE = 2;
    public static final int CID_CONTACT_RECENT_RESPONSE = 3;
    public static final int CID_CONTACT_FRIEND_STATUS_NOTIYF = 5;
    public static final int CID_QUERY_USER_ONLINE_STATUS_REQUEST = 8;
    public static final int CID_QUERY_USER_ONLINE_STATUS_RESPONSE = 9;

    public static final int CID_HEART_BEAT = 1;

    public static final int RES_RESULT_SUCCESS = 0;

    public static final int CLIENT_TYPE = 4;// 表示android，登陆消息服务器时使用

    public static final String CLIENT_VERSION = "ANDROID_TEAMTALK_V1.0.1";

}
