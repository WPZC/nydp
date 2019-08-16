package com.example.nydp.netty.server;

import com.example.nydp.entity.RealTimeDateInfo;
import com.example.nydp.entity.TerminalInfo;
import com.example.nydp.service.RealTimeService;
import com.example.nydp.service.TerminalService;
import com.example.nydp.utils.Context;
import com.example.nydp.utils.SpringUtils;
import com.example.nydp.utils.TimerProjectStrat;
import com.example.nydp.utils.Timers.TimerContext;
import com.example.nydp.utils.Timers.TimerJh;
import com.timer.TimerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

//TimeServerHandler继承自ChannelHandlerAdapter，它用于对网络事件进行读写操作
//通常我们只需要关注channelRead和exceptionCaught方法。
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    TerminalService service = SpringUtils.getApplicationContext().getBean(TerminalService.class);
    @Autowired
    RealTimeService realTimeService = SpringUtils.getApplicationContext().getBean(RealTimeService.class);

    private final static Logger log = LoggerFactory.getLogger(TimeServerHandler.class);


    //当客户端和服务端TCP链路建立成功之后，Netty的NIO线程会调用channelActive方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String out = "010300400003041F";
        byte[] b = HexStringUtils.hexStringToByte(out);
        //延时发送
        TimerProjectStrat strat = new TimerProjectStrat(ctx,b);
        Timer timer = TimerFactory.startTimer(strat,5000,10000);
        //添加进全局，在获取到返回结果以后结束
        Context.timers.put(ctx,timer);
        Context.channelHandler = ctx;
        log.info(out);







    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //做类型转换，将msg转换成Netty的ByteBuf对象。
        //ByteBuf类似于JDK中的java.nio.ByteBuffer 对象，不过它提供了更加强大和灵活的功能。
        ByteBuf buf = (ByteBuf) msg;
        //通过ByteBuf的readableBytes方法可以获取缓冲区可读的字节数，
        //根据可读的字节数创建byte数组
        byte[] req = new byte[buf.readableBytes()];
        //通过ByteBuf的readBytes方法将缓冲区中的字节数组复制到新建的byte数组中
        buf.readBytes(req);
        //通过new String构造函数获取请求消息。
        String body = HexStringUtils.bytesToHexString(req).toUpperCase();
        System.out.println("The time server receive order : " + body);
        //如果是"QUERY TIME ORDER"则创建应答消息，
        System.out.println(body);
        //Context.channelHandlerContextHashMaps.put(body.substring(9,10),ctx);
        log.info(body);
        try {
            //接收到这个通道的数据销毁定时器
            if(Context.timers.get(ctx)!=null){//不等于null的时候上传的应该是设备地址
                String sbid = body.substring(6,18);
                TimerFactory.destroy(Context.timers.get(ctx));
                Context.timers.remove(ctx);
                //01 03 06 D2 01 90 11 40 01 83 C2->body 010306d2019011400183c2
                System.out.println("设备编号:"+sbid);
                int equNum = 9999;
                try {
                    equNum = service.selectEqui(sbid);
                }catch (Exception e){
                    System.out.println("1111111111111");
                    e.printStackTrace();
                    System.out.println("1111111111111");

                }
                if(!(equNum>0)){//
                    ctx.close();
                    log.info(sbid+"--为非法设备");
                }else{
                    //连接成功添加进在线设备
                    Context.realtimeChannel.put(sbid,ctx);
                    //添加进监听
                    Context.closeErrot.put(ctx,sbid);
                    realTimeService.updateTimerInfo(new Date(),sbid);
                    log.info(sbid+"--设备连接成功");
                }
            }else{//就是上传的采集信息数据或者心跳
                //获取TerminalID
                TerminalInfo terminalInfo = service.selectTerminalInfo(Context.closeErrot.get(ctx));
                if (body.length()==46||(body.length()>36&&body.length()<47)){//采集数据
                    String info = body.substring(6,42);
                    System.out.println(Context.closeErrot.get(ctx)+"--设备回复信息:"+info);
                    //List<String> listStr = new ArrayList<String>();
                    String cfinfo = "";
                    RealTimeDateInfo realTimeDateInfo = new RealTimeDateInfo();
                    //for (int i = 0;i<info.length()/4;i++){
                    //cfinfo = info.substring(0,4);
                    //listStr.add(cfinfo);
                    String intoT = info.substring(0,4);//拆分出头，代表的是天气传感器
                    String intoW = info.substring(info.length()-4,info.length());//拆分出尾部，代表的是状态（自动手动）
                    info = info.substring(4,info.length()-4);//去除新增的部分

                    realTimeDateInfo.setK5(Integer.parseInt(intoT,16));//设置天气传感器
                    if(intoW.equals("4949")){
                        realTimeDateInfo.setK6(1);//自动
                    }else{
                        realTimeDateInfo.setK6(0);//手动
                    }
                    //010312000080950013009503E700000000000A4949D84F

                    if(info.substring(0,1).equalsIgnoreCase("8")){//室外温度值
                        System.out.println(info.substring(1,4));
                        String num = "0"+info.substring(1,4);
                        realTimeDateInfo.setAd8(-Double.parseDouble((Integer.parseInt(num,16)/10.0)+"")+"");
                    }else{
                        realTimeDateInfo.setAd8(Double.parseDouble(Integer.parseInt(info.substring(0,4),16)/10.0+"")+"");
                    }
                    realTimeDateInfo.setAd2(Integer.parseInt(info.substring(4,8),16)+"");
                    System.out.println(info.substring(8,9));
                    if(info.substring(8,9).equalsIgnoreCase("8")){//室内温度值
                        System.out.println(info.substring(9,12));
                        String num = "0"+info.substring(9,12);
                        realTimeDateInfo.setAd3(-Double.parseDouble((Integer.parseInt(num,16)/10.0)+"")+"");
                    }else{
                        realTimeDateInfo.setAd3(Double.parseDouble(Integer.parseInt(info.substring(8,12),16)/10.0+"")+"");
                    }
                    realTimeDateInfo.setAd4(Integer.parseInt(info.substring(12,16),16)+"");//湿度值

                    realTimeDateInfo.setAd5(Integer.parseInt(info.substring(16,20),16)+"");//CO2 值

                    realTimeDateInfo.setAd6(Integer.parseInt(info.substring(20,24),16)+"");//位置


                    realTimeDateInfo.setAd7(terminalInfo.getZuowu());
                    if(info.substring(24,28).equalsIgnoreCase("000D")){//设备状态：0正常，1模块信号丢失，2限位开关异
                        service.updateTerminalState("限位开关异常",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(13);
                    }else if(info.substring(24,28).equals("000C")){
                        service.updateTerminalState("模块信号丢失",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(12);
                    }else if(info.substring(24,28).equalsIgnoreCase("0001")){
                        service.updateTerminalState("通风口和遮阳幕完全关闭",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(1);
                    }else if(info.substring(24,28).equalsIgnoreCase("0002")){
                        service.updateTerminalState("正在打开通风口",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(2);
                    }else if(info.substring(24,28).equalsIgnoreCase("0003")){
                        service.updateTerminalState("等待温度稳定",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(3);
                    }else if(info.substring(24,28).equalsIgnoreCase("0004")){
                        service.updateTerminalState("正在降低温度",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(4);
                    }else if(info.substring(24,28).equalsIgnoreCase("0005")){
                        service.updateTerminalState("正在升高温度",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(5);
                    }else if(info.substring(24,28).equalsIgnoreCase("0006")){
                        service.updateTerminalState("正在微调温度",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(6);
                    }else if(info.substring(24,28).equalsIgnoreCase("0007")){
                        service.updateTerminalState("微调等待",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(7);
                    }else if(info.substring(24,28).equalsIgnoreCase("0008")){
                        service.updateTerminalState("遮阳幕完全打开",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(8);
                    }else if(info.substring(24,28).equalsIgnoreCase("0009")){
                        service.updateTerminalState("遮阳幕完全关闭",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(9);
                    }else if(info.substring(24,28).equalsIgnoreCase("000A")){
                        service.updateTerminalState("正在关闭遮阳幕和通风口",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(10);
                    }else if(info.substring(24,28).equalsIgnoreCase("000B")){
                        service.updateTerminalState("正在浇水",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(11);
                    }else{
                        service.updateTerminalState("设备正常",terminalInfo.getTerminalCommunicationid());
                        realTimeDateInfo.setK9(0);
                    }

                    realTimeDateInfo.setTerminalid(terminalInfo.getTerminalId());
                    realTimeDateInfo.setTerminalno(Context.closeErrot.get(ctx));
                    realTimeDateInfo.setUpdatedate(new Date());

                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                    realTimeDateInfo.setAd10("info"+format.format(new Date()));
                    int rs = 0;

                    //监测并插入实事数据
                    try {
                        int sbSum = realTimeService.selectOneRealTimeInfo(realTimeDateInfo.getTerminalno());
                        if(sbSum!=0){
                            realTimeService.updateRealTimeInfo(realTimeDateInfo);
                            rs = 1;
                        }else{
                            realTimeDateInfo.setK1(0);
                            realTimeDateInfo.setK2(0);
                            realTimeDateInfo.setK3(0);
                            realTimeDateInfo.setK4(0);
                            realTimeDateInfo.setK5(0);
                            realTimeDateInfo.setK6(0);
                            realTimeDateInfo.setK7(0);
                            realTimeDateInfo.setK8(0);
                            realTimeDateInfo.setK10(0);
                            realTimeService.saveRealTimeInfo(realTimeDateInfo);
                            rs = 2;
                        }
                        //插入历史数据
                        realTimeService.saveRealTimeLogInfo(realTimeDateInfo);
                    }catch (Exception e){
                        rs = 5;
                        System.out.println("222222222");
                        e.printStackTrace();
                        System.out.println("222222222");

                    }
                    if (rs == 2){
                        log.info(Context.closeErrot.get(ctx)+"--保存成功:"+info);
                    }else if (rs==5){
                        log.error(Context.closeErrot.get(ctx)+"--保存失败:"+info);
                    }else{
                        log.info(Context.closeErrot.get(ctx)+"--修改成功:"+info);
                    }
                }else if(body.length()==8){//心跳数据FE000D0B
                    log.info("心跳数据:"+body);
                    String info = body.substring(2,6);
                    try {
                        if(info.equalsIgnoreCase("000D")){//设备状态：0正常，1模块信号丢失，2限位开关异
                            service.updateTerminalState("限位开关异常",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),13);
                        }else if(info.equals("000C")){
                            service.updateTerminalState("模块信号丢失",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),12);
                        }else if(info.equals("0001")){
                            service.updateTerminalState("通风口和遮阳幕完全关闭",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),1);
                        }else if(info.equals("0002")){
                            service.updateTerminalState("正在打开通风口",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),2);
                        }else if(info.equals("0003")){
                            service.updateTerminalState("等待温度稳定",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),3);
                        }else if(info.equals("0004")){
                            service.updateTerminalState("正在降低温度",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),4);
                        }else if(info.equals("0005")){
                            service.updateTerminalState("正在升高温度",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),5);
                        }else if(info.equals("0006")){
                            service.updateTerminalState("正在微调温度",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),6);
                        }else if(info.equals("0007")){
                            service.updateTerminalState("微调等待",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),7);
                        }else if(info.equals("0008")){
                            service.updateTerminalState("遮阳幕完全打开",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),8);
                        }else if(info.equals("0009")){
                            service.updateTerminalState("遮阳幕完全关闭",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),9);
                        }else if(info.equals("000A")){
                            service.updateTerminalState("正在关闭遮阳幕和通风口",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),10);
                        }else if(info.equals("000B")){
                            service.updateTerminalState("正在浇水",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),11);
                        }else{
                            service.updateTerminalState("设备正常",terminalInfo.getTerminalCommunicationid());
                            realTimeService.updateK9(terminalInfo.getTerminalCommunicationid(),0);
                        }
                        realTimeService.updateTimerInfo(new Date(),terminalInfo.getTerminalCommunicationid());
                    }catch (Exception e){
                        System.out.println("333333");
                        e.printStackTrace();
                        System.out.println("333333");

                    }
                }else if(body.length()==20){//控制回复区
                    realTimeService.updateTimerInfo(new Date(),terminalInfo.getTerminalCommunicationid());
                    /*---------------------------------*/
                    //销毁定时器区
                    //获取timer引用
                    Timer timer = Context.timerUtils.get(terminalInfo.getTerminalCommunicationid()+body);
                    //从timer工厂销毁timer
                    TimerFactory.destroy(timer);
                    //从timer监听取消
                    Context.timerUtils.remove(terminalInfo.getTerminalCommunicationid()+body);

                    List<String> listStr = new ArrayList<String>();
                    //销毁listtimers中的定时器
                    HashMap<String,Timer> timerHashMap =  Context.listTimers.get(terminalInfo.getTerminalCommunicationid());

                    try {
                        for (String key:timerHashMap.keySet()){
                            //销毁定时器
                            if(key.contains(terminalInfo.getTerminalCommunicationid()+body)){
                                TimerFactory.destroy(timerHashMap.get(key));
                                //Context.listTimers.get(map).remove(key);
                                //timerHashMap.remove(key);
                                listStr.add(key);
                            }
                        }
                    }catch (Exception e){
                        System.out.println("44444444444");
                        e.printStackTrace();
                        System.out.println("44444444444");

                    }

                    for (int i = 0;i<listStr.size();i++){
                        Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).remove(listStr.get(i));
                    }
                    try {


                        /*-----------------------------------*/
                        if(body.substring(6,8).equals("20")){//通风口开启
                            if(body.substring(12,16).equals("5858")){//开启

                                if(body.substring(2,4).equals("86")){//失败
                                    realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");
                                }else{
                                    realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k1");
                                }
                            }else if(body.substring(12,16).equals("8585")){//关闭
                                if(!body.substring(2,4).equals("86")){
                                    realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k1");
                                }
                            }
                        }else if(body.substring(6,8).equals("21")){//通风口关闭

                            if(body.substring(12,16).equals("5858")){//正常关闭

                                if(body.substring(2,4).equals("86")){
                                    realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k1");
                                }else{
                                    realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");
                                }
                                //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");

                                //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k2");
                            }else if(body.substring(12,16).equals("8585")){//点动关闭
                                if(!body.substring(2,4).equals("86")){
                                    realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");
                                }

                                //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k2");
                            }
                        }else if(body.substring(6,8).equals("22")){//浇水
                            if(body.substring(12,16).equals("9494")){//开启
//                                TimerJh timerJh = new TimerJh(terminalInfo.getTerminalId()+"",terminalInfo.getTerminalCommunicationid()+"");
//                                Timer timerJhyy = TimerFactory.startTimer(timerJh,1000*60*2,1000);
//                                Context.jhTimers.put(terminalInfo.getTerminalCommunicationid()+"",timerJhyy);
                                realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k3");
                            }else if(body.substring(12,16).equals("4949")){//关闭
                                //停止定时器
//                                TimerFactory.destroy(Context.jhTimers.get(terminalInfo.getTerminalCommunicationid()+""));
//                                Context.jhTimers.remove(terminalInfo.getTerminalCommunicationid()+"");
                                realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k3");
                            }

                        }else if(body.substring(6,8).equals("23")){//加热
                            if(body.substring(12,16).equals("9494")){//开启
                                realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k4");
                            }else if(body.substring(12,16).equals("4949")){//关闭
                                realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k4");
                            }

                        }
                    }catch (Exception e){
                        System.out.println("55555555");
                        e.printStackTrace();
                        System.out.println("55555555");

                    }
                }


                //}
            }
            System.out.println("timerUtils定时器数量:"+Context.timerUtils.size());
            System.out.println("listTimers设备数量:"+Context.listTimers.size());
            System.out.println("realtimeChannel设备数量:"+Context.realtimeChannel.size());
            System.out.println("jhTimers设备数量:"+Context.jhTimers.size());
//            for (String key:Context.listTimers.keySet()){
//                for (String k:Context.listTimers.get(key).keySet()){
//                    System.out.println("listTimers设备数量:"+Context.listTimers.size()+","+k+"定时器数量"+Context.listTimers.get(k).size());
//                }
//            }

        }catch (Exception e){
            System.out.println("66666666");
            e.printStackTrace();
            System.out.println("66666666");
        }



    }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //调用了ChannelHandlerContext的flush方法，它的作用是将消息发送队列中的消息写入到SocketChannel中发送给对方。
        //从性能角度考虑，为了防止频繁地唤醒Selector进行消息发送，
        //Netty的write方法并不直接将消息写入SocketChannel中，调用write方法只是把待发送的消息放到发送缓冲数组中，
        //再通过调用flush方法，将发送缓冲区中的消息全部写到SocketChannel中。
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //当发生异常时，关闭ChannelHandlerContext，释放和ChannelHandlerContext相关联的句柄等资源。
        //从在线设备中移除
        TerminalInfo terminalInfo = service.selectTerminalInfo(Context.closeErrot.get(ctx));
        Context.realtimeChannel.remove(Context.closeErrot.get(ctx));
        log.info(Context.closeErrot.get(ctx)+"--设备发生异常连接断开。");
        String sbid = Context.closeErrot.get(ctx);
        //清除监听
        Context.closeErrot.remove(ctx);

        TimerFactory.destroy(Context.jhTimers.get(sbid));
        TimerFactory.destroy(Context.timerGetInfos.get(terminalInfo.getTerminalId()+""));
        //销毁listtimers中的定时器
        for (String map:Context.listTimers.keySet()){

            HashMap<String,Timer> timerHashMap =  Context.listTimers.get(map);

            try {
                for (String key:timerHashMap.keySet()){
                    //销毁定时器
                    if(key.contains(sbid)){
                        TimerFactory.destroy(timerHashMap.get(key));
                        Context.listTimers.get(map).remove(key);
                    }
                }
            }catch (Exception e){
                System.out.println("7777777777");
                e.printStackTrace();
                System.out.println("7777777777");
            }



        }
        //销毁timerUtils中的定时器
        for (String key:Context.timerUtils.keySet()){
            if(key.contains(terminalInfo.getTerminalCommunicationid())){
                TimerFactory.destroy(Context.timerUtils.get(key));
                Context.timerUtils.remove(key);
            }
        }
        //从全局中移除
        Context.jhTimers.remove(sbid+"");
        Context.timerGetInfos.remove(terminalInfo.getTerminalId()+"");
        Context.listTimers.remove(terminalInfo.getTerminalCommunicationid()+"");
        log.info(Context.closeErrot.get(ctx)+"--设备连接断开。");
        ctx.close();

    }

    /**
     * 通道断开连接执行的方法
     * @param ctx
     * @throws Exception
     */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //当发生异常时，关闭ChannelHandlerContext，释放和ChannelHandlerContext相关联的句柄等资源。
        //从在线设备中移除
        TerminalInfo terminalInfo = service.selectTerminalInfo(Context.closeErrot.get(ctx));
        Context.realtimeChannel.remove(Context.closeErrot.get(ctx));
        log.info(Context.closeErrot.get(ctx)+"--设备发生异常连接断开。");
        String sbid = Context.closeErrot.get(ctx);
        //清除监听
        Context.closeErrot.remove(ctx);

        TimerFactory.destroy(Context.jhTimers.get(sbid));
        TimerFactory.destroy(Context.timerGetInfos.get(terminalInfo.getTerminalId()+""));
        //销毁listtimers中的定时器
        for (String map:Context.listTimers.keySet()){

            HashMap<String,Timer> timerHashMap =  Context.listTimers.get(map);

            for (String key:timerHashMap.keySet()){
                //销毁定时器
                if(key.contains(sbid)){
                    TimerFactory.destroy(timerHashMap.get(key));
                    Context.listTimers.get(map).remove(key);
                }
            }


        }
        //销毁timerUtils中的定时器
        for (String key:Context.timerUtils.keySet()){
            if(key.contains(terminalInfo.getTerminalCommunicationid())){
                TimerFactory.destroy(Context.timerUtils.get(key));
                Context.timerUtils.remove(key);
            }
        }
        //从全局中移除
        Context.jhTimers.remove(sbid+"");
        Context.timerGetInfos.remove(terminalInfo.getTerminalId()+"");
        Context.listTimers.remove(terminalInfo.getTerminalCommunicationid()+"");
    }

}