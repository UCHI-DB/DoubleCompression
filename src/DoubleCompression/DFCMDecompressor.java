package DoubleCompression;

import java.nio.ByteBuffer;

public class DFCMDecompressor {

	ByteBuffer byteInput;
	double[] doubleInput;
	
	public DFCMDecompressor (ByteBuffer byteInput, double[] doubleInput) {
		this.byteInput = byteInput;
		this.doubleInput = doubleInput;
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
		double[] decompressed = new FCMDecompressor(byteInput,null).decompress();
		return convertBack(decompressed);
	}
	
	public double[] decompressFromGorilla() {
		double[] decompressed = new FCMDecompressor(null,doubleInput).decompressFromGorilla();
		return convertBack(decompressed);
	}
	
}
