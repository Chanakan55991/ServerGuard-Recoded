package net.chanakancloud.serverguard.impl.meta;

import com.google.gson.JsonElement;
import lombok.NonNull;
import net.chanakancloud.serverguard.impl.player.PlayerData;

public interface IMetadata {
    String getName();

    JsonElement getJsonElement(@NonNull PlayerData playerData);
}
