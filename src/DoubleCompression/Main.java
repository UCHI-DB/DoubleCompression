package DoubleCompression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
//"/Users/jacobspiegel/DoubleCompression/data/ACSF1/ACSF1_TEST"

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		
		String[] names = allFileNames();
		
		try {
			generateReport("DFCM");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArrayList<double[]> series = 
				Reader.readRaw("/Users/jacobspiegel/DoubleCompression/data/ACSF1/ACSF1_TEST");

		try {
			ByteBuffer temp = new GorillaCompressor(series.get(0)).compress();
			//System.out.println(temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		ArrayList<ByteBuffer> compressed = compressFile
//				("/Users/jacobspiegel/DoubleCompression/data/ACSF1/ACSF1_TEST", 
//				"FCM");
//		try {
//		writeUncompressedToFile(
//				directDecompress("FCM", compressed),
//				"temp2"
//				);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
		
		
//		writeCompressedToFile(
//				compressFile
//					("/Users/jacobspiegel/DoubleCompression/data/ACSF1/ACSF1_TEST", 
//					"FCM"),
//				"temp");
//		try {
//			//ArrayList<double[]> temp = decompressFile("temp", "FCM");
//			System.out.println("Hi");
//			writeUncompressedToFile(
//					decompressFile("/Users/jacobspiegel/DoubleCompression/temp", "FCM"),
//					"temp2"
//					);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	static ArrayList<ByteBuffer> compressFile(String f, String method) 
			throws FileNotFoundException {
		
		f = rawPathify(f);
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
		
		f = compressedPathify(f);
		switch(method) {
			case "FCM":
				return genericFCMDecompressFile(f, false, null);
			case "DFCM":
				return genericFCMDecompressFile(f, true, null);
			default:
				return null;
		}
		
	}
	
	static ArrayList<double[]> directDecompress(ArrayList<ByteBuffer> compressed, String method) 
			throws IOException {
		
		switch(method) {
			case "FCM":
				return genericFCMDecompressFile("", false, compressed);
			case "DFCM":
				return genericFCMDecompressFile("", true, compressed);
			default:
				return null;
		}
		
	}
	
	static ArrayList<double[]> genericFCMDecompressFile(String f, boolean DFCM,
			ArrayList<ByteBuffer> compressed)
			throws IOException {
		
		if(compressed == null) {
			compressed = Reader.readCompressed(f);
		}
        ArrayList<double[]> ret = new ArrayList<double[]>();
		
		for(ByteBuffer b : compressed) {
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
		
		new File(compressedPathify(output)).delete();
		output = compressedPathify(output);
		File outputFile = new File(output);
        FileChannel channel = new FileOutputStream(outputFile, true).getChannel();
        
		for(ByteBuffer b : toWrite) {
			b.flip();
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
	
	static String[] throughputReport (String f, String method) throws IOException {
		
		long compressionStartTime = System.currentTimeMillis();
		ArrayList<ByteBuffer> compressed = compressFile(f, method);
		long compressionEndTime = System.currentTimeMillis();
		ArrayList<double[]> decompressed = directDecompress(compressed, method);
		long decompressionEndTime = System.currentTimeMillis();
		
		double compressionTime = (compressionEndTime - compressionStartTime) / 1000.0;
		double compressionThroughput = fileSize(rawPathify(f)) / compressionTime;
		double decompressionTime = (decompressionEndTime - compressionEndTime) / 1000.0;
		double decompressionThroughput = fileSize(compressedPathify(f)) / decompressionTime;
		
		compressionThroughput = Math.round(compressionThroughput * 100.0) / 100.0;
		decompressionThroughput = Math.round(decompressionThroughput * 100.0) / 100.0;
		
		return new String[] {String.valueOf(compressionThroughput),
				String.valueOf(decompressionThroughput)};
	}
	
	static String[] allFileNames () {
		
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.startsWith(".")) {
					return false;
				} else {
					return true;
				}
			}
		};
		
		File[] fileList = new File(System.getProperty("user.dir") + "/data/").listFiles(filter);
		int len = fileList.length;
		String[] ret = new String[len];
		for(int i = 0; i < len; i++) {
			ret[i] = fileList[i].getName();
		}
		return ret;
	}
	
	static String rawPathify (String f) {
		return System.getProperty("user.dir") + "/data/" + f + "/" + f + "_TEST";
	}
	
	static String compressedPathify (String f) {
		return System.getProperty("user.dir") + "/output/" + f + "_TEST_COMPRESSED";
	}
	
	static double fileSize (String path) {
		return new File(path).length() / (1024.0 * 1024.0);
	}
	
	static String compressionReport (String name) {
		double rawSize = fileSize(rawPathify(name));
		double compressedSize = fileSize(compressedPathify(name));
		double ratio = Math.round((compressedSize / rawSize) * 100.0) / 100.0;
		return String.valueOf(ratio);
	}
	
	static String[] testReport (String name, String method) throws IOException {
		String[] ret = new String[6];
		writeCompressedToFile(compressFile(name, method), name);
		String ratio = compressionReport(name);
		String[] throughputTestResults = throughputReport(name, method);
		ret[0] = name;
		ret[1] = String.valueOf(Math.round(fileSize(rawPathify(name)) * 100.0) / 100.0);
		ret[2] = throughputTestResults[0];
		ret[3] = throughputTestResults[1];
		ret[4] = ratio;
		ret[5] = method;
		return ret;
	}
	
	static String[][] fullTestReport (String method) throws IOException{
		String[] names = allFileNames();
		int len = names.length;
		String[][] ret = new String[len+1][6];
		System.out.println("Beginning testing!");
		ret[0] = new String[] {"File Name", "File Size (mb)", 
				"Compression Throughput (mb/s)", "Decompression Throughput (mb/s)",
				"Compression Ratio", "Method"};
		for(int i = 0; i < len; i++) {
			ret[i+1] = testReport(names[i],method);
			System.out.println((i+1) + " / " + len + " tested");
		}
		System.out.println("Done!");
		return ret;
	}
	
	static String toCSVLine (String[] input) {
		int len = input.length;
		String ret = "";
		for(int i = 0; i < len; i++) {
			ret += input[i];
			ret += (i == len-1 ? "\n" : ",");
		}
		return ret;
	}
	
	static void makeCSV (String[][] input, String outputName) throws IOException {
		new File(outputName).delete();
		FileWriter writer = new FileWriter(outputName);
		for(String[] line : input) {
		  writer.write(toCSVLine(line));
		}
		writer.close();
	}
	
	static void generateReport (String method) throws IOException {
		makeCSV(fullTestReport(method), method + " Report.csv");
	}
	
}
