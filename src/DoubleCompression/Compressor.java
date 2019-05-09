package DoubleCompression;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Compressor {

	static ByteBuffer genericFCMCompressFile(String f, boolean DFCM, boolean Gorilla, double[] series)
			throws IOException {
		
		if(series == null) {
			series = Reader.readRaw(f);
		}
		ByteBuffer compressed;
		
		if(DFCM) {
			if(Gorilla) {
				compressed = new GorillaCompressor(new DFCMCompressor(series).compressForGorilla()).compress();
			} else {
				compressed = new DFCMCompressor(series).compress();
			}
		} else {
			if(Gorilla) {
				compressed = new GorillaCompressor(new FCMCompressor(series).compressForGorilla()).compress();
			} else {
				compressed = new FCMCompressor(series).compress();
			}
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
	
	static ByteBuffer sprintzCompressFile(String f, double[] series)
			throws IOException {
		
		if(series == null) {
			series = Reader.readRaw(f);
		}
		return new SprintzCompressor(series).compress();
		
	}

	static ByteBuffer directCompress(double[] series, String method)
			throws IOException {
		switch(method) {
			case "FCM":
				return genericFCMCompressFile("", false, false, series);
			case "DFCM":
				return genericFCMCompressFile("", true, false, series);
			case "Gorilla":
				return gorillaCompressFile("", series);
			case "Sprintz":
				return sprintzCompressFile("", series);
			case "FCMGorilla":
				return genericFCMCompressFile("", false, true, series);
			case "DFCMGorilla":
				return genericFCMCompressFile("", true, true, series);
			default:
				return null;
		}
	}

	static ByteBuffer compressFile(String f, String method) 
			throws IOException {
		
		f = Util.rawPathify(f);
		switch(method) {
			case "FCM":
				return genericFCMCompressFile(f, false, false, null);
			case "DFCM":
				return genericFCMCompressFile(f, true, false, null);
			case "Gorilla":
				return gorillaCompressFile(f, null);
			case "Sprintz":
				return sprintzCompressFile(f, null);
			case "FCMGorilla":
				return genericFCMCompressFile(f, false, true, null);
			case "DFCMGorilla":
				return genericFCMCompressFile(f, true, true, null);
			default:
				return null;
		}
		
	}

	static double[] genericFCMDecompressFile(String f, boolean DFCM,
			boolean Gorilla, ByteBuffer compressed)
			throws IOException {
		
		if(compressed == null) {
			compressed = Reader.readCompressed(f);
		}
		
		double[] decompressed;
		if(DFCM) {
			if(Gorilla) {
				decompressed = new DFCMDecompressor(null,new GorillaDecompressor(compressed).decompress()).decompressFromGorilla();
			} else {
				decompressed = new DFCMDecompressor(compressed,null).decompress();
			}
		} else {
			if(Gorilla) {
				decompressed = new FCMDecompressor(null,new GorillaDecompressor(compressed).decompress()).decompressFromGorilla();
			} else {
				decompressed = new FCMDecompressor(compressed,null).decompress();
			}
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
	
	static double[] sprintzDecompressFile(String f, ByteBuffer compressed)
			throws IOException {
		
		if(compressed == null) {
			compressed = Reader.readCompressed(f);
		}
		return new SprintzDecompressor(compressed).decompress();
		
	}

	static double[] directDecompress(ByteBuffer compressed, String method) 
			throws IOException {
		
		switch(method) {
			case "FCM":
				return genericFCMDecompressFile("", false, false, compressed);
			case "DFCM":
				return genericFCMDecompressFile("", true, false, compressed);
			case "Gorilla":
				return gorillaDecompressFile("", compressed);
			case "Sprintz":
				return sprintzDecompressFile("", compressed);
			case "FCMGorilla":
				return genericFCMDecompressFile("", false, true, compressed);
			case "DFCMGorilla":
				return genericFCMDecompressFile("", true, true, compressed);
			default:
				return null;
		}
		
	}

	static double[] decompressFile(String f, String method) 
			throws IOException {
		
		f = Util.compressedPathify(f);
		switch(method) {
			case "FCM":
				return genericFCMDecompressFile(f, false, false, null);
			case "DFCM":
				return genericFCMDecompressFile(f, true, false, null);
			case "Gorilla":
				return gorillaDecompressFile(f, null);
			case "Sprintz":
				return sprintzDecompressFile(f, null);
			case "FCMGorilla":
				return genericFCMDecompressFile(f, false, true, null);
			case "DFCMGorilla":
				return genericFCMDecompressFile(f, true, true, null);
			default:
				return null;
		}
		
	}

}
