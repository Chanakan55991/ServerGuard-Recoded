package net.chanakancloud.serverguard.impl.meta.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import net.chanakancloud.serverguard.ServerGuard;
import net.chanakancloud.serverguard.impl.meta.IMetadata;
import net.chanakancloud.serverguard.impl.player.PlayerData;
import org.bukkit.Bukkit;

public class ServerMetadata implements IMetadata {

    @Override
    public String getName() {
        return "server";
    }

    @Override
    public JsonElement getJsonElement(@NonNull PlayerData playerData) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("players", Bukkit.getOnlinePlayers().size());
        jsonObject.addProperty("tps", ServerGuard.INSTANCE.getRecentTps()[0]);
        return jsonObject;
    }
}
