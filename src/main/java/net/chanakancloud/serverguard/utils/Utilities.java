package net.chanakancloud.serverguard.utils;

import cc.funkemunky.api.utils.ReflectionsUtil;
import live.chanakancloud.taputils.utils.BlockUtils;
import live.chanakancloud.taputils.utils.MiscUtils;
import live.chanakancloud.taputils.utils.XMaterial;
import net.chanakancloud.serverguard.blockgetter.MaterialAccess;
import net.chanakancloud.serverguard.impl.common.MovementData;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private static MaterialAccess materialAccess;
    private static boolean underBlock;
    private static final List<Material> CLIMBABLE = new ArrayList<Material>();
    private static final List<Material> INSTANT_BREAK = new ArrayList<Material>();
    private static final List<Material> FOOD = new ArrayList<Material>();
    private static final Map<Material, Material> COMBO = new HashMap<Material, Material>();
    public static volatile int water = 0;
    public static volatile int climbable = 0;

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
        return climbable > 0;
    }

    public static boolean couldBeOnHalfblock(Location location) {
        return isNearHalfblock(
                new Location(location.getWorld(), location.getX(), location.getY() - 0.01D, location.getBlockZ()))
                || isNearHalfblock(new Location(location.getWorld(), location.getX(), location.getY() - 0.51D,
                location.getBlockZ()));
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
        return water > 3;
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
            // Start instant break materials
            INSTANT_BREAK.add(XMaterial.COMPARATOR.parseMaterial());
            INSTANT_BREAK.add(XMaterial.REPEATER.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TORCH.parseMaterial());
            INSTANT_BREAK.add(XMaterial.REDSTONE_TORCH.parseMaterial());
            INSTANT_BREAK.add(XMaterial.REDSTONE_WIRE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TRIPWIRE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TRIPWIRE_HOOK.parseMaterial());
            INSTANT_BREAK.add(XMaterial.FIRE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.FLOWER_POT.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TNT.parseMaterial());
            INSTANT_BREAK.add(XMaterial.SLIME_BLOCK.parseMaterial());
            INSTANT_BREAK.add(XMaterial.CARROT.parseMaterial());
            INSTANT_BREAK.add(XMaterial.DEAD_BUSH.parseMaterial());
            INSTANT_BREAK.add(XMaterial.GRASS.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TALL_GRASS.parseMaterial());
            INSTANT_BREAK.add(XMaterial.LILY_PAD.parseMaterial());
            INSTANT_BREAK.add(XMaterial.MELON_STEM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.MELON_STEM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.BROWN_MUSHROOM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.RED_MUSHROOM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.NETHER_WART.parseMaterial());
            INSTANT_BREAK.add(XMaterial.POTATO.parseMaterial());
            INSTANT_BREAK.add(XMaterial.PUMPKIN_STEM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.PUMPKIN_STEM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.OAK_SAPLING.parseMaterial());
            INSTANT_BREAK.add(XMaterial.SUGAR_CANE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.WHEAT.parseMaterial());
            // End instant break materials

            // Start food
            FOOD.add(XMaterial.APPLE.parseMaterial());
            FOOD.add(XMaterial.BAKED_POTATO.parseMaterial());
            FOOD.add(XMaterial.BREAD.parseMaterial());
            FOOD.add(XMaterial.CAKE.parseMaterial());
            FOOD.add(XMaterial.CARROT.parseMaterial());
            FOOD.add(XMaterial.COOKED_CHICKEN.parseMaterial());
            FOOD.add(XMaterial.COOKED_COD.parseMaterial());
            FOOD.add(XMaterial.COD.parseMaterial());
            FOOD.add(XMaterial.COOKED_MUTTON.parseMaterial());
            FOOD.add(XMaterial.COOKED_PORKCHOP.parseMaterial());
            FOOD.add(XMaterial.COOKED_RABBIT.parseMaterial());
            FOOD.add(XMaterial.COOKIE.parseMaterial());
            FOOD.add(XMaterial.GOLDEN_APPLE.parseMaterial());
            FOOD.add(XMaterial.GOLDEN_CARROT.parseMaterial());
            FOOD.add(XMaterial.GLISTERING_MELON_SLICE.parseMaterial());
            FOOD.add(XMaterial.MUSHROOM_STEM.parseMaterial());
            FOOD.add(XMaterial.POISONOUS_POTATO.parseMaterial());
            FOOD.add(XMaterial.POTATO.parseMaterial());
            FOOD.add(XMaterial.PUMPKIN_PIE.parseMaterial());
            FOOD.add(XMaterial.RABBIT_STEW.parseMaterial());
            FOOD.add(XMaterial.COOKED_BEEF.parseMaterial());
            FOOD.add(XMaterial.BEEF.parseMaterial());
            FOOD.add(XMaterial.COOKED_CHICKEN.parseMaterial());
            FOOD.add(XMaterial.CHICKEN.parseMaterial());
            FOOD.add(XMaterial.MUTTON.parseMaterial());
            FOOD.add(XMaterial.PORKCHOP.parseMaterial());
            FOOD.add(XMaterial.RABBIT.parseMaterial());
            FOOD.add(XMaterial.COOKED_RABBIT.parseMaterial());
            FOOD.add(XMaterial.ROTTEN_FLESH.parseMaterial());
            FOOD.add(XMaterial.SPIDER_EYE.parseMaterial());

            // Start combos
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.WHITE_WOOL.parseMaterial());

            COMBO.put(XMaterial.IRON_SWORD.parseMaterial(), XMaterial.COBWEB.parseMaterial());
            COMBO.put(XMaterial.DIAMOND_SWORD.parseMaterial(), XMaterial.COBWEB.parseMaterial());
            COMBO.put(XMaterial.STONE_SWORD.parseMaterial(), XMaterial.COBWEB.parseMaterial());
            COMBO.put(XMaterial.WOODEN_SWORD.parseMaterial(), XMaterial.COBWEB.parseMaterial());
            // End combos

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

            // Start instant break materials
            INSTANT_BREAK.add(XMaterial.COMPARATOR.parseMaterial());
            INSTANT_BREAK.add(XMaterial.REPEATER.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TORCH.parseMaterial());
            INSTANT_BREAK.add(XMaterial.REDSTONE_TORCH.parseMaterial());
            INSTANT_BREAK.add(XMaterial.REDSTONE_WIRE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TRIPWIRE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TRIPWIRE_HOOK.parseMaterial());
            INSTANT_BREAK.add(XMaterial.FIRE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.FLOWER_POT.parseMaterial());
            INSTANT_BREAK.add(XMaterial.INFESTED_CHISELED_STONE_BRICKS.parseMaterial());
            INSTANT_BREAK.add(XMaterial.INFESTED_COBBLESTONE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.INFESTED_CRACKED_STONE_BRICKS.parseMaterial());
            INSTANT_BREAK.add(XMaterial.INFESTED_MOSSY_STONE_BRICKS.parseMaterial());
            INSTANT_BREAK.add(XMaterial.INFESTED_STONE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.INFESTED_STONE_BRICKS.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TNT.parseMaterial());
            INSTANT_BREAK.add(XMaterial.SLIME_BLOCK.parseMaterial());
            INSTANT_BREAK.add(XMaterial.CARROTS.parseMaterial());
            INSTANT_BREAK.add(XMaterial.DEAD_BUSH.parseMaterial());
            INSTANT_BREAK.add(XMaterial.FERN.parseMaterial());
            INSTANT_BREAK.add(XMaterial.LARGE_FERN.parseMaterial());
            INSTANT_BREAK.add(XMaterial.CHORUS_FLOWER.parseMaterial());
            INSTANT_BREAK.add(XMaterial.SUNFLOWER.parseMaterial());
            INSTANT_BREAK.add(XMaterial.LILY_PAD.parseMaterial());
            INSTANT_BREAK.add(XMaterial.MELON_STEM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.ATTACHED_MELON_STEM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.BROWN_MUSHROOM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.RED_MUSHROOM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.NETHER_WART.parseMaterial());
            INSTANT_BREAK.add(XMaterial.POTATOES.parseMaterial());
            INSTANT_BREAK.add(XMaterial.PUMPKIN_STEM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.ATTACHED_PUMPKIN_STEM.parseMaterial());
            INSTANT_BREAK.add(XMaterial.ACACIA_SAPLING.parseMaterial());
            INSTANT_BREAK.add(XMaterial.BIRCH_SAPLING.parseMaterial());
            INSTANT_BREAK.add(XMaterial.DARK_OAK_SAPLING.parseMaterial());
            INSTANT_BREAK.add(XMaterial.JUNGLE_SAPLING.parseMaterial());
            INSTANT_BREAK.add(XMaterial.OAK_SAPLING.parseMaterial());
            INSTANT_BREAK.add(XMaterial.SPRUCE_SAPLING.parseMaterial());
            INSTANT_BREAK.add(XMaterial.SUGAR_CANE.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TALL_GRASS.parseMaterial());
            INSTANT_BREAK.add(XMaterial.TALL_SEAGRASS.parseMaterial());
            INSTANT_BREAK.add(XMaterial.WHEAT.parseMaterial());
            // Start 1.14 objects
            if (currentVersion.isAtLeast(MinecraftVersion.VILLAGE_UPDATE)) {
                INSTANT_BREAK.add(XMaterial.BAMBOO_SAPLING.parseMaterial());
                INSTANT_BREAK.add(XMaterial.CORNFLOWER.parseMaterial());
            }
            // End 1.14 objects
            // Start 1.15 objects
            if (currentVersion.isAtLeast(MinecraftVersion.BEE_UPDATE)) {
                INSTANT_BREAK.add(XMaterial.HONEY_BLOCK.parseMaterial());
            }
            // End 1.15 objects
            // End instant break materials

            // Start food
            FOOD.add(XMaterial.APPLE.parseMaterial());
            FOOD.add(XMaterial.BAKED_POTATO.parseMaterial());
            FOOD.add(XMaterial.BEETROOT.parseMaterial());
            FOOD.add(XMaterial.BEETROOT_SOUP.parseMaterial());
            FOOD.add(XMaterial.BREAD.parseMaterial());
            FOOD.add(XMaterial.CAKE.parseMaterial());
            FOOD.add(XMaterial.CARROT.parseMaterial());
            FOOD.add(XMaterial.CHORUS_FRUIT.parseMaterial());
            FOOD.add(XMaterial.COOKED_BEEF.parseMaterial());
            FOOD.add(XMaterial.COOKED_CHICKEN.parseMaterial());
            FOOD.add(XMaterial.COOKED_COD.parseMaterial());
            FOOD.add(XMaterial.COOKED_MUTTON.parseMaterial());
            FOOD.add(XMaterial.COOKED_PORKCHOP.parseMaterial());
            FOOD.add(XMaterial.COOKED_RABBIT.parseMaterial());
            FOOD.add(XMaterial.COOKED_SALMON.parseMaterial());
            FOOD.add(XMaterial.COOKIE.parseMaterial());
            FOOD.add(XMaterial.DRIED_KELP.parseMaterial());
            FOOD.add(XMaterial.ENCHANTED_GOLDEN_APPLE.parseMaterial());
            FOOD.add(XMaterial.GOLDEN_APPLE.parseMaterial());
            FOOD.add(XMaterial.GOLDEN_CARROT.parseMaterial());
            FOOD.add(XMaterial.MELON_SLICE.parseMaterial());
            FOOD.add(XMaterial.MUSHROOM_STEW.parseMaterial());
            FOOD.add(XMaterial.POISONOUS_POTATO.parseMaterial());
            FOOD.add(XMaterial.POTATO.parseMaterial());
            FOOD.add(XMaterial.PUFFERFISH.parseMaterial());
            FOOD.add(XMaterial.PUMPKIN_PIE.parseMaterial());
            FOOD.add(XMaterial.RABBIT_STEW.parseMaterial());
            FOOD.add(XMaterial.BEEF.parseMaterial());
            FOOD.add(XMaterial.CHICKEN.parseMaterial());
            FOOD.add(XMaterial.COD.parseMaterial());
            FOOD.add(XMaterial.MUTTON.parseMaterial());
            FOOD.add(XMaterial.PORKCHOP.parseMaterial());
            FOOD.add(XMaterial.RABBIT.parseMaterial());
            FOOD.add(XMaterial.SALMON.parseMaterial());
            FOOD.add(XMaterial.ROTTEN_FLESH.parseMaterial());
            FOOD.add(XMaterial.SPIDER_EYE.parseMaterial());
            FOOD.add(XMaterial.TROPICAL_FISH.parseMaterial());
            // Start 1.14 objects
            if (currentVersion.isAtLeast(MinecraftVersion.VILLAGE_UPDATE)) {
                FOOD.add(XMaterial.SUSPICIOUS_STEW.parseMaterial());
                FOOD.add(XMaterial.SWEET_BERRIES.parseMaterial());
            }
            // End 1.14 objects
            // Start 1.15 objects
            if (currentVersion.isAtLeast(MinecraftVersion.BEE_UPDATE)) {
                FOOD.add(XMaterial.HONEY_BOTTLE.parseMaterial());
            }
            // End 1.15 objects
            // End food

            // Start combos
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.BLACK_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.BLUE_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.BROWN_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.CYAN_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.GRAY_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.GREEN_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.LIGHT_BLUE_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.LIGHT_GRAY_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.LIME_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.MAGENTA_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.MAGENTA_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.ORANGE_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.PINK_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.PURPLE_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.RED_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.WHITE_WOOL.parseMaterial());
            COMBO.put(XMaterial.SHEARS.parseMaterial(), XMaterial.YELLOW_WOOL.parseMaterial());

            COMBO.put(XMaterial.IRON_SWORD.parseMaterial(), XMaterial.COBWEB.parseMaterial());
            COMBO.put(XMaterial.DIAMOND_SWORD.parseMaterial(), XMaterial.COBWEB.parseMaterial());
            COMBO.put(XMaterial.STONE_SWORD.parseMaterial(), XMaterial.COBWEB.parseMaterial());
            COMBO.put(XMaterial.WOODEN_SWORD.parseMaterial(), XMaterial.COBWEB.parseMaterial());
            // End combos

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

    public static List<Location> getBoundingBoxesAroundPlayer(Location loc) {
        List<Location> list = new ArrayList<Location>();
        final Location cloned = loc.clone();
        double limit = ONGROUNDBOUNDINGBOXWITDH;
        for (double x = -limit; x < limit; x += 0.1) {
            for (double z = -limit; z < limit; z += 0.1) {
                for (double y = -ONGROUNDBOUNDINGBOXHEIGHT; y < 0; y += 0.1) {
                    list.add(cloned.clone().add(x, y, z));
                }
            }
        }
        return list;
    }

    public static boolean isNearMaterials(Location loc, Material... materials) {
        for(Material s  : materials) {
            if(isNearMaterial(loc, s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNearMaterial(Location loc, Material material) {
        return loc.getBlock().getType() == material;
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
