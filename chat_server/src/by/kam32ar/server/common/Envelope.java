package by.kam32ar.server.common;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

@SuppressWarnings("serial")
public class Envelope implements Serializable {
	
    private Type type;
    private RequestType requestType;
    private byte[] payload;

    public Envelope() {
    }

    public Envelope(Type type) throws UnsupportedEncodingException {
    	this.type = type;
    	this.requestType = RequestType.UNKNOWN;
    	this.payload = "".getBytes("UTF-8");
    }
    
    public Envelope(Type type, RequestType requestType, byte[] payload) {
        this.type = type;
        this.requestType = requestType;
        this.payload = payload;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    public RequestType getRequestType() {
		return requestType;
	}
    
    public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
    
    public void setPayloadFromString(String payload) {
    	if (payload != null) {
    		this.payload = payload.getBytes(Charset.forName("UTF-8"));
    	}
    }

    public String payloadToString() {
    	return new String(this.payload, Charset.forName("UTF-8"));
    }
    
    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Envelope{")
                .append("type=").append(type)
                .append(", payload=").append(payload == null ? null : payload.length + "bytes")
                .append('}').toString();
    }
	
}
