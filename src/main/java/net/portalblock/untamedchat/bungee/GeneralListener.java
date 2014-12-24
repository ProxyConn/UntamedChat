/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.portalblock.untamedchat.bungee.providers.Provider;

/**
 * Created by portalBlock on 12/20/2014.
 */
public class GeneralListener implements Listener {

    private Provider provider;

    public GeneralListener(Provider provider){
        this.provider = provider;
    }

    @EventHandler
    public void onLogin(PostLoginEvent e){
        //Default new players to be not in global.
        provider.isGlobalMode(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(ChatEvent e){
        if(!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        if(e.isCommand() || !provider.isGlobalMode(player.getUniqueId())) return;
        if(CooldownManager.onChat(player)){
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', UCConfig.SPAM_MESSAGE)));
            e.setCancelled(true);
            return;
        }
        String server = (player.getServer() != null ? player.getServer().getInfo().getName() : null);
        String msg = e.getMessage();
        msg = UCConfig.compileMessage(UCConfig.GLOBAL_FORMAT, msg, server, player.getName(), null);
        provider.sendGlobalChat(msg);
        e.setCancelled(true);
    }

}
