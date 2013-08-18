package edu.unc.csbio.data;

import java.util.Arrays;

/**
 * The class for read information, including sequence string and the offset
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.2
 */
public class Read {
	/**
	 * The offset of a read in a segment sequence coordinate
	 */
	public final int offset;
	/**
	 * The length of the read
	 */
	public final int length;
	/**
	 * The original read sequence, before any modification
	 */
	public final char[] originSequence;
	
	/**
	 * The current read sequence, after modification
	 */
	public char[] sequence;
	
	/**
	 * The strand of the read: 1 for forward and -1 for reversed. 
	 */
	private int strand = 0; 
		
	/**
	 * A constructor for <code>Read</code> object
	 * 
	 * @param _off the offset of a read in a segment sequence coordinate
	 * @param _seq the read sequence
	 */
	public Read(int _off, String _seq) {
		this.offset = _off;
		this.originSequence = _seq.toCharArray();
		this.length = this.originSequence.length;
		this.strand = 1;
		this.sequence=Arrays.copyOf(originSequence, this.length);		
	}
	
	/**
	 * Set the strand of the read. If it is reversed, the sequence
	 * of the read is flip and reverse.
	 * @param isForward true if forward strand, and false if reversed
	 */
	public void setStrand(boolean isForward) {				
		if (isForward)
			this.strand=1;
		else {
			this.strand=-1;
			this.sequence = reverse(flip(sequence));
		}
	}
	
	/**
	 * Get the strand of the read
	 * @param i
	 * @return
	 */
	public int getStrand() {
		return this.strand;
	}		
	
	/**
	 * 
	 * @return
	 */
	public int getChanges(){		
		int count = 0;
		char[] compSeq = null;
		if (this.strand > 0)
			compSeq = sequence;
		else
			compSeq = reverse(flip(sequence));
		
		for(int i=0; i<length; ++i)
			if (originSequence[i]!=compSeq[i])
				count ++;			
		return count;
	}
	
	/**
	 * Flip the allele to get the complementary strand
	 * @param seq old sequence
	 * @return new sequence
	 */
	public static char[] flip(char[] seq) {
		char[] ret = new char[seq.length];
		for(int i=0; i<seq.length; ++i){
			switch (seq[i]){
				case 'A': ret[i]='T'; break;
				case 'T': ret[i]='A'; break;
				case 'C': ret[i]='G'; break;
				case 'G': ret[i]='C'; break;
				case 'N': ret[i]='N'; break;
				default: throw(new RuntimeException("Unknown charaters when flipping"));				
			}
		}		
		return ret;
	}
	
	/**
	 * Reverse the sequence
	 * @param seq old sequence
	 * @return new sequence
	 */
	public static char[] reverse(char[] seq) {
		char[] ret = new char[seq.length];
		for(int i=0; i<seq.length; ++i)
			ret[ret.length-1-i] = seq[i];
		return ret;
	}
	
	/**
	 * A method to get read sequence
	 * 
	 * @see java.lang.Object#toString()
	 * @return the read sequence as a string
	 */
	@Override
	public String toString() {
		return new String(this.sequence);
	}
}
