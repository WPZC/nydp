package com.example.nydp.controller;

import com.example.nydp.entity.RealTimeDateInfo;
import com.example.nydp.entity.TerminalInfo;
import com.example.nydp.netty.server.HexStringUtils;
import com.example.nydp.service.RealTimeService;
import com.example.nydp.service.TerminalService;
import com.example.nydp.utils.Context;
import com.example.nydp.utils.Timers.TimerContext;
import com.example.nydp.utils.Timers.TimerGetInfo;
import com.example.nydp.utils.Timers.TimerJh;
import com.timer.TimerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Timer;

@Controller
@RequestMapping("/request")
public class RequestInfo {

    @Autowired
    TerminalService service;

    @Autowired
    RealTimeService realTimeService;

    private final static Logger log = LoggerFactory.getLogger(RequestInfo.class);

    /**
     * 读取所有设备信息
     */
    @RequestMapping("/allInfo")
    @ResponseBody
    public void getAllInfo(String sbid){
        //利用设备id获取通道
        //目前写死
        sbid = "D20190114001";

        String out = "01030001000755C8";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);

        Context.realtimeChannel.get(sbid).writeAndFlush(buf2);
        log.info(out);

    }

    @RequestMapping("/controllerDeEquipment")
    @ResponseBody
    public String ControllerDeEquipment(String sbid,String state,String jid){
        //state:1开，0关
        //sbid:自定义id，通过sbid找设备号
        //jid:页面上控制的顺序号
        System.out.println(sbid+"----"+state+"---"+jid);

        //180.76.135.14:8859/request/controllerDeEquipment?state=0&sbid=5022&jid=05
        TerminalInfo terminalInfo = service.selectTerminalInfoSbid(sbid);
        RealTimeDateInfo realTimeDateInfo2 = realTimeService.selectRealTimeOne(terminalInfo.getTerminalCommunicationid());
        int rs = 0;
        try {
            if(jid.equals("qr")){//设置温度
                realTimeService.updateSetWd(state,terminalInfo.getTerminalCommunicationid());
            }else {
                if (jid.equals("01")) { //控制通风口启动
                    if (state.equalsIgnoreCase("0")) {
                        //if(!(Double.parseDouble(realTimeDateInfo2.getAd8())<Double.parseDouble(realTimeDateInfo2.getAd9()))){
                            //if(realTimeDateInfo2.getK2()==0){
                                sendTcp("01060020000158580DFA", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                                realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k1");
                                TimerContext timerContext = new TimerContext(terminalInfo,"01060020000158580DFA");
                                Timer timer = TimerFactory.startTimer(timerContext,5000,5000);
                                //判断是否存在listtimer,用于异常时关闭通道
                                if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                                    HashMap<String,Timer> timers = new HashMap<String,Timer>();
                                    timers.put(terminalInfo.getTerminalCommunicationid()+"01060020000158580DFA",timer);
                                    Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                                }else{
                                    Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"01060020000158580DFA",timer);
                                }

                                Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"01060020000158580DFA",timer);
                                //realTimeService.updateKState(sbid, "1", "k1");
                            //}
                        //}
                    } else {//点动
                        sendTcp("010600200001858594F3", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");
                        realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k1");

                        TimerContext timerContext = new TimerContext(terminalInfo,"010600200001858594F3");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);

                        //判断是否存在listtimer
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"010600200001858594F3",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"010600200001858594F3",timer);
                        }
                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"010600200001858594F3",timer);
                        //Context.timerUtils.put("010600200001858594F3",timer);
                        //realTimeService.updateKState(sbid, "0", "k1");
                    }
                } else if (jid.equals("02")) { //控制通风口关闭
                    if (state.equalsIgnoreCase("0")) {
                        //if(realTimeDateInfo2.getK1()==0){
                            sendTcp("0106002100015858303A", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                            //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k2");
                            realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");
                            TimerContext timerContext = new TimerContext(terminalInfo,"0106002100015858303A");
                            Timer timer = TimerFactory.startTimer(timerContext,5000,5000);

                            //判断是否存在listtimer,用于异常时关闭通道
                            if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                                HashMap<String,Timer> timers = new HashMap<String,Timer>();
                                timers.put(terminalInfo.getTerminalCommunicationid()+"0106002100015858303A",timer);
                                Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                            }else{
                                Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"0106002100015858303A",timer);
                            }

                            Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"0106002100015858303A",timer);
                            //Context.timerUtils.put("0106002100015858303A",timer);
                            //realTimeService.updateKState(sbid, "1", "k2");
                       // }
                    } else {//降 点动
                        sendTcp("0106002100018585A933", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k2");
                        realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");
                        TimerContext timerContext = new TimerContext(terminalInfo,"0106002100018585A933");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);

                        //判断是否存在listtimer
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"0106002100018585A933",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"0106002100018585A933",timer);
                        }
                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"0106002100018585A933",timer);
                        //Context.timerUtils.put("0106002100018585A933",timer);
                        //realTimeService.updateKState(sbid, "0", "k2");
                    }
                } else if (jid.equals("03")) { //浇水启动
                    if (state.equalsIgnoreCase("1")) {
                        //创建定海器，两分钟以后销毁并停止浇水
//                        TimerJh timerJh = new TimerJh(terminalInfo.getTerminalCommunicationid(),sbid);
//                        Timer timer = TimerFactory.startTimer(timerJh,1000*60*2,1000);
                        sendTcp("0106002200019494216F", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k3");

                        TimerContext timerContext = new TimerContext(terminalInfo,"0106002200019494216F");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);


                        //判断是否存在listtimer,用于异常时关闭通道
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"0106002200019494216F",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"0106002200019494216F",timer);
                        }

                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"0106002200019494216F",timer);


                        //Context.timerUtils.put("0106002200019494216F",timer);
                        //Context.jhTimers.put(terminalInfo.getTerminalCommunicationid()+"",timer);
                        //realTimeService.updateKState(sbid, "1", "k3");
                    } else {//浇水停止

                        sendTcp("0106002200014949B866", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k3");

                        TimerContext timerContext = new TimerContext(terminalInfo,"0106002200014949B866");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);


                        //判断是否存在listtimer
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"0106002200014949B866",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"0106002200014949B866",timer);
                        }
                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"0106002200014949B866",timer);
                    }
                }else if(jid.equals("04")){ //加热控制

                    if (state.equalsIgnoreCase("1")) {
                        //创建定海器，两分钟以后销毁并停止加热
//                        TimerJh timerJh = new TimerJh(terminalInfo.getTerminalCommunicationid(),sbid);
//                        Timer timer = TimerFactory.startTimer(timerJh,1000*60*2,1000);
                        sendTcp("01060023000194941CAF", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k4");

                        TimerContext timerContext = new TimerContext(terminalInfo,"01060023000194941CAF");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);


                        //判断是否存在listtimer,用于异常时关闭通道
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"01060023000194941CAF",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"01060023000194941CAF",timer);
                        }

                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"01060023000194941CAF",timer);


                        //Context.timerUtils.put("0106002200019494216F",timer);
                        //Context.jhTimers.put(terminalInfo.getTerminalCommunicationid()+"",timer);
                        //realTimeService.updateKState(sbid, "1", "k3");
                    } else {//加热停止
                        sendTcp("010600230001494985A6", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k4");

                        TimerContext timerContext = new TimerContext(terminalInfo,"010600230001494985A6");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);


                        //判断是否存在listtimer
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"010600230001494985A6",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"010600230001494985A6",timer);
                        }
                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"010600230001494985A6",timer);
                    }

                }else if(jid.equals("05")){//发送自动指令
                    if (state.equalsIgnoreCase("1")) {



                        sendTcp("0106000800014949A1A0", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k6");

                        TimerContext timerContext = new TimerContext(terminalInfo,"0106000800014949A1A0");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);


                        //判断是否存在listtimer,用于异常时关闭通道
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"0106000800014949A1A0",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"0106000800014949A1A0",timer);
                        }

                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"0106000800014949A1A0",timer);


                        //Context.timerUtils.put("0106002200019494216F",timer);
                        //Context.jhTimers.put(terminalInfo.getTerminalCommunicationid()+"",timer);
                        //realTimeService.updateKState(sbid, "1", "k3");
                    }
                }else if(jid.equals("06")){
                    if (state.equalsIgnoreCase("0")) {
                        //if(!(Double.parseDouble(realTimeDateInfo2.getAd8())<Double.parseDouble(realTimeDateInfo2.getAd9()))){
                        //if(realTimeDateInfo2.getK2()==0){
                        sendTcp("01060020000158580DFA", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k1");
                        TimerContext timerContext = new TimerContext(terminalInfo,"01060020000158580DFA");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);
                        //判断是否存在listtimer,用于异常时关闭通道
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"01060020000158580DFA",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"01060020000158580DFA",timer);
                        }

                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"01060020000158580DFA",timer);
                        //realTimeService.updateKState(sbid, "1", "k1");
                        //}
                        //}
                    } else {//点动
                        sendTcp("010600200001858594F3", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");
                        //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k1");

                        TimerContext timerContext = new TimerContext(terminalInfo,"010600200001858594F3");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);

                        //判断是否存在listtimer
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"010600200001858594F3",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"010600200001858594F3",timer);
                        }
                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"010600200001858594F3",timer);
                        //Context.timerUtils.put("010600200001858594F3",timer);
                        //realTimeService.updateKState(sbid, "0", "k1");
                    }

                }else if(jid.equals("07")){

                    if (state.equalsIgnoreCase("0")) {
                        //if(realTimeDateInfo2.getK1()==0){
                        sendTcp("0106002100015858303A", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "1", "k2");
                        //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");
                        TimerContext timerContext = new TimerContext(terminalInfo,"0106002100015858303A");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);

                        //判断是否存在listtimer,用于异常时关闭通道
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"0106002100015858303A",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"0106002100015858303A",timer);
                        }

                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"0106002100015858303A",timer);
                        //Context.timerUtils.put("0106002100015858303A",timer);
                        //realTimeService.updateKState(sbid, "1", "k2");
                        // }
                    } else {//降 点动
                        sendTcp("0106002100018585A933", Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
                        //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k2");
                        //realTimeService.updateKState(terminalInfo.getTerminalId()+"", "0", "k1");
                        TimerContext timerContext = new TimerContext(terminalInfo,"0106002100018585A933");
                        Timer timer = TimerFactory.startTimer(timerContext,5000,5000);

                        //判断是否存在listtimer
                        if(Context.listTimers.get(terminalInfo.getTerminalCommunicationid())==null){
                            HashMap<String,Timer> timers = new HashMap<String,Timer>();
                            timers.put(terminalInfo.getTerminalCommunicationid()+"0106002100018585A933",timer);
                            Context.listTimers.put(terminalInfo.getTerminalCommunicationid(),timers);
                        }else{
                            Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).put(terminalInfo.getTerminalCommunicationid()+"0106002100018585A933",timer);
                        }
                        Context.timerUtils.put(terminalInfo.getTerminalCommunicationid()+"0106002100018585A933",timer);
                        //Context.timerUtils.put("0106002100018585A933",timer);
                        //realTimeService.updateKState(sbid, "0", "k2");
                    }
                }
            }
        }catch (Exception e){

            rs = 1;
            System.out.println("888888");
            e.printStackTrace();
            System.out.println("888888");

        }



        return rs+"";
    }

    @RequestMapping("/timerGetInfo")
    @ResponseBody
    public String setTimerGetInfo(String sbid){
        Timer timer  = null;
        if(Context.dssxs.get(sbid)==null){
            if(Context.timerGetInfos.get(sbid+"yh")==null){
                try {
                    TerminalInfo terminalInfo = service.selectTerminalInfoSbid(sbid);
                    TimerGetInfo timerGetInfo = new TimerGetInfo(terminalInfo);
                    timer = TimerFactory.startTimer(timerGetInfo,0,5000);
                    Context.dssxs.put(sbid,1);
                    Context.timerGetInfos.put(sbid+"yh",timer);
                }catch (Exception e){
                    TimerFactory.destroy(timer);
                    System.out.println("9999999999");
                    e.printStackTrace();
                    System.out.println("9999999999");

                }

                System.out.println("timerTimes:"+TimerFactory.timerTimes.size());
                System.out.println("定时器数量:"+TimerFactory.timerTimes.size());
                System.out.println("timers:"+Context.timers.size());
                System.out.println("listTimers:"+Context.listTimers.size());
                System.out.println("timerUtils:"+Context.timerUtils.size());
                System.out.println("timerGetInfos:"+Context.timerGetInfos.size());
                System.out.println("jhTimers:"+Context.jhTimers.size());
                System.out.println("在线设备:"+Context.realtimeChannel.toString());
                System.out.println("timer管理:"+Context.timerUtils.toString());
                //System.out.println("timers777-+-+:"+Context.timers.size());
                //System.out.println("timers777-+-+:"+Context.timers.toString());

                return "1";
            }else {
//                //停止定时器
//            TimerFactory.destroy(Context.timerGetInfos.get(sbid));
//            //从全局中删除定时器
//            Context.timerGetInfos.remove(sbid);
//            //创建新的定时器
//            TerminalInfo terminalInfo = service.selectTerminalInfoSbid(sbid);
//            TimerGetInfo timerGetInfo = new TimerGetInfo(terminalInfo);
//            timer = TimerFactory.startTimer(timerGetInfo,0,10000);
//            Context.timerGetInfos.put(sbid,timer);

                System.out.println("timerTimes:"+TimerFactory.timerTimes.size());
                System.out.println("定时器数量:"+TimerFactory.timerTimes.size());
                System.out.println("timers:"+Context.timers.size());
                System.out.println("listTimers:"+Context.listTimers.size());
                System.out.println("timerUtils:"+Context.timerUtils.size());
                System.out.println("timerGetInfos:"+Context.timerGetInfos.size());
                System.out.println("jhTimers:"+Context.jhTimers.size());
                System.out.println("在线设备:"+Context.realtimeChannel.toString());
                System.out.println("timer管理:"+Context.timerUtils.toString());
                //System.out.println("timers777-+-+:"+Context.timers.size());
                //System.out.println("timers777-+-+:"+Context.timers.toString());


                return "1";
            }

        }

        System.out.println("timerTimes:"+TimerFactory.timerTimes.size());
        System.out.println("定时器数量:"+TimerFactory.timerTimes.size());
        System.out.println("timers:"+Context.timers.size());
        System.out.println("listTimers:"+Context.listTimers.size());
        System.out.println("timerUtils:"+Context.timerUtils.size());
        System.out.println("timerGetInfos:"+Context.timerGetInfos.size());
        System.out.println("jhTimers:"+Context.jhTimers.size());
        System.out.println("在线设备:"+Context.realtimeChannel.toString());
        System.out.println("timer管理:"+Context.timerUtils.toString());
        //System.out.println("timers777-+-+:"+Context.timers.size());
        //System.out.println("timers777-+-+:"+Context.timers.toString());
        return "999";
    }

    public void sendTcp(String out, ChannelHandlerContext ctx){
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);
        ctx.writeAndFlush(buf2);
        log.info(out);
    }

    /**
     * 读取所有设备信息
     */
    @RequestMapping("/sbInfo")
    @ResponseBody
    public void getSb(){
        String out = "010300400003041F";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);

        Context.channelHandler.writeAndFlush(buf2);
        log.info(out);

    }
    /**
     * 读取设备型号
     */
    @RequestMapping("/equInfo")
    @ResponseBody
    public void getEquipment(){
        String out = "010300010001D5CA";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);

        Context.channelHandler.writeAndFlush(buf2);
        log.info(out);
    }

    /**
     * 读取设备光照强度
     */
    @RequestMapping("/lightInfo")
    @ResponseBody
    public void getLight(){
        String out = "01030002000125CA";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);
        Context.channelHandler.writeAndFlush(buf2);
        log.info(out);

    }

    /**
     * 读取设备温度值
     */
    @RequestMapping("/temperatureInfo")
    @ResponseBody
    public void getTemperature(){
        String out = "010300030001740A";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);

        Context.channelHandler.writeAndFlush(buf2);
        log.info(out);

    }

    /**
     * 读取设备湿度值
     */
    @RequestMapping("/humidityInfo")
    @ResponseBody
    public void getHumidity(){
        String out = "010300040001C5CB";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);

        Context.channelHandler.writeAndFlush(buf2);
        log.info(out);

    }

    /**
     * 读取设备CO2值
     */
    @RequestMapping("/co2Info")
    @ResponseBody
    public void getco2(){
        String out = "010300050001940B";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);

        Context.channelHandler.writeAndFlush(buf2);
        log.info(out);

    }

    /**
     * 读取设备位置
     */
    @RequestMapping("/addressInfo")
    @ResponseBody
    public void getAddress(){
        String out = "010300060001640B";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);

        Context.channelHandler.writeAndFlush(buf2);
        log.info(out);

    }

    /**
     * 读取设备状态
     */
    @RequestMapping("/stateInfo")
    @ResponseBody
    public void getState(){
        String out = "01030007000135CB";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);

        Context.channelHandler.writeAndFlush(buf2);
        log.info(out);

    }

    /**
     * 读取设备数据块
     */
    @RequestMapping("/dataInfo")
    @ResponseBody
    public void getData(){
        String out = "01030001000755C8";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);

        Context.channelHandler.writeAndFlush(buf2);
        log.info(out);

    }
    @RequestMapping("/close")
    @ResponseBody
    public void closeChannel(){
        Context.channelHandler.close();
        log.info(Context.channelHandler.toString()+"关闭");
        //Context.thread.interrupt();
    }

    @RequestMapping("/test")
    @ResponseBody
    public void test(String id){
        System.out.println(id);
        int i = service.selectEqui(id);
        System.out.println(i);
        System.out.println(service.getlist().size());

    }

    @RequestMapping("/getRealTimeInfoData")
    @ResponseBody
    public RealTimeDateInfo getRealTimeInfoData(Long sbid){
        return realTimeService.getRealTimeData(sbid);
    }


    public static void main(String[] args){
        HashMap<String,String> map = new HashMap<>();
        map.put("123","777");
        map.put("1234","777");
        map.put("1236","777");

        System.out.println(map.toString());

    }

}
