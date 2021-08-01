package net.chanakancloud.serverguard.impl.check.impl.movement.fly;

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

@CheckInfo(name = "Fly (C)", type = CheckType.MOVEMENT, experimental = true)
public class FlyC extends Check {
    public FlyC(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    private double buffer = 0.0d;
    private int ticks = 0;

    @Override
    public void handle(MovementData movementData, long timestamp) {
        final double deltaY = movementData.to.getY() - movementData.from.getY();
        final double estimation = (playerData.lastDeltaY - 0.08) * 0.9800000190734863;
        final boolean resetting = Math.abs(deltaY) + 0.0980000019 < 0.05;


        if(movementData.from.getY() % (1D/64D) < 0.0001 ||
                movementData.to.getY() % (1D/64D) < 0.0001)
            return;

        if(player.isGliding())
            return;
        if(resetting) return;
        if(player.isFlying()) return;
        if(player.isInsideVehicle() && deltaY < playerData.lastDeltaY) {
            buffer = Math.max(0, buffer - 1.25);
            return;
        }


        if(ServerUtil.lowTPS())
            return;

        final boolean illegal = Utilities.isNearWater(player) || Utilities.isNearClimbable(player);
        //check water & climbable

        debug("estimation=" + estimation, "deltaY=" + deltaY, "touching" + (movementData.getTouchingClimbable().get() || movementData.getTouchingLiquid().get()));
        if(illegal || movementData.getTouchingClimbable().get() || movementData.getTouchingLiquid().get())
        {
            buffer = Math.max(0, buffer - 1.25);
            playerData.airTicks = 0;
            return;
        }
        if(playerData.airTicks > 4 && !(movementData.to.getY() % (1d/64d) < 0.0001)) {
            ++ticks;
            if(ticks > 2 && Math.abs(estimation - deltaY) > 0.01) {
                buffer += 1.5;

                if(buffer > 2.2)
                    flag("estimation=" + estimation, "deltaY=" + deltaY);
            } else {
                buffer = Math.max(0, buffer - 1.25);
            }
        } else {
            ticks = 0;
        }

        playerData.lastDeltaY = (float) deltaY;
    }
}
