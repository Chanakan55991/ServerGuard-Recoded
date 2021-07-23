package net.chanakancloud.serverguard.api.event;

import lombok.NonNull;
import net.chanakancloud.serverguard.api.player.Violation;
import org.bukkit.entity.Player;

public class PlayerPunishEvent extends PlayerCheatEvent {

    public PlayerPunishEvent(@NonNull PlayerCheatEvent playerCheatEvent) {
        super(playerCheatEvent.getPlayer(), playerCheatEvent.getViolation());
    }
}
