package DoubleCompression;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {

		try {
			Tester.generateReport("DFCM");
		} catch (IOException e) {
			e.printStackTrace();
		}

//		String name = "Haptics_103";
//		try {
//			String method = "DFCMGorilla";
//			double[] raw = Reader.readRaw(Util.rawPathify(name));
//			double[] decompressed = Compressor.directDecompress(Compressor.compressFile(name, method),method);
//			int len = raw.length;
//			boolean correct = true;
//			for(int i = 0; i < len; i++) {
//				if(raw[i] != decompressed[i]) {
//					//System.out.println(raw[i] + " = " + decompressed[i]);
//					correct = false;
//				}
//			}
//			System.out.println(correct);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		double[] data = new double[] {12,12,24,15,12,35};
//		try {
//			double[] decompressed = Compressor.directDecompress(Compressor.directCompress(data, "Gorilla"),"Gorilla");
//			System.out.println("Hi");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
