package tech.mystox.framework.mqtt.rpc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class RpcRouting {
    private RpcRouting() { }
    public static String key(String group, String version, String operation) {
        return segment(group) + "." + segment(version) + "." + segment(operation);
    }
    public static String queue(String prefix, String key) {
        String name = prefix + "." + key;
        if (name.getBytes(StandardCharsets.UTF_8).length > 255) {
            throw new IllegalArgumentException("RPC queue name exceeds the AMQP 255-byte limit");
        }
        return name;
    }
    private static String segment(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("RPC route segments must be nonblank");
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
