/*
 * This file is part of PixelChat Guardian.
 * Copyright (C) 2024 PixelMindMC
 */

package de.pixelmindmc.pixelchat.utils;
import de.pixelmindmc.pixelchat.PixelChat;
import de.pixelmindmc.pixelchat.constants.LangConstants;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for managing configuration files
 */
public class ConfigHelper {
    private final PixelChat plugin;
    private final String path;
    private FileConfiguration fileConfiguration;
    private File file;
    private boolean fileExist = true;

    /**
     * Constructs a ConfigHelper object
     *
     * @param plugin The plugin instance
     * @param path   The path of the configuration file
     */
    public ConfigHelper(@NotNull PixelChat plugin, @NotNull String path) {
        this.plugin = plugin;
        this.path = path;
        saveDefaultConfig();
        loadConfig();
    }

    /**
     * Method to save the default config if it doesn't exist
     */
    public void saveDefaultConfig() {
        file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) {
            fileExist = false;
            plugin.saveResource(path, false);
        }
    }

    /**
     * Method to load or reload the config file
     */
    public void loadConfig() {
        file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) saveDefaultConfig();
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Method to save the config back to the file
     */
    public void saveConfig() {
        try {
            fileConfiguration.save(file);
            loadConfig();
        } catch (IOException e) {
            plugin.getLoggingHelper().error(
                    plugin.getConfigHelperLanguage().getString(LangConstants.FAILED_TO_SAVE_CONFIG)
                            + " " + e.getMessage()
            );
        }
    }

    /**
     * Retrieve whether the file existed before
     *
     * @return true if file existed, false if it was created now
     */
    public boolean getFileExist() {
        return fileExist;
    }

    /**
     * Set a specified path with the given value in the config
     */
    public void set(@NotNull String path, Object value) {
        fileConfiguration.set(path, value);
        saveConfig();
    }

    /**
     * Checks if the specified path exists in the file configuration
     */
    public boolean contains(@NotNull String path) {
        return fileConfiguration.contains(path);
    }

    /**
     * Retrieve a string from the config
     *
     * @param path The path of the value
     * @return The value, or a "Message not found" message
     */
    public String getString(@NotNull String path) {
        String message = fileConfiguration.getString(path);
        if (message == null || message.trim().isEmpty()) {
            try (InputStream resource = plugin.getResource(this.path)) {
                if (resource != null) {
                    try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);
                        message = defaultConfig.getString(path);
                    }
                }
            } catch (IOException e) {
                plugin.getLoggingHelper().error(
                        plugin.getConfigHelperLanguage().getString(LangConstants.FAILED_TO_LOAD_DEFAULT)
                                + " " + e.getMessage()
                );
            }
        }
        return (message == null || message.trim().isEmpty())
                ? "Message not found: " + path
                : message;
    }

    /**
     * Retrieve a boolean from the config
     *
     * @param path The path of the value
     * @return The value
     */
    public boolean getBoolean(@NotNull String path) {
        return fileConfiguration.getBoolean(path);
    }

    /**
     * Retrieve an int from the config
     *
     * @param path The path of the value
     * @return The value
     */
    public int getInt(@NotNull String path) {
        return fileConfiguration.getInt(path);
    }

    /**
     * Retrieve a list of strings from the config
     *
     * @param path The path of the list
     * @return A List<String>, never null
     */
    public List<String> getStringList(@NotNull String path) {
        return fileConfiguration.getStringList(path);
    }

    /**
     * Retrieve a string map from the config
     *
     * @param path The path of the value
     * @return The string map
     */
    public Map<String, String> getStringMap(@NotNull String path) {
        Map<String, String> resultMap = new HashMap<>();
        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);
        if (section == null) {
            try (InputStream resource = plugin.getResource(this.path)) {
                if (resource != null) {
                    try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);
                        section = defaultConfig.getConfigurationSection(path);
                    }
                }
            } catch (IOException ignored) {}
        }
        if (section != null) {
            for (String key : section.getKeys(false)) {
                resultMap.put(key, section.getString(key));
            }
        }
        return resultMap;
    }

    /**
     * Retrieve all keys in the config at a given path or at the root
     *
     * @param path The path of the section
     * @return A set of all keys found in that section or root
     */
    public Set<String> getKeys(@NotNull String path) {
        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);
        return (section != null) ? section.getKeys(false) : new HashSet<>();
    }
}