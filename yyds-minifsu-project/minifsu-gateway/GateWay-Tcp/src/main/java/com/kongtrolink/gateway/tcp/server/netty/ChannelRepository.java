package com.kongtrolink.gateway.tcp.server.netty;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Channel Manager
 * @author Ke Shanqiang
 *
 */
public class ChannelRepository {

	public final static Map<String, ChannelHandlerContext> channelCache = new ConcurrentHashMap<>();

}
