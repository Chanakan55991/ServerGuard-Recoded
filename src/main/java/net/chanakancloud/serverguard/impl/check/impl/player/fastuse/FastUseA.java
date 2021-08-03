package net.chanakancloud.serverguard.impl.check.impl.player.fastuse;

import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import lombok.NonNull;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.check.CheckType;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.player.PlayerData;

@CheckInfo(name = "FastUse (A)", type = CheckType.PLAYER, experimental = true)
public class FastUseA extends Check {
    private long lastBlockPlace;
    private int buffer;
    public FastUseA(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    @Override
    public void handle(byte packetId, @NonNull NMSPacket nmsPacket, @NonNull Object packet, long timestamp) {
        if(packetId == PacketType.Play.Client.BLOCK_DIG) {
            long delta = timestamp - lastBlockPlace;

            if(delta < 2) {
                if(buffer++ > 2) {
                    flag("delta=" + delta);
                }
            }
        } else if(packetId == PacketType.Play.Client.BLOCK_PLACE) lastBlockPlace = timestamp;
    }
}
