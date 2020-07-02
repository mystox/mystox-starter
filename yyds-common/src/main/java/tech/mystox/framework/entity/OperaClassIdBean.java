package tech.mystox.framework.entity;

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

    public OperaClassIdBean(Class<?> interfaceClass, String group, String version) {
        this.interfaceClass = interfaceClass;
        this.group = group;
        this.version = version;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OperaClassIdBean)) {
            return false;
        }
        OperaClassIdBean classIdBean = (OperaClassIdBean) obj;
        if (this.interfaceClass == null ? classIdBean.interfaceClass != null
                : !this.interfaceClass.equals(classIdBean.interfaceClass)) {
            return false;
        }
        if (this.group == null ? classIdBean.group != null : !this.group.equals(classIdBean.group)) {
            return false;
        }
        return this.version == null ? classIdBean.version == null
                : this.version.equals(classIdBean.version);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + (this.interfaceClass == null ? 0 : this.interfaceClass.hashCode());
        hashCode = 31 * hashCode + (this.group == null ? 0 : this.group.hashCode());
        hashCode = 31 * hashCode + (this.version == null ? 0 : this.version.hashCode());
        return hashCode;
    }

    @Override
    public String toString() {
        return "ClassIdBean [interfaceClass=" + this.interfaceClass + ", group=" + this.group
                + ", version=" + this.version + "]";
    }
}
