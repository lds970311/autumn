package com.autumnframework.service;

import com.autumnframework.base.stereotype.Component;

@Component("orderService")
public class OrderService {
    public void printOrder() {
        System.out.println("order");
    }
}
