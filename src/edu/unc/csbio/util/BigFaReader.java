package edu.unc.csbio.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.RandomAccessFile;
//import java.util.HashMap;
import java.util.TreeMap;

/**
 * this class is for reading data from a fasta file, and reading/generating an
 * index file
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class BigFaReader {
	/**
	 * end of line character
	 */
	private final static String ENDOFLINE = "\n";
	/**
	 * length end of line character
	 */
	private static int EXTRA = ENDOFLINE.length();
	/**
	 * name of fasta file
	 */
	private static String dataFile = null;
	/**
	 * name of index file
	 */
	private String indexFile = null;
	/**
	 * obtains input bytes from given <code>fasta</code> in a file system
	 */
	private static FileInputStream fis = null;
	/**
	 * this maps a chromosome name to its starting offset and line length
	 */
	private static TreeMap<String, String[]> indexMap;

	/**
	 * This is the constructor of object <code>BigFaReader</code>. It will
	 * access given fasta file. If there is a corresponding index file, it will
	 * read index data from the file; else, it will generate an index file.
	 * 
	 * @param _dataFile
	 *            name of fasta file
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public BigFaReader(String _dataFile) throws IOException {
		dataFile = _dataFile;
		indexFile = _dataFile + ".idx";

		BufferedReader br = null;
		String line = null;
		indexMap = new TreeMap<String, String[]>();

		/* Try to access the Fasta file */
		try {
			fis = new FileInputStream(dataFile);
		} catch (FileNotFoundException e) {
			throw (e);
		}

		/* Try to access the Index file */
		try {
			br = new BufferedReader(new FileReader(indexFile));
			try {
				while ((line = br.readLine()) != null) {
					if (line.length() <= 0)
						continue;
					String[] map = line.split("\t");
					// if (map[0].compareTo(chr) == 0) {
					// globalOffset = Long.parseLong(map[2]);
					// charPerLine = Integer.parseInt(map[3]);
					// // System.out.println(offset);
					// }
					indexMap.put(map[0], new String[] { map[2], map[3] });
				}
			} finally {
				br.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Index file [" + indexFile + "] not found.");
			buildIndex(dataFile);
		} catch (IOException e) {
			throw (e);
		}
	}

	/**
	 * This method will generate an index file from <code>dataFile</code>. For
	 * every chromomsome in the <code>dataFile</code>, the method will append
	 * chromomsome's size, starting offset, line length and line length
	 * including the new line character.
	 * 
	 * @param dataFile
	 *            fasta file
	 */
	private static void buildIndex(String dataFile) {
		System.out.println("Building Index ... ");
		String indexFile = dataFile + ".idx";
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			String line = null;
			String tmp = null;
			long offset = 0;
			long chrSize = 0;
			long chrOffset = 0;
			int lineLength = 0;

			boolean isFirst = true;
			while ((line = br.readLine()) != null) {
				offset += (line.length() + EXTRA);
				if (line.length() <= 0)
					continue;
				if (line.charAt(0) == ';')
					continue;
				if (line.charAt(0) == '>') {
					if (!isFirst) {
						// if (tmp.compareTo(chr) == 0) {
						// globalOffset = chrOffset;
						// charPerLine = lineLength;
						// }
						sb.append(tmp);
						sb.append("\t");
						sb.append(Long.toString(chrSize));
						sb.append("\t");
						sb.append(Long.toString(chrOffset));
						sb.append("\t");
						sb.append(Long.toString(lineLength));
						sb.append("\t");
						sb.append(Long.toString(lineLength + EXTRA));
						sb.append("\t");
						sb.append(ENDOFLINE);

						indexMap.put(
								tmp,
								new String[] { Long.toString(chrOffset),
										Long.toString(lineLength) });

						chrSize = 0;
						chrOffset = 0;
						lineLength = 0;
					}
					isFirst = false;
					lineLength = 0;
					int pos = line.indexOf(' ');
					if (pos == -1)
						pos = line.length();
					tmp = line.substring(1, pos);
					chrOffset = offset;
				} else {
					chrSize += line.length();
					if (lineLength < line.length())
						lineLength = line.length();
				}
			}
			// if (tmp.compareTo(chr) == 0) {
			// globalOffset = chrOffset;
			// charPerLine = lineLength;
			// }
			sb.append(tmp);
			sb.append("\t");
			sb.append(Long.toString(chrSize));
			sb.append("\t");
			sb.append(Long.toString(chrOffset));
			sb.append("\t");
			sb.append(Long.toString(lineLength));
			sb.append("\t");
			sb.append(Long.toString(lineLength + EXTRA));
			sb.append("\t");
			sb.append(ENDOFLINE);

			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile));
			// bw.append(Integer.toString(charPerLine));
			// bw.append("\n");
			bw.append(sb.toString());
			bw.close();
		} catch (FileNotFoundException e) {
			System.err.println("Data file not found.");
			System.err.println("Abort!");
			System.exit(1);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * this method is for getting data given start and end position in one
	 * chromosome
	 * 
	 * @param chr
	 *            chromosome ID
	 * @param start
	 *            start postion
	 * @param end
	 *            end position
	 * @return data within start and end position, represented as a string
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public static String getData(String chr, int start, int end)
			throws IOException {
		if (end < start)
			throw (new RuntimeException(
					"End position smaller than start position."));

		long globalOffset = -1;
		if (indexMap.containsKey(chr))
			globalOffset = Long.parseLong(indexMap.get(chr)[0]);
		else if (indexMap.containsKey("chr" + chr))
			globalOffset = Long.parseLong(indexMap.get("chr" + chr)[0]);
		else
			throw (new RuntimeException("Chromosome " + chr
					+ " not found in Fasta file."));

		fis.getChannel().position(globalOffset);

		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(fis));

		// int numOfLines = 0;
		String line = null;
		int count = 0;
		int rest = end - start + 1;

		/*
		 * Use StringBuilder if frequent concatenation is needed. It is far more
		 * efficient than String + .
		 */
		StringBuilder ret = new StringBuilder(rest);

		try {
			while ((line = br.readLine()) != null) {
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
		} finally {
			// Cannot close here because it will also close InputStreamReader.
			// br.close();
			br = null;
		}
		return ret.toString();
	}

	// Implementation using RandomAccessFile.
	//
	// Drawback: run slowly.
	//
	// public static String getData(String chr, long start, long end) {
	// if (end < start) {
	// System.err.println("end smaller than start.");
	// System.err.println("Abort!");
	// System.exit(1);
	// }
	//
	// long globalOffset = Long.parseLong(indexMap.get(chr)[0]);
	// long charPerLine = Long.parseLong(indexMap.get(chr)[1]);
	//
	// // BuildIndex(dataFile);
	//
	// String line = null;
	//
	// StringBuilder sb = new StringBuilder();
	// long length = 0;
	// long readLength = end - start + 1;
	// long adjustment = start / charPerLine * EXTRA;
	// try {
	// raf.seek(globalOffset + start + adjustment);
	// while ((line = raf.readLine()) != null) {
	// // System.out.println(line);
	// if (length + line.length() < readLength) {
	// sb.append(line);
	// length += line.length();
	// } else {
	// line = line.substring(0, (int) (readLength - length));
	// sb.append(line);
	// length += line.length();
	// break;
	// }
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// // System.out.println(sb.toString());
	// return sb.toString();
	// }
}
