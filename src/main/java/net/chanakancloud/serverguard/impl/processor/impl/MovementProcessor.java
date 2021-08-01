package net.chanakancloud.serverguard.impl.processor.impl;

import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import live.chanakancloud.taputils.utils.MiscUtils;
import lombok.NonNull;
import net.chanakancloud.serverguard.data.BoundingBox;
import net.chanakancloud.serverguard.impl.common.MovementData;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import net.chanakancloud.serverguard.impl.processor.Processor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MovementProcessor extends Processor {
    private Location from = null;

    public MovementProcessor(@NonNull PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketId() == PacketType.Play.Client.POSITION || event.getPacketId() == PacketType.Play.Client.POSITION_LOOK) {
//            final Object[] entities = playerData.getBukkitPlayer().getNearbyEntities(3, 3, 3).toArray();
            WrappedPacketInFlying packet = new WrappedPacketInFlying(event.getNMSPacket());
            if (!event.getPlayer().getUniqueId().equals(playerData.getBukkitPlayer().getUniqueId()))
                return;
            if (playerData.getBukkitPlayer().getAllowFlight() /*|| playerData.getBukkitPlayer().isInsideVehicle() */)
                return;

            Location to = new Location(playerData.getBukkitPlayer().getWorld(), packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ());
            if (from == null) {
                from = to;
            }
            boolean inAir = !(to.getY() % (1d / 64d) < 0.0001) || !(from.getY() % (1d / 64d) < 0.0001);

            playerData.airTicks = inAir ? playerData.getAirTicks() + 1 : 0;

            List<MovementData.MovementType> movementTypes = new ArrayList<>();
            if (from.getX() != to.getX())
                movementTypes.add(MovementData.MovementType.X);
            if (from.getY() != to.getY())
                movementTypes.add(MovementData.MovementType.Y);
            if (from.getZ() != to.getZ())
                movementTypes.add(MovementData.MovementType.Z);
            if (from.getYaw() != to.getYaw())
                movementTypes.add(MovementData.MovementType.YAW);
            if (from.getPitch() != to.getPitch())
                movementTypes.add(MovementData.MovementType.PITCH);
            MovementData movementData = new MovementData(from, to, movementTypes);
            final BoundingBox boundingBox = new BoundingBox(from.getX(), from.getY(), from.getZ(), event.getPlayer().getWorld());

            playerData.getBoundingBox().set(boundingBox);
            playerData.getBoundingBoxes().add(boundingBox);

            this.handleCollisions(boundingBox, movementData);

            //playerData.boundingBox = ReflectionsUtil.getBoundingBox(event.getPlayer());
            //event.getPlayer().sendMessage(playerData.boundingBox.toString());
            //playerData.nearGround = ReflectionsUtil.getCollidingBlocks(event.getPlayer(), ReflectionsUtil
            //      .modifyBoundingBox(playerData.boundingBox, 0, -1,0,0,0,0)).size() > 0;

            // deltaY = Math.abs(from.getY() - to.getY());

            long timestamp = System.currentTimeMillis();
            playerData.getChecks().parallelStream().forEach(check -> check.handle(movementData, timestamp));
            from = to;
        }
    }

    private synchronized void handleCollisions(final BoundingBox boundingBox, MovementData movementData) {
        boundingBox.expand(0.5, 0.07, 0.5).move(0.0, -0.55, 0.0);

        boolean touchingAir = boundingBox.checkBlocks(material -> material == Material.AIR);
        boolean touchingLiquid = boundingBox.checkBlocks(material -> material == Material.WATER || material == Material.LAVA);
        boolean touchingHalfBlock = boundingBox.checkBlocks(material -> material == Material.CUT_COPPER_STAIRS
                || material == Material.EXPOSED_CUT_COPPER_STAIRS
                || material == Material.WEATHERED_CUT_COPPER_STAIRS
                || material == Material.OXIDIZED_CUT_COPPER_STAIRS
                || material == Material.WAXED_CUT_COPPER_STAIRS
                || material == Material.WAXED_EXPOSED_CUT_COPPER_STAIRS
                || material == Material.WAXED_WEATHERED_CUT_COPPER_STAIRS
                || material == Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS
                || material == Material.PURPUR_STAIRS
                || material == Material.OAK_STAIRS
                || material == Material.COBBLESTONE_STAIRS
                || material == Material.BRICK_STAIRS
                || material == Material.STONE_BRICK_STAIRS
                || material == Material.NETHER_BRICK_STAIRS
                || material == Material.SANDSTONE_STAIRS
                || material == Material.SPRUCE_STAIRS
                || material == Material.BIRCH_STAIRS
                || material == Material.JUNGLE_STAIRS
                || material == Material.CRIMSON_STAIRS
                || material == Material.WARPED_STAIRS
                || material == Material.QUARTZ_STAIRS
                || material == Material.ACACIA_STAIRS
                || material == Material.DARK_OAK_STAIRS
                || material == Material.PRISMARINE_STAIRS
                || material == Material.PRISMARINE_BRICK_STAIRS
                || material == Material.DARK_PRISMARINE_STAIRS
                || material == Material.RED_SANDSTONE_STAIRS
                || material == Material.POLISHED_GRANITE_STAIRS
                || material == Material.SMOOTH_RED_SANDSTONE_STAIRS
                || material == Material.MOSSY_STONE_BRICK_STAIRS
                || material == Material.POLISHED_DIORITE_STAIRS
                || material == Material.MOSSY_COBBLESTONE_STAIRS
                || material == Material.END_STONE_BRICK_STAIRS
                || material == Material.STONE_STAIRS
                || material == Material.SMOOTH_SANDSTONE_STAIRS
                || material == Material.SMOOTH_QUARTZ_STAIRS
                || material == Material.GRANITE_STAIRS
                || material == Material.ANDESITE_STAIRS
                || material == Material.RED_NETHER_BRICK_STAIRS
                || material == Material.POLISHED_ANDESITE_STAIRS
                || material == Material.DIORITE_STAIRS
                || material == Material.COBBLED_DEEPSLATE_STAIRS
                || material == Material.POLISHED_DEEPSLATE_STAIRS
                || material == Material.DEEPSLATE_BRICK_STAIRS
                || material == Material.DEEPSLATE_TILE_STAIRS
                || material == Material.BLACKSTONE_STAIRS
                || material == Material.POLISHED_BLACKSTONE_STAIRS
                || material == Material.POLISHED_BLACKSTONE_BRICK_STAIRS
                || material == Material.CUT_COPPER_SLAB ||
                material == Material.EXPOSED_CUT_COPPER_SLAB ||
                material == Material.WEATHERED_CUT_COPPER_SLAB ||
                material == Material.OXIDIZED_CUT_COPPER_SLAB ||
                material == Material.WAXED_CUT_COPPER_SLAB ||
                material == Material.WAXED_EXPOSED_CUT_COPPER_SLAB ||
                material == Material.WAXED_WEATHERED_CUT_COPPER_SLAB ||
                material == Material.WAXED_OXIDIZED_CUT_COPPER_SLAB ||
                material == Material.OAK_SLAB ||
                material == Material.SPRUCE_SLAB ||
                material == Material.BIRCH_SLAB ||
                material == Material.JUNGLE_SLAB ||
                material == Material.ACACIA_SLAB ||
                material == Material.DARK_OAK_SLAB ||
                material == Material.CRIMSON_SLAB ||
                material == Material.WARPED_SLAB ||
                material == Material.STONE_SLAB ||
                material == Material.SMOOTH_STONE_SLAB ||
                material == Material.SANDSTONE_SLAB ||
                material == Material.CUT_SANDSTONE_SLAB ||
                material == Material.PETRIFIED_OAK_SLAB ||
                material == Material.COBBLESTONE_SLAB ||
                material == Material.BRICK_SLAB ||
                material == Material.STONE_BRICK_SLAB ||
                material == Material.NETHER_BRICK_SLAB ||
                material == Material.QUARTZ_SLAB ||
                material == Material.RED_SANDSTONE_SLAB ||
                material == Material.CUT_RED_SANDSTONE_SLAB ||
                material == Material.PURPUR_SLAB ||
                material == Material.PRISMARINE_SLAB ||
                material == Material.PRISMARINE_BRICK_SLAB ||
                material == Material.DARK_PRISMARINE_SLAB ||
                material == Material.POLISHED_GRANITE_SLAB ||
                material == Material.SMOOTH_RED_SANDSTONE_SLAB ||
                material == Material.MOSSY_STONE_BRICK_SLAB ||
                material == Material.POLISHED_DIORITE_SLAB ||
                material == Material.MOSSY_COBBLESTONE_SLAB ||
                material == Material.END_STONE_BRICK_SLAB ||
                material == Material.SMOOTH_SANDSTONE_SLAB ||
                material == Material.SMOOTH_QUARTZ_SLAB ||
                material == Material.GRANITE_SLAB ||
                material == Material.ANDESITE_SLAB ||
                material == Material.RED_NETHER_BRICK_SLAB ||
                material == Material.POLISHED_ANDESITE_SLAB ||
                material == Material.DIORITE_SLAB ||
                material == Material.COBBLED_DEEPSLATE_SLAB ||
                material == Material.POLISHED_DEEPSLATE_SLAB ||
                material == Material.DEEPSLATE_BRICK_SLAB ||
                material == Material.DEEPSLATE_TILE_SLAB ||
                material == Material.BLACKSTONE_SLAB ||
                material == Material.POLISHED_BLACKSTONE_SLAB ||
                material == Material.POLISHED_BLACKSTONE_BRICK_SLAB);
        boolean touchingClimbable = boundingBox.checkBlocks(material -> material == Material.LADDER || material == Material.LAVA);
        boolean touchingIllegalBlock = boundingBox.checkBlocks(material -> material == Material.LILY_PAD || material == Material.BREWING_STAND);

        movementData.getTouchingAir().set(touchingAir && !touchingIllegalBlock);
        movementData.getTouchingAir().set(touchingLiquid);
        movementData.getTouchingHalfBlock().set(touchingHalfBlock);
        movementData.getTouchingClimbable().set(touchingClimbable);
        movementData.getTouchingIllegalBlock().set(touchingIllegalBlock);
        movementData.getTouchingLiquid().set(touchingLiquid);

    }
}
