package by.kam32ar.server.common;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class Encoder extends OneToOneEncoder {
	
	public static ChannelBuffer encodeMessage(Envelope message) throws IllegalArgumentException {

        if ((message.getType() == null) || (message.getType() == Type.UNKNOWN)) {
            throw new IllegalArgumentException("Message type cannot be null or UNKNOWN");
        }

        if ((message.getPayload() == null) || (message.getPayload().length == 0)) {
            throw new IllegalArgumentException("Message payload cannot be null or empty");
        }

        // type(1b) + requesttype(1b) + payload length(4b) + payload(nb)
        int size = 1 + 1 + 4 + message.getPayload().length;
//Log.info(message.getRequestType().name());
        ChannelBuffer buffer = ChannelBuffers.buffer(size);
        buffer.writeByte(message.getType().getByteValue());
        buffer.writeByte(message.getRequestType().getByteValue());
        buffer.writeInt(message.getPayload().length);
        buffer.writeBytes(message.getPayload());

        return buffer;
    }
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (msg instanceof Envelope) {
            return encodeMessage((Envelope) msg);
        } else {
            return msg;
        }
	}

}
