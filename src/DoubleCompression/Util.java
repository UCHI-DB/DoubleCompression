package DoubleCompression;

import java.nio.ByteBuffer;

public class Util {

	public static byte[] toByteArray(double value) {
	    byte[] bytes = new byte[8];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    return bytes;
	}

	public static double toDouble(byte[] bytes) {
	    return ByteBuffer.wrap(bytes).getDouble();
	}
	
	public static byte[] xor(byte[] b1, byte[] b2) {
		int len = b1.length;
		byte[] ret = new byte[len];
		for(int i = 0; i < len; i++) {
			ret[i] = (byte) (b1[i] ^ b2[i]);
		}
		return ret;
	}

}
