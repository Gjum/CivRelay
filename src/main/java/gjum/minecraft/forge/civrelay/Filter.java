package gjum.minecraft.forge.civrelay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static gjum.minecraft.forge.civrelay.Condition.Target.GROUP;
import static gjum.minecraft.forge.civrelay.Condition.Test.IN_LIST;

public class Filter {
    private static final Gson j = new GsonBuilder().disableHtmlEscaping().create();

    public static final Condition EXAMPLE_CONDITION = new Condition().setTarget(GROUP).setTest(IN_LIST).setTestArg("MyGroup, MyOtherGroup");

    @Expose
    public String description = "";

    @Expose
    private boolean enabled = true;

    /**
     * if all conditions match, the event is processed
     */
    @Expose
    public final List<Condition> conditions = new ArrayList<>();

    @Expose
    public Event.Type eventType = Event.Type.SNITCH;

    @Expose
    public String format = "{\"content\": \"`<timeUTC>` **<player>** <actionText> <snitch> ~[<rx> <ry> <rz>]\"}";

    @Expose
    public String gameAddress = "mc.civclassic.com";

    @Expose
    private String webhookAddress = "";

    public String getDescription() {
        if (description == null || description.isEmpty()) {
            return gameAddress + " " + eventType.description + " with " + conditions.size() + " conditions";
        }
        return description;
    }

    public Filter setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getWebhookAddress() {
        return webhookAddress;
    }

    public Filter setWebhookAddress(String address) {
        webhookAddress = address;
        return this;
    }

    public Filter makeCopy() {
        Filter filter = new Filter();

        filter.description = this.description;
        filter.setEnabled(this.isEnabled());
        filter.conditions.addAll(this.conditions.stream()
                .map(Condition::makeCopy).collect(Collectors.toList()));
        filter.eventType = this.eventType;
        filter.format = this.format;
        filter.gameAddress = this.gameAddress;
        filter.setWebhookAddress(this.getWebhookAddress());

        return filter;
    }

    /**
     * @return true if this event should get processed, false if it got filtered out (wrong type or didn't match all conditions)
     */
    public boolean test(Event event, String currentGameAddress) {
        if (!gameAddress.equals(currentGameAddress)) {
            if (!(gameAddress + ":25565").equals(currentGameAddress)
                    && !gameAddress.equals(currentGameAddress + ":25565")) {
                return false;
            }
        }

        if (eventType != event.getType()) return false;

        for (Condition condition : conditions) {
            if (!condition.test(event)) {
                return false;
            }
        }

        return true;
    }

    public String formatEvent(Event event) {
        final String dateFormat = "HH:mm:ss";
        String str = format;

        // TODO snitch type

        final DateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String timeUtc = df.format(new Date());
        final String timeLocal = new SimpleDateFormat(dateFormat).format(new Date());
        str = str.replaceAll("<timeLocal>", timeLocal);
        str = str.replaceAll("<timeUTC>", timeUtc);

        str = str.replaceAll("<event>", event.getType().description);
        str = str.replaceAll("<action>", event.getAction());
        str = str.replaceAll("<actionText>", event.getActionText());
        str = str.replaceAll("<world>", event.getWorld());

        str = str.replaceAll("<chatMsg>", safeStr(event.getChatMessage()));
        str = str.replaceAll("<group>", safeStr(event.getGroup()));
        str = str.replaceAll("<player>", safeStr(event.getPlayer()));
        str = str.replaceAll("<snitch>", safeStr(event.getSnitch()));

        str = str.replaceAll("<x>", String.valueOf(event.getX()));
        str = str.replaceAll("<y>", String.valueOf(event.getY()));
        str = str.replaceAll("<z>", String.valueOf(event.getZ()));

        final int rx = (event.getX() + 5) / 10 * 10 - (event.getX() < 0 ? 10 : 0);
        final int ry = (event.getY() + 5) / 10 * 10 - (event.getY() < 0 ? 10 : 0);
        final int rz = (event.getZ() + 5) / 10 * 10 - (event.getZ() < 0 ? 10 : 0);
        str = str.replaceAll("<rx>", String.valueOf(rx));
        str = str.replaceAll("<ry>", String.valueOf(ry));
        str = str.replaceAll("<rz>", String.valueOf(rz));

        return str;
    }

    private static final String safeStr(String str) {
        return j.toJson(str)
                .replaceAll("^\"|\"$", "")
                .replaceAll("\\$", "\\\\\\$");
    }
}
