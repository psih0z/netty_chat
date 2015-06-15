package by.kam32ar.server.handlers;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.util.CharsetUtil;

import by.kam32ar.server.utils.Log;
import by.kam32ar.server.utils.SmallFileReader;


/**
 * @author <a href="http://www.waywardmonkeys.com/">Bruce Mitchener</a>
 */
public class FlashPolicyServerHandler extends SimpleChannelUpstreamHandler {

	private static ChannelBuffer policyFile;
	private final int portNumber;
//	private static final String NEWLINE = "\r\n";

	public int getPortNumber() {
		return portNumber;
	}

	static {
		policyFile = null;
		String filePath = System.getProperty("flash_policy_file_path");
		if (null != filePath) {
			try {
				String fileContents = SmallFileReader.readSmallFile(filePath);
				policyFile = ChannelBuffers.copiedBuffer(fileContents
						.getBytes());
			} catch (IOException e) {
				Log.error(FlashPolicyServerHandler.class + " - IOException: " + e.getMessage());
			}
		}
	}

	public FlashPolicyServerHandler(int portNum) {
		this.portNumber = portNum;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		ChannelFuture f = null;

		if (null != policyFile) {
			f = e.getChannel().write(policyFile);
		} else {
			f = e.getChannel().write(this.getPolicyFileContents());
		}
		f.addListener(ChannelFutureListener.CLOSE);
	}

	public ChannelBuffer getPolicyFileContents() throws Exception {
		System.out.println("Send policy file");
		return ChannelBuffers
				.copiedBuffer(
						"<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>",
						CharsetUtil.US_ASCII);
		/*
		 * return ChannelBuffers.copiedBuffer( "<?xml version=\"1.0\"?>" +
		 * NEWLINE +
		 * "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">"
		 * + NEWLINE + "" + NEWLINE +
		 * "<!-- Policy file for xmlsocket://socks.example.com -->" + NEWLINE +
		 * "<cross-domain-policy> " + NEWLINE + "" + NEWLINE +
		 * "   <!-- This is a master socket policy file -->" + NEWLINE +
		 * "   <!-- No other socket policies on the host will be permitted -->"
		 * + NEWLINE +
		 * "   <site-control permitted-cross-domain-policies=\"master-only\"/>"
		 * + NEWLINE + "" + NEWLINE +
		 * "   <!-- Instead of setting to-ports=\"*\", administrator's can use ranges and commas -->"
		 * + NEWLINE + "   <allow-access-from domain=\"*\" to-ports=\"" +
		 * portNumber + "\" />" + NEWLINE + "" + NEWLINE +
		 * "</cross-domain-policy>" + NEWLINE, CharsetUtil.US_ASCII);
		 */
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		if (e.getCause() instanceof ReadTimeoutException) {
			e.getChannel().close();
		} else {
			e.getCause().printStackTrace();
			e.getChannel().close();
		}
	}

}
