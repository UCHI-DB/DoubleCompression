package DoubleCompression;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Compressor {
	
	public static int FCMLevel = 1;
	static boolean frontCoding = true;

	static ByteBuffer genericFCMCompressFile(String f, boolean DFCM, boolean Gorilla, boolean Sprintz, double[] series)
			throws IOException {
		
		if(series == null) {
			series = Reader.readRaw(f);
		}
		ByteBuffer compressed;
		
		if(DFCM) {
			if(Gorilla) {
				compressed = new GorillaCompressor(new DFCMCompressor(series, FCMLevel).compressForGorilla(), frontCoding).compress();
			} else if (Sprintz) {
				compressed = new SprintzCompressor(new DFCMCompressor(series, FCMLevel).compressForGorilla()).compress();
			}
			else {
				compressed = new DFCMCompressor(series, FCMLevel).compress();
			}
		} else {
			if(Gorilla) {
				compressed = new GorillaCompressor(new FCMCompressor(series, FCMLevel).compressForGorilla(), frontCoding).compress();
			} else if (Sprintz){
				compressed = new SprintzCompressor(new FCMCompressor(series, FCMLevel).compressForGorilla()).compress();
			}
			else {
				compressed = new FCMCompressor(series, FCMLevel).compress();
			}
		}
		
		return compressed;
		
	}

	static ByteBuffer gorillaCompressFile(String f, double[] series)
			throws IOException {
		
		if(series == null) {
			series = Reader.readRaw(f);
		}
		return new GorillaCompressor(series, frontCoding).compress();
		
	}
	
	static ByteBuffer sprintzCompressFile(String f, double[] series)
			throws IOException {
		
		if(series == null) {
			series = Reader.readRaw(f);
		}
		return new SprintzCompressor(series).compress();
		
	}
	
	static ByteBuffer toBinaryCompressFile(String f, double[] series)
			throws IOException {
		
		if(series == null) {
			series = Reader.readRaw(f);
		}
		return new ToBinaryCompressor(series).compress();
		
	}

	static ByteBuffer directCompress(double[] series, String method)
			throws IOException {
		switch(method) {
			case "FCM":
				return genericFCMCompressFile("", false, false, false, series);
			case "DFCM":
				return genericFCMCompressFile("", true, false, false, series);
			case "Gorilla":
				return gorillaCompressFile("", series);
			case "Sprintz":
				return sprintzCompressFile("", series);
			case "FCMGorilla":
				return genericFCMCompressFile("", false, true, false, series);
			case "DFCMGorilla":
				return genericFCMCompressFile("", true, true, false, series);
			case "FCMSprintz":
				return genericFCMCompressFile("", false, false, true, series);
			case "DFCMSprintz":
				return genericFCMCompressFile("", true, false, true, series);
			case "ToBinary":
				return toBinaryCompressFile("", series);
			default:
				return null;
		}
	}

	static ByteBuffer compressFile(String f, String method) 
			throws IOException {
		
		f = Util.rawPathify(f);
		switch(method) {
			case "FCM":
				return genericFCMCompressFile(f, false, false, false, null);
			case "DFCM":
				return genericFCMCompressFile(f, true, false, false, null);
			case "Gorilla":
				return gorillaCompressFile(f, null);
			case "Sprintz":
				return sprintzCompressFile(f, null);
			case "FCMGorilla":
				return genericFCMCompressFile(f, false, true, false, null);
			case "DFCMGorilla":
				return genericFCMCompressFile(f, true, true, false, null);
			case "FCMSprintz":
				return genericFCMCompressFile(f, false, false, true, null);
			case "DFCMSprintz":
				return genericFCMCompressFile(f, true, false, true, null);
			case "ToBinary":
				return toBinaryCompressFile(f, null);
			default:
				return null;
		}
		
	}

	static double[] genericFCMDecompressFile(String f, boolean DFCM,
			boolean Gorilla, boolean Sprintz, ByteBuffer compressed)
			throws IOException {
		
		if(compressed == null) {
			compressed = Reader.readCompressed(f);
		}
		
		double[] decompressed;
		if(DFCM) {
			if(Gorilla) {
				decompressed = new DFCMDecompressor(null, new GorillaDecompressor(compressed, frontCoding).decompress(), FCMLevel).decompressFromGorilla();
			} else if (Sprintz) {
				decompressed = new DFCMDecompressor(null, new SprintzDecompressor(compressed).decompress(), FCMLevel).decompressFromGorilla();
			} else {
				decompressed = new DFCMDecompressor(compressed, null, FCMLevel).decompress();
			}
		} else {
			if(Gorilla) {
				decompressed = new FCMDecompressor(null, new GorillaDecompressor(compressed, frontCoding).decompress(), FCMLevel).decompressFromGorilla();
			} else if (Sprintz) {
				decompressed = new FCMDecompressor(null, new SprintzDecompressor(compressed).decompress(), FCMLevel).decompressFromGorilla();
			} else {
				decompressed = new FCMDecompressor(compressed, null, FCMLevel).decompress();
			}
		}
		
		return decompressed;
		
	}

	static double[] gorillaDecompressFile(String f, ByteBuffer compressed)
			throws IOException {
		
		if(compressed == null) {
			compressed = Reader.readCompressed(f);
		}
		return new GorillaDecompressor(compressed, frontCoding).decompress();
		
	}
	
	static double[] sprintzDecompressFile(String f, ByteBuffer compressed)
			throws IOException {
		
		if(compressed == null) {
			compressed = Reader.readCompressed(f);
		}
		return new SprintzDecompressor(compressed).decompress();
		
	}
	
	static double[] toBinaryDecompressFile(String f, ByteBuffer compressed)
			throws IOException {
		
		if(compressed == null) {
			compressed = Reader.readCompressed(f);
		}
		return new ToBinaryDecompressor(compressed).decompress();
		
	}

	static double[] directDecompress(ByteBuffer compressed, String method) 
			throws IOException {
		
		switch(method) {
			case "FCM":
				return genericFCMDecompressFile("", false, false, false, compressed);
			case "DFCM":
				return genericFCMDecompressFile("", true, false, false, compressed);
			case "Gorilla":
				return gorillaDecompressFile("", compressed);
			case "Sprintz":
				return sprintzDecompressFile("", compressed);
			case "FCMGorilla":
				return genericFCMDecompressFile("", false, true, false, compressed);
			case "DFCMGorilla":
				return genericFCMDecompressFile("", true, true, false, compressed);
			case "FCMSprintz":
				return genericFCMDecompressFile("", false, false, true, compressed);
			case "DFCMSprintz":
				return genericFCMDecompressFile("", true, false, true, compressed);
			case "ToBinary":
				return toBinaryDecompressFile("", compressed);
			default:
				return null;
		}
		
	}

	static double[] decompressFile(String f, String method) 
			throws IOException {
		
		f = Util.compressedPathify(f);
		switch(method) {
			case "FCM":
				return genericFCMDecompressFile(f, false, false, false, null);
			case "DFCM":
				return genericFCMDecompressFile(f, true, false, false, null);
			case "Gorilla":
				return gorillaDecompressFile(f, null);
			case "Sprintz":
				return sprintzDecompressFile(f, null);
			case "FCMGorilla":
				return genericFCMDecompressFile(f, false, true, false, null);
			case "DFCMGorilla":
				return genericFCMDecompressFile(f, true, true, false, null);
			case "FCMSprintz":
				return genericFCMDecompressFile(f, false, false, true, null);
			case "DFCMSprintz":
				return genericFCMDecompressFile(f, true, false, true, null);
			case "ToBinary":
				return toBinaryDecompressFile(f, null);
			default:
				return null;
		}
		
	}

}
