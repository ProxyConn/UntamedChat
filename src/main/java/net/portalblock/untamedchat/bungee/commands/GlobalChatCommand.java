/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.portalblock.untamedchat.bungee.CooldownManager;
import net.portalblock.untamedchat.bungee.UCConfig;
import net.portalblock.untamedchat.bungee.data.Message;
import net.portalblock.untamedchat.bungee.data.Target;
import net.portalblock.untamedchat.bungee.namesync.NameSyncManager;
import net.portalblock.untamedchat.bungee.providers.Provider;

/**
 * Created by portalBlock on 12/19/2014.
 */
public class GlobalChatCommand extends Command implements TabExecutor {

    private static final String TO_TRUE = "&aYou have entered global chat mode!";
    private static final String TO_FALSE = "&cYou have exited global chat mode!";

    private Provider provider;
    private NameSyncManager nameSyncManager;

    public GlobalChatCommand(Provider provider, NameSyncManager nameSyncManager) {
        super(UCConfig.getRootForGlobal(), "untamedchat.globalchat", UCConfig.getGlobalAliases());
        this.provider = provider;
        this.nameSyncManager = nameSyncManager;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(strings.length == 0){
            if(!(sender instanceof ProxiedPlayer)){
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Only players may toggle global chat mode."));
                return;
            }
            ProxiedPlayer player = (ProxiedPlayer) sender;
            boolean currMode = provider.isGlobalMode(player.getUniqueId());
            provider.setGlobalMode(player.getUniqueId(), !currMode);
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', (!currMode ? TO_TRUE : TO_FALSE))));
            return;
        }
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(CooldownManager.onChat(p)){
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', UCConfig.SPAM_MESSAGE)));
                return;
            }
        }
        StringBuilder builder = new StringBuilder();
        for(String s : strings){
            builder.append(s);
            builder.append(" ");
        }
        String preprocessed = builder.toString().trim();
        if(!sender.hasPermission("untamedchat.color")){
            preprocessed = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', preprocessed));
        }
        String server = "CONSOLE";
        String senderName = sender.getName();
        if(sender instanceof ProxiedPlayer){
            server = (((ProxiedPlayer)sender).getServer() != null ? ((ProxiedPlayer)sender).getServer().getInfo().getName() : null);
            senderName = nameSyncManager.compileName(((ProxiedPlayer)sender).getUniqueId(), sender.getName());
        }
        provider.sendMessage(new Message(senderName, new Target(Target.Kind.GLOBAL, ""), preprocessed, server));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return provider.getAllPlayerNames(strings);
    }
}
