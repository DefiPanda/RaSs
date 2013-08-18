package edu.unc.csbio.data;

import java.util.ArrayList;
import java.util.Collections;

import edu.unc.csbio.data.Exon;

/**
 * The class extending <code>SequenceBuffer</code> for handling structure 
 * variants in the sequence.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.1
 */
public class ExtendedSequenceBuffer extends SequenceBuffer {
	/**
	 * An ArrayList of <code>Feature</code> representing structure variants in
	 * the sequence
	 */
	private final ArrayList<Feature> sv;

	/**
	 * Find the SV that has the position just before the given region
	 * @param start the start position of the region in reference coordinate.
	 * @param end the end position of the region in reference coordinate.
	 * @return the index of the found SV
	 */
	private int findFirstOverlappedSV(int start, int end) {
		
		Exon tmp = new Exon(sv.get(0).getChromosome(),start,end);		
		int index = Collections.binarySearch(sv, tmp);
				
		if (index < 0) 				
			return -index-2;		
		else
			return index-1;
	}
		
	/**
	 * A constructor for object <code>ExtendedSequenceBuffer</code>
	 * 
	 * @param _raw the sequence
	 * @param _offset the position difference between the start position of
	 *            sequence and the start position of the reference gene
	 * @param _sv an ArrayList of <code>Feature</code> representing
	 *            structure variants in the sequence
	 * */	
	public ExtendedSequenceBuffer(String _raw, int _offset,
			ArrayList<Feature> _sv) {
		super(_raw, _offset);
		sv = _sv;
	}

	/**
	 * Get the sequence for the region [start, end] including structure variants
	 * 
	 * @param start the start position of the region in reference coordinate.
	 * @param end the end position of the region in reference coordinate.
	 * @return the sequence in the specified region, including structure variants.
	 */
	@Override
	public String get(int start, int end) {
		StringBuilder sb = new StringBuilder(end-start+10);		
		Feature f = null;			
		int pos = start;
		
		//Find the first overlapped structure variant.
		int SVOffset = findFirstOverlappedSV(start, end);			
		if (SVOffset<0)
			SVOffset = 0;
		
		for (int i=SVOffset; i<sv.size(); ++i){			
			f = sv.get(i);			

			if (f.start < end && f.end > start) {				
				if ( pos < f.start )
					sb.append(super.get(pos, f.start-1));
				
				//If it is a deletion, then the next position will be
				//the position after its end position. 
				if (f.getClass().getSimpleName().equals("Deletion")) {
					pos = f.end+1;
				}
				//If it is an insertion, then the next position will be
				//still its end position.
				else if (f.getClass().getSimpleName().equals("Insertion")) {
					sb.append(( (Insertion) f).getInserted());
					pos = f.end+1;					
				} 				
				else 
					throw(new RuntimeException("Unknown SV type."));
				
			}
			if (f.start > end) break;
		}
		if (pos<end)
			sb.append(super.get(pos, end));
		
		return sb.toString();
	}
}
