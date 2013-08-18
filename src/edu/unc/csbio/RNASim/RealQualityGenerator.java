package edu.unc.csbio.RNASim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import edu.unc.csbio.data.Read;

/*
 * May need to extend to one-end and two-end reads.
 */
/**
 * The class <code>RealQualityGenerator</code> extending
 * <code>QualityGenerator</code> is for generating quality score from real fq
 * files.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class RealQualityGenerator extends QualityGenerator {
	/**
	 * two BufferedReader to read two fq files
	 */
	private BufferedReader br1 = null, br2 = null;
	/**
	 * location of two fq files
	 */
	private String fq1 = null, fq2 = null;
	/**
	 * to check whether quality score is found
	 */
	private boolean isQualityScoreFound;

	/**
	 * this method reset BufferReader <code>br1</code> and <code>br2</code>
	 * 
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	private void ResetBufferReader() throws IOException {
		try {
			if (br1 != null)
				br1.close();
			if (br2 != null)
				br2.close();
		} finally {
			br1 = new BufferedReader(new FileReader(fq1));
			br2 = new BufferedReader(new FileReader(fq2));
		}
	}

	/**
	 * this is the constructor for object <code>RealQualityGenerator</code>
	 * 
	 * @param _fq1
	 *            location of first fq file
	 * @param _fq2
	 *            location of second fq file
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public RealQualityGenerator(String _fq1, String _fq2) throws IOException {
		fq1 = _fq1;
		fq2 = _fq2;
		isQualityScoreFound = false;
		ResetBufferReader();
	}

	/**
	 * Generate read quality score by sampling real Fastq file.
	 * 
	 * @param r an array of read
	 * @return a String array of quality scores 
	 * @throws  there is not enough lines in real Fastq file
	 */
	@Override
	public String[] generate(Read[] r) throws RuntimeException {
		String[] ret = { null, null };
		int i = 0;
		String line1 = null, line2 = null;
		try {
			i = 0;
			while (i < 4) {
				if ((line1 = br1.readLine()) != null
						&& (line2 = br2.readLine()) != null)
					++i;
				else {
					if (isQualityScoreFound)
						/*
						 * To avoid infinite loop if fastq file is less than 4
						 * lines.
						 */
						ResetBufferReader();
					else
						throw (new RuntimeException(
								"Not enough lines in real Fastq file."));
					i = 0;
				}
			}
			isQualityScoreFound = true;
			ret[0] = line1;
			ret[1] = line2;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
