package DoubleCompression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SprintzCompressor {
	
	double prev = 0;
	double[] input;
	double[] block = new double[8];
	int posInBlock = 0;
	byte buf = 0;
	byte posInBuf = 7;
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	byte numZeroBlocks = 0;
	
	public SprintzCompressor (double[] input) {
		this.input = input;
	}
	
	public ByteBuffer compress() throws IOException {
		output.write(Util.toByteArray(input.length));
		for(double d : input) {
			compressOne(d);
		}
		flush();
		return ByteBuffer.wrap(output.toByteArray());
	}
	
	void compressOne(double d) throws IOException {
		if(posInBlock == 8) {
			posInBlock = 0;
			compressBlock(false);
		}
		block[posInBlock] = err(d);
		//System.out.println(block[pos]);
		prev = d;
		posInBlock++;
	}
	
	void compressBlock (boolean flushing) throws IOException {
		byte[][] bs = new byte[8][8]; 
		byte[] b = Util.toByteArray(block[0]);
		bs[0] = b;
		for(int i = 1; i < 8; i++) {
			bs[i] = Util.toByteArray(block[i]);
			b = Util.orByteArrays(b, bs[i]);
		}
		byte nBits = (byte) (64 - Util.bitLeadingZeroesIgnoringSign(b));
		if(nBits == 0 && b[0] == 0) {
			numZeroBlocks++;
			block = new double[8];
			if(flushing) {
				addByte((byte) 0, 7);
				addByte(numZeroBlocks, 7);
			}
		} else {
			if(numZeroBlocks > 0) {
				addByte((byte) 0, 7);
				addByte(numZeroBlocks, 7);
				numZeroBlocks = 0;
			}
			addByte((byte) (nBits+1), 7);
			int numToAdd = (posInBlock == 0) ? 8 : posInBlock;
			for(int i = 0; i < numToAdd; i++) {
				addErrAsNBits(bs[i], nBits);
			}
		}
	}
	
	void flush () throws IOException {
		compressBlock(true);
		int numToAdd = posInBuf + 1;
		if(numToAdd == 8) {
			numToAdd = 0;
		}
		output.write(buf);
		output.write(numToAdd);
	}
	
	void addBit (char c) {
		if(posInBuf == -1) {
			output.write(buf);
			buf = 0;
			posInBuf = 7;
		}
		if(c == '1') {
			buf |= 1 << posInBuf;
		}
		posInBuf--;
	}
	
	void addByte (byte b) {
		addByte(b, 8);
	}
	
	void addByte (byte b, int n) {
		n--;
		for(int i = n; i >= 0; i--) {
			int bit = ((b & (1 << i)) >> i);
			char asChar = (bit == 0) ? '0' : '1';
			addBit(asChar);
		}
	}
	
	void addBytes (byte[] bs) {
		for(byte b : bs) {
			addByte(b);
		}
	}
	
	void addErrAsNBits(byte[] err, byte nBits) {
		int signBit = ((err[0] & (1 << 7)) >> 7);
		addBit((signBit == 0) ? '0' : '1');
		int numBytes = (int) Math.ceil(nBits / 8.0);
		int numHangBits = nBits - ((numBytes - 1) * 8);
		boolean first = true;
		for(int i = (8 - numBytes); i < 8; i++) {
			if(first) {
				addByte(err[i], numHangBits);
				first = false;
			} else {
				addByte(err[i]);
			}
		}
	}
	
	double err (double d) {
		return d - predict(d);
	}
	
	double predict (double d) {
		return prev;
	}

}
