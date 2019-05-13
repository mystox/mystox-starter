package com.kongtrolink.gateway.tcp.server;

import com.kongtrolink.gateway.tcp.server.execute.SendTool;
import com.kongtrolink.gateway.tcp.server.netty.ChannelRepository;
import com.kongtrolink.gateway.tcp.server.netty.ServerDecoder;
import com.kongtrolink.gateway.tcp.server.netty.ServiceHandle;
import com.kongtrolink.gateway.tcp.server.netty.TCPServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Shanqiang Ke
 * @version 1.0.0
 * @blog http://nosqlcoco.cnblogs.com
 * @since 2016-10-15
 */
@SpringBootApplication(scanBasePackages = "com.kongtrolink")
@ComponentScan(value = "com.kongtrolink")
@EnableAutoConfiguration
public class Application{

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        TCPServer tcpServer = context.getBean(TCPServer.class);
        tcpServer.start();
	}

	@Value("${netty.tcp.port}")
    private int tcpPort;

    @Value("${netty.boss.thread}")
    private int bossCount;

    @Value("${netty.worker.thread}")
    private int workerCount;

    @Value("${netty.so.keepalive}")
    private boolean keepAlive;

    @Value("${netty.so.backlog}")
    private int backlog;
    @Autowired
    private SendTool sendTool;

    @SuppressWarnings("unchecked")
    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("ServerDecoder", new ServerDecoder());
                        ch.pipeline().addLast("ServiceHandle", new ServiceHandle(sendTool));
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_SNDBUF, Integer.MAX_VALUE)
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024 * 1024 * 64));

        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
        for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
            b.option(option, tcpChannelOptions.get(option));
        }
        return b;
    }


    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
        options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
        options.put(ChannelOption.SO_BACKLOG, backlog);
        options.put(ChannelOption.SO_SNDBUF, Integer.MAX_VALUE);
        options.put(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024 * 1024 * 64));
        return options;
    }

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossCount);
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount);
    }

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort() {
        return new InetSocketAddress(tcpPort);
    }

    @Bean(name = "channelRepository")
    public ChannelRepository channelRepository() {
        return new ChannelRepository();
    }
}