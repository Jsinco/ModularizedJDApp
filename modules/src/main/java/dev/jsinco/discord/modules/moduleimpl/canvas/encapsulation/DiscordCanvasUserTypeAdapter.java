package dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.jsinco.discord.framework.serdes.Serdes;
import dev.jsinco.discord.modules.cryptography.AESEncrypt;

import java.io.IOException;

public class DiscordCanvasUserTypeAdapter extends TypeAdapter<DiscordCanvasUser> {

    private static final Serdes serdes = Serdes.getInstance();
    private static final AESEncrypt aesEncrypt = AESEncrypt.getInstance();

    @Override
    public void write(JsonWriter jsonWriter, DiscordCanvasUser user) throws IOException {
        jsonWriter.beginObject();

        String encryptedToken;
        try {
            encryptedToken = aesEncrypt.encrypt(user.getCanvasToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        jsonWriter.name("discordId").value(user.getDiscordId());
        jsonWriter.name("canvasToken").value(encryptedToken);
        jsonWriter.name("institution").value(user.getInstitution().name());

        // Serialize userData directly
        jsonWriter.name("userData");
        serdes.getGson().toJson(user.getUserData(), DiscordCanvasUserData.class, jsonWriter);

        jsonWriter.endObject();
    }

    @Override
    public DiscordCanvasUser read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();

        String discordId = null;
        String canvasToken = null;
        Institution institution = null;
        DiscordCanvasUserData userData = null;

        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "discordId" -> discordId = jsonReader.nextString();
                case "canvasToken" -> {
                    try {
                        canvasToken = aesEncrypt.decrypt(jsonReader.nextString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                case "institution" -> institution = Institution.valueOf(jsonReader.nextString());
                case "userData" -> userData = serdes.getGson().fromJson(jsonReader, DiscordCanvasUserData.class);
            }
        }
        jsonReader.endObject();
        return new DiscordCanvasUser(discordId, canvasToken, institution, userData);
    }
}
