package DoubleCompression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class GorillaCompressor {
	
	double prev = 0.0;
	byte prevLeading = 9;
	byte prevTrailing = 9;
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
//			if(d == -1.2930638) {
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
				byte leading = (byte) leadingZeroes(arr);
				byte trailing = (byte) trailingZeroes(arr);
				if(leading >= prevLeading && trailing >= prevTrailing) {
					addBit('0');
					byte[] meaningful = meaningful(arr, prevLeading, prevTrailing);
					addBytes(meaningful);
					prev = d;
				} else {
					addBit('1');
					addByte(leading, 5);
					byte[] meaningful = meaningful(arr, leading, trailing);
					byte meaningfulLen = (byte) meaningful.length;
					addByte(meaningfulLen, 6);
					addBytes(meaningful);
					setPrev(d, leading, trailing);
				}
			}
		}
	}
	
	void setPrev (double d, byte leading, byte trailing) {
		prev = d;
		prevLeading = leading;
		prevTrailing = trailing;
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
	
	void addByte (byte b) {
		addByte(b, 8);
	}
	
	void addByte (byte b, int n) {
		n--;
		for(int i = n; i >= 0; i--) {
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
}
