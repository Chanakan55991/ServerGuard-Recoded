package net.chanakancloud.serverguard.api.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.chanakancloud.serverguard.api.check.CheckInfo;
import org.bukkit.Location;

@AllArgsConstructor @Getter
public class Violation {
    private final CheckInfo checkInfo;
    private final String[] data;
    private final int violations;

    private final Location location;
    private final int ping;

    private final double tps;
    private final long timestamp;
}
