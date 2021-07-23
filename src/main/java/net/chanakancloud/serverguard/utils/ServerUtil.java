package net.chanakancloud.serverguard.utils;

import io.github.retrooper.packetevents.PacketEvents;

public class ServerUtil {
    public static boolean lowTPS(){
        return PacketEvents.get().getServerUtils().getTPS() <= 15;
    }
}
