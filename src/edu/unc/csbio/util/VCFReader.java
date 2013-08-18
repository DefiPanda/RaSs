package edu.unc.csbio.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import edu.unc.csbio.data.*;

/**
 * A VCFReader Parser conforms to VCF 4.0 (http://www.1000genomes.org/node/101)
 *  
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class VCFReader {
	private static String dataFile = null;
	private static String sampleName = null;
	private static int sampleColumn = -1;
	
	private static BufferedReader br = null;
	
	private static final int chrOffset = 0;
	private static final int posOffset = 1;
	private static final int refOffset = 3;
	private static final int altOffset = 4;	
	private static final int sampleOffset = 9;
	
	private static final String FS="\t";
	private static final String altFS=",";
	private static final String REF=".";
	
	/**
	 * Initialize the buffer, skip the meta-information lines, and get
	 * the column id of the selected sample.
	 *  
	 * @param _dataFile the file name of the VCF file
	 * @param _sampleName the selected sample name of the VCF file 
	 * @throws IOException
	 */
	public VCFReader(String _dataFile, String _sampleName) throws IOException {
		dataFile = _dataFile;
		sampleName = _sampleName;
				
		br = new BufferedReader(new FileReader(dataFile));
		String line = null;
		
		while ((line=br.readLine())!=null) {
			if (line.length()<=0) continue;
			
			// If it is not a meta-information line or a header line, stop. 
			// Usually, this condition will not be satisfied.
			// Because the header line should appear before that.
			if (line.charAt(0)!='#')
				break;
			
			if (line.charAt(0)=='#') {
				// the header line
				if ( line.length()>1 && line.charAt(1)!='#') {					
					String[] tmp = line.split(FS);
					for(int i=sampleOffset; i<tmp.length; ++i)
						if (tmp[i].equals(sampleName))
							sampleColumn=i;
					break;
				}
				// else the line is meta-infomation, skip
			}
		}
		
		if (sampleColumn<0)
			throw(new RuntimeException("Sample name not found in VCF file."));
	}
	
	/**
	 * Read the VCF file and return the next insertion/deletion object.
	 * @return A Feature object that is either a insertion or a deletion.
	 * @throws IOException
	 */
	public Feature readNext() throws IOException {
		String line = null;		
		String alt = null;
		String ref = null;
		int altId = -1;
		
		while ((line=br.readLine())!=null) {
			if (line.length()<=0) continue;
			if (line.charAt(0)=='#') continue;
			
			String[] field = line.split(FS);			
			if (!field[sampleColumn].equals(REF)) {
//				System.out.println(field[refOffset]+" "+field[altOffset]+" "+field[sampleOffset]);
				altId = Integer.parseInt(field[sampleColumn].split("/")[0]);
				alt = field[altOffset].split(altFS)[altId-1];
				ref = field[refOffset];
				
				if (ref.length()<alt.length()) {
//					if (alt.startsWith(ref))
					if (alt.endsWith(ref.substring(1))) {
//						System.out.println(field[chrOffset]+" "+field[posOffset]+" "+field[refOffset]+" "+" "+field[altOffset]+" "+field[sampleOffset]);						
						return new Insertion(field[chrOffset],Integer.parseInt(field[posOffset])+1,alt.substring(1, alt.length()-ref.length()+1));
					}						
					else {
						System.err.println("wrong!");
						System.err.println(field[refOffset]+" "+field[altOffset]+" "+field[sampleColumn]);
					}
				}		
				else {
//					if (ref.startsWith(alt))
					if (ref.endsWith(alt.substring(1))) {						
//						System.out.println(field[chrOffset]+" "+field[posOffset]+" "+field[refOffset]+" "+" "+field[altOffset]+" "+field[sampleOffset]);
						return new Deletion(field[chrOffset],Integer.parseInt(field[posOffset])+1,Integer.parseInt(field[posOffset])+ref.length()-alt.length());
					}						
					else {
						System.err.println("wrong!");
						System.err.println(field[refOffset]+" "+field[altOffset]+" "+field[sampleColumn]);
					}
				}
				break;				
			}				
		}
		return null;
	}
	
	/**
	 * Close the buffer
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (br!=null) 
			br.close();
	}
}
