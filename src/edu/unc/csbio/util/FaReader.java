package edu.unc.csbio.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * this class is for reading data from a fasta file with provided starting and
 * end position
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class FaReader {
	/**
	 * this method is for getting data within start and end position
	 * 
	 * @param filename
	 *            file name of fasta file
	 * @param start
	 *            start position
	 * @param end
	 *            end position
	 * @return data within start and end position, represented as a string
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public static String getData(final String filename, int start, int end)
			throws IOException {
		// System.out.println(start+" "+end);
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = null;
		int numOfLines = 0;
		int count = 0;

		int rest = end - start + 1;

		/*
		 * Use StringBuilder if frequent concatenation is needed. It is far more
		 * efficient than String + .
		 */
		StringBuilder ret = new StringBuilder(rest);

		while ((line = br.readLine()) != null) {
			++numOfLines;
			if (numOfLines <= 1)
				continue; /* skip the first line. */
			// System.out.println(count+" "+line.length());

			if (count + line.length() >= start) {
				// System.out.println(start+" "+count);

				int lineStartPos = start > count ? start - count - 1 : 0;
				ret.append(line.substring(lineStartPos,
						lineStartPos + rest <= line.length() ? lineStartPos
								+ rest : line.length()));
				// System.out.println(ret.length());
				// System.out.println(ret);

				rest -= (line.length() - lineStartPos);
				if (rest <= 0)
					break;
			}
			count += line.length();
		}
		return ret.toString();
	}
}
