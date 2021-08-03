package net.chanakancloud.serverguard.impl.check.impl.movement.fly;

import lombok.NonNull;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.check.CheckType;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.common.MovementData;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import net.chanakancloud.serverguard.utils.Utilities;
import org.bukkit.entity.LivingEntity;

@CheckInfo(name = "Fly (B)", type = CheckType.MOVEMENT, experimental = true)
public class FlyB extends Check {
    public FlyB(@NonNull PlayerData playerData) throws ClassNotFoundException {
        super(playerData);
    }

    @Override
    public void handle(MovementData movementData, long timestamp) {
        final boolean clientGround = ((LivingEntity)(player)).isOnGround();
        final boolean serverGround = movementData.to.getY() % (1D/64D) < 0.0001;

        //final boolean illegal = Utilities.isNearMaterials(movementData.to, Material.LADDER, Material.VINE, Material.WATER, Material.LAVA) || PlayerUtils.isNearWater(player);

        if(playerData.getBukkitPlayer().isInsideVehicle())
            return;
        if(Utilities.isNearLiquid(movementData.to))
            return;
        if(clientGround != serverGround) {
            if(playerData.flyBThreshold++ > 2) {
                flag("serverGround=" + serverGround, "clientGround=" + clientGround);
            }
        } else playerData.flyBThreshold-=playerData.flyBThreshold > 0 ? 0.1f : 0;

        debug("serverGround=" + serverGround, "clientGround=" + clientGround);
    }
}
