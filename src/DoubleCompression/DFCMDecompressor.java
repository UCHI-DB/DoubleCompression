package DoubleCompression;

import java.nio.ByteBuffer;

public class DFCMDecompressor {

	ByteBuffer input;
	
	public DFCMDecompressor (ByteBuffer input) {
		this.input = input;
	}
	
	double[] convertBack (double[] input) {
		double prev = 0;
		int len = input.length;
		double[] ret = new double[len];
		for(int i = 0; i < len; i++) {
			ret[i] = input[i] + prev;
			prev = ret[i];
		}
		
		return ret;
	}
	
	public double[] decompress() {
		double[] decompressed = new FCMDecompressor(input).decompress();
		return convertBack(decompressed);
	}
	
}
