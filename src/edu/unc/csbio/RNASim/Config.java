package edu.unc.csbio.RNASim;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

/**
 * The class <code>Config</code> is for reading and processing configuration
 * file. There are three pieces of source for configuration: default setting,
 * config file, and command line. 
 * The priority of them are command line > config_file > default.
 * 
 * @author Shunping Huang <sphuang@cs.unc.edu>, Jack Wang <zhew@live.unc.edu>
 * @version 0.3
 */
public class Config {
	public static final String filename = "config.txt";
	/**
	 * A LinkHashMap that maps configuration description to configuration data.
	 * It is initialized with default settings. The configuration data may be
	 * later replaced by values from configuration file or command line.
	 */
	@SuppressWarnings("serial")
	public static Map<String, String> configuration = new LinkedHashMap<String, String>() {
		{
			put("GTF_File", "../data/RNAseqSim/Mus_musculus.NCBIM37.65.gtf");
			put("FASTA_File",
					"../data/RNAseqSim/Mus_musculus.NCBIM37.65.dna.chromosome.1.fa");									
			put("Chromosome", "1");
			put("Chromosome_Matching", "Exact");
			put("Blacklist","");
			put("Abundance_File", "./abundance.txt");
			put("Abundance_Overwritten","No");
			put("Expressed_Transcript_Percentage", "0.5");
			put("SV_Allowed","No");
			put("SV_File","../data/RNAseqSim/sv.chr1.vcf");
			put("SV_Sample","129S1");					
			put("Read_ID_Prefix", "UNC-");
			put("Read_Length", "100");
			put("Fragment_Minimum_Length", "150");
			put("Fragment_Maximum_Length", "400");
			put("Coverage_Factor", "2.0");
			put("Flip_And_Reverse","Yes");
			put("Quality_Generator", "Perfect");			
			put("Real_Quality_Score_Fastq_1", "./real.1.fq");
			put("Real_Quality_Score_Fastq_2", "./real.2.fq");
			put("Unknown_Factor","0.0001");
			put("Output_Fastq_1", "./1.fq");
			put("Output_Fastq_2", "./2.fq");			
			put("Input_Buffer_Size","10485760"); //10M
			put("Output_Buffer_Size","10485760"); //10M
			put("Process_Window_Size","41943040"); //40Mbp
			
		}
	};

	@SuppressWarnings("serial")
	/**
	 * A class that inherits <code>Properties</code>, but have the keys
	 * in the ordered of the actions of putting.
	 *
	 */
	class OrderPreservedProperties extends Properties {
		public Enumeration<Object> keys() {			
			Vector<Object> keyList = new Vector<Object>(configuration.keySet());
			return keyList.elements();
		}
	}
	
	/**
	 * read command line and get the configuration data.
	 * @param args
	 */
	private void getConfigFromArgs(String[] args) {
		System.out.println("Reading Configuration from command line ...");
		//System.out.println(args[1]);
		//Skip the first argument, i.e., the config file name.
		for (int i=1; i<args.length; ++i) {			
			String []kv = args[i].split("=");
			String key = kv[0].substring(1);
			String value = kv[1];
			System.out.println(key+"="+value);
			if (configuration.containsKey(key))
				configuration.put(key,value);
			else
				throw (new RuntimeException("Unknown configuration item "+key+"."));			
		}			
	}
	
	/**
	 * a constructor for object <code>Config</code>. It will read maps all
	 * configuration description to configuration data. If a Config file is not
	 * found, the program will create a Config file with default settings.
	 * 
	 * @param margs the modified argument array. 
	 */
	public Config(String[] margs) {
		Properties prop = new OrderPreservedProperties();
		try {
			// configuration priority:
			// command line> config_file > default
			
			// load configuration settings from file 
			prop.load(new FileInputStream(margs[0]));
			Set<String> configKeySet = configuration.keySet();
			for (String configKey : configKeySet) {
				if (prop.getProperty(configKey) != null)
					/* Replace the default value with a new one */
					configuration.put(configKey, prop.getProperty(configKey));				
			}
			// load configuration setting from command line
			getConfigFromArgs(margs);
		} catch (FileNotFoundException ex) {
			System.out.println("Config file not found.");
			System.out.println("Creating Config file with default settings ...");
			try {
				// load configuration setting from command line
				getConfigFromArgs(margs);							
				
				Set<String> configKeySet = configuration.keySet();
				for (String configKey : configKeySet) {
//					System.out.println(configKey);
					prop.setProperty(configKey, configuration.get(configKey));
				}
				// save properties to project root folder
				prop.store(new FileOutputStream(margs[0]), null);								
			} catch (IOException e) {
				System.out.println("Unable to create config file.");
				System.out.println("Using default settings.");
			}
		} catch (IOException ex) {
			System.out.println("Unable to load config file.");
			System.out.println("Using default settings.");
		}
		System.out.println();
	}

	/**
	 * the method will get the configuration file/data by key
	 * 
	 * @param key
	 * @return the configuration file/data
	 */
	public static String get(String key) {
		if (configuration.containsKey(key))
			return configuration.get(key);
		throw (new RuntimeException("Unknown configuration item."));
	}
}
