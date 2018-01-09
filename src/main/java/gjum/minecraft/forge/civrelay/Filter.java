package gjum.minecraft.forge.civrelay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
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
    private String format = "{\"content\": \"`<timeUTC>` **<player>** <actionText> <snitch> ~[<rx> <ry> <rz>]\"}";

    @Expose
    public String gameAddress = "mc.civclassic.com";

    @Expose
    private String webhookAddress = "";

    private Formatter formatter;

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

    public String getFormat() {
        return format;
    }

    public Filter setFormat(String format) {
        this.format = format;
        formatter = new Formatter(format);
        return this;
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
        return formatter.formatEvent(event);
    }
}
