package edu.unc.csbio.RNASim;

import edu.unc.csbio.data.Read;

/**
 * The abstract class for all the quality score generator classes.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public abstract class QualityGenerator {
	/**
	 * An abstract method of <code>generate(Read[] r)</code>
	 * 
	 */
	public abstract String[] generate(Read[] r);
}
