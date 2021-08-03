package net.chanakancloud.serverguard.impl.check;

import net.chanakancloud.serverguard.impl.check.impl.combat.killaura.KillAuraA;
import net.chanakancloud.serverguard.impl.check.impl.combat.killaura.KillAuraB;
import net.chanakancloud.serverguard.impl.check.impl.combat.reach.ReachA;
import net.chanakancloud.serverguard.impl.check.impl.movement.fly.FlyA;
import net.chanakancloud.serverguard.impl.check.impl.movement.fly.FlyB;
import net.chanakancloud.serverguard.impl.check.impl.movement.fly.FlyC;
import net.chanakancloud.serverguard.impl.check.impl.movement.speed.SpeedA;
import net.chanakancloud.serverguard.impl.check.impl.player.fastuse.FastUseA;
import net.chanakancloud.serverguard.impl.check.impl.test.SensitivityA;

import java.util.List;

public class CheckManager {
    //more classes here
    public static final List<Class<? extends Check>> CHECK_CLASSES = List.of(
            FlyA.class,
            FlyB.class,
            FlyC.class,
            FastUseA.class,
            SpeedA.class,
            ReachA.class,
            KillAuraA.class,
            SensitivityA.class
    );

    public static Class<? extends Check> getCheckClass(String name) {
        for (Class<? extends Check> checkClass : CHECK_CLASSES) {
            if (checkClass.getSimpleName().equalsIgnoreCase(name)) {
                return checkClass;
            }
        }
        return null;
    }
}
