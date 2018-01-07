package gjum.minecraft.forge.civrelay;

import gjum.minecraft.forge.civrelay.gui.*;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

import static gjum.minecraft.forge.civrelay.CivRelayMod.MOD_NAME;
import static gjum.minecraft.forge.civrelay.gui.ElementLabel.Alignment.ALIGN_CENTER;
import static gjum.minecraft.forge.civrelay.gui.ElementLabel.Alignment.ALIGN_LEFT;
import static gjum.minecraft.forge.civrelay.gui.Vec2.Direction.COLUMN;
import static gjum.minecraft.forge.civrelay.gui.Vec2.Direction.ROW;

public class SettingsGui extends GuiBase {
    public SettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void buildLayout() {
        final LayoutContainer elementsContainer = new LayoutContainer(COLUMN)
                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(new ElementLabel(this, MOD_NAME + " Settings", ALIGN_CENTER));

        final LayoutContainer listControlsRow = new LayoutContainer(ROW)
                .add(new ElementButton(this, () -> {
                    final Filter filter = new Filter();
                    filter.conditions.add(Filter.EXAMPLE_CONDITION);
                    Config.instance.filters.add(filter);
                    mc.displayGuiScreen(new FilterGui(new SettingsGui(parentScreen), filter));
                }, "Add Filter"))

                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(new LayoutSpacer())

                .add(new ElementCycleButton(this, (state) -> {
                    Config.instance.modEnabled = "Mod enabled".equals(state);
                    Config.instance.save(null);
                }, (Config.instance.modEnabled ? "Mod enabled" : "Mod disabled"),
                        "Mod enabled", "Mod disabled"));
        elementsContainer
                .add(new LayoutSpacer(new Vec2(10, 10)))
                .add(listControlsRow);

        for (Filter filter : Config.instance.filters) {
            final ElementLabel label = new ElementLabel(this, filter.getDescription(), ALIGN_LEFT);
            label.setColor(filter.isEnabled() ? Color.WHITE : Color.GRAY);

            final LayoutContainer filterRow = new LayoutContainer(ROW)
                    .add(label)

                    .add(new LayoutSpacer(new Vec2(10, 10)))
                    .add(new LayoutSpacer())

                    .add(new ElementButton(this, () -> {
                        mc.displayGuiScreen(new FilterGui(new SettingsGui(parentScreen), filter));
                    }, "Edit"))

                    .add(new ElementCycleButton(this, status -> {
                        final boolean enabled = "Enabled".equals(status);
                        filter.setEnabled(enabled);
                        label.setColor(enabled ? Color.WHITE : Color.GRAY);
                        Config.instance.save(null);
                    }, (filter.isEnabled() ? "Enabled" : "Disabled"),
                            "Enabled", "Disabled"));

            elementsContainer
                    .add(new LayoutSpacer(new Vec2(10, 10)))
                    .add(filterRow);
        }

        elementsContainer.add(new LayoutSpacer());

        final LayoutContainer lastRow = new LayoutContainer(ROW)
                .add(new LayoutSpacer())
                .add(new ElementButton(this, () -> {
                    Config.instance.save(null);
                    mc.displayGuiScreen(parentScreen);
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

    // TODO highlight erroneous filters in onChanged
}
