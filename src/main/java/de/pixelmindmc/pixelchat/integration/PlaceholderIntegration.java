package de.pixelmindmc.pixelchat.integration;

import de.pixelmindmc.pixelchat.PixelChat;
import de.pixelmindmc.pixelchat.constants.ConfigConstants;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Provides PlaceholderAPI placeholders for PixelChat Guardian.
 */
public class PlaceholderIntegration extends PlaceholderExpansion {
    private final PixelChat plugin;

    public PlaceholderIntegration(@NotNull PixelChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "pixelchat";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equalsIgnoreCase("strikes")) {
            int strikes = plugin.getConfigHelperPlayerStrikes().getInt(player.getUniqueId() + ".strikes");
            return String.valueOf(strikes);
        }
        if (identifier.equalsIgnoreCase("warnings")) {
            int strikes = plugin.getConfigHelperPlayerStrikes().getInt(player.getUniqueId() + ".strikes");
            int warningsUntilKick = plugin.getConfigHelper().getInt(ConfigConstants.CHATGUARD_STRIKES_BEFORE_KICK);
            return String.valueOf(Math.max(0, warningsUntilKick - strikes));
        }
        return null;
    }
}
