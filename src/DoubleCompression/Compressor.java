package DoubleCompression;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Compressor {

	static ByteBuffer genericFCMCompressFile(String f, boolean DFCM, double[] series)
			throws IOException {
		
		if(series == null) {
			series = Reader.readRaw(f);
		}
		ByteBuffer compressed;
		
		if(DFCM) {
			compressed = new DFCMCompressor(series).compress();
		} else {
			compressed = new FCMCompressor(series).compress();
		}
		
		return compressed;
		
	}

	static ByteBuffer gorillaCompressFile(String f, double[] series)
			throws IOException {
		
		if(series == null) {
			series = Reader.readRaw(f);
		}
		return new GorillaCompressor(series).compress();
		
	}

	static ByteBuffer directCompress(double[] series, String method)
			throws IOException {
		switch(method) {
			case "FCM":
				return genericFCMCompressFile("", false, series);
			case "DFCM":
				return genericFCMCompressFile("", true, series);
			case "Gorilla":
				return gorillaCompressFile("", series);
			default:
				return null;
		}
	}

	static ByteBuffer compressFile(String f, String method) 
			throws IOException {
		
		f = Util.rawPathify(f);
		switch(method) {
			case "FCM":
				return genericFCMCompressFile(f, false, null);
			case "DFCM":
				return genericFCMCompressFile(f, true, null);
			case "Gorilla":
				return gorillaCompressFile(f, null);
			default:
				return null;
		}
		
	}

	static double[] genericFCMDecompressFile(String f, boolean DFCM,
			ByteBuffer compressed)
			throws IOException {
		
		if(compressed == null) {
			compressed = Reader.readCompressed(f);
		}
		
		double[] decompressed;
		if(DFCM) {
			decompressed = new DFCMDecompressor(compressed).decompress();
		} else {
			decompressed = new FCMDecompressor(compressed).decompress();
		}
		
		return decompressed;
		
	}

	static double[] gorillaDecompressFile(String f, ByteBuffer compressed)
			throws IOException {
		
		if(compressed == null) {
			compressed = Reader.readCompressed(f);
		}
		return new GorillaDecompressor(compressed).decompress();
		
	}

	static double[] directDecompress(ByteBuffer compressed, String method) 
			throws IOException {
		
		switch(method) {
			case "FCM":
				return genericFCMDecompressFile("", false, compressed);
			case "DFCM":
				return genericFCMDecompressFile("", true, compressed);
			case "Gorilla":
				return gorillaDecompressFile("", compressed);
			default:
				return null;
		}
		
	}

	static double[] decompressFile(String f, String method) 
			throws IOException {
		
		f = Util.compressedPathify(f);
		switch(method) {
			case "FCM":
				return genericFCMDecompressFile(f, false, null);
			case "DFCM":
				return genericFCMDecompressFile(f, true, null);
			case "Gorilla":
				return gorillaDecompressFile(f, null);
			default:
				return null;
		}
		
	}

}
