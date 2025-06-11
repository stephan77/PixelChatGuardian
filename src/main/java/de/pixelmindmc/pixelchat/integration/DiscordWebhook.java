package de.pixelmindmc.pixelchat.integration;

import de.pixelmindmc.pixelchat.PixelChat;
import de.pixelmindmc.pixelchat.constants.ConfigConstants;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Simple helper for sending messages to a Discord webhook
 */
public class DiscordWebhook {
    private final PixelChat plugin;
    private final String webhookUrl;

    public DiscordWebhook(@NotNull PixelChat plugin) {
        this.plugin = plugin;
        this.webhookUrl = plugin.getConfigHelper().getString(ConfigConstants.DISCORD_INTEGRATION_WEBHOOK_URL);
    }

    /**
     * Sends a formatted message to the configured Discord webhook
     *
     * @param content the message to send
     */
    public void sendMessage(@NotNull String content) {
        if (!plugin.getConfigHelper().getBoolean(ConfigConstants.DISCORD_INTEGRATION_ENABLED)) return;
        if (webhookUrl == null || webhookUrl.equalsIgnoreCase("WEBHOOK_URL")) return;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URI(webhookUrl).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String payload = "{\"content\":\"" + content.replace("\"", "\\\"") + "\"}";
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(payload.getBytes(StandardCharsets.UTF_8));
                }

                int code = connection.getResponseCode();
                if (code < 200 || code >= 300) {
                    plugin.getLoggingHelper().warning("Discord webhook responded with HTTP " + code);
                }
            } catch (Exception e) {
                plugin.getLoggingHelper().warning("Failed to send Discord webhook: " + e.getMessage());
            }
        });
    }
}
