package net.chanakancloud.serverguard.impl.check.impl.test;

import lombok.NonNull;
import net.chanakancloud.serverguard.ServerGuard;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.check.CheckType;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.common.MovementData;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import net.chanakancloud.serverguard.utils.Utilities;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Sensitivity (A)", type = CheckType.TEST, experimental = true)
public class SensitivityA extends Check {
    private float lastdeltaPitch;
    private double lastSens;
    private int methodCall = 0;
    private final List<Double> sensSamples = new ArrayList<>();
    public SensitivityA(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    static double gcdFunc(double a, double b)
    {
        if (a < b)
            return gcdFunc(b, a);

        // base case
        if (Math.abs(b) < 0.001)
            return a;

        else
            return (gcdFunc(b, a -
                    Math.floor(a / b) * b));
    }

    @Override
    public void handle(MovementData movementData, long timestamp) {
        methodCall++;
        if(!movementData.getMovementTypes().contains(MovementData.MovementType.PITCH) || !movementData.getMovementTypes().contains(MovementData.MovementType.YAW))
            return;
        final float deltaPitch = Math.abs(movementData.to.getPitch() -  movementData.from.getPitch());
        final float lastDeltaPitch = Math.abs(this.lastdeltaPitch);

        if(methodCall >= 10) {
            sensSamples.clear();
            sensSamples.add(lastSens);
            methodCall = 0;
        }
        for (int i = 0; i < 50; i++) {
            final float gcd = (float) gcdFunc(deltaPitch, lastDeltaPitch);
            final double sensModifier = Math.cbrt(0.8333 * gcd);
            final double sens = ((1.666 * sensModifier) - 0.3333) * 200;
            sensSamples.add(sens);
        }
        final double sens = Utilities.getPopularElement(sensSamples.toArray(new Double[0]));
        debug("sens = " + Math.round(sens) + "%", "methodCall: " + methodCall, "mouseY: " + movementData.to.getPitch());

        this.lastdeltaPitch = deltaPitch;
        this.lastSens = sens;
    }
}
