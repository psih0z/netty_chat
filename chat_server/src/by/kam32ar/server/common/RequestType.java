package by.kam32ar.server.common;

public enum RequestType {

	HANDSHAKE ((byte) 0x01), 		//"рукопожатие" 
    SEND_CHAT((byte) 0x02),			// сообщение в чат
    GET_DATA((byte) 0x03),
    CREATE_ROOM((byte) 0x04),
    ENTER_ROOM((byte) 0x05),
    EXIT_ROOM((byte) 0x06),
    SEND_PRIVATE_MSG((byte) 0x07),
    UNKNOWN((byte) 0x00);
	
    private final byte b;

    private RequestType(byte b) {
        this.b = b;
    }

    public static RequestType fromByte(byte b) {
        for (RequestType code : values()) {
            if (code.b == b) {
                return code;
            }
        }

        return UNKNOWN;
    }
    
    public byte getByteValue() {
    	return b;
    }
	
}
