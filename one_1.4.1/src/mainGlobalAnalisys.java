
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;


public class mainGlobalAnalisys {
	private static final String REPORTS_DIR = "reports/";
	private static final String REPORT_NAME = "FileGatheringReport";
	
	private static final String KEY_VIRTUALFILE = "First VirtualFile satisfied:\t";
	private static final String KEY_DATA = "Total data:\t";
	private static final String KEY_DELEGHE = "VirtualFile delegated:\t";
	private static final String KEY_RITORNATE = "DownloadFWDs returned:\t";
	private static final String KEY_RUN_TIME = "Simulation time:\t";
	private static final String KEY_SIM_TIME = "Simulated time:\t";
	private static Vector<File> files = new Vector<File>();
	 
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File dir = new File(REPORTS_DIR);
		
		scanDir(dir);
		System.out.println(""+files.size()+" simulazioni eseguite.");
		
		long totRunningTime = 0;
		long totSimTime = 0;
		
		
		for(File f : files){
				Scanner scanner = null;
				try {
					scanner = new Scanner(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					
					if(line.contains(KEY_RUN_TIME)){
						double tempValue = Double.parseDouble(line.substring(KEY_RUN_TIME.length()));
						//System.err.println(tempValue);
						totRunningTime += tempValue;					
					}
					
					if(line.contains(KEY_SIM_TIME)){
						double tempValue = Double.parseDouble(line.substring(KEY_SIM_TIME.length()));
						//System.err.println(tempValue);
						totSimTime += tempValue;					
					}
				}
				
				scanner.close();
			}
			
		System.out.println("Tempo simulato: "+totSimTime + " s ("+(totSimTime/86400)+" giorni - "+(totSimTime/86400)/365+" anni)");	
		System.out.println("Durata totale delle simulazioni: "+totRunningTime + " s ("+(totRunningTime/86400)+" giorni)");	
		
		/* FIles dei dati x i tempi del virtual file */
		/*
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_TEMPI_VF));
			out.println("Strat\tNo_delegation\tM2MShare\tDelegation_To_all");
			out.print(labels[0]);
			for(int i=0; i<3; i++){				
				out.print("\t" + (tAvgs[i]/3600));
			}
			out.println();
			out.print(labels[1]);
			for(int i=0; i<3; i++){				
				out.print("\t" + (tMins[i]/3600));
			}
			out.println();
			out.print(labels[2]);
			for(int i=0; i<3; i++){				
				out.print("\t" + (tMaxs[i]/3600));
			}
			out.println();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}



	private static void scanDir(File dir) {
		
		File[] dirFiles = dir.listFiles();
		for(File f : dirFiles){			
			if(f.isDirectory()){
				scanDir(f);
				continue;
			}
			if(f.getName().contains(REPORT_NAME)/* && f.getTotalSpace() != 0*/){
				files.add(f);
			}
		}
		
	}
	
}



