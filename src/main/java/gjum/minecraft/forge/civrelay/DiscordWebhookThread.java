package gjum.minecraft.forge.civrelay;

import net.minecraft.util.Tuple;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DiscordWebhookThread extends Thread {
    private final LinkedList<Tuple<String, String>> jsonQueue = new LinkedList<>();

    public DiscordWebhookThread() {
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                runLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void pushJsonTo(String json, String webhookUrl) {
        jsonQueue.add(new Tuple<>(json, webhookUrl));
        interrupt();
    }

    private void runLoop() throws IOException {
        Tuple<String, String> result;
        synchronized (this) {
            result = jsonQueue.poll();
        }
        Tuple<String, String> jsonAndUrl = result;
        if (jsonAndUrl == null) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
            return; // jump back up to poll
        }

        byte[] json = jsonAndUrl.getFirst().getBytes(StandardCharsets.UTF_8);
        String webhookUrl = jsonAndUrl.getSecond();

        HttpURLConnection connection = (HttpURLConnection) new URL(webhookUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.addRequestProperty("User-Agent", "Mozilla/4.76");
        connection.setRequestProperty("Content-Length", String.valueOf(json.length));
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setConnectTimeout(5000);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(json);
            os.flush();

            if (connection.getResponseCode() < 200 || 300 <= connection.getResponseCode()) {
                CivRelayMod.logger.error("Error sending JSON to " + webhookUrl + " " + connection.getResponseCode() + ": " + connection.getResponseMessage());
            }
        } catch (IOException e) {
            CivRelayMod.logger.error("Error sending JSON to " + webhookUrl);
            e.printStackTrace();
        }
    }
}
