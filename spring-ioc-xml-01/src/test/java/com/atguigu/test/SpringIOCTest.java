package com.atguigu.test;

import com.atguigu.ioc_03.HappyComponent;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * ClassName: SpringIOCTest
 * Package: com.atguigu.test
 */
public class SpringIOCTest {

    /**
     *  如何創建 IOC Container 並讀取配置文件
     */
    @Test
    public void createIOC(){
        /**
         *  interface:spring提供了 BeanFactory 與 ApplicationContext 兩個 interface
         *      BeanFactory: BeanFactory 是 Spring IOC Container 的一個核心接口，
         *      定義了 Spring IOC Container 的最基本規範，所以，這代表所有的具體實現類都要遵守
         *      BeanFactory
         *      ApplicationContext: 是基於 BeanFactory 的擴展，但具體擴充了什麼我沒研究
         *  Implement Class:
         *       以下皆可透過 Contructor 直接創建
         *      具體的 IOC Container 實現類，分為以下:
         *      ClassPathXmlApplicationContext: 讀取 classpath 下的 xml 配置文件的 ioc container
         *          也就是編譯後產生的 classes 下的 XML 配置文件
         *      FileSystemXmlApplicationContext: 讀取指定文件位置的 xml 配置文件的 ioc container
         *      AnnotationConfigApplicationContext: 讀取配置類的 ioc container
         *      WebApplicationContext: 讀取 web 項目專屬的配置的 ioc container
         */

        // 方式 1: 創建同時直接指定 XML(推薦)
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-04.xml");

        // 方式 2: 先創建 ioc 容器，再指定 XML，再刷新(了解即可)
        // 這方式通常是源碼在用的，像方式1內部的 CODE 其實也是這種方式
        ClassPathXmlApplicationContext applicationContext1 = new ClassPathXmlApplicationContext();
        applicationContext1.setConfigLocations("spring-04.xml"); // 配置外部 XML
        applicationContext1.refresh(); // 調用 IOC、DI 的過程
    }

    /**
     * 如何獲取 IOC Container 裡面的 Bean
     */
    @Test
    public void getBeanFromIOC() {
        // 1. 創建 IOC Containter
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-04.xml");
        // 2. 讀取 IOC Container 裡的 Bean
        // 這裡 3 個方案獲得的 bean 都是同一個，因為 spring 默認是使用 singleton 的，若是設定成 prototype，才會每 getBean 一次就得到一個不同的 bean obj
        // 方案1: 只根據 bean id 獲得，getBean 得到的是一個 Object 類型的物件
        HappyComponent happyComponent1 = (HappyComponent) applicationContext.getBean("happyComponent");
        // 方案2: 同時根據 bean id 與 Class 類型獲取
        HappyComponent happyComponent2 = applicationContext.getBean("happyComponent", HappyComponent.class);
        // 方案3: 直接根據 Class 獲取，若用此種方式，須注意，此 Class 在 IOC 中只能有一個 Bean，也就是必須是 singleton 的，
        //       否則會報 NoUniqueBeanDefinitionException
        HappyComponent happyComponent3 = applicationContext.getBean(HappyComponent.class);
        happyComponent1.doWork();
        System.out.println(happyComponent1==happyComponent2);
        System.out.println(happyComponent1==happyComponent3);

        /* result:
                HappyComponent.doWork
                true
                true
         */
    }
}
