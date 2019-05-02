package DoubleCompression;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Util {

	public static byte[] toByteArray(double value) {
	    byte[] bytes = new byte[8];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    return bytes;
	}

	public static double toDouble(byte[] bytes) {
	    return ByteBuffer.wrap(bytes).getDouble();
	}
	
	public static byte[] xor(byte[] b1, byte[] b2) {
		int len = b1.length;
		byte[] ret = new byte[len];
		for(int i = 0; i < len; i++) {
			ret[i] = (byte) (b1[i] ^ b2[i]);
		}
		return ret;
	}
	
	public static double xorDoubles(double d1, double d2) {
		return toDouble(xor(toByteArray(d1), toByteArray(d2)));
	}

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
