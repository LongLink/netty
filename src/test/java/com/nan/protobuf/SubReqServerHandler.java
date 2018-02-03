package com.nan.protobuf;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
		if ("nan".equalsIgnoreCase(req.getUserName())) {
			System.out.println("Service accept client subscribe req: ["
					+ req.toString() + "]");
			ctx.writeAndFlush(resp(req.getSubReqID()));
		}
	}
	
	private SubscribeResqProto.SubscribeResq resp(int subReqID){
		SubscribeResqProto.SubscribeResq.Builder builder = SubscribeResqProto.SubscribeResq.newBuilder();
		builder.setSubReqID(subReqID);
		builder.setRespCode(0);
		builder.setDesc("Netty book order succed,3 days later send to the designated address");
		return builder.build();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
