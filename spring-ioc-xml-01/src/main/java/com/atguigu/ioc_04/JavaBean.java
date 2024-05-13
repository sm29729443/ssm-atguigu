package com.atguigu.ioc_04;

/**
 * ClassName: JavaBean
 * Package: com.atguigu.ioc_04
 */
public class JavaBean {

    /**
     * 對於週期方法，唯一的要求就是必須是 public void，命名隨便
     */
    public void init() {
        System.out.println("Bean 初始化方法調用");
    }

    public void clear() {
        System.out.println("Bean 銷毀方法調用");
    }
}
