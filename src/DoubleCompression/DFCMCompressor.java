package DoubleCompression;

import java.nio.ByteBuffer;

public class DFCMCompressor {
	
	double[] input;
	
	public DFCMCompressor(double[] input) {
		this.input = input;
	}
	
	double[] convert() {
		int len = input.length;
		double[] output = new double[len];
		for(int i = 0; i < len; i++) {
			if(i == 0) {
				output[i] = input[i];
			} else {
				output[i] = input[i] - input[i-1];
			}
		}
		return output;
	}
	
	public ByteBuffer compress() {
		return new FCMCompressor(convert()).compress();
	}
	
}
