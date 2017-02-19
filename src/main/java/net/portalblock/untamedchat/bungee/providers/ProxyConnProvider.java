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
import net.portalblock.pc.publicapi.NetworkPlayer;
import net.portalblock.pc.publicapi.PluginMessageHandler;
import net.portalblock.untamedchat.bungee.UCConfig;
import net.portalblock.untamedchat.bungee.UntamedChat;
import net.portalblock.untamedchat.bungee.data.Message;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by portalBlock on 12/18/2014.
 */
public class ProxyConnProvider implements Provider, PluginMessageHandler {

    private API api = APIAccess.getApi();

    private HashMap<String, String> lastMessages = new HashMap<String, String>();
    private HashMap<UUID, Boolean> globalChat = new HashMap<UUID, Boolean>();
    private HashMap<UUID, Boolean> spying = new HashMap<UUID, Boolean>();

    public ProxyConnProvider(){
        api.registerMessageChannel(UntamedChat.GBL_CHANNEL, this);
        api.registerMessageChannel(UntamedChat.TOG_CHANNEL, this);
    }

    @Override
    public void sendMessage(Message message) {
        api.sendPluginMessage("bcast_" + UntamedChat.GBL_CHANNEL, message.serialize().toString());
    }

    @Override
    public void setGlobalMode(UUID player, boolean mode) {
        JSONObject msg = new JSONObject();
        msg.put("type", "chat");
        msg.put("uuid", player.toString());
        msg.put("mode", mode);
        api.sendPluginMessage("bcast_" + UntamedChat.TOG_CHANNEL, msg.toString());
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
        api.sendPluginMessage("bcast_" + UntamedChat.TOG_CHANNEL, msg.toString());
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
    public boolean isPlayerOnline(String playerName) {
        return (api.getPlayerByName(playerName) != null);
    }

    /*@Override
    public void sendMessage(String sender, String target, String msg) {
        JSONObject m = new JSONObject();
        m.put("sender", sender);
        m.put("target", target);
        m.put("msg", msg);
        api.sendPluginMessage("bcast_" + UntamedChat.GBL_CHANNEL, m); //Use special bcast_<name> channel so there is no 2nd plugin needed.
    }

    @Override
    public void sendGlobalChat(String msg) {
        JSONObject m = new JSONObject();
        m.put("msg", msg);
        api.sendPluginMessage("bcast_" + UntamedChat.GBL_CHANNEL, m.toString());
    }*/

    @Override
    public String getReply(String name) {
        return lastMessages.get(name.toLowerCase());
    }

    @Override
    public void onMessageGet(String chan, String msg) {
        /*if(s.contains(UntamedChat.MSG_CHANNEL)) {
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
        }*/
        if(chan.equals(UntamedChat.GBL_CHANNEL)) {
            Message message = Message.fromJSONObject(new JSONObject(msg));
            switch (message.getTarget().getKind()) {
                case GLOBAL:
                    String mg = UCConfig.compileMessage(UCConfig.GLOBAL_FORMAT, message.getMessage(), message.getServer(), message.getSender(), null);
                    for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', mg)));
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
        }else if(chan.equals(UntamedChat.TOG_CHANNEL)){
            JSONObject jsg = new JSONObject(msg);
            if(jsg.getString("type").equals("chat"))
                globalChat.put(UUID.fromString(jsg.getString("uuid")), jsg.getBoolean("mode"));
            else if(jsg.getString("type").equals("spy"))
                spying.put(UUID.fromString(jsg.getString("uuid")), jsg.getBoolean("mode"));
        }
    }

    /*@Override
    public void setGlobalMode(UUID player, boolean mode) {
        JSONObject msg = new JSONObject();
        msg.put("type", "chat");
        msg.put("uuid", player.toString());
        msg.put("mode", mode);
        api.sendPluginMessage("bcast_" + UntamedChat.TOG_CHANNEL, msg);
    }

    @Override
    public boolean isGlobalMode(UUID player) {
        if(!globalChat.containsKey(player)) setGlobalMode(player, UCConfig.isGcDefault());
        return globalChat.get(player);
    }*/

    @Override
    public Collection<String> getAllPlayerNames(String[] args) {
        Set<String> matches = new HashSet<String>();
        if (args.length >= 1){
            String search = args[args.length-1].toLowerCase();
            for(NetworkPlayer user : api.getAllPlayers()){
                if(user.getName().toLowerCase().startsWith(search.toLowerCase())){
                    matches.add(user.getName());
                }
            }
        }else{
            for(NetworkPlayer user : api.getAllPlayers()) matches.add(user.getName());
        }
        return matches;
    }
}
