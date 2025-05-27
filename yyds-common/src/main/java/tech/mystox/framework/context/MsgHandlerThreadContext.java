package tech.mystox.framework.context;

/**
 * Created by mystox on 2025/5/27, 17:32.
 * company:
 * description:
 * update record:
 */
public class MsgHandlerThreadContext {

    private static final ThreadLocal<MsgHandlerThreadContext> context = ThreadLocal.withInitial(MsgHandlerThreadContext::new);

    private String operaCode;
    private String groupCode;
    private String sourceAddress;

    // 获取当前线程的上下文实例
    public static MsgHandlerThreadContext getContext() {
        return context.get();
    }

    // 清除上下文（防止内存泄漏，特别是在使用线程池时）
    public static void clear() {
        context.remove();
    }

    public String getOperaCode() {
        return operaCode;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
}
