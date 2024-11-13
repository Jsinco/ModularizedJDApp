package dev.jsinco.discord.modules.canvas.encapsulation.institute;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstitutionTypeAdapter extends TypeAdapter<Institution> {

    @Override
    public void write(JsonWriter jsonWriter, Institution institution) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("FIELD_NAME").value(institution.getFIELD_NAME());
        jsonWriter.name("url").value(institution.getUrl());
        jsonWriter.name("properName").value(institution.getProperName());
        jsonWriter.name("abbreviatedName").value(institution.getAbbreviatedName());
        jsonWriter.name("color").value(institution.getColor().getRGB());
        jsonWriter.endObject();
    }

    @Override
    public Institution read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String FIELD_NAME = null;
        String url = null;
        String properName = null;
        String abbreviatedName = null;
        Color color = null;
        List<KnownRestriction> knownRestrictions = new ArrayList<>();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "FIELD_NAME" -> FIELD_NAME = jsonReader.nextString();
                case "url" -> url = jsonReader.nextString();
                case "properName" -> properName = jsonReader.nextString();
                case "abbreviatedName" -> abbreviatedName = jsonReader.nextString();
                case "color" -> color = new Color(jsonReader.nextInt());
            }
        }
        jsonReader.endObject();
        Institution institution = new Institution(url, properName, abbreviatedName, color);
        institution.setCanvasLogo(institution.getCanvasLogo());
        institution.setFIELD_NAME(FIELD_NAME);
        institution.setCustom(true);
        return institution;
    }
}
