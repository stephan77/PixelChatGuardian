/*
 * This file is part of PixelChat Guardian.
 * Copyright (C) $today.year PixelMindMC
 *
 * PixelChatGuardian is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelChatGuardian is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.pixelmindmc.pixelchat;

import de.pixelmindmc.pixelchat.commands.PixelChatCommand;
import de.pixelmindmc.pixelchat.commands.RemoveStrikesCommand;
import de.pixelmindmc.pixelchat.commands.StrikeCommand;
import de.pixelmindmc.pixelchat.constants.ConfigConstants;
import de.pixelmindmc.pixelchat.constants.LangConstants;
import de.pixelmindmc.pixelchat.listener.AsyncPlayerChatListener;
import de.pixelmindmc.pixelchat.listener.PlayerJoinListener;
import de.pixelmindmc.pixelchat.integration.PlaceholderIntegration;
import de.pixelmindmc.pixelchat.utils.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The main class for the PixelChat Guardian plugin
 */
public final class PixelChat extends JavaPlugin {
    private final LoggingHelper loggingHelper = new LoggingHelper(this);
    private String updateChecker;

    // ConfigHelper instances
    private ConfigHelper configHelper;
    private ConfigHelper configHelperPlayerStrikes;
    private ConfigHelper configHelperLangCustom;
    private ConfigHelper configHelperLangGerman;
    private ConfigHelper configHelperLangEnglish;
    private ConfigHelper configHelperLangSpanish;
    private ConfigHelper configHelperLangFrench;
    private ConfigHelper configHelperLangDutch;
    private ConfigHelper configHelperLangSimplifiedChinese;
    private ConfigHelper configHelperLangTraditionalChinese;

    private PixelChatCommand pixelChatCommand;
    private APIHelper apiHelper;
    public @NotNull APIHelper getAPIHelper() {
        return apiHelper;
    }
    @Override
    public void onEnable() {
        loadConfigs();
        registerAPIHelper();
        registerListeners(getServer().getPluginManager());
        registerCommands();
        registerTabCompleter(new TabCompleter());

        initializeMetrics();
        try {
            checkForUpdates();
        } catch (Exception e) {
            loggingHelper.warning("Update check failed: " + e.getMessage());
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderIntegration(this).register();
        }
    }

    /**
     * Loads the plugin's configuration files and checks their versions
     */
    private void loadConfigs() {
        loggingHelper.debug("Loading configurations");

        configHelper = new ConfigHelper(this, "config.yml");
        configHelperPlayerStrikes = new ConfigHelper(this, "player_strikes.yml");
        configHelperLangCustom = new ConfigHelper(this, "locale/locale_custom.yml");
        configHelperLangGerman = new ConfigHelper(this, "locale/locale_de.yml");
        configHelperLangEnglish = new ConfigHelper(this, "locale/locale_en.yml");
        configHelperLangSpanish = new ConfigHelper(this, "locale/locale_es.yml");
        configHelperLangFrench = new ConfigHelper(this, "locale/locale_fr.yml");
        configHelperLangDutch = new ConfigHelper(this, "locale/locale_nl.yml");
        configHelperLangSimplifiedChinese = new ConfigHelper(this, "locale/locale_zh-cn.yml");
        configHelperLangTraditionalChinese = new ConfigHelper(this, "locale/locale_zh-tw.yml");

        ensureConfigEntries();
        ensureDiscordConfigEntries();

        // Check config versions
        String version = getDescription().getVersion();
        if (!version.equalsIgnoreCase(configHelper.getString(ConfigConstants.CONFIG_VERSION))) {
            loggingHelper.warning(getConfigHelperLanguage().getString(LangConstants.CONFIG_OUTDATED));
        }
        if (!version.equalsIgnoreCase(getConfigHelperLanguage().getString(LangConstants.LANGUAGE_CONFIG_VERSION))) {
            loggingHelper.warning(getConfigHelperLanguage().getString(LangConstants.LANGUAGE_CONFIG_OUTDATED));
        }

        // First-time message
        if (!configHelper.getFileExist()) {
            loggingHelper.warning(getConfigHelperLanguage().getString(LangConstants.FIRST_TIME_MESSAGE));
        }

        // Reset strikes on restart
        if (configHelper.getBoolean(ConfigConstants.CHATGUARD_CLEAR_STRIKES_ON_SERVER_RESTART)) {
            resetPlayerStrikesOnServerStart();
        }
    }

    /**
     * Ensures that additional general settings exist in the config.
     */
    private void ensureConfigEntries() {
        boolean changed = false;
        if (!configHelper.contains(ConfigConstants.STRIKE_DISPLAY_ENABLED)) {
            configHelper.set(ConfigConstants.STRIKE_DISPLAY_ENABLED, false);
            changed = true;
        }
        if (!configHelper.contains(ConfigConstants.STRIKE_DISPLAY_USE_ACTIONBAR)) {
            configHelper.set(ConfigConstants.STRIKE_DISPLAY_USE_ACTIONBAR, true);
            changed = true;
        }
        if (!configHelper.contains(ConfigConstants.STRIKE_DISPLAY_TITLE)) {
            configHelper.set(ConfigConstants.STRIKE_DISPLAY_TITLE, "PixelChat Strikes");
            changed = true;
        }
        if (changed) {
            loggingHelper.info("Added missing config defaults to config.yml");
        }
    }

    /**
     * Ensures that Discord integration settings exist in the config.
     */
    private void ensureDiscordConfigEntries() {
        boolean changed = false;
        if (!configHelper.contains(ConfigConstants.DISCORD_INTEGRATION_ENABLED)) {
            configHelper.set(ConfigConstants.DISCORD_INTEGRATION_ENABLED, false);
            changed = true;
        }
        if (!configHelper.contains(ConfigConstants.DISCORD_INTEGRATION_WEBHOOK_URL)) {
            configHelper.set(ConfigConstants.DISCORD_INTEGRATION_WEBHOOK_URL, "WEBHOOK_URL");
            changed = true;
        }
        if (changed) {
            loggingHelper.info("Added missing Discord integration defaults to config.yml");
        }
    }

    public ConfigHelper getConfigHelper() {
        return configHelper;
    }

    public ConfigHelper getConfigHelperPlayerStrikes() {
        return configHelperPlayerStrikes;
    }

    public ConfigHelper getConfigHelperLanguage() {
        String language = configHelper.getString(ConfigConstants.LANGUAGE);
        return switch (language.toLowerCase()) {
            case "custom" -> configHelperLangCustom;
            case "de" -> configHelperLangGerman;
            case "es" -> configHelperLangSpanish;
            case "fr" -> configHelperLangFrench;
            case "nl" -> configHelperLangDutch;
            case "zh-cn" -> configHelperLangSimplifiedChinese;
            case "zh-tw" -> configHelperLangTraditionalChinese;
            default -> configHelperLangEnglish;
        };
    }

    /**
     * Resets the strike count of every player to 0 on server start
     */
    private void resetPlayerStrikesOnServerStart() {
        Set<String> playerUUIDs = configHelperPlayerStrikes.getKeys("");
        for (String playerUUID : playerUUIDs) {
            if (configHelperPlayerStrikes.contains(playerUUID + ".strikes")) {
                configHelperPlayerStrikes.set(playerUUID + ".strikes", 0);
                loggingHelper.debug("Reset strikes for player UUID: " + playerUUID);
            }
        }
        loggingHelper.info(getConfigHelperLanguage().getString(LangConstants.CLEARED_STRIKES_ON_SERVER_RESTART));
    }

    private void registerAPIHelper() {
        loggingHelper.debug("Register API helper");
        if (!configHelper.getBoolean(ConfigConstants.MODULE_CHATGUARD)) return;
        String apiKey = configHelper.getString(ConfigConstants.API_KEY);
        if (configHelper.getFileExist() && (apiKey == null || apiKey.equals("API-KEY"))) {
            loggingHelper.warning(getConfigHelperLanguage().getString(LangConstants.NO_API_KEY_SET));
            return;
        }
        apiHelper = new APIHelper(this);
    }

    private void registerListeners(@NotNull PluginManager pluginManager) {
        loggingHelper.debug("Register listeners");
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new AsyncPlayerChatListener(this), this);
    }

    private void registerCommands() {
        loggingHelper.debug("Register commands");
        Objects.requireNonNull(getCommand("pixelchat")).setExecutor(pixelChatCommand = new PixelChatCommand(this));
        Objects.requireNonNull(getCommand("strike")).setExecutor(new StrikeCommand(this));
        Objects.requireNonNull(getCommand("remove-strikes")).setExecutor(new RemoveStrikesCommand(this));
    }

    private void registerTabCompleter(@NotNull TabCompleter tabCompleter) {
        loggingHelper.debug("Register tabcompleter");
        Objects.requireNonNull(getCommand("pixelchat")).setTabCompleter(tabCompleter);
        Objects.requireNonNull(getCommand("strike")).setTabCompleter(tabCompleter);
        Objects.requireNonNull(getCommand("remove-strikes")).setTabCompleter(tabCompleter);
    }

    private void initializeMetrics() {
        if (configHelper.getBoolean(ConfigConstants.METRICS_ENABLED)) {
            loggingHelper.info(getConfigHelperLanguage().getString(LangConstants.METRICS_ENABLED));
            new Metrics(this, 23371);
        }
    }

    private void checkForUpdates() throws URISyntaxException, IOException {
        if (configHelper.getBoolean(ConfigConstants.CHECK_FOR_UPDATES)) {
            loggingHelper.info(getConfigHelperLanguage().getString(LangConstants.CHECKING_FOR_UPDATES));
            updateChecker = new UpdateChecker(this,
                    new URI("https://api.github.com/repos/PixelMindMC/PixelChatGuardian/releases/latest").toURL())
                    .checkForUpdates();
            loggingHelper.info(updateChecker);
        }
    }

    public @NotNull String updateChecker() {
        return updateChecker;
    }

    public @NotNull LoggingHelper getLoggingHelper() {
        return loggingHelper;
    }

    public @NotNull PixelChatCommand getPixelChatCommand() {
        return pixelChatCommand;
    }
}
