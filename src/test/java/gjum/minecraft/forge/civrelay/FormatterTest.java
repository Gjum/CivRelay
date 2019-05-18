package gjum.minecraft.forge.civrelay;

import junit.framework.TestCase;

import static gjum.minecraft.forge.civrelay.Formatter.safeStr;

public class FormatterTest extends TestCase {
    public void testSafeStrBackslash() {
        assertEquals("\\\\", safeStr("\\"));
    }

    public void testSafeStrQuote() {
        assertEquals("\\\"", safeStr("\""));
    }

    public void testSafeStrNewline() {
        assertEquals("\\n", safeStr("\n"));
    }

    public void testSafeStrAllowed() {
        assertEquals("should not replace allowed characters",
                "=", safeStr("="));
    }
}
