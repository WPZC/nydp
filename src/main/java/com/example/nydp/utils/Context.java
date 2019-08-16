package com.example.nydp.utils;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;

public class Context {

    /**
     * 存放设备
     * key:设备号
     * value:设备通道
     */
    public static HashMap<String,ChannelHandlerContext> channelHandlerContextHashMaps = new HashMap<String, ChannelHandlerContext>();

    /**
     * 测试通道
     */
    public static ChannelHandlerContext channelHandler;

    /**
     *
     */
    public static Thread thread;

    /**
     * 存放定时器
     * key:通道
     * value:定时器引用
     */
    public static  HashMap<ChannelHandlerContext,Timer> timers = new HashMap<ChannelHandlerContext, Timer>();

    /**
     * 在线设备
     */
    public static HashMap<String,ChannelHandlerContext> realtimeChannel = new HashMap<String, ChannelHandlerContext>();
    /**
     * 发生异常是关闭通道
     */
    public static HashMap<ChannelHandlerContext,String> closeErrot = new HashMap<ChannelHandlerContext, String>();

    /**
     * timer管理
     */
    public static HashMap<String,Timer> timerUtils = new HashMap<String, Timer>();

    /**
     * key:设备编号（5022）
     * value:timer,定期器
     */
    public static HashMap<String,Timer> timerGetInfos = new HashMap<String, Timer>();


    /**
     * 浇水定时器
     * key:sbid D*******
     */
    public static HashMap<String,Timer> jhTimers = new HashMap<String, Timer>();

    /**
     * key:sbid(D******)
     */
    public static HashMap<String,HashMap<String,Timer>> listTimers = new HashMap<String, HashMap<String,Timer>>();

    /**
     * 用户访问时判断是否增加定时器
     */
    public static HashMap<String,Integer> dssxs = new HashMap<String, Integer>();
}
