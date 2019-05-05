package DoubleCompression;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class FCMDecompressor {
	
	ByteBuffer byteInput;
	double[] doubleInput;
	double[] output;
	HashMap<Double[], Double> map;
	int count;
	double actual;
	int len;
	
	public FCMDecompressor(ByteBuffer byteInput, double[] doubleInput) {
		if(byteInput != null) {
			this.byteInput = byteInput;
			if(this.byteInput.position() != 0) {
				this.byteInput.flip();
			}
			this.output = new double[byteInput.capacity()/8];
			this.map = new HashMap<Double[], Double>();
			this.count = 0;
		}
		if(doubleInput != null) {
			this.doubleInput = doubleInput;
			this.len = doubleInput.length;
			this.output = new double[len];
		}
	}
	
	public double[] decompress() {
		while(byteInput.position() < byteInput.capacity()) {
			output[count] = decompressOne();
			count++;
		}
		return output;
	}
	
	double decompressOne() {
		actual = byteInput.getDouble();
		Double prediction = predict();
		return Util.xorDoubles(actual, prediction);
	}
	
	public double[] decompressFromGorilla() {
		for(int i = 0; i < len; i++) {
			output[i] = decompressOneFromGorilla(doubleInput[i]);
		}
		return output;
	}
	
	double decompressOneFromGorilla (double input) {
		Double prediction = predict();
		return Util.xorDoubles(input, prediction);

	}
	
	Double[] getPrev() {
		if(count >= 3) {
			Double[] ret = {output[count-3], output[count-2], output[count-1]};
			return ret;
		} else {
			return null;
		}
	}
	
	Double predict() {
		if(count >= 3) {
			Double ret = map.get(getPrev());
			if (ret != null) {
				return ret;
			}
		}
		return (double) 0;
	}
	
	void update() {
		Double[] prev = getPrev();
		if(prev != null) {
			map.put(getPrev(), actual);
		}
	}
	
}
