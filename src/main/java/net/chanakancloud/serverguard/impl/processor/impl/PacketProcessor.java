package net.chanakancloud.serverguard.impl.processor.impl;

import io.github.retrooper.packetevents.event.eventtypes.CancellableNMSPacketEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import lombok.NonNull;
import net.chanakancloud.serverguard.ServerGuard;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import net.chanakancloud.serverguard.impl.processor.Processor;
import org.bukkit.entity.Player;

public class PacketProcessor extends Processor {
    public int ping;
    public long lastFlying;
    public PacketProcessor(@NonNull PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        Player player = event.getPlayer();
        if(!player.getUniqueId().equals(playerData.getBukkitPlayer().getUniqueId()))
            return;

        switch(event.getPacketId()) {
            case PacketType.Play.Client.KEEP_ALIVE: {
                ping = ServerGuard.INSTANCE.getPacketEvents().getPlayerUtils().getPing(player);
                break;
            }
            case PacketType.Play.Client.FLYING: {
                lastFlying = System.currentTimeMillis();
                break;
            }
        }

        hopperIntoChecks(event);
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if(!event.getPlayer().getUniqueId().equals(playerData.getBukkitPlayer().getUniqueId()))
            return;

        hopperIntoChecks(event);
    }

    private void hopperIntoChecks(@NonNull CancellableNMSPacketEvent nmsPacketEvent) {
        NMSPacket nmsPacket = nmsPacketEvent.getNMSPacket();
        long timestamp = System.currentTimeMillis();
        playerData.getChecks().parallelStream().forEach(check ->
                check.handle(nmsPacketEvent.getPacketId(), nmsPacket, nmsPacket.getRawNMSPacket(), timestamp));
    }
}
