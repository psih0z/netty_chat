package by.kam32ar.server.common;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

import by.kam32ar.server.utils.Log;


public class Decoder extends ReplayingDecoder<DecodingState> {

	private Envelope message;

	public Decoder() {
		this.reset();
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer, DecodingState state) throws Exception {

		switch (state) {
		case TYPE:
			this.message.setType(Type.fromByte((buffer.readByte())));
			this.checkpoint(DecodingState.REQUEST_TYPE);
		case REQUEST_TYPE:
			this.message
					.setRequestType(RequestType.fromByte(buffer.readByte()));
			this.checkpoint(DecodingState.PAYLOAD_LENGTH);
		case PAYLOAD_LENGTH:
			int size = buffer.readInt();
			if (size <= 0) {
				Log.error(Decoder.class + " - Invalid content size");
				throw new Exception("Invalid content size");
			}
			byte[] content = new byte[size];
			this.message.setPayload(content);
			this.checkpoint(DecodingState.PAYLOAD);
		case PAYLOAD:
			buffer.readBytes(this.message.getPayload(), 0,
					this.message.getPayload().length);
			try {
				return this.message;
			} finally {
				this.reset();
			}
		default:
			Log.error(Decoder.class + " - Unknown decoding state: " + state);
			throw new Exception("Unknown decoding state: " + state);
		}
	}

	protected void reset() {
		this.checkpoint(DecodingState.TYPE);
		this.message = new Envelope();
	}

}
