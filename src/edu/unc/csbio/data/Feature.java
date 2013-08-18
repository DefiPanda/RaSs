package edu.unc.csbio.data;

/**
 * The abstract class for all annotation features. Each feature belongs to only
 * one chromosome, and it is in the region between its start position
 * (inclusively) and end position(inclusively) in the chromosome. The unit of a
 * position is a base pair. 
 * 
 * These properties are all valid for all inherited classes. 
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public abstract class Feature implements Comparable<Feature> {
	/**
	 * The chromosome of the feature.
	 */
	protected String chrom = null;

	/**
	 * The start position of the feature.
	 * 
	 */
	protected int start = Integer.MAX_VALUE;

	/**
	 * The end position of the feature.
	 */
	protected int end = 0;

	/**
	 * A necessary method for the <code>Comparable</code> interface. Features
	 * are ordered first by their chromosome alphabetically, then by start
	 * position, and finally by end position. Two features are considered to be
	 * the same only if their chromosome, start position and end position are
	 * the same, which means they are totally overlapped.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Feature f) {
		if (this.getChromosome().compareTo(f.getChromosome()) != 0)
			return this.getChromosome().compareTo(f.getChromosome());
		if (this.getStart() < f.getStart())
			return -1;
		if (this.getStart() > f.getStart())
			return 1;
		if (this.getEnd() < f.getEnd())
			return -1;
		if (this.getEnd() > f.getEnd())
			return 1;
		return 0;
	}

	/**
	 * Get the chromosome
	 * 
	 * @return chromosome
	 */
	public final String getChromosome() {
		return this.chrom;
	}

	/**
	 * Get the offset of the end position from the beginning of the chromosome.
	 * The position starts at 1.
	 * 
	 * @return end position.
	 */
	public final int getEnd() {
		return this.end;
	}

	/**
	 * Get the number of base pairs in the sequence related to this feature. 
	 * The length of a feature is not necessarily equal to its range. 
	 * For example, a complex/composite feature, such as a transcript, may 
	 * consists of a few disjoint segments.
	 * Another example is the insertion. It is range is zero, but the length
	 * is the length of the inserted sequence.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return this.getEnd() - this.getStart() + 1;
	}

	/**
	 * Get the size of the spanning/effective range of this feature, 
	 * from start position to end position in the reference coordinate.
	 * 
	 * @return the size of range
	 */
	public final int getRange() {
		return this.getEnd() - this.getStart() + 1;
	}

	/**
	 * Get the offset of the start position from the beginning of the
	 * chromosome. The position starts at 1.
	 * 
	 * @return start position.
	 */
	public final int getStart() {
		return this.start;
	}

	/**
	 * An abstract method of <code>toString()</code>
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();
}
