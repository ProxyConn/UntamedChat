package net.portalblock.untamedchat.bungee.data;

import org.json.JSONObject;

public class Target {
    private final Kind kind;
    private final String target;

    public Target(Kind kind, String target) {
        this.kind = kind;
        this.target = target;
    }

    public static Target fromJSONObject(JSONObject object) {
        Kind kind = Kind.valueOf(object.getString("kind"));
        String type = object.getString("target");
        return new Target(kind, type);
    }

    public Kind getKind() {
        return kind;
    }

    public String getTarget() {
        return target;
    }

    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("kind", kind.name());
        object.put("target", target);
        return object;
    }

    public static enum Kind {
        GLOBAL,
        PLAYER
    }
}
