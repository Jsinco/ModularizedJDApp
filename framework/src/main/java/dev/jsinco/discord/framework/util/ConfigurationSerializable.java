package dev.jsinco.discord.framework.util;

// TODO: Don't use this interface, doesn't do anything yet
public interface ConfigurationSerializable {
    String serialize();

    static ConfigurationSerializable deserialize(String serialized) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
