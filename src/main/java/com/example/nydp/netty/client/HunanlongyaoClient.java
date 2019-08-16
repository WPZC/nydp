package com.example.nydp.netty.client;
 
import io.netty.bootstrap.Bootstrap; 
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;  

public class HunanlongyaoClient  implements Runnable{   
	private static String host="192.168.1.4";
	private  static int port=8882;
	private String sn;
	private EventLoopGroup group = new NioEventLoopGroup();

	public HunanlongyaoClient(String host,int port,String sn){
		this.host=host;
		this.port=port;
		this.sn=sn;
	}
	public Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop) throws InterruptedException {  
		//EventLoopGroup group = new NioEventLoopGroup();
		//Bootstrap b  = null;
		try {
			//b = new Bootstrap();
			bootstrap.group(group) // 注册线程池 
			.option(ChannelOption.TCP_NODELAY, true)
			.channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类 
			.handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					System.out.println("正在连接中..."); 
					ch.pipeline().addLast("encoder", new ClientEncoder());    
					// ch.pipeline().addLast("decoder", new ClientDecoder());    
					// ch.pipeline().addLast(new Encoder());  
					//ch.pipeline().addLast(new StringDecoder());
					// ch.pipeline().addLast(new StringEncoder()); 
					ch.pipeline().addLast(new HunanlongyaoClientHandler(new HunanlongyaoClient(host,port,sn),sn)); 
				}
			});
			// System.out.println("服务端连接成功..");
			//发起异步链接操作
			//ChannelFuture channelFuture = bootstrap.connect(this.host, this.port).sync();
			//channelFuture.channel().closeFuture().sync();
			bootstrap.remoteAddress(host,port); 
			bootstrap.connect().addListener(new ConnectionListener(this)); 
			System.out.println("服务端连接成功..."); // 连接完成
			// cf.channel().writeAndFlush(arg0, arg1)
			// cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
			//System.out.println("连接已关闭.."); // 关闭完成 
		}  catch (Exception e) { 
			e.printStackTrace();
		} finally{
			//关闭，释放线程资源
			//group.shutdownGracefully();
		}
		return bootstrap;


	}   
	public void run() {   
		try {
			createBootstrap(new Bootstrap(),group);  // 连接127.0.0.1/65535，并启动
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		 
	} 
}	
