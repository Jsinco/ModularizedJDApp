package dev.jsinco.discord.modules.reminders;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.jsinco.discord.framework.FrameWork;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * A GSON (serdes) TypeAdapter for the WrappedReminder class
 * @see MessageFrequency
 * @see ReminderDeleteCommand
 * @see WrappedReminder
 * @see ReminderModule
 * @author Jonah
 */
public class WrappedReminderTypeAdapter extends TypeAdapter<WrappedReminder> {

    @Override
    public void write(JsonWriter out, WrappedReminder value) throws IOException {
        out.beginObject();
        out.name("identifier").value(value.getIdentifier());
        out.name("channel").value(value.getChannel().getId());
        out.name("message").value(value.getMessage());
        out.name("frequency").value(value.getFrequency().toString());
        out.name("when").value(value.getWhen().toString());
        if (value.getLastSent() != null) {
            out.name("lastSent").value(value.getLastSent().toString());
        }
        out.endObject();
    }

    @Override
    public WrappedReminder read(JsonReader in) throws IOException {
        in.beginObject();
        String identifier = null;
        TextChannel channel = null;
        String message = null;
        MessageFrequency frequency = null;
        LocalDateTime when = null;
        LocalDateTime lastSent = null;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "identifier" ->
                    identifier = in.nextString();
                case "channel" ->
                    channel = (TextChannel) FrameWork.getJda().getGuildChannelById(in.nextString());
                case "message" ->
                    message = in.nextString();
                case "frequency" ->
                    frequency = MessageFrequency.fromString(in.nextString());
                case "when" ->
                    when = LocalDateTime.parse(in.nextString());
                case "lastSent" ->
                    lastSent = LocalDateTime.parse(in.nextString());
            }
        }
        in.endObject();
        return new WrappedReminder(identifier, channel, message, frequency, when, lastSent);
    }
}
