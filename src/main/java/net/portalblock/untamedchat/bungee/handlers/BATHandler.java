/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee.handlers;

import fr.Alphart.BAT.BAT;
import fr.Alphart.BAT.Modules.InvalidModuleException;
import fr.Alphart.BAT.Modules.Mute.Mute;
import fr.Alphart.BAT.Modules.Mute.MuteEntry;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.portalblock.untamedchat.bungee.UntamedChat;

/**
 * Created by portalBlock on 1/9/2015.
 */
public class BATHandler implements Handler {

    private BAT bat;
    private Mute mute;

    public BATHandler(Plugin bat) {
        this.bat = (BAT) bat;
        try{
            this.mute = this.bat.getModules().getMuteModule();
        }catch(InvalidModuleException e){
            e.printStackTrace();
            UntamedChat.getInstance().getLogger().warning("Unable to get mute module from BAT, is it enabled?");
            mute = null;
            UntamedChat.getInstance().updateHandler(new NullHandler());
        }
    }

    @Override
    public boolean isMuted(ProxiedPlayer player) {
        String serverName = (player.getServer() != null ? player.getServer().getInfo().getName() : "Connecting");
        mute.loadMuteMessage(player.getName(), serverName);
        return (mute.isMute(player, serverName) == 1);
    }

    @Override
    public String getMuteReason(ProxiedPlayer player) {
        for(MuteEntry entry : mute.getMuteData(player.getName())){
            if(entry.isActive()){
                return entry.getReason();
            }
        }
        return "No mute reason found!";
    }
}
