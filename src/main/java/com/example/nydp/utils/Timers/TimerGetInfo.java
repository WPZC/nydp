package com.example.nydp.utils.Timers;

import com.example.nydp.controller.RequestInfo;
import com.example.nydp.entity.TerminalInfo;
import com.example.nydp.netty.server.HexStringUtils;
import com.example.nydp.utils.Context;
import com.timer.TimerFactory;
import com.timer.TimerRun;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerGetInfo extends TimerRun {

    private final static Logger log = LoggerFactory.getLogger(RequestInfo.class);


    public TerminalInfo terminalInfo = new TerminalInfo();

    public static Integer count = 0;

   public TimerGetInfo(TerminalInfo terminalInfo){

       this.terminalInfo = terminalInfo;
   }

    @Override
    public void run() {
        //利用设备id获取通道
        //目前写死
        String sbid = terminalInfo.getTerminalCommunicationid();

        String out = "01030000000985CC";
        byte[] b = HexStringUtils.hexStringToByte(out);
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(b);
        //firstMessage.writeBytes(b);
        if(Context.realtimeChannel.size()>0){
            try {
                System.out.println("--------------------------");
                System.out.println(Context.realtimeChannel);
                System.out.println(terminalInfo.getTerminalCommunicationid());
                System.out.println("--------------------------");
                Context.realtimeChannel.get(terminalInfo.getTerminalCommunicationid()).writeAndFlush(buf2);
            }catch (Exception e){
//                TimerFactory.destroy(Context.timerGetInfos.get(terminalInfo.getTerminalId()+""));
//                Context.timerGetInfos.remove(terminalInfo.getTerminalId()+"");

                TimerFactory.destroy(Context.timerGetInfos.get(terminalInfo.getTerminalId()+"yh"));
                Context.timerGetInfos.remove(terminalInfo.getTerminalId()+"yh");
                Context.dssxs.remove(terminalInfo.getTerminalId()+"");
                System.out.println("ggggggggggg");
                e.printStackTrace();
                System.out.println("ggggggggggg");
            }
        }
        //判断是否发送的60次
        if(count == 5){
            count = 0;
            TimerFactory.destroy(Context.timerGetInfos.get(terminalInfo.getTerminalId()+"yh"));
            Context.timerGetInfos.remove(terminalInfo.getTerminalId()+"yh");
            Context.dssxs.remove(terminalInfo.getTerminalId()+"");
        }else{
            count++;
        }
        log.info(out);
    }
}
