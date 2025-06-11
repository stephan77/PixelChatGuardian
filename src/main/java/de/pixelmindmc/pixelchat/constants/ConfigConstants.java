/*
 * This file is part of PixelChat Guardian.
 * Copyright (C) 2024 PixelMindMC
 */

package de.pixelmindmc.pixelchat.constants;

/**
 * Constant class for holding constant values that are used for configuration
 */
public class ConfigConstants {
    public static final String CONFIG_VERSION = "version";
    // Global settings
    public static final String LANGUAGE = "language";
    public static final String METRICS_ENABLED = "enable-metrics";
    public static final String CHECK_FOR_UPDATES = "check-for-updates";
    public static final String LOG_LEVEL = "log-level";
    public static final String PLUGIN_SUPPORT_CARBONCHAT = "plugin-support.carbonchat";
    // Discord integration settings
    public static final String DISCORD_INTEGRATION_ENABLED = "discord-integration.enabled";
    public static final String DISCORD_INTEGRATION_WEBHOOK_URL = "discord-integration.webhook-url";
    // Strike display settings
    public static final String STRIKE_DISPLAY_ENABLED = "strike-display.enabled";
    public static final String STRIKE_DISPLAY_USE_ACTIONBAR = "strike-display.use-actionbar";
    public static final String STRIKE_DISPLAY_TITLE = "strike-display.title";
    // Advertising filter settings
    public static final String CHATGUARD_BLOCK_EXTERNAL_SERVER_ADS = "chatguard-rules.blockExternalServerAds";
    public static final String CHATGUARD_ALLOWED_SERVER_DOMAINS = "chatguard-rules.allowedServerDomains";
    // Module settings
    public static final String MODULE_CHATGUARD = "modules.chatguard";
    public static final String MODULE_CHAT_CODES = "modules.chat-codes";
    public static final String MODULE_EMOJIS = "modules.emojis";
    // AI and API settings
    public static final String API_ENDPOINT = "api-endpoint";
    public static final String AI_MODEL = "ai-model";
    public static final String API_KEY = "api-key";
    public static final String SYSTEM_PROMPT = "sys-prompt";
    // ChatGuard settings
    public static final String CHATGUARD_ENABLE_CUSTOM_CHATGUARD_PREFIX = "enable-custom-chatguard-prefix";
    public static final String CHATGUARD_CUSTOM_CHATGUARD_PREFIX = "custom-chatguard-prefix";
    public static final String CHATGUARD_MESSAGE_HANDLING = "message-handling";
    public static final String CHATGUARD_NOTIFY_USER = "notify-user";
    public static final String CHATGUARD_RULES_BLOCK_OFFENSIVE_LANGUAGE = "chatguard-rules.blockOffensiveLanguage";
    public static final String CHATGUARD_RULES_BLOCK_USERNAMES = "chatguard-rules.blockUsernames";
    public static final String CHATGUARD_RULES_BLOCK_PASSWORDS = "chatguard-rules.blockPasswords";
    public static final String CHATGUARD_RULES_BLOCK_HOME_ADDRESSES = "chatguard-rules.blockHomeAddresses";
    public static final String CHATGUARD_RULES_BLOCK_EMAIL_ADDRESSES = "chatguard-rules.blockEmailAddresses";
    public static final String CHATGUARD_RULES_BLOCK_WEBSITES = "chatguard-rules.blockWebsites";
    public static final String CHATGUARD_USE_BUILT_IN_STRIKE_SYSTEM = "use-built-in-strike-system";
    public static final String CHATGUARD_CLEAR_STRIKES_ON_SERVER_RESTART = "clear-strikes-on-server-restart";
    public static final String CHATGUARD_CUSTOM_STRIKE_COMMAND = "custom-strike-command";
    public static final String CHATGUARD_STRIKES_BEFORE_KICK = "strikes-before-kick";
    public static final String CHATGUARD_KICK_COMMAND = "kick-command";
    public static final String CHATGUARD_STRIKES_BEFORE_TEMP_BAN = "strikes-before-temp-ban";
    public static final String CHATGUARD_TEMP_BAN_COMMAND = "temp-ban-command";
    public static final String CHATGUARD_STRIKES_BEFORE_BAN = "strikes-before-ban";
    public static final String CHATGUARD_BAN_COMMAND = "ban-command";
    // Emoji settings
    public static final String EMOJI_LIST = "emoji-list";
    // Color/Format settings
    public static final String CHAT_CODES_LIST = "chat-codes-list";

    private ConfigConstants() {
    }
}