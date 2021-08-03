package net.chanakancloud.serverguard.impl.check.impl.combat.killaura;

import lombok.NonNull;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.check.CheckType;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.common.MovementData;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import net.chanakancloud.serverguard.utils.MathUtils;

@CheckInfo(name = "KillAura (B)", type = CheckType.COMBAT, experimental = true)
public class KillAuraB extends Check {
    private float lastDeltaPitch;
    private double buffer;
    public KillAuraB(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    public void handle(MovementData movementData, long timestamp) {
        if(!movementData.getMovementTypes().contains(MovementData.MovementType.PITCH) || !movementData.getMovementTypes().contains(MovementData.MovementType.YAW))
            return;
        final float deltaPitch = Math.abs(movementData.to.getPitch() - movementData.from.getPitch());
        final float lastDeltaPitch = Math.abs(this.lastDeltaPitch);

        if(deltaPitch > 1.0) {
            final long expandedPitch = (long) (deltaPitch * Math.pow(2, 24));
            final long lastExpandedPitch = (long) (lastDeltaPitch * Math.pow(2, 24));

            final double divisorPitch = MathUtils.gcd(expandedPitch, lastExpandedPitch);
            final double constantPitch = divisorPitch / Math.pow(2, 24);

            final double pitch = movementData.to.getPitch();
            final double moduloPitch = Math.abs(pitch % constantPitch);
            if(moduloPitch < 1.2e-5) {
                buffer += 1.5;

                if(buffer > 2) {
                    flag("moduloPitch=" + moduloPitch);
                }
            } else buffer = Math.max(0, buffer - 1.25);
            debug("moduloPitch=" + moduloPitch);
        }

        this.lastDeltaPitch = deltaPitch;
    }
}
