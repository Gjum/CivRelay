package gjum.minecraft.forge.civrelay;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import gjum.minecraft.forge.gui.*;
import net.minecraft.client.gui.GuiScreen;

import java.util.Arrays;
import java.util.regex.Pattern;

import static gjum.minecraft.forge.civrelay.CivRelayMod.MOD_NAME;
import static gjum.minecraft.forge.gui.ElementLabel.Alignment.ALIGN_CENTER;
import static gjum.minecraft.forge.gui.ElementLabel.Alignment.ALIGN_LEFT;
import static gjum.minecraft.forge.gui.Vec2.Direction.COLUMN;
import static gjum.minecraft.forge.gui.Vec2.Direction.ROW;

public class FilterGui extends GuiBase {
    private static final String[] eventTypes = Arrays.stream(Event.Type.values()).map(Enum::name).toArray(String[]::new);
    private static final String[] targets = Arrays.stream(Condition.Target.values()).map(Enum::name).toArray(String[]::new);
    private static final String[] tests = Arrays.stream(Condition.Test.values()).map(Enum::name).toArray(String[]::new);

    private final Filter filter;

    public FilterGui(GuiScreen parentScreen, Filter filter) {
        super(parentScreen);
        this.filter = filter;
    }

    @Override
    public void buildLayout() {
        final Vec2 margin = new Vec2(10, 10);

        // TODO scrolling

        final ElementButton gameAddressBtn = new ElementButton(this, () -> {
            filter.gameAddress = CivRelayMod.getCurrentGameAddress();
            rebuild();
        }, "Use current")
                .setEnabled(!filter.gameAddress.equals(CivRelayMod.getCurrentGameAddress()));
        final ElementTextField gameAddressTxt = new ElementTextField(this, gameAddress -> {
            filter.gameAddress = gameAddress;
            gameAddressBtn.setEnabled(!filter.gameAddress.equals(CivRelayMod.getCurrentGameAddress()));
            return !gameAddress.isEmpty();
        }, filter.gameAddress);

        final LayoutContainer descriptionAndGameAddressRow = new LayoutContainer(ROW)
                .add(new ElementLabel(this, "Description:", ALIGN_LEFT))
                .add(new ElementTextField(this, description -> {
                    filter.description = description;
                    return true;
                }, filter.getDescription()))
                .add(new LayoutSpacer(margin))
                .add(new ElementLabel(this, "Game address:", ALIGN_LEFT))
                .add(gameAddressTxt)
                .add(gameAddressBtn);

        final LayoutContainer webhookRow = new LayoutContainer(ROW)
                .add(new ElementLabel(this, "Webhook URL:", ALIGN_LEFT))
                .add(new ElementTextField(this, webhookAddress -> {
                    final boolean valid = Pattern.matches("https?://.+", webhookAddress);
                    if (valid) filter.setWebhookAddress(webhookAddress);
                    return valid;
                }, filter.getWebhookAddress()));

        final LayoutContainer formatRow = new LayoutContainer(ROW)
                .add(new ElementLabel(this, "Alert format:", ALIGN_LEFT))
                .add(new ElementTextField(this, format -> {
                    try {
                        new Gson().fromJson(format, Object.class);
                        filter.setFormat(format);
                        return true;
                    } catch (JsonSyntaxException e) {
                        return false;
                    }
                }, filter.getFormat()));

        final ElementCycleButton eventTypeBtn = new ElementCycleButton(this, status -> {
            filter.eventType = Event.Type.valueOf(status);
        }, filter.eventType.name(), eventTypes);

        final ElementButton duplicateFilterBtn = new ElementButton(this, () -> {
            Filter newFilter = filter.makeCopy();
            newFilter.description += " (copy)";
            Config.instance.filters.add(newFilter);
            Config.instance.save(null);
            mc.displayGuiScreen(new FilterGui(parentScreen, newFilter));
        }, "Duplicate Filter");

        final ElementCycleButton deleteFilterBtn = new ElementCycleButton(this, status -> {
            if ("!".equals(status)) {
                Config.instance.filters.remove(filter);
                Config.instance.save(null);
                mc.displayGuiScreen(parentScreen);
            }
        }, "Delete Filter", "Delete Filter", "Delete Filter?", "!");

        final LayoutContainer filterControlsRow = new LayoutContainer(ROW)
                .add(new ElementLabel(this, "Filter type:", ALIGN_LEFT))
                .add(eventTypeBtn)
                .add(new LayoutSpacer())
                .add(duplicateFilterBtn)
                .add(new LayoutSpacer(margin))
                .add(deleteFilterBtn);

        final ElementButton addConditionBtn = new ElementButton(this, () -> {
            final Condition lastCondition = filter.conditions.size() > 0
                    ? filter.conditions.get(filter.conditions.size() - 1)
                    : Filter.EXAMPLE_CONDITION;
            filter.conditions.add(lastCondition.makeCopy());
            rebuild();
        }, "Add Condition");

        final ElementCycleButton toggleFilterEnabledBtn = new ElementCycleButton(this,
                status -> {
                    filter.setEnabled("Filter Enabled".equals(status));
                },
                (filter.isEnabled() ? "Filter Enabled" : "Filter Disabled"),
                "Filter Enabled", "Filter Disabled");

        final LayoutContainer listControlsRow = new LayoutContainer(ROW)
                .add(addConditionBtn)
                .add(new LayoutSpacer())
                .add(toggleFilterEnabledBtn);

        final LayoutContainer elementsContainer = new LayoutContainer(COLUMN)
                .setWeight(new Vec2(10, 1))
                .add(new ElementLabel(this, MOD_NAME + " Filter", ALIGN_CENTER))
                .add(new LayoutSpacer(margin))
                .add(descriptionAndGameAddressRow)
                .add(webhookRow)
                .add(formatRow)
                .add(new LayoutSpacer(margin))
                .add(filterControlsRow)
                .add(new LayoutSpacer(margin))
                .add(listControlsRow)
                .add(new LayoutSpacer(margin));

        for (Condition condition : filter.conditions) {
            final LayoutContainer filterRow = new LayoutContainer(ROW)
                    .add(new ElementCycleButton(this, status -> {
                        condition.setNegate("If not".equals(status));
                    }, (!condition.negate ? "If" : "If not"), "If", "If not"))

                    .add(new ElementCycleButton(this, status -> {
                        condition.setTarget(Condition.Target.valueOf(status));
                    }, condition.target.name(), targets))

                    .add(new ElementCycleButton(this, status -> {
                        condition.setTest(Condition.Test.valueOf(status));
                    }, condition.test.name(), tests))

                    .add(new ElementTextField(this, value -> {
                        condition.setTestArg(value);
                        return condition.isValid();
                    }, condition.testArg))

                    .add(new LayoutSpacer(margin))

                    .add(new ElementCycleButton(this, status -> {
                        if ("!".equals(status)) {
                            filter.conditions.remove(condition);
                            rebuild();
                        }
                    }, "X", "X", "X?", "!"));

            elementsContainer.add(filterRow);
        }

        elementsContainer.add(new LayoutSpacer());

        final LayoutContainer lastRow = new LayoutContainer(ROW)
                .add(new ElementButton(this, () -> {
                    Config.instance.save(null);
                    mc.displayGuiScreen(parentScreen);
                }, "Save and back"))

                .add(new LayoutSpacer())

                .add(new ElementButton(this, () -> {
                    Config.instance.save(null);
                    mc.displayGuiScreen(null);
                }, "Save and close"));

        elementsContainer
                .add(new LayoutSpacer(margin))
                .add(lastRow)
                .add(new LayoutSpacer(margin));

        layoutRoot = new LayoutContainer(ROW)
                .add(new LayoutSpacer(margin))
                .add(elementsContainer)
                .add(new LayoutSpacer(margin));
    }
}
