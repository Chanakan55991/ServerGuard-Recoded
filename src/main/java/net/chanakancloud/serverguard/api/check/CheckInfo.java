package net.chanakancloud.serverguard.api.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckInfo {
    String name();
    CheckType type();
    boolean experimental() default false;
    int maxVl() default 10;
    boolean ban() default true;
}
