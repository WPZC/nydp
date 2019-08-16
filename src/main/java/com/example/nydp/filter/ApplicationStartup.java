package com.example.nydp.filter;

import com.example.nydp.utils.Context;
import com.example.nydp.utils.ThreadStratNetty;
import com.example.nydp.utils.TimerRwGetInfo;
import com.example.nydp.utils.Timers.ReamTimeUpadateTmte;
import com.timer.TimerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Timer;


/**
 * springboot启动的启动的方法
 * 启动事件写在onApplicationEvent
 */
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("成功启动------+++++++++++++");
        try {
            //启动通信
            ThreadStratNetty threadStratNetty = new ThreadStratNetty();
            //开启单独线程
            Thread thread = new Thread(threadStratNetty);
            thread.start();
            Context.thread = thread;

            //定时采集
            TimerRwGetInfo timerRwGetInfo = new TimerRwGetInfo();
            Timer timer =TimerFactory.startTimer(timerRwGetInfo,10000,10000*6*2);
            Context.timerUtils.put("定时获取设备采集数据",timer);


            //启动判断设备在线监听
            ReamTimeUpadateTmte reamTimeUpadateTmte = new ReamTimeUpadateTmte();
            Timer timer1 = TimerFactory.startTimer(reamTimeUpadateTmte,5000,1000*60*2);
            Context.timerUtils.put("监听设备是否在线",timer1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
