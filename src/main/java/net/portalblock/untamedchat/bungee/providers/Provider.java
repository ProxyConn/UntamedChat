/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee.providers;

import net.portalblock.untamedchat.bungee.data.Message;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by portalBlock on 12/18/2014.
 */
public interface Provider {

    public boolean isPlayerOnline(String playerName);

    public void sendMessage(Message message);

    public void setGlobalMode(UUID player, boolean mode);

    public boolean isGlobalMode(UUID player);

    public String getReply(String name);

    public Collection<String> getAllPlayerNames(String[] args);

}
