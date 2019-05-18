package gjum.minecraft.forge.civrelay;

import com.google.gson.Gson;
import net.minecraft.util.math.Vec3i;

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
        final long timeUnix = System.currentTimeMillis() / 1000;
        str = str.replaceAll("<timeLocal>", timeLocal);
        str = str.replaceAll("<timeUTC>", timeUtc);
        str = str.replaceAll("<timeUnix>", String.valueOf(timeUnix));

        if (event.getType() != null && event.getType().description != null)
            str = str.replaceAll("<event>", event.getType().description);
        if (event.getAction() != null) str = str.replaceAll("<action>", event.getAction());
        if (event.getActionText() != null) str = str.replaceAll("<actionText>", event.getActionText());
        if (event.getWorld() != null) str = str.replaceAll("<world>", event.getWorld());

        if (event.getChatMessage() != null) str = str.replaceAll("<chatMsg>", safeStr(event.getChatMessage()));
        if (event.getGroup() != null) str = str.replaceAll("<group>", safeStr(event.getGroup()));
        if (event.getPlayer() != null) str = str.replaceAll("<player>", safeStr(event.getPlayer()));
        if (event.getSnitch() != null) str = str.replaceAll("<snitch>", safeStr(event.getSnitch()));

        final Vec3i pos = event.getPos();
        if (pos != null) {
            str = str.replaceAll("<x>", String.valueOf(pos.getX()));
            str = str.replaceAll("<y>", String.valueOf(pos.getY()));
            str = str.replaceAll("<z>", String.valueOf(pos.getZ()));

            final int rx = (pos.getX() + 5) / 10 * 10 - (pos.getX() < 0 ? 10 : 0);
            final int ry = (pos.getY() + 5) / 10 * 10 - (pos.getY() < 0 ? 10 : 0);
            final int rz = (pos.getZ() + 5) / 10 * 10 - (pos.getZ() < 0 ? 10 : 0);
            str = str.replaceAll("<rx>", String.valueOf(rx));
            str = str.replaceAll("<ry>", String.valueOf(ry));
            str = str.replaceAll("<rz>", String.valueOf(rz));
        }

        return str;
    }

    public static String safeStr(String str) {
        return str.replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\"", "\\\\\"")
                .replaceAll("\n", "\\\\n");
    }
}
