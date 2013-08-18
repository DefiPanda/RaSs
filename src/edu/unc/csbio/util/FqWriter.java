package edu.unc.csbio.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The class to output a fq file
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class FqWriter {
	/**
	 * an output BufferedWriter
	 */
	private BufferedWriter out = null;

	/**
	 * constructor for object <code>FqWriter</code>
	 * 
	 * @param filename
	 *            file name of FqWriter
	 * @param bufferSize
	 *            buffer size of FqWriter
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public FqWriter(String filename, int bufferSize) throws IOException {
		out = new BufferedWriter(new FileWriter(filename), bufferSize);
	}

	/**
	 * this method will close BufferedWriter <code>out</code>
	 * 
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public void close() throws IOException {
		out.flush();
		out.close();
	}

	/**
	 * this method will append a given string to BufferedWriter <code>out</code>
	 * , without starting a new line
	 * 
	 * @param s
	 *            string
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public void print(String s) throws IOException {
		out.append(s);
	}

	/**
	 * this method will append a given string to BufferedWriter <code>out</code>
	 * , then start a new line
	 * 
	 * @param s
	 *            string
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public void println(String s) throws IOException {
		out.append(s + "\n");
	}
}
