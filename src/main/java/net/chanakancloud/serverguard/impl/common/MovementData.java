package net.chanakancloud.serverguard.impl.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.chanakancloud.serverguard.observable.Observable;
import org.bukkit.Location;

import java.util.List;

@AllArgsConstructor @Getter
public class MovementData {
    public final Location from, to;
    private final Observable<Boolean> touchingAir = new Observable<>(false);
    private final Observable<Boolean> touchingLiquid = new Observable<>(false);
    private final Observable<Boolean> touchingHalfBlock = new Observable<>(false);
    private final Observable<Boolean> touchingClimbable = new Observable<>(false);
    private final Observable<Boolean> touchingIllegalBlock = new Observable<>(false);
    private final Observable<Object[]> nearbyEntities = new Observable<>(null);
    private final List<MovementType> movementTypes;

    public boolean isPositionLook() {
        return isPosition() && isPositionLook();
    }
    public boolean isAscending() {
        return to.getY() > from.getY();
    }
    public boolean isDescending() {
        return !isAscending();
    }
    public boolean isPosition() {
        return movementTypes.contains(MovementType.X) || movementTypes.contains(MovementType.Y) || movementTypes.contains(MovementType.Z);
    }

    public boolean isLook() {
        return movementTypes.contains(MovementType.YAW) || movementTypes.contains(MovementType.PITCH);
    }

    public enum MovementType {
        X, Y, Z, YAW, PITCH
    }

}
