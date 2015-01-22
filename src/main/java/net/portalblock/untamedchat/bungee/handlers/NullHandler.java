/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee.handlers;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by portalBlock on 1/9/2015.
 */
public class NullHandler implements Handler {

    @Override
    public boolean isMuted(ProxiedPlayer player) {
        return false;
    }

    @Override
    public String getMuteReason(ProxiedPlayer player) {
        return "";
    }
}
