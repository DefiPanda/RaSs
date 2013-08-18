package edu.unc.csbio.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A GTF Parser conforms to GTF2.2 (http://mblab.wustl.edu/GTF22.html)
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.2
 */

public class GTFParser {
	/**
	 * right basic fields as a String array
	 */
	private static final String[] basicKeyArray = { "seqname", "source",
			"feature", "start", "end", "score", "strand", "frame" };

	/**
	 * three delimiters
	 */
	private static final String basicDelimiter = "\t",
			attributeDelimiter = ";", assignmentDelimiter = " ";
	/**
	 * a quote sign
	 */
	private static final String quoteSign = "\"";

	/**
	 * A method to remove the leading and trailing whitespaces. "#" will be
	 * removed.
	 * 
	 * @param line original line
	 * @return formatted line
	 */
	private static String stripLine(String line) {
		String ret = line.trim();
		int commentPos = ret.indexOf('#');
		if (commentPos < 0)
			return ret;
		return ret.substring(0, commentPos);
	}

	/**
	 * This method returns a HashMap that contain the fields. Each field is
	 * represented as a key=>value mapping The first eight fields are the basic
	 * fields, and the names of keys are listed in the array. The last field
	 * contains additional attributes. Those attributes are also inserted into
	 * the HashMap as the basic fields, except the names of their keys will have
	 * a "_" as prefix.
	 * 
	 * @param line original line
	 * @return a HashMap that contain the fields
	 */
	public static ArrayList<HashMap<String, String>> parse(String line) {				
		ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>(1);
		HashMap<String, String> fields = new HashMap<String, String>();
		
		String stripped = GTFParser.stripLine(line);
		String[] valueArray = stripped.split(basicDelimiter, 9);
		for (int i = 0; i < 8; ++i)
			fields.put(basicKeyArray[i], valueArray[i]);

		String[] attributeArray = valueArray[8].split(attributeDelimiter);
		for (int i = 0; i < attributeArray.length; ++i) {
			String[] tmp = attributeArray[i].trim().split(assignmentDelimiter,
					2);
			fields.put("_" + tmp[0].trim(),
					tmp[1].trim().replaceAll(quoteSign, ""));
		}
		
		ret.add(fields);
		return ret;
	}
}
