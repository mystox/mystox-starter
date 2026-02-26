package tech.mystox.framework.constants;

/**
 * Created by mystox on 2026/2/11, 9:47.
 * company:
 * description:
 * update record:
 */
public class OperaConstants {
    /**
     * The znode will not be automatically deleted upon client's disconnect.
     */
    public static final int PERSISTENT = 0;
    /**
     * The znode will not be automatically deleted upon client's disconnect,
     * and its name will be appended with a monotonically increasing number.
     */
    public static final int PERSISTENT_SEQUENTIAL = 2;
    /**
     * The znode will be deleted upon the client's disconnect.
     */
    public static final int EPHEMERAL = 1;
    /**
     * The znode will be deleted upon the client's disconnect, and its name
     * will be appended with a monotonically increasing number.
     */
    public static final int EPHEMERAL_SEQUENTIAL = 3;

    public static final String MqttMsgBus = "mqtt";

    public static final String ZkRegType = "zookeeper";

}
