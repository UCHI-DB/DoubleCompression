package DoubleCompression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class GorillaCompressor {
	
	double prev = 0.0;
	int prevLeading = 17;
	int prevTrailing = 0;
	boolean first = true;
	double[] input;
	String bitBuffer = "";
	ByteArrayOutputStream ret = new ByteArrayOutputStream();
	
	public GorillaCompressor (double[] input) {
		this.input = input;
		//prevLeading = 17;
	}
	
	public ByteBuffer compress() throws IOException {
		for(double d : input) {
			compressOne(d);
		}
		flush();
		return ByteBuffer.wrap(ret.toByteArray());
	}
	
	void compressOne(double d) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(8);
		double xored = Util.xorDoubles(d, prev);
		byte[] test = Util.toByteArray(xored);
		b.putDouble(xored);
		b.flip();
		String xorString = padXorString(String.format("0x%X", b.getLong()));
		//System.out.println(xorString);
		if(first == true) {
			addBytes(Util.toByteArray(d));
			first = false;
			prev = d;
		} else {
			if (xored == 0) {
				addBit('0');
			} else {
				addBit('1');
				int leading = leadingZeroes(xorString);
				int trailing = trailingZeroes(xorString);
				if(leading >= prevLeading && trailing == prevTrailing) {
					addBit('0');
					for(char c : meaningfulPrev(xorString).toCharArray()) {
						addInt(Character.digit(c, 16), 8);
					}
				} else {
					addBit('1');
					addInt(leading, 5);
					String meaningful = meaningful(xorString);
					//System.out.println(padLength(Integer.toBinaryString(meaningful.length()), 6));
					addInt(meaningful.length(), 6);
					//System.out.println("Meaningful: " + meaningful);
					//System.out.println(meaningful.length() % 2);
					for(char c : meaningful.toCharArray()) {
						addInt(Character.digit(c, 16), 8);
					}
				}
				setPrev(d, xorString);
			}
		}
	}
	
	void setPrev (double d, String xorString) {
		prev = d;
		prevLeading = leadingZeroes(xorString);
		prevTrailing = trailingZeroes(xorString);
	}
	
	String padXorString (String s) {
		int missing = 18 - s.length();
		String ret = "";
		for(int i = 0; i < missing; i++) {
			ret += "0";
		}
		ret += s.substring(2);
		return ret;
	}
	
	String padLength (String s, int targetLen) {
		int len = s.length();
		int toAdd = targetLen - len;
		String ret = "";
		for(int i = 0; i < toAdd; i++) {
			ret += "0";
		}
		ret += s;
		return ret;
	}
	
	int trailingZeroes (String s) {
		int ret = 0;
		int len = s.length();
		for(int i = 0; i < len; i++) {
			if(s.charAt(i)== '0') {
				ret++;
			} else {
				return ret;
			}
		}
		return ret;
	}
	
	int leadingZeroes (String s) {
		return trailingZeroes(new StringBuilder(s).reverse().toString());
	}
	
	String meaningfulPrev (String s) {
		int len = s.length();
		return s.substring(prevTrailing, len - prevLeading);
	}
	
	String meaningful (String s) {
		int len = s.length();
		return s.substring(trailingZeroes(s), len - leadingZeroes(s));
	}
	
	void flush () {
		int numToAdd = 8 - bitBuffer.length();
		if(numToAdd == 8) {
			numToAdd = 0;
		}
		for(int i = 0; i < numToAdd; i++) {
			addBit('0');
		}
		ret.write((byte) Integer.parseInt(bitBuffer,2));
//		if(bitBuffer.length() > 0) {
//			ret.write(Byte.parseByte(bitBuffer,2));
//		}
		ret.write(numToAdd);
	}
	
	void addBit (char c) {
		if(bitBuffer.length() == 8) {
			ret.write(Integer.parseInt(bitBuffer,2));
			bitBuffer = "";
		}
		bitBuffer += c;
	}
	
	void addBits (String s) {
		int len = s.length();
		for(int i = 0; i < len; i++) {
			addBit(s.charAt(i));
		}
	}
	
	void addInt (int i, int len) {
		addBits(padLength(Integer.toBinaryString(i), len));
	}
	
	void addByte (byte b) {
		addBits(byteToBits(b));
	}
	
	void addBytes (byte[] bs) {
		for(byte b : bs) {
			addByte(b);
		}
	}
	
	String byteToBits (byte b) {
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	}
}
