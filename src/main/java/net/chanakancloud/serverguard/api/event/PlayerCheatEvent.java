package net.chanakancloud.serverguard.api.event;

import io.github.retrooper.packetevents.packettype.PacketTypeClasses;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.chanakancloud.serverguard.api.player.Violation;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Setter @Getter
public class PlayerCheatEvent extends Event implements Cancellable {
    public PlayerCheatEvent(@NotNull Player player, @NotNull Violation violation) {
        super(true);
        this.violation = violation;
        this.player = player;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    private final @NonNull Player player;
    private final @NonNull Violation violation;
    private boolean cancelled;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
