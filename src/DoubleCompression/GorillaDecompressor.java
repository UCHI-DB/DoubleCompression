package DoubleCompression;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class GorillaDecompressor {
	
	ByteBuffer input;
	String inputString = "";
	int numExtra;
	boolean first = true;
	ArrayList<Double> doubleList = new ArrayList<Double>();
	String nextBit;
	double prev;
	int prevLeading;
	int prevTrailing;
	int numMeaningful;
	
	public GorillaDecompressor(ByteBuffer input) {
		this.input = input;
		constructInputString();
	}
	
	public double[] decompress() {
		//System.out.println(numExtra + " extra");
		//System.out.println(inputString.length());
		//System.out.println(inputString);
		while(inputString.length() != 0) {
			double decompressed = decompressOne();
			System.out.println(decompressed);
			if(decompressed == 2.0537059) {
				System.out.println("Hit");
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
		//System.out.println("next: " + inputString);
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
					//numMeaningful = 16 - prevLeading - prevTrailing;
					//String meaningful = nextN(numMeaningful * 8);
					return generateDouble();
				} else {
					prevLeading = Integer.parseInt(nextN(5), 2);
					numMeaningful = Integer.parseInt(nextN(6), 2);
					prevTrailing = 16 - prevLeading - numMeaningful;
					return generateDouble();
				}
			}
	}
	
	String nextN (int n) {
		String ret = inputString.substring(0, n);
		inputString = inputString.substring(n);
		//System.out.println("Next " + n + ": " + ret);
		//System.out.println("Remaining: " + inputString);
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
		byte[] array = new byte[16];
		for(int i = 0; i < numMeaningful; i++) {
			String sub = meaningful.substring(i * 8, (i+1)*8);
			//System.out.println(Long.parseLong(sub, 2));
			array[i+trailing] = (byte) Integer.parseInt(sub, 2);
		}
		byte[] eightArray = new byte[8];
		for(int i = 0; i < 8; i++) {
			eightArray[i] = (byte) (array[i*2] * 16 + array[i*2+1]);
		}
		return Util.toDouble(eightArray);
	}
	
	String signExtend(String str){
        //TODO add bounds checking
        int n=32-str.length();
        char[] sign_ext = new char[n];
        Arrays.fill(sign_ext, str.charAt(0));

        return new String(sign_ext)+str;
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
		
		int len = input.capacity();
		for(int i = 0; i < len; i++) {
			if(i == len-1) {
				numExtra = input.get();
			} else {
				inputString += byteToBits(input.get());
			}
		}
		//System.out.println(inputString.length());
		//fixNumExtra();
		inputString = inputString.substring(0, inputString.length() - numExtra);
		//System.out.println(inputString.length());
		input = null;
		
	}
	
	void fixNumExtra () {
		String reversed = new StringBuilder(inputString).reverse().toString();
		int i = 0;
		while(reversed.charAt(i) == '0') {
			i++;
		}
		numExtra = Math.min(i, numExtra);
	}
	
	String byteToBits (byte b) {
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	}
	
}
