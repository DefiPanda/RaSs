package edu.unc.csbio.data;

import java.util.TreeSet;

import edu.unc.csbio.data.Exon;

/**
 * The class <code>Transcript</code> inherits abstract class
 * <code>Feature</code> and contains information of transcript.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.2
 */
public class Transcript extends Feature {
	/**
	 * The name and id of the transcript.
	 */
	private String name, id;
	/**
	 * An TreeSet of exons in the gene.
	 * The exons are ordered by its location.
	 * For some transcripts, its strand is "-" and the order of exons
	 * number is reversed to the order of the location.
	 */
	private TreeSet<Exon> exons;

	/**
	 * A constructor for <code>Transcript</code> object
	 * 
	 * @param _id id of the transcript
	 * @param _name name of the transcript
	 * @param _e the first exon in the transcript
	 */
	public Transcript(String _id, String _name, Exon _e) {
		this.id = _id;
		this.name = _name;
		this.exons = new TreeSet<Exon>();
		this.addExon(_e);
	}

	/**
	 * the method to get id of transcript
	 * 
	 * @return transcript id
	 */
	public String getId() {
		return id;
	}

	/**
	 * The method to add a new exon
	 * 
	 */
	public void addExon(Exon e) {
		if (this.chrom == null)
			this.chrom = e.getChromosome();
		else if (!this.chrom.equals(e.getChromosome()))
			throw (new RuntimeException("chromosome in exons not matched"));

		for (Exon exon : exons) {
			if (exon.getStart() > e.getEnd()
					|| exon.getEnd() < e.getStart())
				continue;
			else
				throw (new RuntimeException("exons overlapped"));
		}

		this.start = this.start <= e.getStart() ? this.start : e.getStart();
		this.end = this.end >= e.getEnd() ? this.end : e.getEnd();
		this.exons.add(e);
	}

	/**
	 * The method to convert a position in transcript sequence coordinate to
	 * the reference coordinate
	 * 
	 * @return the position in reference gene
	 */
	public int refPos(int seqPos) {
		int count = 0;
		for (Exon exon : exons) {
			if (count + exon.getLength() > seqPos)
				return exon.getStart() + seqPos - count;
			else
				count += exon.getLength();
		}
		throw (new RuntimeException("bad sequence position"));
	}

	/**
	 * The method to get a transcript sequence from a sequence buffer. 
	 * @param motherSequence a sequence buffer
	 * @return a transcript sequence
	 */
	public String getSequence(SequenceBuffer motherSequence) {
		StringBuilder ret = new StringBuilder(this.getLength());
		for (Exon exon : exons)
			ret.append(exon.getSequence(motherSequence));
		return ret.toString();
	}

	/**
	 * The method to get the exon part length of the transcript
	 * 
	 * @return the total length of all exons on the transcript
	 */
	@Override
	public int getLength() {
		int length = 0;
		for (Exon exon : exons)
			length += exon.getLength();
		return length;
	}

	/**
	 * A method to get transcript information
	 * 
	 * @see java.lang.Object#toString()
	 * @return a string containing transcript id, name, biotype, chromosome
	 *         name, start/end position and its exons information
	 */
	@Override
	public String toString() {
		String ret = "\n" + " (Transcript " + this.id + " " + this.name + ")"
				+ "Chr" + this.getChromosome() + ":" + this.getStart() + "..."
				+ this.getEnd() + "{";
		
		int i = 0; 
		for (Exon exon : exons) {
			ret += (i > 0 ? "," : "") + exon;
			++i;
		}
		ret += "\n" + " }";
		return ret;
	}

}
