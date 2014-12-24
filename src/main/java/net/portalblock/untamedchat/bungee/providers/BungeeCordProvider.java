/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee.providers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.portalblock.untamedchat.bungee.UCConfig;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by portalBlock on 12/19/2014.
 */
public class BungeeCordProvider implements Provider {

    private ProxyServer proxy;

    private HashMap<String, String> lastMessages = new HashMap<String, String>();
    private HashMap<UUID, Boolean> globalChat = new HashMap<UUID, Boolean>();

    public BungeeCordProvider(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean isPlayerOnline(String playerName) {
        return (proxy.getPlayer(playerName) != null);
    }

    @Override
    public void sendMessage(String sender, String target, String msg) {
        ProxiedPlayer t = proxy.getPlayer(target);
        if(t != null){
            lastMessages.put(target.toLowerCase(), sender);
            t.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
        }
    }

    @Override
    public void sendGlobalChat(String msg) {
        for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
    }

    @Override
    public String getReply(String name) {
        return lastMessages.get(name.toLowerCase());
    }

    @Override
    public void setGlobalMode(UUID player, boolean mode) {
        globalChat.put(player, mode);
    }

    @Override
    public boolean isGlobalMode(UUID player) {
        if(!globalChat.containsKey(player)) setGlobalMode(player, UCConfig.isGcDefault());
        return globalChat.get(player);
    }
}
