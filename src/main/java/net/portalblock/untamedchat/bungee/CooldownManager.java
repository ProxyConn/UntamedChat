/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

/**
 * Created by portalBlock on 12/23/2014.
 */
public class CooldownManager {

    private static HashMap<String, Long> chatCooldown = new HashMap<String, Long>();
    private static long delay;

    static {
        delay = UCConfig.getChatCooldown();
    }

    private static boolean coolCheck(ProxiedPlayer player) {
        if (!player.hasPermission("untamedchat.spam.bypass")) {
            if (chatCooldown.get(player.getName()) != null) {
                return chatCooldown.get(player.getName()) < (System.currentTimeMillis() - delay * 1000);
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean onChat(ProxiedPlayer player){
        if(!UCConfig.isChatCoolDowns()) return false;
        if(coolCheck(player)) {
            chatCooldown.put(player.getName(),
                    System.currentTimeMillis());
            return false;
        }else{
            return true;
        }
    }

}
