package net.chanakancloud.serverguard.impl.check.impl.combat.killaura;

import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import lombok.NonNull;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.check.CheckType;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.player.PlayerData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@CheckInfo(name = "KillAura (A)", type = CheckType.COMBAT, experimental = true)
public class KillAuraA extends Check {
    private double buffer;
    private long lastFlying;

    private static final Set<Byte> packetToUse = new HashSet<>(Arrays.asList(PacketType.Play.Client.POSITION, PacketType.Play.Client.LOOK, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.FLYING, PacketType.Play.Client.USE_ENTITY));
    public KillAuraA(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    public void handle(byte packetId, @NonNull NMSPacket nmsPacket, @NonNull Object rawPacket, long timestamp) {
        if(!packetToUse.contains(packetId)) return;
        if(packetId == PacketType.Play.Client.USE_ENTITY) {
            if(System.currentTimeMillis() - this.lastFlying < 5) {
                buffer += 1.5;

                if(buffer > 3)
                    flag("currentMillis=" + System.currentTimeMillis(), "lastFlyingPacket=" + this.lastFlying);
            } else buffer = Math.max(0, buffer - 1.25);
            debug("currentMillis=" + System.currentTimeMillis(), "lastFlyingPacket=" + this.lastFlying, "diff=" + (System.currentTimeMillis() - this.lastFlying));
        } else {
            this.lastFlying = System.currentTimeMillis();
        }
    }
}
