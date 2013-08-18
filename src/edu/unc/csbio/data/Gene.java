package edu.unc.csbio.data;

import java.util.ArrayList;

/**
 * The class <code>Gene</code> inherits abstract class <code>Feature</code> and
 * contains information of gene.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class Gene extends Feature {
	/**
	 * The name, id and biotype of the gene.
	 */
	private String name, id, biotype;
	/**
	 * An ArrayList of transcripts in the gene
	 */
	private ArrayList<Transcript> transcripts;

	/**
	 * A constructor for <code>Gene</code> object
	 * 
	 * @param _id id of the gene
	 * @param _name name of the gene
	 * @param _biotype biotype of the gene
	 * @param _t a list of transcript in the gene
	 */
	public Gene(String _id, String _name, String _biotype, Transcript _t) {
		this.id = _id;
		this.name = _name;
		this.biotype = _biotype;
		this.transcripts = new ArrayList<Transcript>();
		this.addTranscript(_t);
	}

	/**
	 * The method to get transcripts in the gene
	 * 
	 * @return an ArrayList of transcripts
	 */
	public ArrayList<Transcript> getTranscripts() {
		return transcripts;
	}

	/**
	 * The method to add a new transcript
	 * 
	 */
	public void addTranscript(Transcript t) {
		if (this.chrom == null)
			this.chrom = t.getChromosome();
		else if (!this.chrom.equals(t.getChromosome()))
			throw (new RuntimeException("chromosome in transcripts not matched"));

		this.start = this.start <= t.getStart() ? this.start : t.getStart();
		this.end = this.end >= t.getEnd() ? this.end : t.getEnd();
		if (!this.transcripts.contains(t))
			this.transcripts.add(t);
	}

	/**
	 * The method to get the exon part length of the gene
	 * 
	 * @return the length of a transcript, which has the max length
	 */
	@Override
	public int getLength() {
		int length = 0;
		for (int i = 0; i < transcripts.size(); ++i)
			if (length < transcripts.get(i).getLength())
				length = transcripts.get(i).getLength();
		return length;
	}

	/**
	 * A method to get gene information
	 * 
	 * @see java.lang.Object#toString()
	 * @return a string containing gene id, name, biotype, chromosome name,
	 *         start/end position and its transcripts information
	 */
	@Override
	public String toString() {
		String ret = "\n(Gene " + this.id + " " + this.name + " "
				+ this.biotype + ")" + "Chr" + this.getChromosome() + ":"
				+ this.getStart() + "..." + this.getEnd() + "{";
		for (int i = 0; i < transcripts.size(); ++i)
			ret += (i > 0 ? "," : "") + transcripts.get(i);
		ret += "\n" + "}\n";
		return ret;
	}
}
