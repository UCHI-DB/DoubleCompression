package DoubleCompression;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;

public class SprintzDecompressor {
	
	ByteBuffer input;
	byte[] inputArray;
	int inputByteLen;
	int inputBitLen;
	int pos = 0;
	double prev = 0;
	byte[] block = new byte[64];
	ArrayList<Double> doubleList = new ArrayList<Double>();
	int numBlocksDecompressed = 0;
	
	public SprintzDecompressor (ByteBuffer input) {
		this.input = input;
		this.inputByteLen = input.capacity();
		this.inputBitLen = inputByteLen * 8;
		this.inputArray = new byte[inputByteLen];
		input.get(inputArray);
		int numExtra = inputArray[inputByteLen-1];
		inputBitLen -= numExtra + 8;
	}
	
	public double[] decompress() {
		
		int targetLen = nextN(32).getInt();
		while(pos < inputBitLen) {
			byte nBits = nextN(7).get();
			if(nBits == 0) {
				int numZeroBlocks = nextN(7).get();
				for(int i = 0; i < numZeroBlocks * 8; i++) {
					doubleList.add(prev);
				}
			} else {
				numBlocksDecompressed++;
				decompressBlock(nBits);
			}
		}
		
		int len = doubleList.size();
		double[] ret = new double[targetLen];
		for(int i = 0; i < targetLen; i++) {
			ret[i] = doubleList.get(i);
		}
		return ret;
		
	}
	
	void decompressBlock (int nBits) {
		
		int numToGet = Math.min(8, (inputBitLen - pos) / nBits);
		for(int i = 0; i < numToGet; i++) {
			byte[] arr = new byte[8];
			arr[0] |= nextN(1).get() << 7;
			int numBytes = (int) Math.floor((nBits - 1) / 8.0);
			int numHangBits = (nBits - 1) - (numBytes * 8);
			arr[7 - numBytes] |= nextN(numHangBits).get();
			for(int j = 0; j < numBytes; j++) {
				arr[8 - numBytes + j] = nextN(8).get();
			}
			double ret = Util.toDouble(arr);
			ret += prev;
			prev = ret;
			doubleList.add(ret);
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
		if(numLeft != 0) {
			buf &= (byte) Math.pow(2, (8-numLeft)) - 1;
		}
		ret.put(buf);
		ret.flip();
		return ret;
	}

}
