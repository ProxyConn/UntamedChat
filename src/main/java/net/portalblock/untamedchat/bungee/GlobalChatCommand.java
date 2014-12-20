/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.portalblock.untamedchat.bungee.providers.Provider;

/**
 * Created by portalBlock on 12/19/2014.
 */
public class GlobalChatCommand extends Command {

    private Provider provider;

    public GlobalChatCommand(Provider provider) {
        super("globalchat", "untamedchat.globalchat", "g", "global");
        this.provider = provider;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        StringBuilder builder = new StringBuilder();
        for(String s : strings){
            builder.append(s);
            builder.append(" ");
        }
        String msg = builder.toString().trim();
        String server = "CONSOLE";
        if(sender instanceof ProxiedPlayer){
            server = (((ProxiedPlayer)sender).getServer() != null ? ((ProxiedPlayer)sender).getServer().getInfo().getName() : null);
        }
        msg = UCConfig.compileMessage(UCConfig.GLOBAL_FORMAT, msg, server, sender.getName(), null);
        provider.sendGlobalChat(msg);
    }
}
