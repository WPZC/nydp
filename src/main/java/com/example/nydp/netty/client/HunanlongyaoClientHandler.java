package com.example.nydp.netty.client;
import java.util.concurrent.TimeUnit; 
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;   
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop; 
import io.netty.channel.SimpleChannelInboundHandler; 

public class HunanlongyaoClientHandler extends SimpleChannelInboundHandler<ByteBuf> { 

	private static final Log log = LogFactory.getLog(HunanlongyaoClientHandler.class);

	private HunanlongyaoClient client;  
	private String sn;

	public HunanlongyaoClientHandler(HunanlongyaoClient client,String sn) {   
		this.client = client;   
		this.sn=sn;
	} 
 
	/**
	 * channelInactive
	 * channel 通道 Inactive 不活跃的
	 * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。
	 * 也就是说客户端与服务端的关闭了通信通道并且不可以传输数据 
	 */
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端与服务端通道-关闭：" + ctx.channel().localAddress() + "channelInactive");
		final EventLoop eventLoop = ctx.channel().eventLoop();  
		eventLoop.schedule(new Runnable() {  
			public void run() {   
				try {
					client.createBootstrap(new Bootstrap(), eventLoop);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}  
			}  
		}, 10L, TimeUnit.SECONDS);   
		super.channelInactive(ctx);   
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("异常退出:" + cause.getMessage());
		ctx.close();
	}


	/*
	 * 覆盖channelActive 方法在channel被启用的时候触发（在建立连接的时候）
	 * 覆盖了 channelActive() 事件处理方法。服务端监听到客户端活动
	 */
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx); 
		//CommonData.longyaoChannels.put(sn, ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext tex, ByteBuf buff) 	throws Exception {		 
		if (buff.readableBytes() <= 0) { 
			return;
		}
		byte[] bytes = new byte[buff.readableBytes()];
		buff.readBytes(bytes);
//		String message =HexUtils.bytesToHexString(bytes);
//		if (message == null)
//			return;
//		log.info("接收服务端信息:"+message);
// 				String num = message.substring(16, 18);
//				if("71".equalsIgnoreCase(num)){
//					//发送注册包（0x61）
// 					String msgly = nettyff.bjm61("61",sn);
//					if (CommonData.longyaoChannels.get(sn).channel().isOpen()) {
//						try{
//							byte[] send =HexUtils.hexStringToByte(msgly);
//							ByteBuf buf = Unpooled.buffer();
//							buf.writeBytes(send);
//							CommonData.longyaoChannels.get(sn).writeAndFlush(buf);
//							log.info("邢台隆尧转发成功61:SN:"+sn+";MSG:"+msgly);
//						} catch (Exception e) {
//							log.error(e.getMessage());
//						}
//					}
//				}else if("62".equalsIgnoreCase(num)){
//
//				}else if("65".equalsIgnoreCase(num)){
//					String s ;
//					if(CommonData.longyaoMsgs.get(sn) == null||CommonData.longyaoMsgs.get(sn) == ""){
//						s= "{\"fengsu\":0,\"yuliang\":0,\"turangshidu\":0,\"guangzhaoqiangdu\":0,\"kongqiwendu\":0,\"zaoyin\":0,\"biaomianwendu\":0,\"daqiyali\":0,\"jingdu\":\"0\",\"videoId\":null,\"tid\":363,\"siteName\":null,\"mn\":\"\",\"kongqishidu\":30.5,\"youxiaofushe\":0,\"fengxiang\":0,\"weidu\":\"0\",\"dateday\":\"2018-08-01 17:39:48\",\"shebeimingcheng\":\"成安县污水处理\",\"shebeibianhao\":\"SHEB2106\",\"lid\":0,\"pm25Switch\":0,\"ppm\":0,\"pm10\":0,\"turangwendu\":0,\"mp25\":0}";
//						CommonData.longyaoMsgs.put(sn, s);
//					}else{
//					    s=CommonData.longyaoMsgs.get(sn); //"{\"fengsu\":0,\"yuliang\":0,\"turangshidu\":0,\"guangzhaoqiangdu\":0,\"kongqiwendu\":27.3,\"zaoyin\":43.5,\"biaomianwendu\":0,\"daqiyali\":0,\"jingdu\":\"114.6664\",\"videoId\":null,\"tid\":363,\"siteName\":null,\"mn\":\"\",\"kongqishidu\":30.5,\"youxiaofushe\":0,\"fengxiang\":0,\"weidu\":\"36.4884\",\"dateday\":\"2018-08-01 17:39:48\",\"shebeimingcheng\":\"成安县污水处理\",\"shebeibianhao\":\"SHEB2106\",\"lid\":0,\"pm25Switch\":0,\"ppm\":0,\"pm10\":90,\"turangwendu\":0,\"mp25\":79}";
// 					}
// 					String cd1 = message.substring(36, 38);//截取服务器请求几个数据
//					String cd = message.substring(36, 38+Integer.parseInt(cd1, 16)*2);//截取服务器请求几个数据
//					String msgly = nettyff.getsssj(s,cd);
//					if (CommonData.longyaoChannels.get(sn).channel().isOpen()) {
//						try{
//							byte[] send =HexUtils.hexStringToByte(msgly);;
//							ByteBuf buf = Unpooled.buffer();
//							buf.writeBytes(send);
//							CommonData.longyaoChannels.get(sn).writeAndFlush(buf);
//							log.info("邢台隆尧转发成功:SN:"+sn+";MSG:"+msgly);
//						} catch (Exception e) {
//							log.error(e.getMessage());
//						}
//					}
//				}
			}  
}