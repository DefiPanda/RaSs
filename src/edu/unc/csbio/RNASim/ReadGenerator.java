package edu.unc.csbio.RNASim;

import edu.unc.csbio.data.Read;

/**
 * The class generates pair-end reads from a sequence.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class ReadGenerator {
	/**
	 * read length
	 */
	private final int readLength;
	/**
	 * minimum sequence length
	 */
	private final int fragMinLength;
	/**
	 * maximum sequence length
	 */
	private final int fragMaxLength;

	/**
	 * A constructor for <code>ReadGenerator</code> object
	 * 
	 * @param _readLength read length
	 * @param _fragMinLength minimum sequence length
	 * @param _fragMaxLength maximum sequence length
	 */
	public ReadGenerator(int _readLength, int _fragMinLength, int _fragMaxLength) {
		this.readLength = _readLength;
		this.fragMinLength = _fragMinLength;
		this.fragMaxLength = _fragMaxLength;
	}

	/**
	 * Randomly sample a fragment from a transcript sequence, and then
	 * return its both ends.
	 * 
	 * @param sequence the transcript sequence.
	 * @return an array for pair-end reads,  null if the sequence is 
	 *         too short to get reads
	 */
	public Read[] generate(String sequence) {
		String[] reads = new String[2];
		reads[0] = "";
		reads[1] = "";

		int seqLength = sequence.length();
		if (seqLength < fragMinLength)
			return null;

		int fragLength = fragMinLength
				+ (int) (Math.random() * ((seqLength > fragMaxLength ? fragMaxLength
						: seqLength) - fragMinLength));

		// System.out.println(seqLength);
		// System.out.println(fragMinLength);
		// System.out.println(fragMaxLength);
		// System.out.println(fragLength);

		int fragStart = (int) (Math.random() * (seqLength - fragLength));

		String fragment = sequence.substring(fragStart, fragStart + fragLength);

		reads[0] = fragment.substring(0, readLength);
		reads[1] = fragment.substring(fragLength - readLength, fragLength);

		Read[] r = new Read[] { new Read(fragStart, reads[0]),
				new Read(fragStart + fragLength - readLength, reads[1]) };
		return r;
	}

}
