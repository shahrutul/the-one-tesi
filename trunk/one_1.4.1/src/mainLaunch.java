import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;


public class mainLaunch {
	
	private static final String REPORTS_DIR = "reports/cluster";
	private static final String RUNNING_DIR = "../runningSettings";
	private static final String SEED_FILE = "seedsCluster.txt";
	private static final String SEED_SETTING_FILE_PREFIX = "../runningSettings/seedSettings";
	private static final String JOBFILE = "../sim";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* Read the seeds */
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(SEED_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		Vector<Integer> seeds = new Vector<Integer>();
		while (scanner.hasNextLine()) {
			try {
				seeds.add(scanner.nextInt());
			}catch (Exception e){}
		}
		System.out.println("Read "+seeds.size()+" seeds.");
		
		int fileGenIndex = 0, movementIndex = 0;
		boolean validSeeds = false;
		boolean validSeedsFreq = false;
		
		/* Read the file list of reports */
		File folder = new File(REPORTS_DIR);
		File folderRunning = new File(RUNNING_DIR);

		/* Start generating configuration seeds */
		while(!validSeeds && !validSeedsFreq){
			final String matchString = "FG"+seeds.get(fileGenIndex)+"_MM"+seeds.get(movementIndex);
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.contains(matchString);
				}
			};
			FilenameFilter filterFreq = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.contains(matchString) && name.contains("FT3");
				}
			};

			if(folder.list(filter).length == 0 && folderRunning.list(filter).length == 0){
				validSeeds = true;
			}
			if(folder.list(filterFreq).length == 0 && folderRunning.list(filterFreq).length == 0){
				validSeedsFreq = true;
			}
			if(!validSeeds && !validSeedsFreq){
				/* next combination of seeds */
				//System.err.println("Combination "+matchString+" already used");
				if(fileGenIndex == movementIndex && movementIndex == seeds.size()){
					System.out.println("All seed combinations tested!");
					return;
				}

				if(movementIndex <  seeds.size()){
					movementIndex++;
					fileGenIndex++;
				}
			}			
		}
		/* Found a valid seeds combination */
		System.out.println("Valid combination: "+seeds.get(fileGenIndex)+"_"+seeds.get(movementIndex));
		if(validSeedsFreq){
			System.out.println("Valid for different frequency");
		}

		try {
			String settingFileName = SEED_SETTING_FILE_PREFIX+"FG"+seeds.get(fileGenIndex)+"_MM"+seeds.get(movementIndex);
			String jobFileName = JOBFILE +"FG"+seeds.get(fileGenIndex)+"_MM"+seeds.get(movementIndex)+".job";
			
			Runtime run = Runtime.getRuntime();
			
			Process pr = null;
			try {
				pr = run.exec("cp "+JOBFILE+".job "+jobFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				pr.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			PrintWriter jobFile = new PrintWriter(new FileWriter(jobFileName,true));
			//jobFile.println("#PBS -N sim"+"FG"+seeds.get(fileGenIndex)+"_MM"+seeds.get(movementIndex));
			//jobFile.println("#PBS -e localhost:${HOME}/out/"+"FG"+seeds.get(fileGenIndex)+"_MM"+seeds.get(movementIndex)+".err");
			//jobFile.println("#PBS -o localhost:${HOME}/out/"+"FG"+seeds.get(fileGenIndex)+"_MM"+seeds.get(movementIndex)+".out");
			jobFile.println();
			if(validSeeds){
				PrintWriter out = new PrintWriter(new FileWriter(settingFileName));
				out.println("FilesGenerator.rngSeed = "+seeds.get(fileGenIndex));
				out.println("MovementModel.rngSeed = "+seeds.get(movementIndex));
				out.close();						
				
				jobFile.println("sh -c 'cd tesi-src/ && ./one.sh -b 3 WDM_settings.txt m2mshare_settings.txt "+settingFileName+"'");
				jobFile.println("sh -c 'cd tesi-src/ && rm "+settingFileName+"'");
			}
			if(validSeedsFreq){
				settingFileName = settingFileName+"_FT3";
				PrintWriter out = new PrintWriter(new FileWriter(settingFileName));
				out.println("FilesGenerator.rngSeed = "+seeds.get(fileGenIndex));
				out.println("MovementModel.rngSeed = "+seeds.get(movementIndex));
				out.close();
				
				jobFile.println("sh -c 'cd tesi-src/ && ./one.sh -b 1 WDM_settings.txt m2mshare_settings_freq3.txt "+settingFileName+"'");
				jobFile.println("sh -c 'cd tesi-src/ && rm "+settingFileName+"'");
			}
			
			jobFile.println("rm "+jobFileName.substring(3));
			jobFile.close();
			
			try {
				System.err.println("sh -c 'cd .. && qsub "+jobFileName.substring(3)+"'");
				pr = run.exec("qsub -N sim"+"FG"+seeds.get(fileGenIndex)+"_MM"+seeds.get(movementIndex)+" "+jobFileName.substring(3), null, new File("../"));

			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				pr.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			try {
				while ((line=buf.readLine())!=null) {
					System.out.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 

			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
