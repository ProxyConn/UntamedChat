package net.portalblock.untamedchat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.portalblock.untamedchat.bungee.UCConfig;
import net.portalblock.untamedchat.bungee.providers.Provider;

/**
 * Created by tux on 2/3/15.
 */
public class SocialSpyCommand extends Command {

    private static final String TO_TRUE = "&aYou have entered social spy mode!";
    private static final String TO_FALSE = "&cYou have exited social spy mode!";

    private Provider provider;

    public SocialSpyCommand(Provider provider) {
        super(UCConfig.getRootForGlobal(), "untamedchat.socialspy", UCConfig.getSocialSpyAliases());
        this.provider = provider;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Only players may toggle social spy mode."));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        boolean currMode = provider.isSpying(player.getUniqueId());
        provider.setSpying(player.getUniqueId(), !currMode);
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', !currMode ? TO_TRUE : TO_FALSE)));
    }
}
