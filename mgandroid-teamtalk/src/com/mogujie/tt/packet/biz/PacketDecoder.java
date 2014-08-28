
package com.mogujie.tt.packet.biz;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.mogujie.tt.config.SysConstant;
import com.mogujie.tt.packet.base.DataBuffer;

public class PacketDecoder extends FrameDecoder {

    /**
     * 解析数据包，主要负责解析数据包前8个字节统一格式的头部信息，生成Packet对象， 剩余的数据部分的解析在后面具体的action处理
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel,
            ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < SysConstant.PROTOCOL_HEADER_LENGTH) {
            return null;
        }
        ChannelBuffer packetContent = buffer.readBytes(buffer.readableBytes());
        DataBuffer dataBuffer = new DataBuffer(packetContent);

        return dataBuffer;

    }

}
