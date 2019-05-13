package DoubleCompression;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class GorillaDecompressor {
	
	byte[] inputArray;
	int inputByteLen;
	int inputBitLen;
	int pos = 0;
	boolean first = true;
	ArrayList<Double> doubleList = new ArrayList<Double>();
	String nextBit;
	double prev;
	int prevLeading = 9;
	int prevTrailing;
	int numMeaningful;
	boolean frontCoding = true;
	
	public GorillaDecompressor(ByteBuffer input, boolean frontCoding) {
		inputByteLen = input.capacity();
		inputBitLen = inputByteLen * 8;
		inputArray = new byte[inputByteLen];
		input.get(inputArray);
		int numExtra = inputArray[inputByteLen-1];
		inputBitLen -= numExtra + 8;
		this.frontCoding = frontCoding;
	}
	
	public double[] decompress() {
		while(pos < inputBitLen) {
			double decompressed = decompressOne();
//			System.out.println("Decompressed " + decompressed);
//			if(decompressed == -1.4457912) {
//				System.out.println("Hit");
//			}
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
			double ret = nextN(64).getDouble();
			first = false;
			prev = ret;
			return ret;
		} else
			if(nextBit() == 0) {
				return prev;
			} else {
				if(frontCoding) {
					if(nextBit() == 0){
						return generateDouble();
					} else {
						prevLeading = nextN(5).get();
						numMeaningful = nextN(6).get();
						prevTrailing = 8 - prevLeading - numMeaningful;
						return generateDouble();
					}
				} else {
					prevLeading = nextN(5).get();
					numMeaningful = nextN(6).get();
					prevTrailing = 8 - prevLeading - numMeaningful;
					return generateDouble();
				}
			}
	}
	
	int currIndex() {
		return (int) Math.floor(pos/8);
	}
	
	int currOffset() {
		return pos - (currIndex() * 8);
	}
	
	int currShiftAmount() {
		return 7 - currOffset();
	}
	
	int nextBit() {
		byte b = inputArray[currIndex()];
		int shift = currShiftAmount();
		pos++;
		return (b & 1 << shift) >> shift;
	}
	
	ByteBuffer nextN (int n) {
		ByteBuffer ret = ByteBuffer.allocate((int) Math.ceil(n/8.0));
		byte buf = 0;
		int posInBuf = 7;
		for(int i = 0; i < n; i++) {
			if(posInBuf == -1) {
				ret.put(buf);
				buf = 0;
				posInBuf = 7;
			}
			buf |= nextBit() << posInBuf;
			posInBuf--;
		}
		int numLeft = posInBuf + 1;
		buf = (byte) (buf >> numLeft);
		ret.put(buf);
		ret.flip();
		return ret;
	}
	
	double generateDouble () {
		ByteBuffer meaningful = nextN(numMeaningful * 8);
		double xor = constructXor(prevTrailing, prevLeading, meaningful);
		double ret = Util.xorDoubles(prev, xor);
		prev = ret;
		return ret;
	}
	
	double constructXor(int trailing, int leading, ByteBuffer meaningful) {
		byte[] array = new byte[8];
		meaningful.get(array, trailing, numMeaningful);
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
	
}
