package edu.unc.csbio.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A BED Parser conforms to UCSC BED format (http://genome.ucsc.edu/FAQ/FAQformat.html#format1)
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */

public class BED2GTF {
	/**
	 * right basic fields as a String array
	 */
	
//	private static final String[] basicKeyArray 
//			= { "chrom", "chromStart", "chromEnd" };
//
//	private static final String[] optionalKeyArray 
//			= { "name", "score", "strand", "thickStart", "thickEnd", "itemRgb", 
//				"blockCount", "blockSizes", "blockStarts" };
	
	
	private static final String basicDelimiter = "[ \t]+";
	
	private static final String blockDelimiter = ",";
	
	private static long lineNumber = 0;

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
	public static ArrayList<String> parse(String line) {
		ArrayList<String> ret = new ArrayList<String>();
		
		if (line.toLowerCase().startsWith("track") || 
				line.toLowerCase().startsWith("browser")) {
			ret.add("#"+line);
			return ret;
		}
		
		if (line.startsWith("#")) {
			ret.add(line);
			return ret;
		}
		
		
		String stripped = BED2GTF.stripLine(line);		
		String[] valueArray = stripped.split(basicDelimiter, 12);
		
//		for(int i=0; i<valueArray.length; ++i){
//			System.out.println(valueArray[i]);
//		}
//		System.out.println("done\n");
		
		if (valueArray.length<3) {
			throw(new RuntimeException("Not enough fields in BED file"));
		}
		
		String chrom = valueArray[0];
		
		// In BED format, position starts at 0. 
		// To covert to GTF, we have to add 1.
		long chromStart = Long.parseLong(valueArray[1])+1;
		
		// In BED format, the end position is not included in the region.
		// But in GTF, it is included.
		long chromEnd = Long.parseLong(valueArray[2]);						
			
		String tid, tname, gid, gname;				
		
		if (valueArray.length >= 4)
			tid = valueArray[3];			
		else 
			tid = Long.toString(lineNumber);
		
		tname = tid;
		gid = tid;
		gname = tid;
		
		int score = 0;
		if (valueArray.length >= 5)
			score = Integer.parseInt(valueArray[4]);
		
		String strand = "+";		
		if (valueArray.length >= 6)
			strand = valueArray[5];
		
		int blockCount;
		String blockSize;
		String blockStarts;
		if (valueArray.length >= 12) {
			blockCount = Integer.parseInt(valueArray[9]);
			blockSize = valueArray[10];
			blockStarts = valueArray[11];						
		}
		else {
			blockCount = 1;
			blockSize = Long.toString(chromEnd-chromStart+1);
			blockStarts = "0";
		}		
//		System.out.println(blockCount);
//		System.out.println(blockSize);
//		System.out.println(blockStarts);
		
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append(chrom);
		sb1.append("\t");
		sb1.append("filename");
		sb1.append("\t");
		
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append(score);
		sb2.append("\t");
		sb2.append(strand);
		sb2.append("\t");
		sb2.append(".");
		sb2.append("\t");
		sb2.append("gene_id \""+gid+"\"; ");
		sb2.append("transcript_id \""+tid+"\"; ");
		sb2.append("gene_name \""+gname+"\"; ");
		sb2.append("transcript_name \""+tname+"\"; ");
		
		StringBuilder sb = new StringBuilder();
		sb.append(sb1.toString());
		sb.append("transcript");
		sb.append("\t");
		sb.append(chromStart);
		sb.append("\t");
		sb.append(chromEnd);		
		sb.append("\t");
		sb.append(sb2.toString());
		
//		System.out.println(sb.toString());
		ret.add(sb.toString());
		
		String[] exonStart = blockStarts.split(blockDelimiter);
		String[] exonSize  = blockSize.split(blockDelimiter);
		//reversed strand.
		if (strand.equals("-")) {
			int exonNum = 0;
			for (int i=blockCount-1; i>=0; --i) {
				sb = new StringBuilder();
				sb.append(sb1.toString());
				sb.append("exon");
				sb.append("\t");
				sb.append(chromStart+Long.parseLong(exonStart[i]));
				sb.append("\t");
				sb.append(chromStart+Long.parseLong(exonStart[i])+Long.parseLong(exonSize[i])-1);		
				sb.append("\t");
				sb.append(sb2.toString());
				++exonNum;				
				sb.append("exon_number \""+exonNum+"\"; ");
				ret.add(sb.toString());
			}
		}
		else {
			int exonNum = 0;
			for (int i=0; i<blockCount; ++i) {
				sb = new StringBuilder();
				sb.append(sb1.toString());
				sb.append("exon");
				sb.append("\t");
				sb.append(chromStart+Long.parseLong(exonStart[i]));
				sb.append("\t");
				sb.append(chromStart+Long.parseLong(exonStart[i])+Long.parseLong(exonSize[i])-1);		
				sb.append("\t");
				sb.append(sb2.toString());
				++exonNum;				
				sb.append("exon_number \""+exonNum+"\"; ");
				ret.add(sb.toString());
			}
				
		}
			
		return ret;
	}
	
	public static void main(String[] args) {
		if (args.length<1) {
			System.err.println("Argument not enought!");
			System.err.println("Usage: java -jar BED2GTF.jar BED_File");
			System.err.println("abort!");
			System.exit(1);
		}
		
		String filename = args[0];
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));
			try {
				String line = null;
				while ( (line=br.readLine())!= null ){
//					System.out.println(line);
					lineNumber++;
					ArrayList<String> data=parse(line);
					for(String l : data) {
						System.out.println(l);
					}
				}
			}finally{
				br.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
