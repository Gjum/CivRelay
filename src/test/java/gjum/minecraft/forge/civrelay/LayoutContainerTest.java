package gjum.minecraft.forge.civrelay;

import gjum.minecraft.forge.civrelay.gui.Vec2;
import junit.framework.TestCase;
import org.junit.Test;

public class LayoutContainerTest extends TestCase {
    public void testSetSize() {
        final SettingsGui gui = new SettingsGui(null);
        gui.buildLayout();

        for (int i = -10; i < 10; i++) {
            gui.layoutRoot.setSize(new Vec2(854 + i, 480 + i));

            assertEquals(854 + i, gui.layoutRoot.getCurrentSize().x);
            assertEquals(480 + i, gui.layoutRoot.getCurrentSize().y);
        }
    }
}
