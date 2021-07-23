package net.chanakancloud.serverguard.utils;

import org.bukkit.entity.Player;
import java.util.List;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import java.util.Arrays;

public class VersionUtil {

    public static final MinecraftVersion CURRENT_VERSION;
    private static final List<String> SUPPORTED_VERSIONS;

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static boolean isSupported() {
        for (String versionId : SUPPORTED_VERSIONS) {
            if (getVersion().startsWith(versionId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOfVersion(String versionId) {
        return getVersion().startsWith(versionId);
    }

    /**
     * @return the if server is running Bountiful Update (1.8)
     */
    public static boolean isBountifulUpdate() {
        return isOfVersion("v1_8");
    }

    public static boolean isRiptiding(Player player) {
        if (!CURRENT_VERSION.isAtLeast(MinecraftVersion.AQUATIC_UPDATE)) {
            return false;
        }
        return player.isRiptiding();
    }

    public static boolean isFlying(Player player) {
        if (isBountifulUpdate()) {
            return player.isFlying();
        }
        return player.isFlying() || player.isGliding() || player.hasPotionEffect(PotionEffectType.LEVITATION)
                /*|| AntiCheatReloaded.getManager().getBackend().justLevitated(player)*/;
    }

    public static boolean isGliding(Player player) {
        if (isBountifulUpdate()) {
            return false;
        }
        return player.isGliding();
    }

    public static boolean isSwimming(Player player) {
        if (!CURRENT_VERSION.isAtLeast(MinecraftVersion.AQUATIC_UPDATE)) {
            return false;
        }
        return player.isSwimming();
    }

    public static boolean isLevitationEffect(PotionEffect effect) {
        if (isBountifulUpdate()) {
            return false;
        }
        if (effect != null) {
            return effect.getType().equals(PotionEffectType.LEVITATION);
        } else {
            return false;
        }
    }

    static {
        SUPPORTED_VERSIONS = Arrays.asList("v1_17", "v1_16", "v1_15", "v1_14", "v1_13", "v1_12", "v1_8_R3");
        CURRENT_VERSION = MinecraftVersion.getCurrentVersion();
    }
}
