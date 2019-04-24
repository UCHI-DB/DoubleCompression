package DoubleCompression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class GorillaCompressor {
	
	double prev = 12.0;
	int prevLeading = 0;
	int prevTrailing = 0;
	boolean first = true;
	double[] input;
	ByteArrayOutputStream ret = new ByteArrayOutputStream();
	
	public GorillaCompressor (double[] input) {
		this.input = input;
	}
	
	public ByteBuffer compress() throws IOException {
		for(double d : input) {
			compressOne(d);
		}
		return ByteBuffer.wrap(ret.toByteArray());
	}
	
	void compressOne(double d) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(8);
		double xored = Util.xorDoubles(d, prev);
		b.putDouble(xored);
		b.flip();
		String xorString = padXorString(String.format("0x%X", b.getLong()));
		//System.out.println(xorString);
		if(first == true) {
			ret.write(Util.toByteArray(d));
			first = false;
		} else {
			if (xored == 0) {
				ret.write(0);
			} else {
				ret.write(1);
				int leading = leadingZeroes(xorString);
				int trailing = trailingZeroes(xorString);
				if(leading >= prevLeading && trailing == prevTrailing) {
					ret.write(0);
					for(char c : meaningfulPrev(xorString).toCharArray()) {
						ret.write(Character.digit(c, 16));
					}
				} else {
					ret.write(1);
					ret.write(leading);
					String meaningful = meaningful(xorString);
					ret.write(meaningful.length());
					for(char c : meaningful.toCharArray()) {
						ret.write(Character.digit(c, 16));
					}
				}
				prev = d;
				prevLeading = leading;
				prevTrailing = trailing;
			}
		}
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
		return s.substring(prevLeading, len - prevTrailing);
	}
	
	String meaningful (String s) {
		int len = s.length();
		return s.substring(trailingZeroes(s), len - trailingZeroes(s));
	}
}
