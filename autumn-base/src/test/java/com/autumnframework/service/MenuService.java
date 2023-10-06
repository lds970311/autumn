package com.autumnframework.service;

import com.autumnframework.base.annotation.Autowired;
import com.autumnframework.base.annotation.Scope;
import com.autumnframework.base.beans.BeanNameAware;
import com.autumnframework.base.stereotype.Component;


@Component("menuService")
@Scope("prototype")
public class MenuService implements BeanNameAware {
    @Autowired
    private OrderService orderService;

    private String beanName;

    public void printMenu() {
        System.out.println("Menu");
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
