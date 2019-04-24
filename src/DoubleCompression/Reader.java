package DoubleCompression;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.DoubleStream;


public class Reader {
	
	public static ArrayList<double[]> readRaw (String f) throws FileNotFoundException
    {
		Scanner scanner = new Scanner(new File(f));
		ArrayList<double[]> timeseries = new ArrayList<double[]>();
        
        scanner.useDelimiter("\n");
         
        while (scanner.hasNext())
        {
        	String[] line = scanner.next().split(",");
        	ArrayList<String> firstRemoved = new ArrayList<String>(Arrays.asList(line));
        	firstRemoved.remove(0);
        	double[] asDoubles = firstRemoved.stream().mapToDouble(num -> Double.parseDouble(num)).toArray();
            timeseries.add(asDoubles);
        }
         
        scanner.close();
        return timeseries;
        
    }
	
	public static ArrayList<ByteBuffer> readCompressed (String f) throws IOException
    {
		Scanner scanner = new Scanner(new File(f));
		ArrayList<ByteBuffer> ret = new ArrayList<ByteBuffer>();
        
        scanner.useDelimiter("\n");
         
        while (scanner.hasNext())
        {
        	String line = scanner.next();
        	InputStream is = new ByteArrayInputStream(line.getBytes());
        	ByteBuffer b = ByteBuffer.allocate(is.available());;
        	while (is.available() > 0) {
                b.put((byte) is.read());
            }
            ret.add(b);
        }
         
        scanner.close();
        return ret;
        
    }
	
}
