package net.chanakancloud.serverguard.impl.command;

import live.chanakancloud.taputils.utils.MiscUtils;
import net.chanakancloud.serverguard.impl.check.Check;
import net.chanakancloud.serverguard.impl.check.CheckManager;
import net.chanakancloud.serverguard.impl.meta.MetadataManager;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class ServerguardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("serverguard.command")) {
            MiscUtils.sendToCommandSender(sender, "&cNo permission.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§c/" + label + " saveMetadata <player>");
            sender.sendMessage("§c/" + label + " debug <player> <check>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null)
            sender.sendMessage("§cPlayer is offline.");
        else {
            switch (args[0].toLowerCase()) {
                case "savemeta", "savemetadata" -> {
                    if (sender instanceof Player)
                        sender.sendMessage("§cOnly the terminal can save metadata.");
                    else {
                        PlayerData targetPlayerData = PlayerData.get(target);
                        if (targetPlayerData == null) // This should never happen
                            return true;
                        sender.sendMessage("§7Metadata for §f" + target.getName() + "§7:");
                        sender.sendMessage(MetadataManager.getMetadataJson(targetPlayerData));
                    }
                }
                case "debug" -> {
                    if (sender instanceof ConsoleCommandSender)
                        sender.sendMessage("§cThe terminal cannot debug.");
                    else {
                        PlayerData playerData = PlayerData.get((Player) sender);
                        if (playerData == null) // This should never happen
                            return true;
                        if (playerData.isDebugging()) {
                            playerData.stopDebugging();
                            sender.sendMessage("§cNo-longer debugging.");
                            return true;
                        }
                        if (args.length < 3)
                            sender.sendMessage("§cYou must provide a check to debug.");
                        else {
                            Class<? extends Check> checkClass = CheckManager.getCheckClass(args[2]);
                            if (checkClass == null) {
                                sender.sendMessage("§cInvalid check. Checks: §f" + CheckManager.CHECK_CLASSES.stream()
                                        .map(Class::getSimpleName)
                                        .collect(Collectors.joining("§7, §f")));
                                return true;
                            }
                            playerData.startDebugging(target.getUniqueId(), checkClass);
                            sender.sendMessage("§7Now debugging §f" + checkClass.getSimpleName() + " §7for §f" + target.getName() + "§7.");
                        }
                    }
                }
            }
        }
        return true;
    }
}