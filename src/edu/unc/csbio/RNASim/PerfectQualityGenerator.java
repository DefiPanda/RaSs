package edu.unc.csbio.RNASim;

import java.util.Arrays;

import edu.unc.csbio.data.Read;

/**
 * The class <code>PerfectQualityGenerator</code> extending
 * <code>QualityGenerator</code> is for generating perfect quality score.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class PerfectQualityGenerator extends QualityGenerator {
	/**
	 * given a read, this method generates perfect quality score for every
	 * position of the read
	 * 
	 * @param r the array of reads.
	 * @return a String array of quality score
	 */
	@Override
	public String[] generate(Read[] r) {
		String[] ret = new String[r.length];

		for (int i = 0; i < r.length; ++i) {
			char[] tmp = new char[r[i].length];
			Arrays.fill(tmp, '~');
			ret[i] = new String(tmp);
		}
		return ret;
	}
}
