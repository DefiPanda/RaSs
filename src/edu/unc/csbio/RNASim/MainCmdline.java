package edu.unc.csbio.RNASim;

import java.io.*;
import java.util.*;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;

import edu.unc.csbio.data.*;
import edu.unc.csbio.util.*;

/**
 * The class <code>MainCmdline</code> is the main class
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.3
 */
public class MainCmdline {
	/**
	 * this will be the prefix of the all read ID
	 */
	static String IDPREFIX = null;
	/**
	 * this will be the prefix of the all read ID
	 */
	static String chrom = null;
	/**
	 * specify the matching of chromosome string, Exact or Fuzzy.
	 */
	static String chromMatching = null;

	// static int annotLimit = 400000;
	/**
	 * max number of lines allowed for annotation file
	 */
	static int annotLimit = Integer.MAX_VALUE;
	/**
	 * input buffer size
	 */
	static int inputBufferSize = 1024 * 1024 * 10;
	/**
	 * output buffer size
	 */
	static int outputBufferSize = 1024 * 1024 * 10;
	/**
	 * buffer size for accumulating gene range
	 */
	static long processBufferSize = 1024 * 1024 * 40;
	/**
	 * read length
	 */
	static int readLength = -1;
	/**
	 * minimum sequence length
	 */
	static int fragMinLength = -1;
	/**
	 * maximum sequence length
	 */
	static int fragMaxLength = -1;

	/**
	 * coverage factor = total read length / total sequence size
	 */
	static double coverageFactor = -1;
	/**
	 * percentage of gene that will be expressed
	 */
	static double expressionPercentage = 0.0;
	/**
	 * distribution mean value, will be used when calculating abundance.
	 * abundance is related to distMean and probability associated with
	 * T-distribution.
	 */
	static double distMean = 5;
	/**
	 * the value used to calculate distMean. distMean = the value of
	 * TDistribution at tDistDOF
	 */
	static double tDistDOF = 10;
	/**
	 * time when program start running
	 */
	static long startTime;
	/**
	 * A blacklist of unprocessed ensembl transcript id.
	 */
	static HashSet<String> blacklist = null;
	/**
	 * a TreeMap that maps a string to a transcript
	 */
	static TreeMap<String, Transcript> transcriptPool = null;
	/**
	 * a TreeMap that maps a string to a gene
	 */
	static TreeMap<String, Gene> genePool = null;
	/**
	 * a Hashmap that maps a string to an abundance value
	 */
	static HashMap<String, Double> abundancePool = null;
	/**
	 * a TreeSet that stores the chromosome names
	 */
	static TreeSet<String> chromPool = null;
	/**
	 * an array that stores the structure variants
	 */
	static HashMap<String, ArrayList<Feature>> svPool = null;	
	/**
	 * an array that stores the genes
	 */
	static Gene[] genes;
	/**
	 * the file location of fq writer file (for first pair end read)
	 */
	static FqWriter out1 = null;
	/**
	 * the file location of fq writer file (for second pair end read)
	 */
	static FqWriter out2 = null;
	/**
	 * a qualityGenerator
	 */
	static QualityGenerator qg = null;
	/**
	 * total number of the reads
	 */
	static long totalReads = 0;
	/**
	 * total number of the genes
	 */
	static long totalGenes = 0;
	/**
	 * total number of transcripts
	 */
	static long totalTranscripts = 0;
	/**
	 * total number of reads in a chromosome
	 */
	static long totalReadsByChrom = 0;
	/**
	 * total number of transcripts in a chromosome
	 */
	static long totalTranscriptsByChrom = 0;
	/**
	 * total number of genes in a chromosome
	 */
	static long totalGenesByChrom = 0;

	/**
	 * the method to print out the run time of the program
	 */
	public static void printRuntime() {
		System.out.println("Time elapsed: "
				+ (System.currentTimeMillis() - startTime) / 1000.0 + "s.\n");
	}

	/**
	 * the method to abort the program
	 */
	public static void abort(Exception e) {
		// System.err.println(e);
		e.printStackTrace();
		System.err.println("Abort!");
		System.exit(1);
	}

	/**
	 * given an array, this method sequentially changes a position with another
	 * randomly selected position, for n times, where n is the size of array.
	 */
	public static void shuffle(Object[] array) {
		Random r;
		int j;
		Object tmp = null;
		for (int i = array.length - 1; i >= 1; --i) {
			r = new Random();
			j = r.nextInt(i + 1);
			tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	/**
	 * this method read configuration file
	 * 
	 * @param args the argument array from command line 
	 *            
	 */
	public static void ReadConfig(String[] args) {
		ArrayList<String> margs = new ArrayList<String>(Arrays.asList(args));
		
		String configFn = null;
		if (margs == null || margs.size() <= 0 || margs.get(0).matches("^-.*=.*")) {
			configFn = Config.filename;			
			margs.add(0, configFn);		
		} 
		else {
			configFn = margs.get(0);
		}
		System.out.println("Reading Configuration File [" + configFn + "] ...");		
		new Config(margs.toArray(new String[0]));		
		
		chrom = Config.get("Chromosome");
		chromMatching = Config.get("Chromosome_Matching");

		readLength = Integer.parseInt(Config.get("Read_Length"));
		fragMinLength = Integer.parseInt(Config.get("Fragment_Minimum_Length"));
		fragMaxLength = Integer.parseInt(Config.get("Fragment_Maximum_Length"));
		expressionPercentage = Double.parseDouble(Config
				.get("Expressed_Transcript_Percentage"));
		IDPREFIX = Config.get("Read_ID_Prefix");
		coverageFactor = Double.parseDouble(Config.get("Coverage_Factor"));
		
		inputBufferSize = Integer.parseInt(Config.get("Input_Buffer_Size"));
		outputBufferSize = Integer.parseInt(Config.get("Output_Buffer_Size"));
		processBufferSize = Long.parseLong(Config.get("Process_Window_Size"));
	}
	
	public static void ReadBlacklist() {
		if (Config.get("Blacklist")==null || Config.get("Blacklist").length()<1) {
			blacklist=null;
			return;
		}
			
		
		System.out.println("Reading Blacklist [" + Config.get("Blacklist")
				+ "] ... ");
		blacklist = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					Config.get("Blacklist")), inputBufferSize);
			int numOfLines = 0;
			try {
				String line = null;
				while ((line = br.readLine()) != null) {
					if (numOfLines % 1000 == 0)
						System.out.print((numOfLines + 1) + " ");
					++numOfLines;
					blacklist.add(line);
				}
			} finally {
				br.close();
			}
			System.out.println();
			System.out.print(numOfLines + " lines read.");			
			printRuntime();
		} catch (IOException e) {
			abort(e);
		}
	}
	
	/**
	 * this method reads annotation file.
	 */
	public static void ReadAnnotation() {
		transcriptPool = new TreeMap<String, Transcript>();
		genePool = new TreeMap<String, Gene>();
		chromPool = new TreeSet<String>();

		System.out.println("Reading Annotation File [" + Config.get("GTF_File")
				+ "] ... ");
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					Config.get("GTF_File")), inputBufferSize);
			int numOfLines = 0;
			try {
				String line = null;
				while ((line = br.readLine()) != null) {
					if (numOfLines % 100000 == 0)
						System.out.print((numOfLines + 1) + " ");
					++numOfLines;

					ArrayList<HashMap<String, String>> hma = GTFParser.parse(line);
					
					for (HashMap<String, String> hm : hma) {
						if (hm.get("feature").equals("exon")
								&& ((!chromMatching.equals("Exact") && hm.get(
										"seqname").matches(chrom)) || (chromMatching
										.equals("Exact") && hm.get("seqname")
										.equals(chrom)))
								&& (blacklist == null || !blacklist.contains(hm.get("_transcript_id")))
								// && !hm.get("source").equals("pseudogene")
								// && !hm.get("source").equals("polymorphic_pseudogene")
								// && !hm.get("_gene_name").matches("^Gm[0-9]+$")
								) {
	
							// System.out.println(hm);
							Exon e = new Exon(hm.get("seqname"),
									Integer.parseInt(hm.get("start")),
									Integer.parseInt(hm.get("end")));
							// System.out.println(e);
	
							chromPool.add(e.getChromosome());
	
							String gid = hm.get("_gene_id");
							String tid = hm.get("_transcript_id");
							Transcript t = null;
							Gene g = null;
	
							/* Recover transcript information */
							if (!transcriptPool.containsKey(tid)) {
								t = new Transcript(tid, hm.get("_transcript_name"),
										e);
								transcriptPool.put(tid, t);
							} else {
								t = transcriptPool.get(tid);
								t.addExon(e);
							}
	
							/* Recover gene information */
							if (!genePool.containsKey(gid)) {
								g = new Gene(gid, hm.get("_gene_name"),
										hm.get("_gene_biotype"), t);
								genePool.put(gid, g);
							} else {
								g = genePool.get(gid);
								g.addTranscript(t);
							}
						} // if hm.get ...						
					}					
					if (numOfLines >= annotLimit)
						break;
				} // while line
					// System.out.println(transcriptPool);
					// System.out.println(genePool);
			} finally {
				br.close();
			}
			System.out.println();
			System.out.print(numOfLines + " lines read: ");
			System.out.print(transcriptPool.size() + " transcripts, ");
			System.out.println(genePool.size() + " genes after filtering.");
			printRuntime();
		} catch (IOException e) {
			abort(e);
		}

		genes = genePool.values().toArray(new Gene[0]);

		// System.out.println("Sorting Genes ... ");
		// Arrays.sort(genes);
		// System.out.println(genes.length + " genes sorted.");
		// System.out.println("Time elapsed:"
		// + (System.currentTimeMillis() - startTime) / 1000.0 + "s\n");
	}

	/**
	 * this method creates abundance file with default settings
	 * 
	 * @param abundanceFn
	 *            file location of abundance file to be created
	 */
	public static void BuildAbundance(String abundanceFn) {
		System.out.println("Creating Abundance File with default settings ...");
		try {
			BufferedWriter out = new BufferedWriter(
					new FileWriter(abundanceFn), inputBufferSize);

			TDistributionImpl tDist = new TDistributionImpl(tDistDOF);

			Random r = new Random();
			double abundance = 0;
			double prob = 0;

			String[] tidArray = transcriptPool.keySet().toArray(new String[0]);
			shuffle(tidArray);

			int expressionCount = (int) (tidArray.length * expressionPercentage);

			try {
				for (int i = 0; i < expressionCount; ++i) {
					String tid = tidArray[i];
					/* transcript expressed. */
					prob = r.nextDouble();
					try {
						abundance = distMean
								+ tDist.inverseCumulativeProbability(prob);
					} catch (MathException e) {
						System.out.println(e.getMessage());
						System.out.println("Use random number instead.");
						abundance = distMean + prob;
					}
					if (abundance < 0)
						abundance = -abundance;
					abundancePool.put(tid, abundance);
					out.write(tid + " " + abundance + "\n");
				}
			} finally {
				out.close();
			}
			System.out.println(expressionCount + " lines/transcripts written.");
		} catch (IOException e) {
			abort(e);
		}
	}

	/**
	 * this method read the transcript abundance from
	 * <code>Abundance_File</code> in <code>Config</code> file
	 */
	public static void ReadTranscriptAbundance() {
		String abundanceFn = Config.get("Abundance_File");
		abundancePool = new HashMap<String, Double>();
		BufferedReader br = null;
		
		if (Config.get("Abundance_Overwritten").equals("Yes")) {
			System.out.println("Overwriting Transcript Abundance File ["
					+ Config.get("Abundance_File") + "] ... ");
			BuildAbundance(abundanceFn);
			printRuntime();
			return;
		}		
		
		System.out.println("Reading Transcript Abundance File ["
				+ Config.get("Abundance_File") + "] ... ");
		try {
			br = new BufferedReader(new FileReader(abundanceFn),
					inputBufferSize);
			int numOfLines = 0;
			try {
				String tmp = null;
				while ((tmp = br.readLine()) != null) {
					if (numOfLines % 10000 == 0)
						System.out.print((numOfLines + 1) + " ");
					++numOfLines;
					if (tmp != "") {
						String[] array = tmp.split(" ");
						for (String k : array) {
							k.trim();
						}
						abundancePool.put(array[0],
								Double.parseDouble(array[1]));
					}
				}
			} finally {
				br.close();
			}
			System.out.println();
			System.out.println(numOfLines + " lines/transcripts read.");
			printRuntime();
		} catch (FileNotFoundException a) {
			System.out.println("Abundance file [" + abundanceFn
					+ "] not found.");
			BuildAbundance(abundanceFn);
			printRuntime();

		} catch (IOException e) {
			abort(e);
		}

	}

	/**
	 * this method read structure variants.
	 */
	public static void ReadStructureVariant() {
		System.out.println("Reading Structure Variant File [" + Config.get("SV_File")
				+ "] for Sample ["+Config.get("SV_Sample")+"] ... ");
		
		svPool = new HashMap<String, ArrayList<Feature>>();
				
		int numOfLines = 0;
		int numOfInsertions = 0;
		int numOfDeletions = 0;
		int numOfSVs = 0;
		try {
			VCFReader vcf = new VCFReader(Config.get("SV_File"),Config.get("SV_Sample"));
			Feature sv = null;
			try {
				while ((sv = vcf.readNext()) != null) {
					if (numOfLines % 10000 == 0)
						System.out.print((numOfLines + 1) + " ");
					++numOfLines;
					
					// System.out.println(vcf.sampleColumn);
					if ( (!chromMatching.equals("Exact") && sv.getChromosome().matches(chrom))
						|| (chromMatching.equals("Exact") && sv.getChromosome().equals(chrom))) {
						if (!svPool.containsKey(sv.getChromosome())) {
							ArrayList<Feature> al = new ArrayList<Feature>();
							al.add(sv);							
							svPool.put(sv.getChromosome(), al);							
						}
						else {							
							svPool.get(sv.getChromosome()).add(sv);
						}
					}
					
					if (sv.getClass().getSimpleName().equals("Deletion"))
						++numOfDeletions;
					else if (sv.getClass().getSimpleName().equals("Insertion"))
						++numOfInsertions;
				}
			} finally{
				vcf.close();
			}			
			for(ArrayList<Feature> al : svPool.values()){
				numOfSVs += al.size();
			}
			System.out.println();
			System.out.println(numOfLines + " lines read.");
			System.out.println(numOfSVs+" structure variants after filtering.");
			System.out.print(numOfDeletions+" deletions, ");
			System.out.println(numOfInsertions+" insertions.");
			
			for (ArrayList<Feature> al : svPool.values()) {
				Collections.sort(al);
			}
			printRuntime();						
			
		} catch (IOException e) {
			abort(e);
		} catch (RuntimeException e) {
			abort(e);
		}
	}

	/**
	 * given a gene, this method generates read and appends read information to
	 * output fq file
	 * 
	 * @param g
	 *            A <code>Gene</code> object
	 * @param buffer
	 *            A <code>SequenceBuffer</code> object
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public static void GenerateSequenceForSingleGene(Gene g,
			SequenceBuffer buffer) throws IOException {

		String chromosome = g.getChromosome();
		Random random = new Random();
		ArrayList<Transcript> ts = g.getTranscripts();

		for (Transcript t : ts) {
			double coverage = 0;
			if (abundancePool.containsKey(t.getId()))
				coverage = abundancePool.get(t.getId()) * coverageFactor;
			else
				continue;

			/* Get a transcript sequence */
			String seq = t.getSequence(buffer);
			// System.out.println("Transcript "+ts.get(j).getStart()+"..."+ts.get(j).getEnd());
			// System.out.println(seq);

			ReadGenerator rg = new ReadGenerator(readLength, fragMinLength,
					fragMaxLength);

			/* Generate reads from a transcript sequence */
			int maxTimes = (int) Math.floor(coverage * t.getLength()
					/ (readLength * 2));

			// System.out.println(maxTimes);
			for (int times = 0; times < maxTimes; ++times) {
				Read[] r = null;
				String[] qs = null;				
				try {
					r = rg.generate(seq);
					if (r == null)
						continue;

					// System.out.println(r[0].sequence);
					qs = qg.generate(r);					
					
					if (Config.get("Flip_And_Reverse").equals("Yes"))
						ReadModifier.flipAndReverse(r);
					ReadModifier.modifyByQualityScoreAlt(r,qs);
					ReadModifier.modifyByProbability(r,Double.parseDouble(Config.get("Unknown_Factor")));
				} catch (RuntimeException e) {
					System.out.println("Error occurs in generating read.");
					e.printStackTrace();
					continue;
				}				
								
				
				// Output reads
				StringBuilder idsb = new StringBuilder();

				idsb.append(IDPREFIX);
				idsb.append(random.nextInt(10000));
				idsb.append(":");
				idsb.append(chromosome);
				idsb.append(":");
				idsb.append(t.refPos(r[0].offset));
				idsb.append(":");
				idsb.append(t.refPos(r[1].offset));
				idsb.append(":");								
				idsb.append(r[0].getStrand());
				idsb.append(":");
				idsb.append(r[1].getStrand());
				idsb.append(":");
				idsb.append(r[0].getChanges());
				idsb.append(":");
				idsb.append(r[1].getChanges());
				
				String readId = idsb.toString();

				StringBuilder sb1 = new StringBuilder(readLength * 4);
				sb1.append("@");
				sb1.append(readId);
				sb1.append("/1\n");
				sb1.append(r[0].sequence);
				sb1.append("\n");
				sb1.append("+");
				sb1.append(readId);
				sb1.append("/1\n");
				sb1.append(qs[0]);
				sb1.append("\n");

				StringBuilder sb2 = new StringBuilder(readLength * 4);
				sb2.append("@");
				sb2.append(readId);
				sb2.append("/2\n");
				sb2.append(r[1].sequence);
				sb2.append("\n");
				sb2.append("+");
				sb2.append(readId);
				sb2.append("/2\n");
				sb2.append(qs[1]);
				sb2.append("\n");

				out1.print(sb1.toString());
				out2.print(sb2.toString());

				++totalReadsByChrom;
			} // for times
			++totalTranscriptsByChrom;
		} // for each transcript
	}

	/**
	 * Given a chromosome, this method call
	 * <code>public static void GenerateSequenceForSingleGene(Gene g,
			SequenceBuffer buffer) throws IOException</code> on all genes of that
	 * chromosome, and prints out total genes, reads and transcripts associated
	 * with chromosome.
	 * 
	 * @param chrom
	 *            A chromosome
	 * @throws IOException
	 *             if an input or output exception occurred
	 */
	public static void GenerateSequenceForGenes(String chrom)
			throws IOException {

		System.out.println("Generating Sequences for Chromosome " + chrom
				+ " ... ");

		int dataStart = Integer.MAX_VALUE;
		int dataEnd = 0;
		int genesBuffered = 0;

		totalGenesByChrom = 0;
		totalTranscriptsByChrom = 0;
		totalReadsByChrom = 0;

		ArrayList<Gene> genesInChr = new ArrayList<Gene>(genes.length / 2);

		for (Gene g : genes) {
			if (g.getChromosome().equals(chrom))
				genesInChr.add(g);
		}

		// System.out.println(chrom);
		// System.out.println(genesInChr.size());

		Collections.sort(genesInChr);

		for (int i = 0; i < genesInChr.size(); ++i) {
			// System.out.println(genes[i].getRange());
			// System.out.println(genes[i].getStart());
			// System.out.println(genes[i].getEnd());
			// System.out.println();

			Gene g = genesInChr.get(i);

			/* Compute the data range of multiple genes */
			if (dataEnd < g.getEnd())
				dataEnd = g.getEnd();
			if (dataStart > g.getStart())
				dataStart = g.getStart();

			/* Accumulate the range until it is over the buffersize */
			if ((dataEnd - dataStart) < processBufferSize
					&& i < (genesInChr.size() - 1))
				continue;

			// System.out.print("["+(i+1-genesDone)+"]");

			/*
			 * To avoid reading the .fa file many times, use a buffer to read
			 * data for multiple genes each time.
			 */
			SequenceBuffer buffer = null;

			try {
				if (Config.get("SV_Allowed").equalsIgnoreCase("Yes")) {					
					buffer = new ExtendedSequenceBuffer(BigFaReader.getData(chrom,
							dataStart, dataEnd), dataStart, svPool.get(chrom));
				}
				else 
					buffer = new SequenceBuffer(BigFaReader.getData(chrom,
						dataStart, dataEnd), dataStart);
				// String buffer2 = FaReader.getData(Config.get("FASTA_File"),
				// dataStart, dataEnd);

				/*
				 * Using ExtendedSequenceBuffer later to support Structure
				 * Variant
				 */

				for (int k = genesBuffered; k < (i + 1); ++k) {
					g = genesInChr.get(k);
					GenerateSequenceForSingleGene(g, buffer);
					// System.out.println("Gene " + (k + 1) + " " + g.getStart()
					// + "..." + g.getEnd());
					totalGenesByChrom++;
					if (totalGenesByChrom % 100 == 1)
						System.out.print(totalGenesByChrom + " ");
				}
			} catch (RuntimeException e) {
				System.out.print(e.getMessage());
				// When runtime exception occurs, stop running on the
				// chromosome.
				break;
			} finally {
				dataStart = Integer.MAX_VALUE;
				dataEnd = 0;
				genesBuffered = i + 1;
			}
		} // for i

		totalGenes += totalGenesByChrom;
		totalTranscripts += totalTranscriptsByChrom;
		totalReads += totalReadsByChrom;

		System.out.println();
		System.out.println(totalGenesByChrom + " genes processed.");
		System.out.println(totalTranscriptsByChrom + " transcripts processed.");
		System.out.println(totalReadsByChrom + " reads generated.");
		printRuntime();
	}

	/**
	 * this method generate read sequence from given <code>Config</code> file
	 */
	public static void GenerateSequence() {
		System.out.println("Preparing ...");

		try {
			new BigFaReader(Config.get("FASTA_File"));
			System.out.println("FASTA file [" + Config.get("FASTA_File")
					+ "] in used.");
		} catch (IOException e) {
			abort(e);
		}

		qg = QualityGeneratorFactory.CreateQualityGenertor();
		System.out.println("Quality Score Generator ["
				+ qg.getClass().getSimpleName() + "] in used.");

		System.out.println("Min Fragment length: " + fragMinLength);
		System.out.println("Max Fragment length: " + fragMaxLength);
		System.out.println("Read length: " + readLength);

		String outFq1 = Config.get("Output_Fastq_1");
		String outFq2 = Config.get("Output_Fastq_2");
		System.out.println("Output written to [" + outFq1 + "] and [" + outFq2
				+ "]");
		System.out.print(chromPool.size()
				+ " chromosomes found in annotation: ");
		for (String chrom : chromPool) {
			System.out.print(chrom + " ");
		}
		System.out.println("\n");

		try {
			out1 = new FqWriter(outFq1, outputBufferSize);
			out2 = new FqWriter(outFq2, outputBufferSize);
			try {
				for (String chrom : chromPool) {
					GenerateSequenceForGenes(chrom);
				}
			} finally {
				out1.close();
				out2.close();
			}
			System.out.println("Finishing ...");
			System.out.println(totalGenes + " genes processed.");
			System.out.println(totalTranscripts + " transcripts processed.");
			System.out.println(totalReads + " reads generated.");
			printRuntime();
			System.out.println("Done!");
		} catch (IOException e) {
			abort(e);
		}
	}

	/**
	 * this is the driver for the whole program
	 * 
	 * @param args
	 *            program arguments
	 */
	public static void main(String[] args) {
		startTime = System.currentTimeMillis();

		/*
		 * if (args.length < 1) { System.err.println("Argument not enough.");
		 * System.err.println("Abort!");
		 * System.err.println("Usage: java -jar RNAseqSim.jar [config_file] [options]");
		 * System.exit(1); }
		 */	
		
		
		ReadConfig(args);		
		
		ReadBlacklist();
		
		ReadAnnotation();

		ReadTranscriptAbundance();
		
		if (Config.get("SV_Allowed").equalsIgnoreCase("Yes"))
			ReadStructureVariant();
		
		GenerateSequence();
		
		/*
		Read[] r = new Read[1];
		r[0]=new Read(10,"AACT");		
		System.out.println(r[0]);
		ReadModifier.modifyByProbability(r, 0.5);
		System.out.println(r[0]);
		
		System.out.println(r[0].getChanges());
		r[0].setStrand(false);
		System.out.println(r[0]);		
		System.out.println(r[0].getChanges());
		*/
	}
}