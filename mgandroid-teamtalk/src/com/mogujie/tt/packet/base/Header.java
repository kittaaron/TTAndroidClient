
package com.mogujie.tt.packet.base;

import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.log.Logger;

/**
 * TCP协议的头文件
 * 
 * @author dolphinWang
 * @time 2014/04/30
 */
public class Header {

    private Logger logger = Logger.getLogger(Header.class);

    private int length; // 数据包长度，包括包头

    private short version; // 版本号

    private short flag; // 标记: 分拆数据包，压缩

    private int serviceId; // SID

    private int commandId; // CID

    private short error; // 服务器错误

    private short reserved; // 保留，可用于如序列号等

    public Header() {
        length = 0;
        version = 0;
        flag = 0;
        serviceId = 0;
        commandId = 0;
        error = 0;
        reserved = 0;
    }

    /**
     * 头文件的压包函数
     * 
     * @return 数据包
     */
    public DataBuffer encode() {
        DataBuffer db = new DataBuffer(SysConstant.PROTOCOL_HEADER_LENGTH);
        db.writeInt(length);
        db.writeShort(version);
        db.writeShort(flag);
        db.writeShort((short) serviceId);
        db.writeShort((short) commandId);
        db.writeShort(error);
        db.writeShort(reserved);
        return db;
    }

    /**
     * 头文件的解包函数
     * 
     * @param buffer
     */
    public void decode(DataBuffer buffer) {
        if (null == buffer)
            return;
        try {
            length = buffer.readInt();
            version = buffer.readShort();
            flag = buffer.readShort();
            serviceId = buffer.readShort();
            commandId = buffer.readShort();
            error = buffer.readShort();
            reserved = buffer.readShort();
        } catch (Exception e) {
            logger.e(e.getMessage());
        }
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandID) {
        this.commandId = commandID;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceID) {
        this.serviceId = serviceID;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public short getError() {
        return error;
    }

    public void setError(short error) {
        this.error = error;
    }

    public short getReserved() {
        return reserved;
    }

    public void setReserved(short reserved) {
        this.reserved = reserved;
    }

}
