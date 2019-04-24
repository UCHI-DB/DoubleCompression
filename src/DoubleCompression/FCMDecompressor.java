package DoubleCompression;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class FCMDecompressor {
	
	ByteBuffer input;
	double[] output;
	HashMap<Double[], Double> map;
	int count;
	double actual;
	
	public FCMDecompressor(ByteBuffer input) {
		this.input = input;
		if(this.input.position() != 0) {
			this.input.flip();
		}
		this.output = new double[input.capacity()/8];
		this.map = new HashMap<Double[], Double>();
		this.count = 0;
	}
	
	public double[] decompress() {
		while(input.position() < input.capacity()) {
			output[count] = decompressOne();
			count++;
		}
		return output;
	}
	
	double decompressOne() {
		actual = input.getDouble();
		Double prediction = predict();
		
		return Util.toDouble(
				Util.xor(Util.toByteArray(actual), Util.toByteArray(prediction)));
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
