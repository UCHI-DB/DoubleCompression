package DoubleCompression;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {

//		try {
//			Tester.generateReport("Gorilla"); //warm up
//			Tester.generateReport("FCMGorilla");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		Tester.testCorrectnessFull("DFCM");

		String name = "Worms_66";
		try {
			String method = "FCMGorilla";
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

//		double[] data = new double[] {3,4,6,1,2,3,4,5,
//									  0,0,0,0,0,0,0,0,
//									  0,0,0,0,0,1};
//		try {
//			String method = "Sprintz";
//			double[] decompressed = Compressor.directDecompress(Compressor.directCompress(data, method),method);
//			System.out.println("Hi");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
