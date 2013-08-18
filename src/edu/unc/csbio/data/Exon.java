package edu.unc.csbio.data;


/**
 * The class <code>Exon</code> inherits abstract class <code>Feature</code>.
 * Each exon belongs to only one chromosome, and it is in the region between its
 * start position (inclusively) and end position(inclusively) in the chromosome.
 * The unit of a position is a base pair.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */

public class Exon extends Feature {
	/**
	 * A constructor for <code>Exon</code> object
	 * 
	 * @param _chrom name of the chromosome
	 * @param _start start position of exon
	 * @param _end end position of exon
	 */
	public Exon(String _chrom, int _start, int _end) {
		if (_start > _end)
			throw (new RuntimeException(
					"Start position greater than end position."));
		if (_start < 1)
			throw (new RuntimeException("Start position smaller than 1."));
		if (_end < 1)
			throw (new RuntimeException("End position smaller than 1."));

		this.chrom = _chrom;
		this.start = _start;
		this.end = _end;
	}

	/**
	 * A method to get the sequence of an exon from a mother sequence 
	 * (a sequence buffer).
	 * 
	 * @param motherSequence a sequence buffer
	 * @return exon sequence
	 */
	public String getSequence(SequenceBuffer motherSequence) {
		return motherSequence.get(this.getStart(), this.getEnd());
	}

	/**
	 * A method to get exon length
	 * 
	 * @see java.lang.Object#toString()
	 * @return exon length
	 */
	@Override
	public int getLength() {
		return this.getEnd() - this.getStart() + 1;
	}

	/**
	 * A method to get exon information
	 * 
	 * @see java.lang.Object#toString()
	 * @return a string containing chromosome name, start position and end
	 *         position
	 */
	@Override
	public String toString() {
		return "\n" + "  (Exon)Chr" + this.chrom + ":" + this.start + "..."
				+ this.end;
	}
}
