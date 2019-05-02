package DoubleCompression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class GorillaCompressor {
	
	double prev = 0.0;
	int prevLeading = 17;
	int prevTrailing = 0;
	boolean first = true;
	double[] input;
	byte buf = 0;
	byte posInBuf = 7;
	ByteArrayOutputStream ret = new ByteArrayOutputStream();
	
	public GorillaCompressor (double[] input) {
		this.input = input;
	}
	
	public ByteBuffer compress() throws IOException {
		for(double d : input) {
			//System.out.println("Compressing " + d);
//			if(d == -0.999456909) {
//				System.out.println("Hit");
//			}
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
				String meaningful = meaningful(xorString);
				int meaningfulLen = meaningful.length();
				if(trailing % 2 == 1) {
					meaningful = "0" + meaningful;
					trailing -= 1;
				} 
				if(leading % 2 == 1) {
					meaningful += 0;
					leading -= 1;
				}
				meaningfulLen = meaningful.length() / 2;
				leading /= 2;
				trailing /= 2;
				if(leading == prevLeading && trailing == prevTrailing) {
					addBit('0');
					for(int i = 0; i < meaningfulLen; i++) {
						String sub = meaningful.substring(i*2,i*2+2);
						addInt(Integer.parseInt(sub, 16), 8);
					}
				} else {
					addBit('1');
					addInt(leading, 5);
					addInt(meaningfulLen, 6);
					for(int i = 0; i < meaningfulLen; i++) {
						String sub = meaningful.substring(i*2,i*2+2);
						addInt(Integer.parseInt(sub, 16), 8);
					}
				}
				setPrev(d, leading, trailing);
			}
		}
	}
	
	void setPrev (double d, int leading, int trailing) {
		prev = d;
		prevLeading = leading;
		prevTrailing = trailing;
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
		char[] padArray = new char[toAdd];
		Arrays.fill(padArray, '0');
		String padString = new String(padArray);
//		String ret = "";
//		for(int i = 0; i < toAdd; i++) {
//			ret += "0";
//		}
//		ret += s;
		
		return padString + s;
	}
	
	int trailingZeroes (String s) {
		int ret = 0;
		int len = s.length();
		for(int i = 0; i < len; i++) {
			if(s.charAt(i) == '0') {
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
		int numToAdd = posInBuf + 1;
		if(numToAdd == 8) {
			numToAdd = 0;
		}
		ret.write(buf);
		ret.write(numToAdd);
	}
	
	void addBit (char c) {
		if(posInBuf == -1) {
			ret.write(buf);
			buf = 0;
			posInBuf = 7;
		}
		if(c == '1') {
			buf |= 1 << posInBuf;
		}
		posInBuf--;
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
		for(int i = 7; i >= 0; i--) {
			int bit = ((b & (1 << i)) >> i);
			char asChar = (bit == 0) ? '0' : '1';
			addBit(asChar);
		}
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
