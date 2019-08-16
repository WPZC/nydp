package com.example.nydp.utils;

import com.example.nydp.controller.RequestInfo;
import com.example.nydp.netty.server.HexStringUtils;
import com.timer.TimerRun;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerRwGetInfo extends TimerRun {

    private final static Logger log = LoggerFactory.getLogger(RequestInfo.class);


    @Override
    public void run() {
        //利用设备id获取通道
        //目前写死
        for (String key:Context.realtimeChannel.keySet()){

            String sbid = key;
            String out = "01030000000985CC";
            byte[] b = HexStringUtils.hexStringToByte(out);
            ByteBuf buf2 = Unpooled.buffer();
            buf2.writeBytes(b);
            //firstMessage.writeBytes(b);

            Context.realtimeChannel.get(sbid).writeAndFlush(buf2);
            log.info(out);
        }

    }
}
