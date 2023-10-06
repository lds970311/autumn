package com.autumnframework.base.context;

import com.autumnframework.base.annotation.Autowired;
import com.autumnframework.base.annotation.ComponentScan;
import com.autumnframework.base.annotation.Scope;
import com.autumnframework.base.beans.BeanNameAware;
import com.autumnframework.base.stereotype.Component;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public class AutumnApplicationContext implements ApplicationContext {
    private final Class<?> configureClass;

    private final ConcurrentHashMap<String, Object> singletonBeans = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public AutumnApplicationContext(Class<?> configureClass) {
        this.configureClass = configureClass;
        this.parseConfigClass();
        this.checkBeans();
    }

    private void checkBeans() {
        Set<Map.Entry<String, BeanDefinition>> entries = this.beanDefinitionMap.entrySet();
        for (Map.Entry<String, BeanDefinition> definitionEntry : entries) {
            String beanName = definitionEntry.getKey();
            BeanDefinition beanDefinition = definitionEntry.getValue();
            if (beanDefinition.getScope().equals("singleton")) {
                Object singletonBean = this.createSingletonBean(beanName, beanDefinition);
                this.singletonBeans.put(beanName, singletonBean);
            }
        }

    }

    /**
     * 创建单例bean
     *
     * @param beanDefinition
     */
    private Object createSingletonBean(String beanName, BeanDefinition beanDefinition) {
        Class<?> clazz = beanDefinition.getClazz();
        Object o;
        try {
            o = clazz.getDeclaredConstructor().newInstance();
            //依赖注入
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    //属性赋值
                    Object bean = this.getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(o, bean);
                }
            }

            if (o instanceof BeanNameAware) {
                ((BeanNameAware) o).setBeanName(beanName);
            }

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return o;
    }

    private void parseConfigClass() {
        //解析配置类
        //1.获取@componentScan标注
        ComponentScan annotation = this.configureClass.getDeclaredAnnotation(ComponentScan.class);
        // 2.通过注解拿到所有被标注的bean类型(即建立于package,basePackage,importPackage等通配符后的全限定类名)
        String basePackages = null;
        if (annotation.value().isEmpty()) {
            basePackages = annotation.basePackages()[0];
        } else {
            basePackages = annotation.value();
        }


        //获取basePackages下面所有class
        ClassLoader classLoader = this.getClass().getClassLoader();
        basePackages = basePackages.replace(".", "/");
        URL resource = classLoader.getResource(basePackages);
        File file = new File(resource.getFile());
        this.handleFiles(file, classLoader);
    }

    private void handleFiles(File file, ClassLoader classLoader) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    handleFiles(f, classLoader);
                }
                //不是文件夹
                try {
                    String path = f.getAbsolutePath();
                    if (path.endsWith(".class")) {
                        String newClassPath = path.substring(path.indexOf("com"), path.indexOf(".class"));
                        newClassPath = newClassPath.replace("\\", ".");
                        Class<?> cls = classLoader.loadClass(newClassPath);
                        if (cls.isAnnotationPresent(Component.class)) {
                            //创建Bean对象
                            this.handleBeans(cls);
                        }
                    }

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 判断bean类型,是prototype还是singleton
     *
     * @param cls class
     */
    private void handleBeans(Class<?> cls) {
        //获取类上的Component注解
        Component component = cls.getDeclaredAnnotation(Component.class);
        //获取Component注解上的value值
        String beanName = component.value();
        //创建BeanDefinition对象
        BeanDefinition beanDefinition = new BeanDefinition();
        //设置BeanDefinition对象的clazz属性
        beanDefinition.setClazz(cls);
        //如果类上有Scope注解，则设置BeanDefinition对象的scope属性
        if (cls.isAnnotationPresent(Scope.class)) {
            Scope scope = cls.getDeclaredAnnotation(Scope.class);
            beanDefinition.setScope(scope.value());
        } else {
            //否则，设置BeanDefinition对象的scope属性为singleton
            beanDefinition.setScope("singleton");
        }
        //将BeanDefinition对象放入beanDefinitionMap中
        this.beanDefinitionMap.put(beanName, beanDefinition);
    }

    public Object getBean(String beanName) {
        if (this.beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                return this.singletonBeans.get(beanName);
            } else {
                //创建bean
                return this.createSingletonBean(beanName, beanDefinition);
            }
        } else {
            throw new NullPointerException("不存在对应的bean!");
        }
    }

    public <E> E getBean(Class<E> beanType) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //获取并返回beanType实例
        Set<Map.Entry<String, BeanDefinition>> entries = this.beanDefinitionMap.entrySet();
        for (Map.Entry<String, BeanDefinition> entry : entries) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getClazz() == beanType) {
                if (beanDefinition.getScope().equals("singleton")) {
                    try {
                        return beanType.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return (E) this.createSingletonBean(beanName, beanDefinition);
                }
            }
        }
        return null;
    }
}
