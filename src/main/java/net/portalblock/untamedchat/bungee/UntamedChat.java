/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.portalblock.untamedchat.bungee.commands.GlobalChatCommand;
import net.portalblock.untamedchat.bungee.commands.MsgCommand;
import net.portalblock.untamedchat.bungee.commands.ReplyCommand;
import net.portalblock.untamedchat.bungee.commands.SocialSpyCommand;
import net.portalblock.untamedchat.bungee.handlers.BATHandler;
import net.portalblock.untamedchat.bungee.handlers.Handler;
import net.portalblock.untamedchat.bungee.handlers.NullHandler;
import net.portalblock.untamedchat.bungee.providers.BungeeCordProvider;
import net.portalblock.untamedchat.bungee.providers.Provider;
import net.portalblock.untamedchat.bungee.providers.ProxyConnProvider;
import net.portalblock.untamedchat.bungee.providers.RedisBungeeProvider;

/**
 * Created by portalBlock on 12/18/2014.
 */
public class UntamedChat extends Plugin {

    public static final String GBL_CHANNEL = "uc_messages";
    public static final String TOG_CHANNEL = "uc_toggle";

    private static UntamedChat instance;

    public static UntamedChat getInstance() {
        return instance;
    }

    private Provider provider;
    private Handler handler;

    @Override
    public void onEnable() {
        instance = this;
        setProvider();
        setHandler();
        getProxy().getPluginManager().registerCommand(this, new MsgCommand(provider));
        getProxy().getPluginManager().registerCommand(this, new ReplyCommand(provider));
        getProxy().getPluginManager().registerCommand(this, new GlobalChatCommand(provider));
        getProxy().getPluginManager().registerCommand(this, new SocialSpyCommand(provider));

        getProxy().getPluginManager().registerListener(this, new GeneralListener(provider, handler));
    }

    public void updateHandler(Handler handler){
        this.handler = handler;
    }

    private void setProvider(){
        /*if(getProxy().getPluginManager().getPlugin("ProxyConn") != null){
            provider = new ProxyConnProvider();
            return;
        }*/
        if(getProxy().getPluginManager().getPlugin("RedisBungee") != null){
            provider = new RedisBungeeProvider();
            return;
        }
        provider = new BungeeCordProvider(getProxy());
    }

    private void setHandler(){
        if(getProxy().getPluginManager().getPlugin("BungeeAdminTools") != null){
            handler = new BATHandler(getProxy().getPluginManager().getPlugin("BungeeAdminTools"));
        }else{
            handler = new NullHandler();
        }
    }
}
