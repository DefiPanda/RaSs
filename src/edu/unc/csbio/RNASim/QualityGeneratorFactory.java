package edu.unc.csbio.RNASim;

import java.io.IOException;

/**
 * The factory that creates different quality score generator objects based on
 * configuration.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public final class QualityGeneratorFactory {
	/**
	 * this method creates different quality score generator object based on
	 * configuration.
	 * 
	 * @return a quality score generator object
	 */
	public static QualityGenerator CreateQualityGenertor() {
		String qgType = Config.get("Quality_Generator");
		QualityGenerator qg = null;
		try {
			if (qgType.compareTo("Real") == 0) {
				String realFq1 = Config.get("Real_Quality_Score_Fastq_1");
				String realFq2 = Config.get("Real_Quality_Score_Fastq_2");
				qg = new RealQualityGenerator(realFq1, realFq2);
			} else
				qg = new PerfectQualityGenerator();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			qg = new PerfectQualityGenerator();
		}
		return qg;
	}
}
