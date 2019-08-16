package com.example.nydp.utils.Timers;

import com.example.nydp.entity.TerminalInfo;
import com.example.nydp.netty.server.HexStringUtils;
import com.example.nydp.utils.Context;
import com.timer.TimerFactory;
import com.timer.TimerRun;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

public class TimerContext extends TimerRun {

    private final static Logger log = LoggerFactory.getLogger(TimerContext.class);

    private TerminalInfo terminalInfo;

    private String out;

    private Integer count = 0;

    public TimerContext(TerminalInfo terminalInfo,String out){
        this.terminalInfo = terminalInfo;
        this.out = out;
    }

    @Override
    public void run() {

        if(count == 7){
            String body = out;
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


            for (String key:timerHashMap.keySet()){
                try {
                    //销毁定时器
                    if(key.contains(terminalInfo.getTerminalCommunicationid()+body)){
                        TimerFactory.destroy(timerHashMap.get(key));
                        //Context.listTimers.get(map).remove(key);
                        //timerHashMap.remove(key);
                        listStr.add(key);
                    }
                }catch (Exception e){
                    log.info("tttttttttt");
                    log.error(e.getMessage());
                    e.printStackTrace();
                    log.info("tttttttttt");
                }
            }


            for (int i = 0;i<listStr.size();i++){
                Context.listTimers.get(terminalInfo.getTerminalCommunicationid()).remove(listStr.get(i));
            }
        }else{
            sendTcp(out, Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()));
            count++;
        }

    }

    /**
     *
     * @param out 下发的指令
     * @param ctx 通道
     */
    public void sendTcp(String out, ChannelHandlerContext ctx){
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);
        ctx.writeAndFlush(buf2);
        log.info(out);
    }
}
