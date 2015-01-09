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
import net.portalblock.untamedchat.bungee.UCConfig;

/**
 * Created by portalBlock on 12/27/2014.
 */
public class UCReloadCommand extends Command {

    public UCReloadCommand() {
        super("ucreload", "untamedchat.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(sender instanceof ProxiedPlayer){
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Only console may reload!"));
            return;
        }
        UCConfig.load();
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "UntamedChat has been reloaded!"));
    }
}
