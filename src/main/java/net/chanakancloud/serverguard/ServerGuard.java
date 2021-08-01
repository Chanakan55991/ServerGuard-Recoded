package net.chanakancloud.serverguard;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.settings.PacketEventsSettings;
import live.chanakancloud.taputils.TapUtils;
import live.chanakancloud.taputils.utils.MiscUtils;
import lombok.Getter;
import net.chanakancloud.serverguard.impl.command.ServerguardCommand;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import net.chanakancloud.serverguard.impl.player.PlayerDataManager;
import net.chanakancloud.serverguard.impl.processor.Processor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Getter
public class ServerGuard extends JavaPlugin {
    public static ServerGuard INSTANCE;
    private PacketEvents packetEvents;
    private double[] recentTps;
    private ClassLoader materialAccessClassLoader;

    @Override
    public void onLoad() {
        packetEvents = PacketEvents.create(this);
        packetEvents.loadAsyncNewThread();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        packetEvents.init(new PacketEventsSettings().checkForUpdates(false));
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            recentTps = packetEvents.getServerUtils().getRecentTPS();
        }, 0L, 20L);
        TapUtils.setPlugin(this);
        TapUtils.setPluginName("ServerGuard");
        TapUtils.setPrefixColor(ChatColor.GREEN);
        MiscUtils.sendToConsole("ServerGuard Loading...");
        MiscUtils.sendToConsole("Registering Events");
        new PlayerDataManager(this);
        // this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        MiscUtils.sendToConsole("Registering Commands");
        Objects.requireNonNull(this.getCommand("serverguard")).setExecutor(new ServerguardCommand());
        MiscUtils.sendToConsole("Hooking into PacketListener");
    }

    @Override
    public void onDisable() {
        packetEvents.terminate();
        for (Player player : Bukkit.getOnlinePlayers())
            PlayerData.cleanup(player);
    }

}
