package edu.unc.csbio.data;

/* To be developed for Structure Variants*/
/**
 * The class for insertion variant. This class is to be developed.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class Insertion extends Feature {
	/**
	 * An char array of sequences to be inserted
	 */
	char[] insertedSequence = null;

	/**
	 * A constructor for <code>Insertion</code> object
	 * The end position is smaller than start position. Because the effective
	 * range of an insertion is zero on the reference coordinate. 
	 * 
	 * @param chrom name of the chromosome
	 * @param insertedPos the first position for the inserted sequence
	 * @param sequence the sequence to be inserted
	 */
	public Insertion(String chrom, int insertedPos, String sequence) {
		this.chrom = chrom;
		this.start = insertedPos;
		this.end = start-1;
		this.insertedSequence = sequence.toCharArray();
	}

	/**
	 * Get the length of the insertion, which is the length of the inserted
	 * sequence.
	 * 
	 * @see edu.unc.csbio.data.Feature
	 * @return The length of an insertion (i.e. the length of the inserted
	 *         sequence)
	 */
	@Override
	public int getLength() {
		return insertedSequence.length;
	}

	public char[] getInserted() {
		return insertedSequence;
	}
	
	/**
	 * A stub method for toString()
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Insertion: "+chrom+" "+start+" "+(new String(insertedSequence));
	}

}
