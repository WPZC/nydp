package com.example.nydp.utils.Timers;

import com.example.nydp.netty.server.HexStringUtils;
import com.example.nydp.netty.server.TimeServerHandler;
import com.example.nydp.service.RealTimeService;
import com.example.nydp.utils.Context;
import com.example.nydp.utils.SpringUtils;
import com.timer.TimerFactory;
import com.timer.TimerRun;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TimerJh extends TimerRun {

    private final static Logger log = LoggerFactory.getLogger(TimerJh.class);


    private static Integer count = 0;


    @Autowired
    RealTimeService realTimeService = SpringUtils.getApplicationContext().getBean(RealTimeService.class);

    public String sbid;

    public String sbid2;

    public TimerJh(String sbid,String sbid2){
        this.sbid = sbid;
        this.sbid2 = sbid2;
    }

    @Override
    public void run() {
        try {
            sendTcp("0106002200014949B866", Context.realtimeChannel.get(sbid2));
            realTimeService.updateKState(sbid+"", "0", "k3");
            TimerFactory.destroy(Context.jhTimers.get(sbid2));
            Context.jhTimers.remove(sbid2);
        }catch (Exception e){
            TimerFactory.destroy(Context.jhTimers.get(sbid2));
            System.out.println("00000000");
            e.printStackTrace();
            System.out.println("00000000");

        }




    }

    public void sendTcp(String out, ChannelHandlerContext ctx){
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);
        ctx.writeAndFlush(buf2);
        log.info(out);
    }
}
