/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee.providers;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.portalblock.untamedchat.bungee.UCConfig;
import net.portalblock.untamedchat.bungee.UntamedChat;
import net.portalblock.untamedchat.bungee.data.Message;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by portalBlock on 12/18/2014.
 */
public class RedisBungeeProvider implements Provider, Listener {

    private RedisBungeeAPI api = RedisBungee.getApi();

    private HashMap<String, String> lastMessages = new HashMap<String, String>();
    private HashMap<UUID, Boolean> globalChat = new HashMap<UUID, Boolean>();
    private HashMap<UUID, Boolean> spying = new HashMap<UUID, Boolean>();

    public RedisBungeeProvider(){
        api.registerPubSubChannels(UntamedChat.GBL_CHANNEL);
        api.registerPubSubChannels(UntamedChat.TOG_CHANNEL);
        ProxyServer.getInstance().getPluginManager().registerListener(UntamedChat.getInstance(), this);
    }

    @Override
    public boolean isPlayerOnline(String playerName) {
        UUID uuid = api.getUuidFromName(playerName, false);
        if(uuid == null) return false;
        return api.isPlayerOnline(uuid);
    }

    @Override
    public void sendMessage(Message message) {
        api.sendChannelMessage(UntamedChat.GBL_CHANNEL, message.serialize().toString());
    }

    @Override
    public String getReply(String name) {
        return lastMessages.get(name.toLowerCase());
    }

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent e){
        if(e.getChannel().equals(UntamedChat.GBL_CHANNEL)) {
            Message message = Message.fromJSONObject(new JSONObject(e.getMessage()));
            switch (message.getTarget().getKind()) {
                case GLOBAL:
                    String msg = UCConfig.compileMessage(UCConfig.GLOBAL_FORMAT, message.getMessage(), message.getServer(), message.getSender(), null);
                    for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
                    break;
                case PLAYER:
                    ProxiedPlayer t = ProxyServer.getInstance().getPlayer(message.getTarget().getTarget());
                    if(t != null){
                        String trgtMsg = UCConfig.compileMessage(UCConfig.TARGET_FORMAT, message.getMessage(), message.getServer(), message.getSender(), message.getTarget().getTarget());
                        lastMessages.put(t.getName().toLowerCase(), message.getSender());
                        t.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', trgtMsg)));

                        String ssMsg = UCConfig.compileMessage(UCConfig.SOCIAL_SPY_FORMAT, message.getMessage(), "", message.getSender(), message.getTarget().getTarget());
                        for (Map.Entry<UUID, Boolean> entry : spying.entrySet()) {
                            if (entry.getValue()) {
                                ProxiedPlayer player1 = ProxyServer.getInstance().getPlayer(entry.getKey());
                                if (player1 != null) {
                                    if(player1.getName().equalsIgnoreCase(message.getSender()) ||
                                            player1.getName().equalsIgnoreCase(message.getTarget().getTarget()))
                                        continue;
                                    player1.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', ssMsg)));
                                }
                            }
                        }
                    }
                    break;
            }
        }else if(e.getChannel().equals(UntamedChat.TOG_CHANNEL)){
            JSONObject msg = new JSONObject(e.getMessage());
            if(msg.getString("type").equals("chat"))
                globalChat.put(UUID.fromString(msg.getString("uuid")), msg.getBoolean("mode"));
            else if(msg.getString("type").equals("spy"))
                spying.put(UUID.fromString(msg.getString("uuid")), msg.getBoolean("mode"));
        }
    }

    @Override
    public void setGlobalMode(UUID player, boolean mode) {
        JSONObject msg = new JSONObject();
        msg.put("type", "chat");
        msg.put("uuid", player.toString());
        msg.put("mode", mode);
        api.sendChannelMessage(UntamedChat.TOG_CHANNEL, msg.toString());
    }

    @Override
    public boolean isGlobalMode(UUID player) {
        if(!globalChat.containsKey(player)) {
            setGlobalMode(player, UCConfig.isGcDefault());
            // Although the change will eventually apply, it will not as the message is delivered asynchronously.
            // Return the intended value here instead.
            return UCConfig.isGcDefault();
        }
        return globalChat.get(player);
    }

    @Override
    public void setSpying(UUID player, boolean mode) {
        JSONObject msg = new JSONObject();
        msg.put("type", "spy");
        msg.put("uuid", player.toString());
        msg.put("mode", mode);
        api.sendChannelMessage(UntamedChat.TOG_CHANNEL, msg.toString());
    }

    @Override
    public boolean isSpying(UUID player) {
        if(!spying.containsKey(player)) {
            setSpying(player, UCConfig.isSpDefault());
            // Although the change will eventually apply, it will not as the message is delivered asynchronously.
            // Return the intended value here instead.
            return UCConfig.isSpDefault();
        }
        return spying.get(player);
    }

    @Override
    public Collection<String> getAllPlayerNames(String[] args) {
        Set<String> matches = new HashSet<String>();
        if (args.length >= 1){
            String search = args[args.length-1].toLowerCase();
            for(String s : api.getHumanPlayersOnline()){
                if(s.toLowerCase().startsWith(search.toLowerCase())){
                    matches.add(s);
                }
            }
        }else{
            for(String s : api.getHumanPlayersOnline()) matches.add(s);
        }
        return matches;
    }
}
