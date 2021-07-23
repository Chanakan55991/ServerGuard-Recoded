package net.chanakancloud.serverguard.impl.meta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.NonNull;
import net.chanakancloud.serverguard.impl.meta.impl.PlayerMetadata;
import net.chanakancloud.serverguard.impl.meta.impl.ServerMetadata;
import net.chanakancloud.serverguard.impl.meta.impl.ViolationMetadata;
import net.chanakancloud.serverguard.impl.player.PlayerData;

public class MetadataManager {
    private static final IMetadata[] METADATA_ELEMENTS = new IMetadata[] {
            new PlayerMetadata(),
            new ViolationMetadata(),
            new ServerMetadata()
    };

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public static String getMetadataJson(@NonNull PlayerData playerData) {
        JsonObject jsonElement = new JsonObject();
        for (IMetadata metadataElement : METADATA_ELEMENTS)
            jsonElement.add(metadataElement.getName(), metadataElement.getJsonElement(playerData));
        return GSON.toJson(jsonElement);
    }
}
