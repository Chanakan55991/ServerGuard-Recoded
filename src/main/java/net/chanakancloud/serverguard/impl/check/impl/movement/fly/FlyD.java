package net.chanakancloud.serverguard.impl.check.impl.movement.fly;

import lombok.NonNull;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.check.CheckType;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.common.MovementData;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import net.chanakancloud.serverguard.utils.Utilities;

@CheckInfo(name = "Fly (D)", type = CheckType.MOVEMENT, experimental = true)
public class FlyD extends Check {
    private double lastDeltaY;
    private double buffer = 0.0d;
    boolean isJumping;

    public FlyD(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    @Override
    public void handle(MovementData movementData, long timestamp) {
        final double deltaY = movementData.to.getY() - movementData.from.getY();
        isJumping = deltaY > lastDeltaY;
        if(Utilities.isNearLiquid(movementData.to) || Utilities.isNearClimbable(movementData.to) || movementData.getTouchingClimbable().get() || movementData.getTouchingLiquid().get())
        {
            buffer = Math.max(0, buffer - 1.25);
            playerData.airTicks = 0;
            return;
        }
        if(playerData.airTicks > 4 && isJumping && (!(movementData.to.getY() % (1d/64d) < 0.0001))) {
            flag("isJumping: " + isJumping, "isOnAir (Math): " + !(movementData.to.getY() % (1d/64d) < 0.0001));
        }  else {
            buffer = Math.max(0, buffer - 1.25);
        }
        debug("isJumping: " + isJumping, "buffer: " + buffer, "isOnAir (Math): " + !(movementData.to.getY() % (1d/64d) < 0.0001));
        this.lastDeltaY = deltaY;
    }
}
