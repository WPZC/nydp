package com.example.nydp.utils;

import com.example.nydp.netty.server.TimeServer;

public class ThreadStratNetty implements  Runnable {
    @Override
    public void run() {
        try {
            new TimeServer().bind(5555);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
