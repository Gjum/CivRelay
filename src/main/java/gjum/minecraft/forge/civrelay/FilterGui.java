package gjum.minecraft.forge.civrelay;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import gjum.minecraft.forge.civrelay.gui.*;
import net.minecraft.client.gui.GuiScreen;

import java.util.Arrays;
import java.util.regex.Pattern;

import static gjum.minecraft.forge.civrelay.CivRelayMod.MOD_NAME;
import static gjum.minecraft.forge.civrelay.gui.ElementLabel.Alignment.ALIGN_CENTER;
import static gjum.minecraft.forge.civrelay.gui.ElementLabel.Alignment.ALIGN_LEFT;
import static gjum.minecraft.forge.civrelay.gui.Vec2.Direction.COLUMN;
import static gjum.minecraft.forge.civrelay.gui.Vec2.Direction.ROW;

public class FilterGui extends GuiBase {
    private final Filter filter;

    public FilterGui(GuiScreen parentScreen, Filter filter) {
        super(parentScreen);
        this.filter = filter;
    }

    @Override
    public void buildLayout() {
        String[] eventTypes = Arrays.stream(Event.Type.values()).map(Enum::name).toArray(String[]::new);
        String[] targets = Arrays.stream(Condition.Target.values()).map(Enum::name).toArray(String[]::new);
        String[] tests = Arrays.stream(Condition.Test.values()).map(Enum::name).toArray(String[]::new);

        // TODO reorganize

        final LayoutContainer elementsContainer = new LayoutContainer(COLUMN)
                .setWeight(new Vec2(10, 1))
                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(new ElementLabel(this, MOD_NAME + " Filter", ALIGN_CENTER));

        final LayoutContainer descriptionRow = new LayoutContainer(ROW)
                .add(new ElementLabel(this, "Description:", ALIGN_LEFT)
                )
                .add(new ElementTextField(this, description -> {
                    filter.description = description;
                    return true;
                }, filter.getDescription()));
        elementsContainer
                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(descriptionRow);

        final LayoutContainer gameAddressRow = new LayoutContainer(ROW)
                .add(new ElementLabel(this, "Game address:", ALIGN_LEFT)
                )
                .add(new ElementTextField(this, gameAddress -> {
                    filter.gameAddress = gameAddress;
                    return !gameAddress.isEmpty();
                }, filter.gameAddress));
        elementsContainer
                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(gameAddressRow);

        final LayoutContainer webhookRow = new LayoutContainer(ROW)
                .add(new ElementLabel(this, "Webhook URL:", ALIGN_LEFT)
                )
                .add(new ElementTextField(this, webhookAddress -> {
                    final boolean valid = Pattern.matches("https?://.+", webhookAddress);
                    if (valid) filter.setWebhookAddress(webhookAddress);
                    return valid;
                }, filter.getWebhookAddress()));
        elementsContainer
                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(webhookRow);

        final LayoutContainer formatRow = new LayoutContainer(ROW)
                .add(new ElementLabel(this, "Alert format:", ALIGN_LEFT)
                )
                .add(new ElementTextField(this, format -> {
                    try {
                        new Gson().fromJson(format, Object.class);
                        filter.format = format;
                        return true;
                    } catch (JsonSyntaxException e) {
                        return false;
                    }
                }, filter.format));
        elementsContainer
                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(formatRow);

        final LayoutContainer listControlsRow = new LayoutContainer(ROW)
                .add(new ElementButton(this, () -> {
                    filter.conditions.add(Filter.EXAMPLE_CONDITION);
                    rebuild();
                }, "Add Condition"))

                .add(new LayoutSpacer())
                .add(new LayoutSpacer(new Vec2(10, 10)))

                .add(new ElementCycleButton(this, status -> {
                    filter.eventType = Event.Type.valueOf(status);
                    Config.instance.save(null);
                }, filter.eventType.name(), eventTypes))

                .add(new LayoutSpacer())
                .add(new LayoutSpacer(new Vec2(10, 10)))

                .add(new ElementCycleButton(this, status -> {
                    filter.setEnabled("Filter Enabled".equals(status));
                    Config.instance.save(null);
                }, (filter.isEnabled() ? "Filter Enabled" : "Filter Disabled"),
                        "Filter Enabled", "Filter Disabled"))

                .add(new LayoutSpacer())
                .add(new LayoutSpacer(new Vec2(10, 10)))

                .add(new ElementCycleButton(this, status -> {
                    if ("!".equals(status)) {
                        Config.instance.filters.remove(filter);
                        Config.instance.save(null);
                        mc.displayGuiScreen(parentScreen);
                    }
                }, "Delete Filter", "Delete Filter", "Delete Filter?", "!"));
        elementsContainer
                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(listControlsRow);

        for (Condition condition : filter.conditions) {
            final LayoutContainer filterRow = new LayoutContainer(ROW)
                    .add(new ElementCycleButton(this, status -> {
                        condition.setNegate("If not".equals(status));
                        Config.instance.save(null);
                    }, (!condition.negate ? "If" : "If not"), "If", "If not"))

                    .add(new ElementCycleButton(this, status -> {
                        condition.setTarget(Condition.Target.valueOf(status));
                        Config.instance.save(null);
                    }, condition.target.name(), targets))

                    .add(new ElementCycleButton(this, status -> {
                        condition.setTest(Condition.Test.valueOf(status));
                        Config.instance.save(null);
                    }, condition.test.name(), tests))

                    .add(new ElementTextField(this, value -> {
                        condition.setTestArg(value);
                        return condition.isValid();
                    }, condition.testArg))

                    .add(new ElementCycleButton(this, status -> {
                        if ("!".equals(status)) {
                            filter.conditions.remove(condition);
                            Config.instance.save(null);
                            rebuild();
                        }
                    }, "X", "X", "X?", "!"));

            elementsContainer
                    .add(new LayoutSpacer(new Vec2(10, 10)))
                    .add(filterRow);
        }

        elementsContainer.add(new LayoutSpacer());

        final LayoutContainer lastRow = new LayoutContainer(ROW)
                .add(new ElementButton(this, () -> {
                    Config.instance.save(null);
                    mc.displayGuiScreen(parentScreen);
                }, "Back to Settings"))

                .add(new LayoutSpacer())

                .add(new ElementButton(this, () -> {
                    Config.instance.save(null);
                    mc.displayGuiScreen(null);
                }, "Save and close"));

        elementsContainer
                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(lastRow)
                .add(new LayoutSpacer(new Vec2(10, 10)));

        layoutRoot = new LayoutContainer(ROW)
                .add(new LayoutSpacer())
                .add(elementsContainer)
                .add(new LayoutSpacer());
    }
}
