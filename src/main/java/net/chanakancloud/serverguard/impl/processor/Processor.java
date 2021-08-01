package net.chanakancloud.serverguard.impl.processor;

import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.PacketListenerPriority;
import lombok.NonNull;
import net.chanakancloud.serverguard.ServerGuard;
import net.chanakancloud.serverguard.impl.player.PlayerData;

public class Processor extends PacketListenerAbstract {
    protected final PlayerData playerData;

    public Processor(@NonNull PlayerData playerData) {
        super(PacketListenerPriority.NORMAL);
        this.playerData = playerData;
        ServerGuard.INSTANCE.getPacketEvents().getEventManager().registerListener(this);
    }
}
