package net.chanakancloud.serverguard.impl.check.impl.movement.speed;

import live.chanakancloud.taputils.math.MathUtils;
import live.chanakancloud.taputils.utils.BlockUtils;
import lombok.NonNull;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.check.CheckType;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.common.MovementData;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import net.chanakancloud.serverguard.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed (A)", type = CheckType.MOVEMENT, experimental = true)
public class SpeedA extends Check {
    private double buffer = 0.0;

    public SpeedA(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    private float getBaseSpeed(Player player) {
        float returner = (float) 0.34
                + (Utilities.getPotionLevel(player, PotionEffectType.SPEED) * 0.062f)
                + ((player.getWalkSpeed() - 0.2f) * 1.6f);
        if (Utilities.isNearMaterial(player.getLocation(), Material.ICE)) {
            returner += 0.34;
        }
        if (BlockUtils.getTopBlock(player) != null) {
            returner += 0.8;
        }

        return returner;
    }

    public void handle(MovementData movementData, long timestamp) {
        final double maxDist = getBaseSpeed(player);
        final double XZDiff = MathUtils.hypot(movementData.to.getX() - movementData.from.getX(), movementData.to.getZ() - movementData.from.getZ());

        if (playerData.airTicks > 5 && XZDiff > maxDist
                && !player.getAllowFlight() && !Utilities.isNearVehicle(player)
                && !player.isRiptiding()) {
            flag("XZDiff=" + XZDiff, "maxDist=" + maxDist);
        }
        debug("XZDiff=" + XZDiff, "maxDist=" + maxDist);
    }
}
