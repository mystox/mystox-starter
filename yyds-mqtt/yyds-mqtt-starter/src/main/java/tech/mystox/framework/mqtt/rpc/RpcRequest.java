package tech.mystox.framework.mqtt.rpc;

/** One RPC request is one AMQP message; no MQTT-style fragmentation. */
public record RpcRequest(String operaCode, String payload, String sourceAddress, long deadline) { }
