package com.example.nydp.netty.client;

import java.util.concurrent.TimeUnit; 
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;  
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
 


public class ConnectionListener implements ChannelFutureListener {  
	private static final Log log = LogFactory.getLog(ConnectionListener.class);
	  private HunanlongyaoClient client;  
	     
	  public ConnectionListener(HunanlongyaoClient client) {  
		  this.client = client;  
 	  }  
	     
	  public void operationComplete(ChannelFuture channelFuture) throws Exception {  
 	    if (!channelFuture.isSuccess()) {  
 	    	log.info("Reconnect");  
 	      final EventLoop loop = channelFuture.channel().eventLoop();  
 	      loop.schedule(new Runnable() {  
	        public void run() {   
	          try {
				client.createBootstrap(new Bootstrap(), loop);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
 	        }   
	      }, 1L, TimeUnit.SECONDS);  
 	    }  
 	  }  
 	}