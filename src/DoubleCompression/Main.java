package DoubleCompression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		
//		String[] names = allFolderNames();
//		for(String name : names) {
//			new File(System.getProperty("user.dir") + "/output/" + name).mkdir();
//		}
		
//		String name = "Haptics_103";
//		try {
//			double[] raw = Reader.readRaw(rawPathify(name));
//			double[] decompressed = directDecompress(compressFile(name, "Gorilla"),"Gorilla");
//			int len = raw.length;
//			boolean correct = true;
//			for(int i = 0; i < len; i++) {
//				if(raw[i] != decompressed[i]) {
//					correct = false;
//				}
//			}
//			System.out.println(correct);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		try {
//			String[] results = testReport("Haptics_12", "Gorilla");
//			for(String s : results) {
//				System.out.println(s);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		byte b = 9;
//		for(int i = 7; i >= 0; i--) {
//			System.out.println((b & (1 << i)) >> i);
//		}
			
		try {
			generateReport("Gorilla");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		byte[] arr = Util.toByteArray(2.73862637264295E-310);
//		double ret = Util.toDouble(arr);
//		System.out.println(ret);
		
//		double[] data = new double[] {12,12,24,15,12,35};
//		try {
//			double[] decompressed = directDecompress(directCompress(data, "Gorilla"),"Gorilla");
//			System.out.println("Hi");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	static ByteBuffer compressFile(String f, String method) 
			throws IOException {
		
		f = rawPathify(f);
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
	
	static double[] decompressFile(String f, String method) 
			throws IOException {
		
		f = compressedPathify(f);
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
	
	static void writeCompressedToFile(ByteBuffer toWrite, String output, boolean flip) 
			throws IOException {
		
		output = compressedPathify(output);
        OutputStream outstream = Files.newOutputStream(Paths.get(output));
        if(flip) {
        	toWrite.flip();
        }
		int len = toWrite.capacity();
		byte[] outputArray = new byte[len];
		toWrite.get(outputArray);
		
		outstream.write(outputArray);
		outstream.flush();
		outstream.close();
		
		
	}
	
	static void writeUncompressedToFile(double[] toWrite, String output) throws FileNotFoundException {

		PrintWriter out = new PrintWriter(output);
		out.println(seriesToString(toWrite));
		out.close();
		
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
		
		double[] data = Reader.readRaw(rawPathify(f));
		long compressionStartTime = System.nanoTime();
		ByteBuffer compressed = directCompress(data, method);
		long compressionEndTime = System.nanoTime();
		double[] decompressed = directDecompress(compressed, method);
		long decompressionEndTime = System.nanoTime();
		
		//double compressionTime = (compressionEndTime - compressionStartTime) / 1000.0;
		double compressionTime = (compressionEndTime - compressionStartTime);
		double compressionThroughput = fileSize(rawPathify(f)) / compressionTime;
		//double decompressionTime = (decompressionEndTime - compressionEndTime) / 1000.0;
		double decompressionTime = (decompressionEndTime - compressionEndTime);
		double decompressionThroughput = fileSize(compressedPathify(f)) / decompressionTime;
		//System.out.println(fileSize(compressedPathify(f)) + "/" + decompressionTime + "=" + decompressionThroughput);
		
		compressionThroughput = Math.round(1000000000.0 * compressionThroughput * 100.0) / 100.0;
		decompressionThroughput = Math.round(1000000000.0 * decompressionThroughput * 100.0) / 100.0;
		
		return new String[] {String.valueOf(compressionThroughput),
				String.valueOf(decompressionThroughput)};
	}
	
	static FilenameFilter filter () {
		return new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.startsWith(".")) {
					return false;
				} else {
					return true;
				}
			}
		};
	}
	
	static String[] allFolderNames () {
		
		File[] fileList = new File(System.getProperty("user.dir") + "/data/").listFiles(filter());
		int len = fileList.length;
		String[] ret = new String[len];
		for(int i = 0; i < len; i++) {
			ret[i] = fileList[i].getName();
		}
		return ret;
		
	}
	
	static String[] allFileNames () {
		String[] folderNames = allFolderNames();
		ArrayList<File> files = new ArrayList<File>();
		for(String name : folderNames) {
			files.addAll(Arrays.asList(new File(System.getProperty("user.dir") + "/data/" + name + "/").listFiles(filter())));
		}
		int len = files.size();
		String[] ret = new String[len];
		for(int i = 0; i < len; i++) {
			ret[i] = files.get(i).getName();
		}
		return ret;
	}
	
	static String getFolderName (String f) {
		return f.split("_")[0];
	}
	
	static String rawPathify (String f) {
		return System.getProperty("user.dir") + "/data/" + getFolderName(f) + "/" + f;
	}
	
	static String compressedPathify (String f) {
		return System.getProperty("user.dir") + "/output/" + getFolderName(f) + "/" + f + "_COMPRESSED";
	}
	
	static double fileSize (String path) {
		return new File(path).length() / (1024.0 * 1024.0);
		//return new File(path).length();
	}
	
	static String compressionReport (String name) {
		double rawSize = fileSize(rawPathify(name));
		double compressedSize = fileSize(compressedPathify(name));
		double ratio = Math.round((compressedSize / rawSize) * 100.0) / 100.0;
		return String.valueOf(ratio);
	}
	
	static String[] testReport (String name, String method) throws IOException {
		String[] ret = new String[6];
		writeCompressedToFile(compressFile(name, method), name, !method.equals("Gorilla"));
		String ratio = compressionReport(name);
		String[] throughputTestResults = throughputReport(name, method);
		ret[0] = name;
		ret[1] = String.valueOf(Math.round(fileSize(rawPathify(name)) * 1024.0 * 100.0) / 100.0);
		ret[2] = throughputTestResults[0];
		ret[3] = throughputTestResults[1];
		ret[4] = ratio;
		ret[5] = method;
		return ret;
	}
	
	static String[][] fullTestReport (String method) throws IOException{
//		String[] names = allFileNames();
		String[] namesFull = allFileNames();
		int numToTest = 2000;
		String[] names = new String[numToTest];
		for(int i = 0; i < numToTest; i++) {
			names[i] = namesFull[i];
		}
		int len = names.length;
		String[][] ret = new String[len+1][6];
		System.out.println("Beginning testing!");
		ret[0] = new String[] {"File Name", "File Size (kb)", 
				"Compression Throughput (mb/s)", "Decompression Throughput (mb/s)",
				"Compression Ratio", "Method"};
		for(int i = 0; i < len; i++) {
			//System.out.println("Decompressing " + names[i]);
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
