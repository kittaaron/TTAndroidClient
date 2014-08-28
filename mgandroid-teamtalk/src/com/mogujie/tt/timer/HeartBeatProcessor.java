
package com.mogujie.tt.timer;

import com.mogujie.tt.config.ProtocolConstant;
import com.mogujie.tt.conn.StateManager;
import com.mogujie.tt.packet.PacketDistinguisher;
import com.mogujie.tt.packet.SocketMessageQueue;
import com.mogujie.tt.packet.action.Action;
import com.mogujie.tt.packet.action.Action.Builder;
import com.mogujie.tt.packet.base.Packet;

public class HeartBeatProcessor implements ITimerProcessor {

    private static StateManager smInstance = StateManager.getInstance();

    @Override
    public void process() {

        if (smInstance.isOnline())

        {

            Packet packet = PacketDistinguisher.make(ProtocolConstant.SID_OTHER,
                    ProtocolConstant.CID_HEART_BEAT, null, true);

            if (null == packet)

                return;

            Builder builer = new Builder();

            Action action = builer.setPacket(packet).setCallback(null).build();

            SocketMessageQueue.getInstance().submitAndEnqueue(action);

        }

    }

}
