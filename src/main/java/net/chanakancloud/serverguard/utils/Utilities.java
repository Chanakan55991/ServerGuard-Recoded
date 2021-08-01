package net.chanakancloud.serverguard.utils;

import live.chanakancloud.taputils.utils.XMaterial;
import net.chanakancloud.serverguard.ServerGuard;
import net.chanakancloud.serverguard.impl.common.MovementData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Utilities {
    private static final float ONGROUNDBOUNDINGBOXWITDH = 0.4f;
    private static final float ONGROUNDBOUNDINGBOXHEIGHT = 0.7f;
    private static volatile int water;
    private static volatile int climbable;
    private static final List<Material> CLIMBABLE = new ArrayList<Material>();

    public static boolean isNearWater(Player player) {
        return player.getLocation().getBlock().isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.NORTH).isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.SOUTH).isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.EAST).isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.WEST).isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.NORTH_EAST).isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.NORTH_WEST).isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.SOUTH_EAST).isLiquid()
                || player.getLocation().getBlock().getRelative(BlockFace.SOUTH_WEST).isLiquid();
    }

    public static Set<Block> getNearbyBlocksHorizontally(Location location, int radius) {
        Set<Block> blocks = new HashSet<>();

        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY(); y <= location.getBlockY(); y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static Set<Block> getNearbyBlocksVertically(Location location, int radius) {
        Set<Block> blocks = new HashSet<>();

        for (int x = location.getBlockX(); x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY(); y - radius <= location.getBlockY(); y++) {
                for (int z = location.getBlockZ(); z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static boolean isBed(Block block) {
        Material type = block.getType();
        return type.name().endsWith("BED");
    }

    /*
     * public static boolean isUnderBlock(Player player) {
     * if(player.getLocation().add(0, 2, 0).getBlock().getType() != Material.AIR) {
     * underBlock = true; return true; } underBlock = false; return false; }
     *
     * public static boolean isWasUnderBlock() { return underBlock; }
     */

    public static boolean isNearBed(Location location) {
        return isBed(location.getBlock()) || isBed(location.getBlock().getRelative(BlockFace.NORTH))
                || isBed(location.getBlock().getRelative(BlockFace.SOUTH))
                || isBed(location.getBlock().getRelative(BlockFace.EAST))
                || isBed(location.getBlock().getRelative(BlockFace.WEST))
                || isBed(location.getBlock().getRelative(BlockFace.NORTH_EAST))
                || isBed(location.getBlock().getRelative(BlockFace.NORTH_WEST))
                || isBed(location.getBlock().getRelative(BlockFace.SOUTH_EAST))
                || isBed(location.getBlock().getRelative(BlockFace.SOUTH_WEST));
    }

    public static boolean isNearHalfblock(Location location) {
        return isHalfblock(location.getBlock()) || isHalfblock(location.getBlock().getRelative(BlockFace.NORTH))
                || isHalfblock(location.getBlock().getRelative(BlockFace.SOUTH))
                || isHalfblock(location.getBlock().getRelative(BlockFace.EAST))
                || isHalfblock(location.getBlock().getRelative(BlockFace.WEST))
                || isHalfblock(location.getBlock().getRelative(BlockFace.NORTH_EAST))
                || isHalfblock(location.getBlock().getRelative(BlockFace.NORTH_WEST))
                || isHalfblock(location.getBlock().getRelative(BlockFace.SOUTH_EAST))
                || isHalfblock(location.getBlock().getRelative(BlockFace.SOUTH_WEST));
    }

    public static boolean isHalfblock(Block block) {
        if (MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.VILLAGE_UPDATE)) {
            BoundingBox box = block.getBoundingBox();
            double height = box.getMaxY() - box.getMinY();
            if (height > 0.42 && height <= 0.6 && block.getType().isSolid())
                return true;
        }
        return isSlab(block) || isStair(block) || isWall(block) || block.getType() == Material.SNOW
                || block.getType().name().endsWith("HEAD");
    }

    public static boolean isStair(Block block) {
        Material type = block.getType();
        return type.name().endsWith("STAIRS");
    }

    public static boolean isNearGround(Location location) {
        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (location.clone().add(x, -0.5001, z).getBlock().getType() != Material.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isWall(Block block) {
        Material type = block.getType();
        return type.name().endsWith("WALL") || type.name().endsWith("FENCE");
    }

    public static boolean isSlab(Block block) {
        Material type = block.getType();
        return type.name().endsWith("SLAB");
    }

    public static boolean isTeleporting(MovementData data) {
        return Math.abs(data.to.getY() - data.from.getY()) >= 400;
    }

    public static boolean isNearClimbable(Player player) {
        return isClimbableBlock(player.getLocation().getBlock())
                || isClimbableBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN))
                || isClimbableBlock(player.getLocation().getBlock().getRelative(BlockFace.UP))
                || isClimbableBlock(player.getLocation().getBlock().getRelative(BlockFace.NORTH))
                || isClimbableBlock(player.getLocation().getBlock().getRelative(BlockFace.SOUTH))
                || isClimbableBlock(player.getLocation().getBlock().getRelative(BlockFace.EAST))
                || isClimbableBlock(player.getLocation().getBlock().getRelative(BlockFace.WEST));
    }

    /**
     * Determine whether a location is near a climbable block
     *
     * @param location location to check
     * @return true if near climbable block
     */
    // if (material.name().contains("LADDER") || material.name().contains("VINE") || material.name().contains("SCAFFOLD")) {
    //                    climbable++;
    //                }

    public static boolean isNearClimbable(Location location) {
        double limit = 0.22;
        for (double x = -limit; x < limit + 0.1; x += limit) {
            for (double z = -limit; z < limit + 0.1; z += limit) {
                double finalX = x;
                double finalZ = z;
                CompletableFuture.supplyAsync(() -> {
                    return location.clone().add(finalX, 0, finalZ).getBlock().getType();
                }).thenAccept(material -> {
                    if (!material.isSolid()) {
                        if (material.name().contains("LADDER") || material.name().contains("VINE") || material.name().contains("SCAFFOLD")) {
                            climbable++;
                        }
                    }
                });
            }
        }
        boolean climbableRes = climbable > 0;
        climbable = 0;
        return climbableRes;
    }

    public synchronized static boolean isNearLiquid(Location loc) {
        double limit = 0.22;
        for (double x = -limit; x < limit + 0.1; x += limit) {
            for (double z = -limit; z < limit + 0.1; z += limit) {
                double finalX = x;
                double finalZ = z;
                CompletableFuture.supplyAsync(() -> {
                    return loc.clone().add(finalX, 0, finalZ).getBlock().getType();
                }).thenAccept(material -> {
                    if (!material.isSolid()) {
                        if (material.name().contains("WATER") || material.name().contains("LAVA")) {
                            water++;
                        }
                    }
                });
            }
        }
        boolean waterRes = water > 3;
        water = 0;
        return waterRes;
    }

    public static boolean couldBeOnHalfblock(Location location) {
        return isNearHalfblock(
                new Location(location.getWorld(), location.getX(), location.getY() - 0.01D, location.getBlockZ()))
                || isNearHalfblock(new Location(location.getWorld(), location.getX(), location.getY() - 0.51D,
                location.getBlockZ()));
    }




    public static boolean isClimbableBlock(Block block) {
        return CLIMBABLE.contains(block.getType());
    }

    public static boolean isUnderBlock(Player player) {
        return player.getLocation().add(0, 2, 0).getBlock().getType() != Material.AIR;
    }


    static {
        // Start 1.8.8
        if (VersionUtil.isBountifulUpdate()) {
            // Start climbable
            CLIMBABLE.add(XMaterial.VINE.parseMaterial());
            CLIMBABLE.add(XMaterial.LADDER.parseMaterial());
            CLIMBABLE.add(XMaterial.WATER.parseMaterial());
            // End climbable
        }
        // End 1.8.8
        // Start other version
        else {
            MinecraftVersion currentVersion = MinecraftVersion.getCurrentVersion();

            // Start climbable
            CLIMBABLE.add(XMaterial.VINE.parseMaterial());
            CLIMBABLE.add(XMaterial.LADDER.parseMaterial());
            CLIMBABLE.add(XMaterial.WATER.parseMaterial());
            // Start 1.14 objects
            if (currentVersion.isAtLeast(MinecraftVersion.VILLAGE_UPDATE)) {
                CLIMBABLE.add(XMaterial.SCAFFOLDING.parseMaterial());
                CLIMBABLE.add(XMaterial.SWEET_BERRY_BUSH.parseMaterial());
            }
            // End 1.14 objects

            // Start 1.16 objects
            if (currentVersion.isAtLeast(MinecraftVersion.NETHER_UPDATE)) {
                CLIMBABLE.add(XMaterial.TWISTING_VINES.parseMaterial());
                CLIMBABLE.add(XMaterial.TWISTING_VINES_PLANT.parseMaterial());
                CLIMBABLE.add(XMaterial.WEEPING_VINES.parseMaterial());
                CLIMBABLE.add(XMaterial.WEEPING_VINES_PLANT.parseMaterial());
            }
            // End 1.16 objects
            // End climbable
        }
    }

    public static boolean isNearVehicle(Player player) {
        final double range = 3.1;
        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (e instanceof Vehicle) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNearMaterials(Location loc, Material... materials) {
        for (Material s : materials) {
            if (isNearMaterial(loc, s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNearMaterial(Location loc, Material material) {
        return loc.getBlock().getType() == material;
    }

    public static double getPopularElement(Double[] a) {
        int count = 1, tempCount;
        double popular = a[0];
        double temp = 0;
        for (int i = 0; i < (a.length - 1); i++) {
            temp = a[i];
            tempCount = 0;
            for (int j = 1; j < a.length; j++) {
                if (temp == a[j])
                    tempCount++;
            }
            if (tempCount > count) {
                popular = temp;
                count = tempCount;
            }
        }
        return popular;
    }

    public static boolean hasLivingEntityNear(Player player) {
        final int range = 2;
        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (e instanceof LivingEntity && !e.getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public static List<Block> getBlocksAround(Location loc, int radius) {
        List<Block> result = new ArrayList<>();
        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    // Cloning location with default native method isn't good and cause performance
                    // issues
                    Location cloned = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
                    result.add(loc.getWorld().getBlockAt(cloned.add(x, y, z)));
                }
            }
        }
        return result;
    }
}
