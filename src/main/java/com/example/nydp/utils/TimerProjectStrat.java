package com.example.nydp.utils;

import com.timer.TimerRun;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class TimerProjectStrat extends TimerRun{

    public ChannelHandlerContext cctx;
    public byte[] sstr;

    public TimerProjectStrat(ChannelHandlerContext ctx,byte[] str){
        cctx = ctx;
        sstr = str;
    }
    @Override
    public void run() {
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(sstr);
        cctx.writeAndFlush(buf2);
    }
}
