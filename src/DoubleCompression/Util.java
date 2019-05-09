package DoubleCompression;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

public class Util {
	
//	public static BitSet toBitSet (double value) {
//		return BitSet.valueOf(toByteArray(value));
//	}

	public static byte[] toByteArray(double value) {
	    byte[] bytes = new byte[8];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    return bytes;
	}
	
	public static byte[] toByteArray(int value) {
	    byte[] bytes = new byte[4];
	    ByteBuffer.wrap(bytes).putInt(value);
	    return bytes;
	}

	public static double toDouble(byte[] bytes) {
	    return ByteBuffer.wrap(bytes).getDouble();
	}
	
	public static byte[] xorByteArrays(byte[] b1, byte[] b2) {
		int len = b1.length;
		byte[] ret = new byte[len];
		for(int i = 0; i < len; i++) {
			ret[i] = (byte) (b1[i] ^ b2[i]);
		}
		return ret;
	}
	
	public static byte[] orByteArrays(byte[] b1, byte[] b2) {
		int len = b1.length;
		byte[] ret = new byte[len];
		for(int i = 0; i < len; i++) {
			ret[i] = (byte) (b1[i] | b2[i]);
		}
		return ret;
	}
	
	public static double xorDoubles(double d1, double d2) {
		return toDouble(xorByteArrays(toByteArray(d1), toByteArray(d2)));
	}
	
	public static int bitLeadingZeroesIgnoringSign (byte[] bs) {
		boolean first = true;
		int ret = 1;
		for(byte b : bs) {
			int n = 7;
			if(first == true) {
				n = 6;
				first = false;
			}
			for(int i = n; i >= 0; i--) {
				int bit = ((b & (1 << i)) >> i);
				if(bit == 0) {
					ret++;
				} else {
					return ret;
				}
			}
		}
		return ret;
	}
	
//	public static BitSet concatBitSets(BitSet input1, BitSet input2) {
//		  BitSet input1_clone = (BitSet)input1.clone();
//		  BitSet input2_clone = (BitSet)input2.clone();
//		  int n = 5;//_desired length of the first (leading) vector
//		  int index = -1;
//		  while (index < (input2_clone.length() - 1)) {
//		    index = input2_clone.nextSetBit((index + 1));
//		    input1_clone.set((index + n));
//		  }
//		  return input1_clone;
//		}
	
//	public static BitSet concatBitSetArray(BitSet[] bs) {
//		BitSet ret = bs[0];
//		for(int i = 1; i < bs.length; i++) {
//			ret = concatBitSets(ret, bs[i]);
//		}
//		return ret;
//	}
	
//	public static int leadingZeroes(BitSet b) {
//		return b.nextSetBit(0) + 1;
//	}

	static double fileSize (String path) {
		return new File(path).length() / (1024.0 * 1024.0);
	}

	static String compressedPathify (String f) {
		return System.getProperty("user.dir") + "/output/" + Util.getFolderName(f) + "/" + f + "_COMPRESSED";
	}

	static String rawPathify (String f) {
		return System.getProperty("user.dir") + "/data/" + Util.getFolderName(f) + "/" + f;
	}

	static String getFolderName (String f) {
		return f.split("_")[0];
	}

	static String[] allFileNames () {
		String[] folderNames = Util.allFolderNames();
		ArrayList<File> files = new ArrayList<File>();
		for(String name : folderNames) {
			files.addAll(Arrays.asList(new File(System.getProperty("user.dir") + "/data/" + name + "/").listFiles(Util.filter())));
		}
		int len = files.size();
		String[] ret = new String[len];
		for(int i = 0; i < len; i++) {
			ret[i] = files.get(i).getName();
		}
		return ret;
	}

	static String[] allFolderNames () {
		
		File[] fileList = new File(System.getProperty("user.dir") + "/data/").listFiles(Util.filter());
		int len = fileList.length;
		String[] ret = new String[len];
		for(int i = 0; i < len; i++) {
			ret[i] = fileList[i].getName();
		}
		return ret;
		
	}

	static FilenameFilter filter () {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith(".")) {
					return false;
				} else {
					return true;
				}
			}
		};
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

}
