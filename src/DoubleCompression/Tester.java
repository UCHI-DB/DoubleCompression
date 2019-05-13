package DoubleCompression;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Tester {

	static String compressionReport (String name) {
		double rawSize = Util.fileSize(Util.rawPathify(name));
		double compressedSize = Util.fileSize(Util.compressedPathify(name));
		double ratio = Math.round((compressedSize / rawSize) * 100.0) / 100.0;
		return String.valueOf(ratio);
	}

	static String[] testReport (String name, String method) throws IOException {
		String[] ret = new String[6];
		double[] data = Reader.readRaw(Util.rawPathify(name));
		long compressionStartTime = System.nanoTime();
		ByteBuffer compressed = Compressor.directCompress(data, method);
		long compressionEndTime = System.nanoTime();
		double[] decompressed = Compressor.directDecompress(compressed, method);
		long decompressionEndTime = System.nanoTime();
		Writer.writeCompressedToFile(compressed, name);
		String ratio = compressionReport(name);
		
		double fileSize = Util.fileSize(Util.rawPathify(name));
		double compressionTime = (compressionEndTime - compressionStartTime);
		double compressionThroughput = fileSize / compressionTime;
		double decompressionTime = (decompressionEndTime - compressionEndTime);
		double decompressionThroughput = fileSize / decompressionTime;
		
		compressionThroughput = Math.round(1000000000.0 * compressionThroughput * 100.0) / 100.0;
		decompressionThroughput = Math.round(1000000000.0 * decompressionThroughput * 100.0) / 100.0;
		
		ret[0] = name;
		ret[1] = String.valueOf(Math.round(fileSize * 1024.0 * 100.0) / 100.0);
		ret[2] = String.valueOf(compressionThroughput);
		ret[3] = String.valueOf(decompressionThroughput);
		ret[4] = ratio;
		ret[5] = method;
		return ret;
	}

	static String[][] fullTestReport (String method) throws IOException{
			String[] names = Util.allFileNames();
//			String[] namesFull = Util.allFileNames();
//			int numToTest = 100;
//			String[] names = new String[numToTest];
//			for(int i = 0; i < numToTest; i++) {
//				names[i] = namesFull[i];
//			}
			int len = names.length;
			String[][] ret = new String[len+1][6];
			//System.out.println("Beginning testing!");
			ret[0] = new String[] {"File Name", "File Size (kb)", 
					"Compression Throughput (mb/s)", "Decompression Throughput (mb/s)",
					"Compression Ratio", "Method"};
			for(int i = 0; i < len; i++) {
				//System.out.println("Decompressing " + names[i]);
				ret[i+1] = testReport(names[i],method);
				System.out.println(method + Compressor.FCMLevel + ": " + (i+1) + " / " + len + " tested");
			}
			//System.out.println("Done!");
			return ret;
		}
	
	public static void testCorrectnessFull (String method) {
		String[] names = Util.allFileNames();
		int len = names.length;
		for(int i = 0; i < len; i++) {
			//System.out.println("Decompressing " + names[i]);
			testCorrectnessOneFile(names[i], method);
			System.out.println(method + ": " + (i+1) + " / " + len + " tested");
		}
	}
	
	static void testCorrectnessOneFile (String name, String method) {
		try {
			double[] raw = Reader.readRaw(Util.rawPathify(name));
			double[] decompressed = Compressor.directDecompress(Compressor.compressFile(name, method),method);
			int len = raw.length;
			boolean correct = true;
			for(int i = 0; i < len; i++) {
				if(Math.abs(raw[i] - decompressed[i]) > 0.0001) {
					System.out.println(raw[i] + " != " + decompressed[i]);
					correct = false;
				}
			}
			System.out.println(correct);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		makeCSV(fullTestReport(method), "Analyses/Reports/" + method + " Report.csv");
	}

	static void generateReport (String method, int level) throws IOException {
		Compressor.FCMLevel = level;
		if(method != "Gorilla" && method != "Sprintz") {
			makeCSV(fullTestReport(method), "Analyses/Reports/" + method + level + " Report.csv");
		} else {
			makeCSV(fullTestReport(method), "Analyses/Reports/" + method + " Report.csv");
		}
	}

}
