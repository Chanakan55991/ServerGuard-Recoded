package net.chanakancloud.serverguard.impl.check;


import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.chanakancloud.serverguard.ServerGuard;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import net.chanakancloud.serverguard.api.event.PlayerCheatEvent;
import net.chanakancloud.serverguard.api.event.PlayerPunishEvent;
import net.chanakancloud.serverguard.api.player.Violation;
import net.chanakancloud.serverguard.impl.common.MovementData;
import net.chanakancloud.serverguard.impl.meta.MetadataManager;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class Check {
    protected final PlayerData playerData;
    protected final Player player;
    private final CheckInfo checkInfo;

    @Getter(AccessLevel.NONE) private int violations;

    public Check(@NonNull PlayerData playerData) throws ClassNotFoundException {
        this.playerData = playerData;
        if(!getClass().isAnnotationPresent(CheckInfo.class))
            throw new ClassNotFoundException("Check is missing @CheckInfo annotation");
        player = playerData.getBukkitPlayer();
        checkInfo = getClass().getAnnotation(CheckInfo.class);
    }

    public void handle(byte packetId, @NonNull NMSPacket nmsPacket, @NonNull Object packet, long timestamp) {}
    public void handle(MovementData movementData, long timestamp) {}

    protected final void debug(String... data) {
        if(data.length < 1)
            throw new IllegalArgumentException("Cannot debug with no data");
        String message = ChatColor.stripColor(String.join(", ", data)).trim();
        for (Player debugger : Bukkit.getOnlinePlayers()) {
            PlayerData debuggerPlayerData = PlayerData.get(debugger);
            if(!debuggerPlayerData.isDebugging(playerData, getClass()))
                continue;
            debugger.sendMessage("§8[§cDEBUG§8] §7" + message);
        }
    }

    protected final void flag(String... data) {
        violations++;

        Violation violation = new Violation(
                checkInfo,
                data,
                violations,
                player.getLocation(),
                playerData.packetProcessor.ping,
                ServerGuard.INSTANCE.getRecentTps()[0],
                System.currentTimeMillis()
        );
        playerData.addViolation(violation);

        PlayerCheatEvent playerCheatEvent = new PlayerCheatEvent(player, violation);
        try {
            Bukkit.getPluginManager().callEvent(playerCheatEvent);
        } catch (IllegalStateException ignored) {}
        if (playerCheatEvent.isCancelled())
            return;

        String message = ChatColor.stripColor(String.join(", ", data)).trim();
        String checkName = (checkInfo.experimental() ? "§7§o*" : "§f") + checkInfo.name();
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("serverguard.alert"))
                staff.sendMessage("§8[§6§lServerGuard§8] §f" + player.getName() + " §7flagged " + checkName + " §c(" + violations + " VL)" +
                    (message.isEmpty() ? "" : " §7[" + message + "]"));
        }
        if (violations >= checkInfo.maxVl() && checkInfo.ban() && !checkInfo.experimental() && !playerData.isBanned()) {
            playerData.setBanned(true);

            String metadataJson = MetadataManager.getMetadataJson(playerData);
            ServerGuard.INSTANCE.getLogger().info(player.getName() + " was banned for cheating (" + checkInfo.name() + "): " + metadataJson);

            Bukkit.getPluginManager().callEvent(new PlayerPunishEvent(playerCheatEvent));
            Bukkit.getScheduler().runTask(ServerGuard.INSTANCE, () -> player.kickPlayer("[AC] Unfair Advantage"));
        }
    }
}