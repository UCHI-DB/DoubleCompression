package DoubleCompression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class Reader {
	
	public static double[] readRaw (String f) throws IOException
    {
		
			BufferedReader reader = new BufferedReader(new FileReader(f)); 
        	String[] line = reader.readLine().split(",");
//        	ArrayList<String> firstRemoved = new ArrayList<String>(Arrays.asList(line));
//        	firstRemoved.remove(0);
        	double[] asDoubles = Arrays.stream(line).mapToDouble(num -> Double.parseDouble(num)).toArray();
        	reader.close();
            return asDoubles;
        
    }
	
	public static ByteBuffer readCompressed (String f) throws IOException
    {
		
		int len = (int) Math.ceil(new File(f).length());
		InputStream stream = Files.newInputStream(Paths.get(f));
		byte[] byteArray = new byte[len];
		stream.read(byteArray);
		stream.close();
		return ByteBuffer.wrap(byteArray);
        
    }
	
}
