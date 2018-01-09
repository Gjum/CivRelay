package gjum.minecraft.forge.civrelay;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Formatter {
    private static final Gson j = new Gson();

    public final String format;

    public Formatter(String format) {
        this.format = format;
    }

    public String formatEvent(Event event) {
        final String dateFormat = "HH:mm:ss";
        String str = format;

        // TODO snitch type

        final DateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String timeUtc = df.format(new Date());
        final String timeLocal = new SimpleDateFormat(dateFormat).format(new Date());
        str = str.replaceAll("<timeLocal>", timeLocal);
        str = str.replaceAll("<timeUTC>", timeUtc);

        str = str.replaceAll("<event>", event.getType().description);
        str = str.replaceAll("<action>", event.getAction());
        str = str.replaceAll("<actionText>", event.getActionText());
        str = str.replaceAll("<world>", event.getWorld());

        str = str.replaceAll("<chatMsg>", safeStr(event.getChatMessage()));
        str = str.replaceAll("<group>", safeStr(event.getGroup()));
        str = str.replaceAll("<player>", safeStr(event.getPlayer()));
        str = str.replaceAll("<snitch>", safeStr(event.getSnitch()));

        str = str.replaceAll("<x>", String.valueOf(event.getX()));
        str = str.replaceAll("<y>", String.valueOf(event.getY()));
        str = str.replaceAll("<z>", String.valueOf(event.getZ()));

        final int rx = (event.getX() + 5) / 10 * 10 - (event.getX() < 0 ? 10 : 0);
        final int ry = (event.getY() + 5) / 10 * 10 - (event.getY() < 0 ? 10 : 0);
        final int rz = (event.getZ() + 5) / 10 * 10 - (event.getZ() < 0 ? 10 : 0);
        str = str.replaceAll("<rx>", String.valueOf(rx));
        str = str.replaceAll("<ry>", String.valueOf(ry));
        str = str.replaceAll("<rz>", String.valueOf(rz));

        return str;
    }

    private static String safeStr(String str) {
        return j.toJson(str)
                .replaceAll("^\"|\"$", "")
                .replaceAll("\\$", "\\\\\\$");
    }
}
