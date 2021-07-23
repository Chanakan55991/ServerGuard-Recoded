package net.chanakancloud.serverguard.impl.check;

import net.chanakancloud.serverguard.impl.check.impl.movement.fly.FlyA;
import net.chanakancloud.serverguard.impl.check.impl.movement.fly.FlyB;
import net.chanakancloud.serverguard.impl.check.impl.movement.fly.FlyC;

import java.util.List;

public class CheckManager {
    //more classes here
    public static final List<Class<? extends Check>> CHECK_CLASSES = List.of(
            FlyA.class,
            FlyB.class,
            FlyC.class
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
