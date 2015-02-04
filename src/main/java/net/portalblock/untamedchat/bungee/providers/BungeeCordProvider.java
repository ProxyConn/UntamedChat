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
import net.portalblock.untamedchat.bungee.data.Message;

import java.util.*;

/**
 * Created by portalBlock on 12/19/2014.
 */
public class BungeeCordProvider implements Provider {

    private ProxyServer proxy;

    private HashMap<String, String> lastMessages = new HashMap<String, String>();
    private HashMap<UUID, Boolean> globalChat = new HashMap<UUID, Boolean>();
    private HashMap<UUID, Boolean> spying = new HashMap<UUID, Boolean>();

    public BungeeCordProvider(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean isPlayerOnline(String playerName) {
        return (proxy.getPlayer(playerName) != null);
    }

    @Override
    public void sendMessage(Message message) {
        switch (message.getTarget().getKind()) {
            case GLOBAL:
                String msg = UCConfig.compileMessage(UCConfig.GLOBAL_FORMAT, message.getMessage(), message.getServer(), message.getSender(), null);
                for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
                }
                break;
            case PLAYER:
                ProxiedPlayer t = proxy.getPlayer(message.getTarget().getTarget());
                if(t != null){
                    lastMessages.put(t.getName().toLowerCase(), message.getSender());
                    String trgtMsg = UCConfig.compileMessage(UCConfig.TARGET_FORMAT, message.getMessage(), message.getServer(), message.getSender(), message.getTarget().getTarget());
                    t.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', trgtMsg)));

                    String ssMsg = UCConfig.compileMessage(UCConfig.SOCIAL_SPY_FORMAT, message.getMessage(), "", message.getSender(), message.getTarget().getTarget());
                    for (Map.Entry<UUID, Boolean> entry : spying.entrySet()) {
                        if (entry.getValue()) {
                            ProxiedPlayer player1 = ProxyServer.getInstance().getPlayer(entry.getKey());

                            if (player1 != null)
                                player1.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', ssMsg)));
                        }
                    }
                }
                break;
        }
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

    @Override
    public void setSpying(UUID player, boolean mode) {
        spying.put(player, mode);
    }

    @Override
    public boolean isSpying(UUID player) {
        if(!spying.containsKey(player)) setSpying(player, UCConfig.isSpDefault());
        return spying.get(player);
    }

    @Override
    public Collection<String> getAllPlayerNames(String[] args) {
        Set<String> matches = new HashSet<String>();
        if (args.length >= 1){
            String search = args[args.length-1].toLowerCase();
            for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
                if(player.getName().toLowerCase().startsWith(search.toLowerCase())){
                    matches.add(player.getName());
                }
            }
        }else{
            for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) matches.add(player.getName());
        }
        return matches;
    }
}
