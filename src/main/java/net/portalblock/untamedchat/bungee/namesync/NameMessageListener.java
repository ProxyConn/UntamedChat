/*
 * Copyright (c) 2017 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee.namesync;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by portalBlock on 2/19/2017.
 */
public class NameMessageListener implements Listener {

    private NameSyncManager manager;

    public NameMessageListener(NameSyncManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onMessage(PluginMessageEvent e) {
        if(!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
        if(e.getTag().equals("uc-namesync")) {
            DataInputStream data = new DataInputStream(new ByteArrayInputStream(e.getData()));
            try {
                String channel = data.readUTF();
                String value = data.readUTF();
                if(channel.equals("prefix")){
                    manager.updatePrefix(sender.getUniqueId(), value);
                } else if(channel.equals("suffix")) {
                    manager.updateSuffix(sender.getUniqueId(), value);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
