package tech.mystox.framework.entity;

import java.util.List;

/**
 * Created by mystoxlol on 2019/11/6, 20:45.
 * company: mystox
 * description: privFuncConfig.yml映射实体类
 * update record:
 */
public class PrivFuncEntity {
    private String name;
    private String code;
    private Integer priority;
    private String remark;
    private String type;
    private String uri;
    private String staticPath; //前端静态文件地址，配置路由使用，在权限类型为PAGE时有效
    private String routeMark; //后端请求默认路由
    private String pageRoute; //前端请求默认路由
    private Boolean root;
    private List<PrivFuncEntity> children;

    public Boolean getRoot() {
        return root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    public String getStaticPath() {
        return staticPath;
    }

    public void setStaticPath(String staticPath) {
        this.staticPath = staticPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRouteMark() {
        return routeMark;
    }

    public void setRouteMark(String routeMark) {
        this.routeMark = routeMark;
    }

    public String getPageRoute() {
        return pageRoute;
    }

    public void setPageRoute(String pageRoute) {
        this.pageRoute = pageRoute;
    }

    public List<PrivFuncEntity> getChildren() {
        return children;
    }

    public void setChildren(List<PrivFuncEntity> children) {
        this.children = children;
    }
}
