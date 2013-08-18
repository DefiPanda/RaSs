package edu.unc.csbio.RNASim;

import edu.unc.csbio.data.Read;

/**
 * The class modifies pair-end reads from a sequence.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.2
 */
public class ReadModifier {
	/**
	 * all possible nucleotides as a char array
	 */
	private static char[] whole = { 'A', 'T', 'C', 'G' };

	/**
	 * the method to change original nucleotide based on given error probability
	 * 
	 * @param original nucleotide
	 * @param errorProb the error probability
	 * @return the character of new nucleotide
	 */
	protected static char change(char original, double errorProb) {
		if (errorProb > 1 || errorProb < 0)
			throw (new RuntimeException());
		char[] set = new char[3];
		int j = 0;
		for (int i = 0; i < 4; i++) {
			if (whole[i] != original) {
				if (j > 2) {
					// Other character occurs.
					break;
				}
				set[j] = whole[i];
				j++;
			}

		}
		
		double cptr = Math.random();
		if (cptr < errorProb / 3) {
			return set[0];
		} else if (cptr < 2 * errorProb / 3) {
			return set[1];
		} else if (cptr < errorProb) {
			return set[2];
		}
		return original;
	}

	/**
	 * a method to change read sequence based on given quality score For each
	 * char, qs[i], in the array, Qphred = qs[i] - 64. Also, Qphred = -10
	 * log10(e), where e is the estimated probability of a base being wrong
	 * 
	 * @param r read sequence
	 * @return new read sequence
	 * @throws RuntimeException
	 */
	public static Read[] modifyByQualityScore(Read[] r, String[] qs) throws RuntimeException {		
				
		for (int i = 0; i < r.length; ++i) {			
			if (r[i].length != qs[i].length())
				throw (new RuntimeException(
						"length of quality score not match the read"));
			
			double errorProb = 0;
			for (int j = 0; j < r[i].length; ++j) {
				/*
				 * qs = -10*log_10(e) e = 10^(-qs/10) = e^(-qs/10*log_e(10))
				 */
				errorProb = Math.exp(-(qs[i].charAt(j) - 64) * 1.0 / 10
						* Math.log(10));				

				r[i].sequence[j] = change(r[i].sequence[j], errorProb);
				
			}
		}

		return r;
	}
	
	/**
	 * The same as <code>modifyByQualtyScore</code>, except for 
	 * the error probability of 'B'.
	 */	
	public static Read[] modifyByQualityScoreAlt(Read[] r, String[] qs) throws RuntimeException {
		
		for (int i = 0; i < r.length; ++i) {			
			if (r[i].length != qs[i].length())
				throw (new RuntimeException(
						"length of quality score not match the read"));
			
			double errorProb = 0;
			for (int j = 0; j < r[i].length; ++j) {
				/*
				 * qs = -10*log_10(e) e = 10^(-qs/10) = e^(-qs/10*log_e(10))
				 */
				if(qs[i].charAt(j)!='B'){
				errorProb = Math.exp(-(qs[i].charAt(j) - 64) * 1.0 / 10
						* Math.log(10));}
				else {errorProb =0.045; }
              
				r[i].sequence[j] = change(r[i].sequence[j], errorProb);				
			}
		}
		return r;
	}
	
	/**
	 * 
	 * @param r read sequence
	 * @return new read sequence
	 * @throws RuntimeException
	 */
	public static Read[] flipAndReverse(Read[] r) throws RuntimeException{
		boolean isFirstForward = (Math.random()>=0.5);		
		r[0].setStrand(isFirstForward);
		
		for(int i=1; i<r.length; ++i)
			r[i].setStrand(!isFirstForward);				
		
		return r;
	}
	
	public static Read[] modifyByProbability(Read[] r, double prob) throws RuntimeException {
		for (int i = 0; i < r.length; ++i) {
			for (int j = 0; j < r[i].length; ++j) {
				r[i].sequence[j] = change(r[i].sequence[j], prob);
			}
		}
		return r;
	}
}
