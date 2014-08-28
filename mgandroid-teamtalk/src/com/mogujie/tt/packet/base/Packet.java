
package com.mogujie.tt.packet.base;

/**
 * 协议包基类，子类必须实现{@link #encode()}和{@link #decode(DataBuffer)}
 * 
 * @author dolphinWang
 * @time 2014/04/30
 */
public abstract class Packet {

    protected Request mRequest;

    protected Response mResponse;

    protected boolean mNeedMonitor;

    public void setNeedMonitor(boolean bNeedMonitor) {
        mNeedMonitor = bNeedMonitor;
    }

    public int getSequenceNo() {
        return (int) (mRequest.mHeader.getReserved());
    }

    public boolean getNeedMonitor() {
        return mNeedMonitor;
    }

    public Response getResponse() {
        return mResponse;
    }

    public void setRequest(Request request) {
        mRequest = request;
    }

    public Request getRequest() {
        return mRequest;
    }

    /**
     * 把Request数据结构编码成一个DataBuffer，必须先调用setRequest
     */
    public abstract DataBuffer encode();

    /**
     * 把DataBuffer解包构造一个Response对象，getResponse函数必须在调用完decode函数之后才能得到真实的包
     */
    public abstract void decode(DataBuffer buffer);

    /**
     * 请求包的数据结构基类，子类可以继承后添加属于自己的字段
     */
    public static class Request {
        /*** 成员变量 ***/
        protected Header mHeader;

        public Header getHeader() {
            return mHeader;
        }

        public void setHeader(Header header) {
            mHeader = header;
        }
    }

    /**
     * 应答包的数据结构基类，子类可以继承后添加属于自己的字段
     */
    public static class Response {
        /*** 成员变量 ***/
        protected Header mHeader;

        public Header getHeader() {
            return mHeader;
        }

        public void setHeader(Header header) {
            mHeader = header;
        }
    }
}
