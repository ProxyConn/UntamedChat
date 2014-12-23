/*
 * Copyright (c) 2014 portalBlock. This work is provided AS-IS without any warranty.
 * You must provide a link back to the original project and clearly point out any changes made to this project.
 * This license must be included in all project files.
 * Any changes merged with this project are property of the copyright holder but may include the author's name.
 */

package net.portalblock.untamedchat.bungee;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by portalBlock on 12/19/2014.
 */
public class UCConfig {

    public static String TARGET_FORMAT;
    public static String SENDER_FORMAT;
    public static String GLOBAL_FORMAT;

    private static String[] msgAliases;
    private static String[] replyAliases;
    private static String[] globalAliases;

    static {
        final String NEW_LINE = System.getProperty("line.separator");
        File cfgDir = new File("plugins/UntamedChat");
        if(!cfgDir.exists() && cfgDir.isDirectory()){
            cfgDir.mkdir();
        }
        File configFile = new File(cfgDir + "/config.json");
        if(!configFile.exists()){
            try{
                InputStream is = UCConfig.class.getResourceAsStream("/config.json");
                String line;
                if(is == null) throw new NullPointerException("is");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                FileWriter configWriter = new FileWriter(configFile);
                while((line = reader.readLine()) != null){
                    configWriter.write(line + NEW_LINE);
                }
                configWriter.flush();
                configWriter.close();
                reader.close();
                is.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        try{
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            StringBuilder configBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                configBuilder.append(line);
            }
            JSONObject config = new JSONObject(configBuilder.toString());
            TARGET_FORMAT = config.optString("target_format", "&6{sender} &7-> &6Me&7: {msg}");
            SENDER_FORMAT = config.optString("sender_format", "&6Me &7-> &6{target}&7: {msg}");
            GLOBAL_FORMAT = config.optString("global_format", "&7[&6{server}&7] [&6{sender}&7]: &r{msg}");
            JSONObject commands = config.optJSONObject("commands");
            if(commands == null){
                msgAliases = new String[]{"msg", "m"};
                replyAliases = new String[]{"reply", "r"};
                globalAliases = new String[]{"globalchat", "global", "g"};
            }else{
                msgAliases = makeCommandArray(commands.optJSONArray("msg"), "msg", "m");
                replyAliases = makeCommandArray(commands.optJSONArray("reply"), "reply", "r");
                globalAliases = makeCommandArray(commands.optJSONArray("global_chat"), "globalchat", "global", "g");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private static String[] makeCommandArray(JSONArray array, String... defs){
        if(array == null) return defs;
        LinkedList<String> val = new LinkedList<String>();
        for(int i = 0; i < array.length(); i++){
            val.add(array.getString(i));
        }
        return val.toArray(new String[val.size()]);
    }

    public static String compileMessage(String format, String msg, String server, String sender, String target){
        return format
                .replaceAll("\\{server\\}", (server != null ? server : ""))
                .replaceAll("\\{sender\\}", (sender != null ? sender : ""))
                .replaceAll("\\{target\\}", (target != null ? target : ""))
                .replaceAll("\\{msg\\}", (msg != null ? msg : ""));
    }

    public static String getRootForGlobal(){
        return globalAliases[0];
    }

    public static String getRootForMsg(){
        return msgAliases[0];
    }

    public static String getRootForReply(){
        return replyAliases[0];
    }

    public static String[] getMsgAliases() {
        return msgAliases;
    }

    public static String[] getReplyAliases() {
        return replyAliases;
    }

    public static String[] getGlobalAliases() {
        return globalAliases;
    }
}
