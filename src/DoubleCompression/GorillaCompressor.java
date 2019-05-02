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
		double xored = Util.xorDoubles(d, prev);
		byte[] arr = Util.toByteArray(xored);
		if(first == true) {
			addBytes(Util.toByteArray(d));
			first = false;
			prev = d;
		} else {
			if (xored == 0) {
				addBit('0');
			} else {
				addBit('1');
				int leading = leadingZeroes(arr);
				int trailing = trailingZeroes(arr);
				byte[] meaningful = meaningful(arr, leading, trailing);
				if(leading == prevLeading && trailing == prevTrailing) {
					addBit('0');
					for(byte b : meaningful) {
						addByte(b);
					}
				} else {
					addBit('1');
					addInt(leading, 5);
					int meaningfulLen = meaningful.length;
					addInt(meaningfulLen, 6);
					for(byte b : meaningful) {
						addByte(b);
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
	
	String padLength (String s, int targetLen) {
		int len = s.length();
		int toAdd = targetLen - len;
		char[] padArray = new char[toAdd];
		Arrays.fill(padArray, '0');
		String padString = new String(padArray);
		return padString + s;
	}
	
	int trailingZeroes (byte[] bs) {
		int len = bs.length;
		int ret = 0;
		for(int i = 0; i < len; i++) {
			if(bs[i] == 0) {
				ret++;
			} else {
				return ret;
			}
		}
		return ret;
	}
	
	int leadingZeroes (byte[] bs) {
		int len = bs.length - 1;
		int ret = 0;
		for(int i = len; i >= 0; i--) {
			if(bs[i] == 0) {
				ret++;
			} else {
				return ret;
			}
		}
		return ret;
	}
	
	byte[] meaningful (byte[] input, int leading, int trailing) {
		return Arrays.copyOfRange(input, trailing, input.length - leading);
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
