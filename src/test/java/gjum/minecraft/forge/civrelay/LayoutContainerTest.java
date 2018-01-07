package gjum.minecraft.forge.civrelay;

import gjum.minecraft.forge.civrelay.gui.Vec2;
import junit.framework.TestCase;

import java.util.Arrays;

public class LayoutContainerTest extends TestCase {
    public void testEvenSize() {
        final SettingsGui gui = new SettingsGui(null);
        gui.buildLayout();
        gui.layoutRoot.setSize(new Vec2(854/2, 480/2));
    }

    public void testOddSize() {
        final SettingsGui gui = new SettingsGui(null);
        gui.buildLayout();
        gui.layoutRoot.setSize(new Vec2(854-3, 480-3));
    }
}
