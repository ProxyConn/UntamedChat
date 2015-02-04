package net.portalblock.untamedchat.bungee.data;

import org.json.JSONObject;

public class Message {
    private final String sender;
    private final Target target;
    private final String message;
    private final String formattedMessage;

    public Message(String sender, Target target, String message, String formattedMessage) {
        this.sender = sender;
        this.target = target;
        this.message = message;
        this.formattedMessage = formattedMessage;
    }

    public String getSender() {
        return sender;
    }

    public Target getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("sender", sender);
        object.put("target", target.serialize());
        object.put("message", message);
        object.put("formatted", formattedMessage);
        return object;
    }

    public static Message fromJSONObject(JSONObject object) {
        String sender = object.getString("sender");
        Target target = Target.fromJSONObject(object.getJSONObject("target"));
        String message = object.getString("message");
        String formatted = object.getString("formatted");
        return new Message(sender, target, message, formatted);
    }
}
