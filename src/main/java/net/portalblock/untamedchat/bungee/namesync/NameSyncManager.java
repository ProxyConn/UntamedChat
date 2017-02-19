/*
 * Copyright (c) 2017 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee.namesync;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by portalBlock on 2/19/2017.
 */
public class NameSyncManager {

    private HashMap<UUID, NameSet> nameMap = new HashMap<UUID, NameSet>();

    private final NameSet DEFAULT = new NameSet();

    public void updatePrefix(UUID id, String prefix) {
        if(nameMap.get(id) == null) nameMap.put(id, new NameSet());
        nameMap.get(id).setPrefix(prefix);
    }

    public void updateSuffix(UUID id, String suffix) {
        if(nameMap.get(id) == null) nameMap.put(id, new NameSet());
        nameMap.get(id).setSuffix(suffix);
    }

    public String compileName(UUID id, String name) {
        return nameMap.getOrDefault(id, DEFAULT).compile(name);
    }

    private class NameSet {
        private String prefix = "", suffix = "";

        public void setPrefix(String prefix) {
            this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        }

        public void setSuffix(String suffix) {
            this.suffix = ChatColor.translateAlternateColorCodes('&', suffix);
        }

        public String compile(String name) {
            return prefix + name + suffix;
        }

    }

}
