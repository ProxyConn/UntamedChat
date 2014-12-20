/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee;

/**
 * Created by portalBlock on 12/19/2014.
 */
public class UCConfig {

    public static final String TARGET_FORMAT = "&6{sender} &7-> &6Me&7: {msg}";
    public static final String SENDER_FORMAT = "&6Me &7-> &6{target}&7: {msg}";
    public static final String GLOBAL_FORMAT = "&7[&6{server}&7] [&6{sender}&7]: &r{msg}";

    public static String compileMessage(String format, String msg, String server, String sender, String target){
        return format
                .replaceAll("\\{server\\}", (server != null ? server : ""))
                .replaceAll("\\{sender\\}", (sender != null ? sender : ""))
                .replaceAll("\\{target\\}", (target != null ? target : ""))
                .replaceAll("\\{msg\\}", (msg != null ? msg : ""));
    }

}
