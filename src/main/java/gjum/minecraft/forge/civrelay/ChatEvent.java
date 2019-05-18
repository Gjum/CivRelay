package gjum.minecraft.forge.civrelay;

import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatEvent implements Event {
    public static final Pattern groupChatPattern = Pattern.compile("\\[(\\S+)\\] ([a-zA-Z0-9_]{3,16}): (.+)");
    public static final Pattern localChatPattern = Pattern.compile("<([a-zA-Z0-9_]{3,16})> (.+)");

    public final String group;
    public final String player;
    public final String message;

    public ChatEvent(String group, String player, String message) {
        this.group = group;
        this.player = player;
        this.message = message;
    }

    public static ChatEvent fromChat(ITextComponent textComponent) {
        final String text = TextFormatting.getTextWithoutFormattingCodes(textComponent.getUnformattedText());

        final Matcher groupChatMatcher = groupChatPattern.matcher(text);
        if (groupChatMatcher.matches()) {
            final String group = groupChatMatcher.group(1);
            final String player = groupChatMatcher.group(2);
            final String message = groupChatMatcher.group(3);
            return new ChatEvent(group, player, message);
        }

        final Matcher localChatMatcher = localChatPattern.matcher(text);
        if (localChatMatcher.matches()) {
            final String group = "";
            final String player = localChatMatcher.group(1);
            final String message = localChatMatcher.group(2);
            return new ChatEvent(group, player, message);
        }

        // TODO allow configuring regexps
        // TODO emit event for private message

        return null;
    }

    @Override
    public Type getType() {
        return Type.CHAT;
    }

    @Override
    public String getAction() {
        return null;
    }

    @Override
    public String getActionText() {
        return null;
    }

    @Override
    public String getChatMessage() {
        return message;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public String getSnitch() {
        return null;
    }

    @Override
    public String getWorld() {
        return null;
    }

    @Override
    public Vec3i getPos() {
        return null;
    }
}
