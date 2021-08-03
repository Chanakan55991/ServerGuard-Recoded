package net.chanakancloud.serverguard.impl.check.impl.movement.fly;

import io.github.retrooper.packetevents.packettype.PacketType;
import it.unimi.dsi.fastutil.Hash;
import live.chanakancloud.taputils.utils.BlockUtils;
import live.chanakancloud.taputils.utils.MiscUtils;
import live.chanakancloud.taputils.utils.PlayerUtils;
import lombok.NonNull;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.check.CheckType;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.common.MovementData;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import net.chanakancloud.serverguard.utils.ServerUtil;
import net.chanakancloud.serverguard.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CheckInfo(name = "Fly (A)", type = CheckType.MOVEMENT, experimental = true)
public class FlyA extends Check {
    private Map<UUID, Double> buffer = new HashMap<>();
    private double xDistance, yDistance, zDistance, hDistance;

    public FlyA(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    @Override
    public void handle(MovementData movementData, long timestamp) {
        float deltaY = (float) (movementData.to.getY() - movementData.from.getY());

        if (player.isFlying() || player.isGliding()) return;
        if(ServerUtil.lowTPS())
            return;

        //detect water only

        if(Utilities.isNearLiquid(movementData.to) || movementData.getTouchingLiquid().get())
        {
            playerData.flyThreshold -= playerData.flyThreshold > 0 ? 0.1f : 0;
            playerData.airTicks = 0;
            return;
        }
        //final boolean illegal = Utilities.isNearWater(player) || Utilities.isNearClimbable(player);

        if (playerData.airTicks > 4 && !(movementData.to.getY() % (1d / 64d) < 0.0001) && deltaY > playerData.lastDeltaY) {
            if (playerData.flyThreshold++ > 2) {
                flag("deltaY=" + deltaY, "lastDeltaY=" + playerData.lastDeltaY);
            }
        } else playerData.flyThreshold -= playerData.flyThreshold > 0 ? 0.1f : 0;

        debug("deltaY=" + deltaY, "lastDeltaY=" + playerData.lastDeltaY);
        playerData.lastDeltaY = deltaY;
    }
}
