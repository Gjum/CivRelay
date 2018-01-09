package gjum.minecraft.forge.civrelay;

import com.google.gson.annotations.Expose;

import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Condition {
    @Expose
    public Target target = null;

    @Expose
    public Test test = null;

    @Expose
    public String testArg = "";

    @Expose
    public boolean negate = false;

    public Condition setTarget(Target target) {
        this.target = target;
        return this;
    }

    public Condition setTest(Test test) {
        this.test = test;
        return this;
    }

    public Condition setTestArg(String testArg) {
        this.testArg = testArg;
        return this;
    }

    public Condition setNegate(boolean negate) {
        this.negate = negate;
        return this;
    }

    public Condition makeCopy() {
        return new Condition()
                .setTarget(target)
                .setTest(test)
                .setTestArg(testArg)
                .setNegate(negate);
    }

    enum Target {
        ACTION(Event::getAction),
        CHAT_MSG(Event::getChatMessage),
        GROUP(Event::getGroup),
        PLAYER(Event::getPlayer),
        SNITCH(Event::getSnitch),
        X(Event::getX),
        Y(Event::getY),
        Z(Event::getZ);

        private final Function<Event, Object> getValue;

        Target(Function<Event, Object> getValue) {
            this.getValue = getValue;
        }
    }

    enum Test {
        IN_LIST, CONTAINS, EXACTLY, BETWEEN;

        public boolean apply(String testStr, String valueStr) {
            if (valueStr == null) valueStr = "null";
            // TODO tests could be cached, benchmark this
            switch (this) {
                case EXACTLY:
                    return Pattern.matches(testStr, valueStr);

                case CONTAINS:
                    return Pattern.matches(".*" + testStr + ".*", valueStr);

                case IN_LIST:
                    final String[] entries = testStr.split("[,;]+ *");
                    for (String entry : entries) {
                        if (valueStr.equals(entry)) return true;
                    }
                    return false;

                case BETWEEN:
                    try {
                        final int val, from, to;
                        val = Integer.parseInt(valueStr);
                        final String[] split = testStr.split("[ ,;]+");
                        from = Integer.parseInt(split[0]);
                        to = Integer.parseInt(split[1]);
                        return from <= val && val <= to;
                    } catch (NumberFormatException e) {
                        return false;
                    }
            }
            throw new IllegalStateException("Unexpected Test " + this);
        }
    }

    public boolean test(Event event) {
        String valueStr = String.valueOf(target.getValue.apply(event));
        boolean success = test.apply(testArg, valueStr);
        return success != negate;
    }

    public boolean isValid() {
        if (target == null) return false;
        if (test == null) return false;
        if (test == Test.BETWEEN) {
            if (!Pattern.matches("[-0-9]+[ ,;][-0-9]+", testArg)) return false;
            if (target != Target.X && target != Target.Y && target != Target.Z) return false;
        } else {
            try {
                //noinspection ResultOfMethodCallIgnored
                Pattern.compile(testArg);
            } catch (PatternSyntaxException e) {
                return false;
            }
        }
        return true;
    }
}
