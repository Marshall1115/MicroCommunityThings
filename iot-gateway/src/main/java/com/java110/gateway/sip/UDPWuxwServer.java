package com.java110.gateway.sip;

import com.java110.gateway.sip.handler.UDPHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;

public class UDPWuxwServer {
    private Bootstrap bootstrap = null;
    private EventLoopGroup workerGroup = null;
    private Channel serverChannel;

    public void startServer(){
        new Thread(() -> {
            workerGroup = new NioEventLoopGroup();
            try {
                bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)//
                        .channel(NioDatagramChannel.class) //
                        .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                        .handler(new ChannelInitializer<NioDatagramChannel>() { //
                            @Override
                            public void initChannel(NioDatagramChannel ch) throws Exception {
                                ChannelPipeline entries = ch.pipeline().addLast(new UDPHandler(1, true, new UDPServer(), 20000));
                            }
                        });
                ChannelFuture channelFuture = bootstrap.bind(20000).sync();

                serverChannel = channelFuture.channel();
                System.out.println("udp 启动完成");
                // 阻塞至channel关闭
                serverChannel.closeFuture().sync();
            }catch (Exception e){
                e.printStackTrace();
            }
        }, "123123sdfsf").start();
    }

    public void stopServer() {
        if(serverChannel != null){
            try {
                serverChannel.close().sync();
                serverChannel = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            Future<?> future = this.workerGroup.shutdownGracefully().await();
            if (!future.isSuccess()) {

            }
            System.out.println("udp 停止完成"+future.isSuccess());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
