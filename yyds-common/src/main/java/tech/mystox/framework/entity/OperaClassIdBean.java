package tech.mystox.framework.entity;

import tech.mystox.framework.stereotype.OperaTimeout;

import java.util.Objects;

/**
 * Created by mystoxlol on 2020/6/23, 9:25.
 * company: kongtrolink
 * description:
 * update record:
 */
public class OperaClassIdBean {
    private static final long serialVersionUID = -6632632504039058978L;

    private final Class<?> interfaceClass;
    private final String group;
    private final String version;
    private final OperaType operaType;
    private final OperaTimeout operaTimeout;

    public OperaClassIdBean(Class<?> interfaceClass, String group, String version, OperaType operaType, OperaTimeout operaTimeout) {
        this.interfaceClass = interfaceClass;
        this.group = group;
        this.version = version;
        this.operaType = operaType;
        this.operaTimeout = operaTimeout;
    }

    public Class<?> getInterfaceClass() {
        return this.interfaceClass;
    }

    public String getGroup() {
        return this.group;
    }

    public String getVersion() {
        return this.version;
    }

    public OperaType getOperaType() {
        return operaType;
    }

    public OperaTimeout getOperaTimeout() {
        return operaTimeout;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OperaClassIdBean)) return false;
        OperaClassIdBean classIdBean = (OperaClassIdBean) obj;
        if (!Objects.equals(this.interfaceClass, classIdBean.interfaceClass)) return false;
        if (!Objects.equals(this.group, classIdBean.group)) return false;
        if (!Objects.equals(this.version, classIdBean.version)) return false;
        if (!Objects.equals(this.operaTimeout, classIdBean.operaTimeout)) return false;
        return Objects.equals(this.operaType, classIdBean.operaType);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + (this.interfaceClass == null ? 0 : this.interfaceClass.hashCode());
        hashCode = 31 * hashCode + (this.group == null ? 0 : this.group.hashCode());
        hashCode = 31 * hashCode + (this.version == null ? 0 : this.version.hashCode());
        hashCode = 31 * hashCode + (this.operaTimeout == null ? 0 : this.operaTimeout.hashCode());
        hashCode = 31 * hashCode + (this.operaType == null ? 0 : this.operaType.hashCode());
        return hashCode;
    }

    @Override
    public String toString() {
        return "OperaClassIdBean{" +
                "interfaceClass=" + interfaceClass +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                ", operaType=" + operaType +
                ", operaTimeout=" + operaTimeout +
                '}';
    }
}
