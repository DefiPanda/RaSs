package edu.unc.csbio.data;

/* To be developed for Structure Variants*/

/**
 * The class for deletion variant. This class is to be developed.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */

public class Deletion extends Feature {
	/**
	 * A constructor for <code>Deletion</code> object
	 * 
	 * @param chrom name of the deletion
	 * @param start start position of the deletion
	 * @param end end position of deletion
	 */
	public Deletion(String chrom, int start, int end) {
		this.chrom = chrom;
		this.start = start;
		this.end = end;
	}

	/**
	 * A stub method for toString()
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Deletion: "+chrom+" "+start+" "+end;
	}
}
