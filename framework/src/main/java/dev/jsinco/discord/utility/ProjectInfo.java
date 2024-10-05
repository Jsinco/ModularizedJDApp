package dev.jsinco.discord.utility;

import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ProjectInfo {

    // FIXME
    private final SnakeYamlConfig file;

    public ProjectInfo(String fileName) {
        file = new SnakeYamlConfig(fileName, true);
    }

    /**
     * Get the project version
     * @return The version of the project.
     */
    public String getVersion() {
        String s = file.getString("version");
        return s == null ? "${version}" : s;
    }

    /**
     * Get the authors of the project.
     * @return A map of the authors and their websites.
     */
    public Map<String, String> getAuthors() {
        Map<String, String> authors = new HashMap<>();
        List<String> keys = file.getConfigurationSection("authors").getKeys();
        for (String key : keys) {
            authors.put(key, file.getString("authors." + key));
        }
        return authors;
    }
}
