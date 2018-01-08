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
        final Config config = Config.instance;
        final Vec2 margin = new Vec2(10, 10);

        // TODO button to reload config from file

        final LayoutContainer elementsContainer = new LayoutContainer(COLUMN)
                .add(new ElementLabel(this, MOD_NAME + " Settings", ALIGN_CENTER))
                .add(new LayoutSpacer(margin));

        final LayoutContainer listControlsRow = new LayoutContainer(ROW)
                .add(new ElementButton(this, () -> {
                    final Filter filter;
                    if (config.filters.size() <= 0) filter = new Filter();
                    else filter = config.filters.get(config.filters.size() - 1).makeCopy();
                    config.filters.add(filter);
                    mc.displayGuiScreen(new FilterGui(new SettingsGui(parentScreen), filter));
                }, "Add Filter"))

                .add(new LayoutSpacer(margin))
                .add(new LayoutSpacer())

                .add(new ElementCycleButton(this, (state) -> {
                    config.modEnabled = "Mod enabled".equals(state);
                    config.save(null);
                }, (config.modEnabled ? "Mod enabled" : "Mod disabled"),
                        "Mod enabled", "Mod disabled"));
        elementsContainer
                .add(listControlsRow)
                .add(new LayoutSpacer(margin));

        for (Filter filter : config.filters) {
            final ElementLabel label = new ElementLabel(this, filter.getDescription(), ALIGN_LEFT);
            label.setColor(filter.isEnabled() ? Color.WHITE : Color.GRAY);

            final LayoutContainer filterRow = new LayoutContainer(ROW)
                    .add(label)

                    .add(new LayoutSpacer(margin))
                    .add(new LayoutSpacer())

                    .add(new ElementButton(this, () -> {
                        mc.displayGuiScreen(new FilterGui(new SettingsGui(parentScreen), filter));
                    }, "Edit"))

                    .add(new ElementCycleButton(this, status -> {
                        final boolean enabled = "Enabled".equals(status);
                        filter.setEnabled(enabled);
                        label.setColor(enabled ? Color.WHITE : Color.GRAY);
                        config.save(null);
                    }, (filter.isEnabled() ? "Enabled" : "Disabled"),
                            "Enabled", "Disabled"));

            elementsContainer.add(filterRow);
        }

        elementsContainer.add(new LayoutSpacer());

        final LayoutContainer lastRow = new LayoutContainer(ROW)
                .add(new ElementButton(this, () -> {
                    config.save(null);
                    mc.displayGuiScreen(parentScreen);
                }, "Save and back")
                        .setWeight(new Vec2(1, 0)))

                .add(new LayoutSpacer())

                .add(new ElementButton(this, () -> {
                    config.save(null);
                    mc.displayGuiScreen(null);
                }, "Save and close")
                        .setWeight(new Vec2(1, 0)));

        elementsContainer
                .add(new LayoutSpacer(margin))
                .add(lastRow)
                .add(new LayoutSpacer(margin));

        layoutRoot = new LayoutContainer(ROW)
                .add(new LayoutSpacer())
                .add(elementsContainer)
                .add(new LayoutSpacer());
    }

    // TODO highlight erroneous filters
}
