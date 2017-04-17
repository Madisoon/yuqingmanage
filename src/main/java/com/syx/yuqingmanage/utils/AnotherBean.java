package com.syx.yuqingmanage.utils;

import org.springframework.stereotype.Component;

/**
 * Created by Msater Zg on 2017/2/17.
 */
@Component("anotherBean")
public class AnotherBean {
    public void printAnotherMessage() {
       /* System.out.println("I am AnotherBean. I am called by Quartz jobBean using CronTriggerFactoryBean");*/
    }
}
