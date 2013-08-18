package edu.unc.csbio.data;

/**
 * The class for sequence information, including sequence string and the offset
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class SequenceBuffer {
	/**
	 * The sequence of the object <code>SequenceBuffer</code>
	 */
	private String raw = null;
	/**
	 * The the position difference between the start position of sequence and
	 * the start position of the reference gene, of the object
	 * <code>SequenceBuffer</code>
	 */	
	private int offset = -1;

	private int length = -1;
	/**
	 * A constructor for object <code>SequenceBuffer</code>
	 * 
	 * @param _raw the sequence
	 * @param _offset the position difference between the start position of
	 *            sequence and the start position of the reference gene
	 * */
	public SequenceBuffer(String _raw, int _offset) {
		raw = _raw;
		offset = _offset;
		length = raw.length();
	}

	/**
	 * Get the sequence for the region [start, end]
	 * 
	 * @param start the start position of the region in reference coordinate.
	 * @param end the end position of the region in reference coordinate.
	 * @return the sequence in the specified region
	 */
	public String get(int start, int end) {
		if (end < offset || start>(offset+length))
			throw(new RuntimeException("Required sequence out of range"));
		return raw.substring(start-offset, end-offset+1);
	}
}
