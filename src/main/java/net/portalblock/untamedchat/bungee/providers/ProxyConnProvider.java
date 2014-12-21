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
import net.portalblock.pc.publicapi.API;
import net.portalblock.pc.publicapi.APIAccess;
import net.portalblock.pc.publicapi.PluginMessageHandler;
import net.portalblock.untamedchat.bungee.UntamedChat;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by portalBlock on 12/18/2014.
 */
public class ProxyConnProvider implements Provider, PluginMessageHandler {

    private API api = APIAccess.getApi();

    private HashMap<String, String> lastMessages = new HashMap<String, String>();
    private HashMap<UUID, Boolean> globalChat = new HashMap<UUID, Boolean>();

    public ProxyConnProvider(){
        api.registerMessageChannel(UntamedChat.MSG_CHANNEL, this);
        api.registerMessageChannel(UntamedChat.GBLCHT_CHANNEL, this);
        api.registerMessageChannel(UntamedChat.TOG_CHANNEL, this);
    }

    @Override
    public boolean isPlayerOnline(String playerName) {
        return (api.getPlayerByName(playerName) != null);
    }

    @Override
    public void sendMessage(String sender, String target, String msg) {
        JSONObject m = new JSONObject();
        m.put("sender", sender);
        m.put("target", target);
        m.put("msg", msg);
        api.sendPluginMessage("bcast_" + UntamedChat.MSG_CHANNEL, m); //Use special bcast_<name> channel so there is no 2nd plugin needed.
    }

    @Override
    public void sendGlobalChat(String msg) {
        JSONObject m = new JSONObject();
        m.put("msg", msg);
        api.sendPluginMessage("bcast_" + UntamedChat.GBLCHT_CHANNEL, m);
    }

    @Override
    public String getReply(String name) {
        return lastMessages.get(name.toLowerCase());
    }

    @Override
    public void onMessageGet(String s, JSONObject jsonObject) {
        if(s.contains(UntamedChat.MSG_CHANNEL)) {
            lastMessages.put(jsonObject.getString("target").toLowerCase(), jsonObject.getString("sender"));
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(jsonObject.getString("target"));
            if (target != null) {
                target.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', jsonObject.getString("msg"))));
            }
        }else if(s.contains(UntamedChat.GBLCHT_CHANNEL)){
            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', jsonObject.getString("msg"))));
        }else if(s.contains(UntamedChat.TOG_CHANNEL)){
            if(jsonObject.getString("type").equals("chat"))
                globalChat.put(UUID.fromString(jsonObject.getString("uuid")), jsonObject.getBoolean("mode"));
        }
    }

    @Override
    public void setGlobalMode(UUID player, boolean mode) {
        JSONObject msg = new JSONObject();
        msg.put("type", "chat");
        msg.put("uuid", player.toString());
        msg.put("mode", mode);
        api.sendPluginMessage("bcast_" + UntamedChat.TOG_CHANNEL, msg);
    }

    @Override
    public boolean isGlobalMode(UUID player) {
        if(!globalChat.containsKey(player)) setGlobalMode(player, false);
        return globalChat.get(player);
    }
}
