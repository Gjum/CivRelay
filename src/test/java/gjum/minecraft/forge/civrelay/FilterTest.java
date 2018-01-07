package gjum.minecraft.forge.civrelay;

import junit.framework.TestCase;
import net.minecraft.util.text.TextComponentString;

public class FilterTest extends TestCase {
    private static final String gameAddress = "mc.example.com";

    public void testChecksMultipleConditions() throws Exception {
        final Filter filter = new Filter();
        filter.gameAddress = gameAddress;
        filter.eventType = Event.Type.PLAYER_STATUS;

        filter.conditions.add(new Condition()
                .setTarget(Condition.Target.PLAYER)
                .setTest(Condition.Test.IN_LIST)
                .setTestArg("me,you"));
        filter.conditions.add(new Condition()
                .setTarget(Condition.Target.ACTION)
                .setTest(Condition.Test.EXACTLY)
                .setTestArg("Login"));

        assertTrue(filter.test(new PlayerStatusEvent("me", Event.Action.LOGIN), gameAddress));
        assertFalse(filter.test(new PlayerStatusEvent("me", Event.Action.LOGOUT), gameAddress));
        assertFalse(filter.test(new PlayerStatusEvent("someone_else", Event.Action.LOGIN), gameAddress));
    }

    public void testChecksEventType() throws Exception {
        final Filter filter = new Filter();
        filter.gameAddress = gameAddress;
        filter.eventType = Event.Type.PLAYER_STATUS;

        assertTrue(filter.test(new PlayerStatusEvent("me", Event.Action.LOGOUT), gameAddress));
        assertFalse(filter.test(new SnitchEvent("me", 1, 2, 3, "entered snitch at", "My_Snitch", "world", "MyGroup", "Entry", null), gameAddress));
    }

    public void testFormat() throws Exception {
        final Filter filter = new Filter();
        filter.format = "<player> <action> <snitch> (<group>) [<x> <y> <z>] ~[<rx> <ry> <rz>]";

        assertEquals("me Enter My_Snitch (MyGroup) [-11 21 -46] ~[-10 20 -50]", filter.formatEvent(new SnitchEvent("me", -11, 21, -46, "entered snitch at", "My_Snitch", "world", "MyGroup", "Entry", new TextComponentString(""))));
    }
}
