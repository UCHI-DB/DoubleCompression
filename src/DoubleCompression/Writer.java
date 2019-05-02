package DoubleCompression;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Writer {

	static void writeCompressedToFile(ByteBuffer toWrite, String output) 
			throws IOException {
		
		output = Util.compressedPathify(output);
	    OutputStream outstream = Files.newOutputStream(Paths.get(output));
	    toWrite.flip();
		int len = toWrite.capacity();
		byte[] outputArray = new byte[len];
		toWrite.get(outputArray);
		
		outstream.write(outputArray);
		outstream.flush();
		outstream.close();
		
		
	}

	static void writeUncompressedToFile(double[] toWrite, String output) throws FileNotFoundException {
	
		PrintWriter out = new PrintWriter(output);
		out.println(Util.seriesToString(toWrite));
		out.close();
		
	}

}
