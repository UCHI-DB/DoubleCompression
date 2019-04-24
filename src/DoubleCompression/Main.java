package DoubleCompression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
//"/Users/jacobspiegel/DoubleCompression/data/ACSF1/ACSF1_TEST"

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<double[]> series = 
				Reader.readRaw("/Users/jacobspiegel/DoubleCompression/data/ACSF1/ACSF1_TEST");
		writeUncompressedToFile(series, "test");
		System.out.println(series.get(0));
		//System.out.println()
	}
	
	static ArrayList<ByteBuffer> compressFile(String f, String method) 
			throws FileNotFoundException {
		
		switch(method) {
			case "FCM":
				return genericFCMCompressFile(f, false);
			case "DFCM":
				return genericFCMCompressFile(f, true);
			default:
				return null;
		}
		
	}
	
	static ArrayList<ByteBuffer> genericFCMCompressFile(String f, boolean DFCM)
			throws FileNotFoundException {
		
		ArrayList<double[]> timeseries = Reader.readRaw(f);
        ArrayList<ByteBuffer> ret = new ArrayList<ByteBuffer>();
		
		for(double[] series : timeseries) {
			ByteBuffer compressed;
			if(DFCM) {
				compressed = new DFCMCompressor(series).compress();
			} else {
				compressed = new FCMCompressor(series).compress();
			}
			ret.add(compressed);
		}
		
		return ret;
		
	}
	
	static ArrayList<double[]> decompressFile(String f, String method) 
			throws IOException {
		
		switch(method) {
			case "FCM":
				return genericFCMDecompressFile(f, false);
			case "DFCM":
				return genericFCMDecompressFile(f, true);
			default:
				return null;
		}
		
	}
	
	static ArrayList<double[]> genericFCMDecompressFile(String f, boolean DFCM)
			throws IOException {
		
		ArrayList<ByteBuffer> bbs = Reader.readCompressed(f);
        ArrayList<double[]> ret = new ArrayList<double[]>();
		
		for(ByteBuffer b : bbs) {
			double[] decompressed;
			if(DFCM) {
				decompressed = new DFCMDecompressor(b).decompress();
			} else {
				decompressed = new FCMDecompressor(b).decompress();
			}
			ret.add(decompressed);
		}
		
		return ret;
		
	}
	
	static void writeCompressedToFile(ArrayList<ByteBuffer> toWrite, String output) 
			throws FileNotFoundException {
		
		File outputFile = new File(output);
        FileChannel channel = new FileOutputStream(outputFile, true).getChannel();
        
		for(ByteBuffer b : toWrite) {
			try {
				channel.write(b);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	static void writeUncompressedToFile(ArrayList<double[]> toWrite, String output) throws FileNotFoundException {

		PrintWriter out = new PrintWriter(output);
		for(double[] s : toWrite) {
			out.println(seriesToString(s));
		}
		
	}

	static String seriesToString(double[] series) {
		int len = series.length;
		String ret = "";
		for(int i = 0; i < len; i++) {
			ret += String.valueOf(series[i]);
			if(i != len-1) {
				ret += ",";
			}
		}
		return ret;
	}
	
//	static long[] timeTest (String f, String method) throws FileNotFoundException {
//		long compressionStartTime = System.currentTimeMillis();
//		ArrayList<ByteBuffer> compressed = compressFile(f, method);
//		long compressionEndTime = System.currentTimeMillis();
//		
//	}
	
}
