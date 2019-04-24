package DoubleCompression;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class FCMCompressor {
	
	double[] input;
	HashMap<Double[], Double> map;
	int count;
	
	public FCMCompressor(double[] input) {
		this.input = input;
		this.map = new HashMap<Double[], Double>();
		this.count = 0;
	}
	
	public ByteBuffer compress() {
		ByteBuffer ret = ByteBuffer.allocate(8 * input.length);
		for(double d : input) {
			ret.put(compressOne(d));
			count++;
		}
		return ret;
	}
	
	byte[] compressOne(double d) {
		Double prediction = predict();
		if(prediction == null) {
			return Util.toByteArray(d);
		} else {
			return Util.xor(Util.toByteArray(d), Util.toByteArray(prediction));
		}
	}
	
	Double[] getPrev() {
		if(count >= 3) {
			Double[] ret = {input[count-3], input[count-2], input[count-1]};
			return ret;
		} else {
			return null;
		}
	}
	
	Double predict() {
		if(count >= 3) {
			return map.get(getPrev());
		} else {
			return null;
		}
	}
	
	Double actual() {
		return input[count];
	}
	
	void update() {
		Double[] prev = getPrev();
		if(prev != null) {
			map.put(getPrev(), actual());
		}
	}
	
}
