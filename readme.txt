My Related Publication
        http://bioinformatics.oxfordjournals.org/content/29/13/i291.full.pdf

[Manual of RNAseqSim] 

NAME
	RNAseqSim - Simulate RNAseq Reads

SYNOPSIS
	java -jar RNAseqSim-version.jar [CONFIG] [OPTIONS]

DESCRIPTION
	RNAseqSim creates two FastQ files based on the user-defined settings.

CONFIG
	A text file that contains configuration RNAseqSim requires to generate reads.
	The avaiable settings in CONFIG file are the same as those in the OPTIONS.
	If CONFIG is not specified, "config.txt" will be used.

OPTIONS
	The key-value strings that specify configuration.
	OPTIONS are represeted in "-key=value" format. The available keys and values
	are listed in DETAILS.
	
DETAILS			
    	GTF_File
    		The required input file name of the annotation file in GTF(2.2) 
		format. 
    		It contains the exons, transcripts and genes annotation data.
		
	FASTA_File
		The required input file name of the genome sequence in Fasta format.
		It contains the sequence data of some chromosomes or the entire genome.
	
	Chromosome 
		The chromosome or chromosome patterns that are used.
		e.g., chr1 or 1 for exact matching, and [[0-9]* for fuzzy matching.

	Chromosome_Matching
		The matching method of chromosomes. 
		Available values are "Exact" and "Fuzzy".
		If "Exact" is used, the program will work on the chromosome that has
		the exact name of the Chromosome value. Otherwise, it will use regular
		expression to match the Chromosome value.
	
	Blacklist
		The file name of a list of ensembl transcript ids that are not included 
		in the simulation.
		It is a blank string by default.				

	Abundance_File 
		The file name of transcript abundance. Each line in the file contains 
		a transcriptId (consistent with the ones in GTF_File) and an abundance 
		value. The two fields are separated by a blank space.		
		If specified, the abundance value will be used to generate reads; 
		otherwise, the program will dump the abundance values randomly generated
		to this file. The default name is "abundance.txt".

	Abundance_Overwritten
		A switch that specifies whether the abundance file is overwritten if
		it exists.
		Available values are "Yes" and "No"(default).
	
	Expressed_Transcript_Percentage
		If "Abundance_File" is not specified, the program will randomly select 
		this percentage	of transcripts to generate abundance values.
	
	SV_Allowed
		A switch that specifies whether structure variants are included.
		Available values are "Yes" and "No".

	SV_File
		The input file name of structure variants in VCF(4.0) format.
					
	SV_Sample
		The name of the sample of which the structure variants in "SV_File"
		will be embeded in the sequence.

	Read_Length
		The length of each simulated read. Its default value is 100.

	Fragment_Minimum_Length
		The minimum length of a fragment simulated. Its default value is 150.
		It is better to specify a value greater than "Read_Length", because
		no read will be created for a fragment whose length is shorter than 
		"Read_Length".
	
	Fragment_Maximum_Length
		The maxium length of a fragment simulated. Its default value is 400.

	Coverage_Factor
		The coverage factor, together with the abundance of a transcript, 
		affects the number of reads generated in a unit of region.
	
	Read_ID_Prefix
		The prefix of each read ID.

	Flip_And_Reverse
		A switch that specifies whether to flip and reverse one of the ends
		of the reads. 
		Available values are "Yes" and "No".

	Quality_Generator 
		The quality score generator in use. perfect or real.
		Available values are "Perfect" for the maximum(best) quality score for 
		each read, and "Real" for sampling real Fastq files for quality score.
	
	Real_Quality_Score_Fastq_1
		The real Fastq file for generating quality score for the first pair end 
		read if "Quality_Generator" is "Real".

	Real_Quality_Score_Fastq_2
		The real Fastq file for generating quality score for the second pair end
		read if "Quality_Generator" is "Real".

	Unknown_Factor
		The probability of changing a base-pair. It is a factor of simulating
		random noise, unknown mismatches, RNA Editing, and etc.

	Output_Fastq_1
		The output of the first pair end reads.
	
	Output_Fastq_2
		The output of the second pair end reads.

	Input_Buffer_Size
		The size of input file buffer.
	
	Output_Buffer_Size
		The size of the output file buffer.
	
	Process_Window_Size
		The size of the window on the reference coordinate processed each time					
	
OUTPUT
	Two files, specified in "Output_Fastq_1" and "Output_Fastq_2", are the main
	output that contain all reads.

	If the abundance file does not exist, it will be created with the abundance
	value the program randomly chooses.
	
	An .INX file will also be built for fast accessing the "FASTA_File".
		
AUTHORS
	Shunping Huang <sphuang@cs.unc.edu>
	Jack Wang <zhew@live.unc.edu>

VERSION
	1.3
