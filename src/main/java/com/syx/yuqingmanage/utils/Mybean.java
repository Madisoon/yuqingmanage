package com.syx.yuqingmanage.utils;

import org.springframework.stereotype.Component;

/**
 * Created by Msater Zg on 2017/2/17.
 */
@Component("myBean")
public class Mybean {
    //调用某个类的某个方法无任何的数据交互
    public void printMessage() {
        /*System.out.println("I am MyBean. I am called by MethodInvokingJobDetailFactoryBean using SimpleTriggerFactoryBean");*/
    }
}
