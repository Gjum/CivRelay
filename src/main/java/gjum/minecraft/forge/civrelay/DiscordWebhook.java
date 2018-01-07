package gjum.minecraft.forge.civrelay;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DiscordWebhook extends Thread {
    public static final Map<String, DiscordWebhook> runningWebhooks = new HashMap<>();

    private final LinkedList<byte[]> jsonQueue = new LinkedList<>();
    private final String webhookUrl;
    private boolean ending = false;

    public DiscordWebhook(String webhookUrl) {
        if (webhookUrl == null || webhookUrl.length() <= 0) {
            throw new IllegalArgumentException("Invalid webhook URL: " + webhookUrl);
        }
        this.webhookUrl = webhookUrl;
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                runLoop();

                if (ending) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void pushJson(String json) {
        jsonQueue.add(json.getBytes(StandardCharsets.UTF_8));
        interrupt();
    }

    public synchronized void end() {
        ending = true;
        interrupt();
    }

    private synchronized byte[] popJson() {
        return jsonQueue.poll();
    }

    private void runLoop() throws IOException {
        byte[] json = popJson();
        if (json == null) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
            return; // jump back up to popJson
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(webhookUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.addRequestProperty("User-Agent", "Mozilla/4.76");
        connection.setRequestProperty("Content-Length", String.valueOf(json.length));
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(json);
            os.flush();

            if (connection.getResponseCode() < 200 || 300 <= connection.getResponseCode()) {
                CivRelayMod.logger.error(connection.getResponseCode() + ": " + connection.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DiscordWebhook getOrStartDiscord(String webhookAddress) {
        final DiscordWebhook discord = runningWebhooks.get(webhookAddress);
        if (discord != null) return discord;
        // not running: start
        DiscordWebhook newDiscord = new DiscordWebhook(webhookAddress);
        runningWebhooks.put(webhookAddress, newDiscord);
        return newDiscord;
    }

    public static synchronized void stopDiscord(String webhookAddress) {
        final DiscordWebhook discord = runningWebhooks.get(webhookAddress);
        if (discord == null) return;
        runningWebhooks.remove(webhookAddress);
        discord.end();
    }
}
