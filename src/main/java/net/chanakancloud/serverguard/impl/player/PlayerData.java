package net.chanakancloud.serverguard.impl.player;

import cc.funkemunky.api.utils.objects.evicting.EvictingList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.chanakancloud.serverguard.api.player.Violation;
import net.chanakancloud.serverguard.data.BoundingBox;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.check.CheckManager;
import net.chanakancloud.serverguard.impl.processor.impl.MovementProcessor;
import net.chanakancloud.serverguard.impl.processor.impl.PacketProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public class PlayerData {
    private static final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public float airTicks = 0.0f;
    public static PlayerData playerData;
    @Getter @Setter private boolean onAir = false;
    private final net.chanakancloud.serverguard.observable.Observable<BoundingBox> boundingBox = new net.chanakancloud.serverguard.observable.Observable<>(new BoundingBox(0, 0, 0, null));
    private final EvictingList<BoundingBox> boundingBoxes = new EvictingList<>(10);
    public int flyThreshold;
    public int flyBThreshold;
    private final UUID uuid;
    private final long timeCreated;
    public float lastDeltaY;

    @Getter(AccessLevel.NONE) public PacketProcessor packetProcessor;
    @Getter(AccessLevel.NONE) public MovementProcessor movementProcessor;
    private final List<Check> checks = new ArrayList<>();

    private UUID debuggingTarget; // The uuid of the player to debug
    private Class<? extends Check> debuggingCheck; // The class of the check to debug for the target
    private boolean packetSniffing;

    private final List<Violation> violations = new ArrayList<>();
    @Setter private boolean extremePrejudice, banned;

    public PlayerData(@NonNull Player player) {
        playerData = this;
        uuid = player.getUniqueId();
        timeCreated = System.currentTimeMillis();
        packetProcessor = new PacketProcessor(this);
        movementProcessor= new MovementProcessor(this);
        for (Class<? extends Check> checkClass : CheckManager.CHECK_CLASSES) {
            try {
                checks.add(checkClass.getConstructor(PlayerData.class).newInstance(this));
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
        dataMap.put(uuid, this);
    }

    /**
     * Get the Bukkit player for this data object
     *
     * @return the Bukkit player
     * @see Player
     */
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void startDebugging(@NonNull UUID targetUuid) {
        startDebugging(targetUuid, null);
    }

    /**
     * Start debugging for the target player with the target check class.
     * <p>
     * The player is identified by their uuid and the check class is nullable
     * to allow for packet sniffing. If the check class is null, packet sniffing
     * will be enabled.
     *
     * @param targetUuid the uuid of the player to debug
     * @param targetCheckClass the class of the check to debug
     * @see Player
     * @see Check
     */
    public void startDebugging(@NonNull UUID targetUuid, Class<? extends Check> targetCheckClass) {
        debuggingTarget = targetUuid;
        debuggingCheck = targetCheckClass;
        packetSniffing = debuggingCheck == null;
    }

    /**
     * Check whether or not the player is sniffing packets.
     *
     * @return the packet sniffing status
     */
    public boolean isPacketSniffing() {
        return isDebugging() && packetSniffing;
    }

    /**
     * Check whether or not the player is debugging the target player data and the target check class.
     *
     * @param target the target player data
     * @param targetCheckClass the target check class
     * @return the debugging status
     * @see Check
     */
    public boolean isDebugging(PlayerData target, Class<? extends Check> targetCheckClass) {
        return isDebugging() && (debuggingTarget.equals(target.getUuid()) && debuggingCheck.equals(targetCheckClass));
    }

    /**
     * Check whether or not the player is debugging.
     *
     * @return the debugging status
     */
    public boolean isDebugging() {
        return debuggingTarget != null;
    }

    /**
     * Stop debugging.
     */
    public void stopDebugging() {
        debuggingTarget = null;
        debuggingCheck = null;
    }

    public void addViolation(Violation violation) {
        violations.add(violation);
        if (violations.size() >= 50)
            violations.remove(0);
    }

    /**
     * Get the potion effect level for the provided potion effect type.
     *
     * @param potionEffectType the type of effect to get the level for
     * @return the level, 0 if not active
     */
    public int getPotionEffectLevel(PotionEffectType potionEffectType) {
        PotionEffect potionEffect = getPotionEffect(potionEffectType);
        return potionEffect == null ? 0 : potionEffect.getAmplifier() + 1;
    }

    /**
     * Get the active potion effect with the provided potion effect type for the player.
     *
     * @param potionEffectType the type of effect to get
     * @return the potion effect if present, otherwise null
     * @see PotionEffect
     * @see PotionEffectType
     */
    public PotionEffect getPotionEffect(PotionEffectType potionEffectType) {
        for (PotionEffect potionEffect : getBukkitPlayer().getActivePotionEffects()) {
            if (potionEffect.getType().equals(potionEffectType)) {
                return potionEffect;
            }
        }
        return null;
    }

    /**
     * Get the player data for the provided player
     *
     * @param player the player to get the player data for
     * @return the player data if found, otherwise null
     * @see Player
     */
    public static PlayerData get(@NonNull Player player) {
        return dataMap.get(player.getUniqueId());
    }

    /**
     * Cleanup the player data for the provided player.
     * <p>
     * If the provided player does not have any player data, a {@link NullPointerException} will be thrown
     *
     * @param player the player to cleanup the player data for
     * @see Player
     */
    public static void cleanup(@NonNull Player player) {
        PlayerData playerData = dataMap.remove(player.getUniqueId());
        if (playerData == null)
            throw new NullPointerException("Player does not have player data to cleanup: " + player.getName());
        playerData.packetProcessor = null;
        playerData.getChecks().clear();
        playerData.getViolations().clear();
    }
}
