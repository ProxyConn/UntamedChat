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
import net.portalblock.untamedchat.bungee.UCConfig;
import net.portalblock.untamedchat.bungee.data.Message;
import net.portalblock.untamedchat.bungee.data.Target;
import net.portalblock.untamedchat.bungee.providers.Provider;

/**
 * Created by portalBlock on 12/19/2014.
 */
public class ReplyCommand extends Command implements TabExecutor{

    private Provider provider;

    public ReplyCommand(Provider provider){
        super(UCConfig.getRootForReply(), "untamedchat.reply", UCConfig.getReplyAliases());
        this.provider = provider;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(strings.length < 1){
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Please include a message! /reply {message}"));
            return;
        }
        String targetName = provider.getReply(sender.getName());
        if(targetName == null || !provider.isPlayerOnline(targetName)){
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You have nobody to reply to!"));
            return;
        }
        StringBuilder msgBuilder = new StringBuilder();
        for(byte i = 0x00; i < strings.length; i+=0x01){
            msgBuilder.append(strings[i]);
            msgBuilder.append(" ");
        }
        String msg = msgBuilder.toString().trim();
        String server = "CONSOLE";
        if(sender instanceof ProxiedPlayer){
            server = (((ProxiedPlayer)sender).getServer() != null ? ((ProxiedPlayer)sender).getServer().getInfo().getName() : "Connecting");
        }
        String mg = UCConfig.compileMessage(UCConfig.TARGET_FORMAT, msg, server, sender.getName(), targetName);
        mg = ChatColor.translateAlternateColorCodes('&', mg);
        provider.sendMessage(new Message(sender.getName(), new Target(Target.Kind.PLAYER, targetName), msg, mg));
        mg = UCConfig.compileMessage(UCConfig.SENDER_FORMAT, msg, server, sender.getName(), targetName);
        mg = ChatColor.translateAlternateColorCodes('&', mg);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', mg)));

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return provider.getAllPlayerNames(strings);
    }
}
