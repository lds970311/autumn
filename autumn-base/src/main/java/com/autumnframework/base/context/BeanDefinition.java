package com.autumnframework.base.context;

public class BeanDefinition {
    private Class<?> clazz; //Bean类型
    private String scope; //作用域

    public BeanDefinition(Class<?> clazz, String scope) {
        this.clazz = clazz;
        this.scope = scope;
    }

    public BeanDefinition() {
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
