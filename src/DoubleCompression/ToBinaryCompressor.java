package DoubleCompression;

import java.nio.ByteBuffer;

public class ToBinaryCompressor {
	
	double[] input;
	
	public ToBinaryCompressor (double[] input) {
		this.input = input;
	}
	
	public ByteBuffer compress() {
		ByteBuffer ret = ByteBuffer.allocate(8 * input.length);
		for(double d : input) {
			ret.put(Util.toByteArray(d));
		}
		return ret;
	}

}
