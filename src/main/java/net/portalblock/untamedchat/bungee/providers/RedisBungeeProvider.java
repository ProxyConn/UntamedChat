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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by portalBlock on 12/18/2014.
 */
public class RedisBungeeProvider implements Provider, Listener {

    private RedisBungeeAPI api = RedisBungee.getApi();

    private HashMap<String, String> lastMessages = new HashMap<String, String>();
    private HashMap<UUID, Boolean> globalChat = new HashMap<UUID, Boolean>();

    public RedisBungeeProvider(){
        api.registerPubSubChannels(UntamedChat.MSG_CHANNEL);
        api.registerPubSubChannels(UntamedChat.GBLCHT_CHANNEL);
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
    public void sendMessage(String sender, String target, String msg) {
        JSONObject m = new JSONObject();
        m.put("sender", sender);
        m.put("target", target);
        m.put("msg", msg);
        api.sendChannelMessage(UntamedChat.MSG_CHANNEL, m.toString());
    }

    @Override
    public void sendGlobalChat(String msg) {
        JSONObject m = new JSONObject();
        m.put("msg", msg);
        api.sendChannelMessage(UntamedChat.GBLCHT_CHANNEL, m.toString());
    }

    @Override
    public String getReply(String name) {
        return lastMessages.get(name.toLowerCase());
    }

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent e){
        if(e.getChannel().equals(UntamedChat.MSG_CHANNEL)) {
            JSONObject msg = new JSONObject(e.getMessage());
            lastMessages.put(msg.getString("target").toLowerCase(), msg.getString("sender"));
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(msg.getString("target"));
            if (target != null) {
                target.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg.getString("msg"))));
            }
        }else if(e.getChannel().equals(UntamedChat.GBLCHT_CHANNEL)){
            JSONObject jsonObject = new JSONObject(e.getMessage());
            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', jsonObject.getString("msg"))));
        }else if(e.getChannel().equals(UntamedChat.TOG_CHANNEL)){
            JSONObject msg = new JSONObject(e.getMessage());
            if(msg.getString("type").equals("chat"))
                globalChat.put(UUID.fromString(msg.getString("uuid")), msg.getBoolean("mode"));
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
        if(!globalChat.containsKey(player)) setGlobalMode(player, UCConfig.isGcDefault());
        return globalChat.get(player);
    }
}
