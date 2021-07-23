package net.chanakancloud.serverguard.impl.meta.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import net.chanakancloud.serverguard.impl.meta.IMetadata;
import net.chanakancloud.serverguard.impl.player.PlayerData;

public class PlayerMetadata implements IMetadata {

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public JsonElement getJsonElement(@NonNull PlayerData playerData) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", playerData.getUuid().toString());
        jsonObject.addProperty("name", playerData.getBukkitPlayer().getName());
        jsonObject.addProperty("playerDataCreated", playerData.getTimeCreated());
        return jsonObject;
    }
}
