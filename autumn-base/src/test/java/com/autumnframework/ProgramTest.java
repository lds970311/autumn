package com.autumnframework;


import com.autumnframework.base.context.AutumnApplicationContext;
import com.autumnframework.service.MenuService;
import lombok.SneakyThrows;

public class ProgramTest {

    @SneakyThrows
    public static void main(String[] args) {
        AutumnApplicationContext applicationContext = new AutumnApplicationContext(AppConfig.class);
        MenuService menuService = (MenuService) applicationContext.getBean("menuService");
        MenuService bean = applicationContext.getBean(MenuService.class);
        bean.printMenu();
    }
}
