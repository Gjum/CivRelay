package gjum.minecraft.forge.civrelay;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnitchEvent implements Event {
    public static final Pattern snitchAlertPattern = Pattern.compile("\\s*\\*\\s*([^\\s]*)\\s\\b(entered snitch at|logged out in snitch at|logged in to snitch at)\\b\\s*([^\\s]*)\\s\\[([^\\s]*)\\s([-\\d]*)\\s([-\\d]*)\\s([-\\d]*)\\]");
    public static final Pattern snitchAlertHoverPattern = Pattern.compile("^(?i)\\s*Location:\\s*\\[(\\S+?) (-?[0-9]+) (-?[0-9]+) (-?[0-9]+)\\]\\s*Group:\\s*(\\S+?)\\s*Type:\\s*(Entry|Logging)\\s*(?:Cull:\\s*([0-9]+\\.[0-9]+)h?)?\\s*(?:Previous name:\\s*(\\S+?))?\\s*(?:Name:\\s*(\\S+?))?\\s*");

    public final String playerName;
    public final BlockPos pos;
    public final String snitchName;
    public final String actionText;
    public final Action action;
    public final String world;
    public final String group;
    public final String snitchType;
    public final ITextComponent rawMessage;

    public SnitchEvent(String playerName, int x, int y, int z, String actionText, String snitchName, String world, String group, String snitchType, ITextComponent rawMessage) {
        this.playerName = playerName;
        this.pos = new BlockPos(x, y, z);
        this.actionText = actionText;
        this.action = Action.fromSnitchMatch(actionText);
        this.snitchName = snitchName;
        this.world = world;
        this.group = group;
        this.snitchType = snitchType == null ? null : snitchType.toLowerCase();
        this.rawMessage = rawMessage;
    }

    public static SnitchEvent fromChat(ITextComponent rawMessage) {
        Matcher matcher = snitchAlertPattern.matcher(TextFormatting.getTextWithoutFormattingCodes(rawMessage.getUnformattedText()));
        if (!matcher.matches()) {
            return null;
        }

        String group;
        String snitchType;
        HoverEvent hover = getHoverEvent(rawMessage);
        if (hover == null) {
            group = null;
            snitchType = null;
            CivRelayMod.logger.error(
                    "No hover info in snitch alert. The server needs JukeAlert >= v1.6.1");
        } else {
            String hoverText = hover.getValue().getUnformattedComponentText().replace("\n", " ");
            Matcher hoverMatcher = snitchAlertHoverPattern.matcher(hoverText);
            if (!hoverMatcher.matches()) {
                group = null;
                snitchType = null;
                CivRelayMod.logger.error(
                        "Snitch alert hover regex failed to match hover in snitch alert");
            } else {
                group = hoverMatcher.group(5);
                snitchType = hoverMatcher.group(6);
            }
        }

        String playerName = matcher.group(1);
        String activity = matcher.group(2);
        String snitchName = matcher.group(3);
        String worldName = matcher.group(4);
        int x = Integer.parseInt(matcher.group(5));
        int y = Integer.parseInt(matcher.group(6));
        int z = Integer.parseInt(matcher.group(7));
        return new SnitchEvent(playerName, x, y, z, activity, snitchName, worldName, group, snitchType, rawMessage);
    }

    @Override
    public Type getType() {
        return Type.SNITCH;
    }

    @Override
    public String getAction() {
        return action.msg;
    }

    @Override
    public String getActionText() {
        return actionText;
    }

    @Override
    public String getChatMessage() {
        return TextFormatting.getTextWithoutFormattingCodes(rawMessage.getUnformattedText());
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getPlayer() {
        return playerName;
    }

    @Override
    public String getSnitch() {
        return snitchName;
    }

    @Override
    public int getX() {
        return pos.getX();
    }

    @Override
    public int getY() {
        return pos.getY();
    }

    @Override
    public int getZ() {
        return pos.getZ();
    }

    private static HoverEvent getHoverEvent(ITextComponent rawMessage) {
        List<ITextComponent> siblings = rawMessage.getSiblings();
        if (siblings.size() <= 0) {
            return null;
        }
        ITextComponent hoverComponent = siblings.get(0);
        HoverEvent hover = hoverComponent.getStyle().getHoverEvent();
        if (hover == null) {
            return null;
        }
        return hover;
    }
}
