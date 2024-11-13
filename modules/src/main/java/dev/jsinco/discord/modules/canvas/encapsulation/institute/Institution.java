package dev.jsinco.discord.modules.canvas.encapsulation.institute;

import com.google.gson.reflect.TypeToken;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.serdes.Serdes;
import dev.jsinco.discord.framework.serdes.TypeAdapter;
import dev.jsinco.discord.modules.util.CanvasUtil;
import dev.jsinco.discord.modules.util.ImageUtil;
import dev.jsinco.discord.modules.util.StringUtil;
import dev.jsinco.discord.modules.util.Util;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TypeAdapter(InstitutionTypeAdapter.class)
@Getter
@Setter
public class Institution {



    //////////////////////////
    // Defined Institutions //
    //////////////////////////

    public static final Institution UNKNOWN_INSTITUTION = new Institution(
            "https://canvas.instructure.com", // Must be a valid instructure URL (obviously)
            "Unknown Institution", // The institution's proper name
            "Unknown-Institution", // The institution's abbreviated name
            "#e4fbea" // A color to represent the institution
            // Any known restrictions for this institution's Canvas API
    );


    public static final Institution HILLSBOROUGH_COMMUNITY_COLLEGE = new Institution(
            "https://hcc.instructure.com",
            "Hillsborough Community College",
            "HCC",
            "#001e60",
            KnownRestriction.CANNOT_VIEW_ACCOUNTS
    );
    public static final Institution UNIVERSITY_OF_SOUTH_FLORIDA = new Institution(
            "https://usflearn.instructure.com",
            "University of South Florida",
            "USF",
            "#016948"
    );
    public static final Institution FLORIDA_ATLANTIC_UNIVERSITY = new Institution(
            "https://fau.instructure.com",
            "Florida Atlantic University",
            "FAU",
            "#c10435"
    );
    public static final Institution KANSAS_STATE_UNIVERSITY = new Institution(
            "https://k-state.instructure.com",
            "Kansas State University",
            "KSU",
            "#512888"
    );




    //////////////////////////
    // Defined Institutions //
    //////////////////////////



    private final String url;
    private final String properName;
    private final String abbreviatedName;
    private final Color color;
    private final KnownRestriction[] knownRestrictions;

    private BufferedImage canvasLogo;
    private String FIELD_NAME;
    private boolean isCustom = false;

    public Institution(String url, String properName, String abbreviatedName, KnownRestriction... knownRestrictions) {
        this.url = url;
        this.properName = properName;
        this.abbreviatedName = abbreviatedName;
        this.color = Color.DARK_GRAY;
        this.knownRestrictions = knownRestrictions;

        this.canvasLogo = getCanvasLogo();
    }

    public Institution(String url, String properName, String abbreviatedName, Color color, KnownRestriction... knownRestrictions) {
        this.url = url;
        this.properName = properName;
        this.abbreviatedName = abbreviatedName;
        this.color = color;
        this.knownRestrictions = knownRestrictions;

        this.canvasLogo = getCanvasLogo();
    }

    public Institution(String url, String properName, String abbreviatedName, String color, KnownRestriction... knownRestrictions) {
        this.url = url;
        this.properName = properName;
        this.abbreviatedName = abbreviatedName;
        this.color = StringUtil.hex(color);
        this.knownRestrictions = knownRestrictions;

        this.canvasLogo = getCanvasLogo();
    }

    @Nullable
    public BufferedImage getCanvasLogo() {
        return CanvasUtil.getCanvasLogo(color);
    }

    @Nullable
    public FileUpload getCanvasLogoFileUpload()  {
        try {
            return FileUpload.fromData(ImageUtil.bufferedImageToInputStream(getCanvasLogo(), "png"), "canvas_logo_" + abbreviatedName + ".png");
        } catch (IOException e) {
            FrameWorkLogger.error("Error creating FileUpload for Canvas logo: " + e.getMessage(), e);
            return null;
        }
    }

    public String getCanvasLogoUrl() {
        return "attachment://canvas_logo_" + abbreviatedName + ".png";
    }

    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setColor(color)
                .setThumbnail(getCanvasLogoUrl())
                .setAuthor("Canvas LMS For " + abbreviatedName, url, getCanvasLogoUrl());
    }


    public static void loadCustomInstitutions() {
        Serdes serdes = Serdes.getInstance();

        List<Institution> institutions = null;

        try (FileReader fileReader = new FileReader(Util.getFile("customInstitutions.json"))) {
            Type institutionListType = new TypeToken<List<Institution>>() {}.getType();
            institutions = serdes.getGson().fromJson(fileReader, institutionListType);
        } catch (IOException e) {
            FrameWorkLogger.error("Error loading custom institutions", e);
        }

        if (institutions == null) {
            FrameWorkLogger.info("Loaded 0 custom institutions");
            return;
        }

        for (Institution institution : institutions) {
            VALUES.put(institution.getFIELD_NAME(), institution);
        }
        FrameWorkLogger.info("Loaded " + institutions.size() + " custom institutions");
    }

    public static void saveCustomInstitutions() {
        Serdes serdes = Serdes.getInstance();

        try (FileWriter fileWriter = new FileWriter(Util.getFile("customInstitutions.json"))) {
            List<Institution> institutions = values().stream().filter(Institution::isCustom).toList();
            serdes.getGson().toJson(institutions, fileWriter);
        } catch (IOException e) {
            FrameWorkLogger.error("Error saving custom institutions", e);
        }
        FrameWorkLogger.info("Saved " + values().stream().filter(Institution::isCustom).count() + " custom institutions");
    }


    // Make this class work like an enum

    @Override
    public String toString() {
        return FIELD_NAME;
    }

    public String name() {
        return FIELD_NAME;
    }


    public static final Map<String, Institution> VALUES = new HashMap<>();

    static {
        for (Field field : Institution.class.getDeclaredFields()) {
            if (field.getType() == Institution.class) {
                try {
                    Institution itemType = (Institution) field.get(null);
                    itemType.FIELD_NAME = field.getName();
                    VALUES.put(field.getName(), itemType);
                } catch (IllegalAccessException e) {
                    FrameWorkLogger.error("An error occurred while registering an Institution", e);
                }
            }
        }

        Institution.loadCustomInstitutions();
    }

    public static Institution valueOf(String name) {
        return VALUES.get(name);
    }

    public static List<Institution> values() {
        return VALUES.values().stream().toList();
    }


}
