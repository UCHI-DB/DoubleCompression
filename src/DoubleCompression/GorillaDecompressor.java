package DoubleCompression;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class GorillaDecompressor {
	
	ByteBuffer input;
	String inputString;
	int numExtra;
	boolean first = true;
	ArrayList<Double> doubleList = new ArrayList<Double>();
	String nextBit;
	double prev;
	int prevLeading = 17;
	int prevTrailing;
	int numMeaningful;
	
	public GorillaDecompressor(ByteBuffer input) {
		this.input = input;
		constructInputString();
	}
	
	public double[] decompress() {
		while(inputString.length() != 0) {
			double decompressed = decompressOne();
			//System.out.println(decompressed);
			if(decompressed == -0.999456909) {
				//System.out.println("Hit");
			}
			doubleList.add(decompressed);
		}
		int len = doubleList.size();
		double[] ret = new double[len];
		for(int i = 0; i < len; i++) {
			ret[i] = doubleList.get(i);
		}
		return ret;
	}
	
	double decompressOne() {
		if(first) {
			String sub = nextN(64);
			first = false;
			double ret = Double.longBitsToDouble(new BigInteger(sub, 2).longValue());
			prev = ret;
			return ret;
		} else
			if(nextN(1).equals("0")) {
				return prev;
			} else {
				if(nextN(1).equals("0")){
					return generateDouble();
				} else {
					prevLeading = Integer.parseInt(nextN(5), 2);
					numMeaningful = Integer.parseInt(nextN(6), 2);
					prevTrailing = 8 - prevLeading - numMeaningful;
					return generateDouble();
				}
			}
	}
	
	String nextN (int n) {
		String ret = inputString.substring(0, n);
		inputString = inputString.substring(n);
		return ret;
	}
	
	double generateDouble () {
		String meaningful = nextN(numMeaningful * 8);
		double xor = constructXor(prevTrailing, prevLeading, meaningful);
		double ret = Util.xorDoubles(prev, xor);
		prev = ret;
		return ret;
	}
	
	double constructXor(int trailing, int leading, String meaningful) {
		byte[] array = new byte[8];
		for(int i = 0; i < numMeaningful; i++) {
			String sub = meaningful.substring(i * 8, (i+1)*8);
			array[i+trailing] = (byte) Integer.parseInt(sub, 2);
		}
		return Util.toDouble(array);
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
	
	void constructInputString () {
		
		StringBuilder inputStringBuilder = new StringBuilder("");
		int len = input.capacity();
		for(int i = 0; i < len; i++) {
			if(i == len-1) {
				numExtra = input.get();
			} else {
				inputStringBuilder.append(byteToBits(input.get()));
			}
		}
		int stringLen = inputStringBuilder.length();
		inputStringBuilder.delete(stringLen - numExtra, stringLen);
		inputString = inputStringBuilder.toString();
		input = null;
		
	}
	
	String byteToBits (byte b) {
		return Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
	}
	
}
