package net.chanakancloud.serverguard.impl.check.impl.combat.reach;

import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.play.out.entitydestroy.WrappedPacketOutEntityDestroy;
import lombok.NonNull;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.check.CheckType;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@CheckInfo(name = "Reach (A)", type = CheckType.COMBAT, experimental = true)
public class ReachA extends Check {

    public ReachA(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    public void handle(byte packetId, @NonNull NMSPacket nmsPacket, @NonNull Object rawPacket, long timestamp) {
        if(packetId == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity packet = new WrappedPacketInUseEntity(nmsPacket);

            Entity taking = packet.getEntity();
            double limit = 3.5;
            Location locationDamager = player.getLocation();
            assert taking != null;
            Location locationDamagee = taking.getLocation();
            double LocationDamagerX = locationDamager.getX();
            double LocationDamagerY = locationDamager.getY() + 1.0;
            double LocationDamagerZ = locationDamager.getZ();
            double LocationDamageeX = locationDamagee.getX();
            double LocationDamageeY = locationDamagee.getY();
            double LocationDamageeZ = locationDamagee.getZ();
            double range;
            range = Math.sqrt(Math.pow(LocationDamagerX - LocationDamageeX, 2) + Math.pow(LocationDamagerY - LocationDamageeY, 2)+ Math.pow(LocationDamagerZ - LocationDamageeZ, 2));
            if (range >= limit)
                flag("range=" + range);
            debug("range=" + range);
        }
    }
}
