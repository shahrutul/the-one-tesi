
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class mainAnalisysMultiHop {
	private static final String REPORTS_DIR = "reports/multiHop";
	private static final String OUTPUT_FILE_TEMPI_VF = "reports/datiFinali/datiTempiMultiHop.dat";
	private static final String OUTPUT_FILE_PERC_COMPLETATA = "reports/datiFinali/datiPercCompletaMultiHop.dat";
	private static final String KEY_VIRTUALFILE = "First VirtualFile satisfied:\t";
	private static final String KEY_DATA = "Total data:\t";
	private static final String KEY_DELEGHE = "VirtualFile delegated:\t";
	private static final String KEY_RITORNATE = "DownloadFWDs returned:\t";
	private static File[][] files = new File[4][];
	 
	
	private static class ParamFilenameFilter implements FilenameFilter{
		private int strategy;
		
		public ParamFilenameFilter(int i) {
			strategy = i;
		}

		@Override
		public boolean accept(File dir, String name) {
			switch (strategy) {
			case 0:
				return name.contains("Del0") && name.contains("FileGatheringReport");
			case 1:
				return name.contains("Del1_FDS1_multiHop_1") && name.contains("FileGatheringReport");
			case 2:
				return name.contains("Del2") && name.contains("FileGatheringReport");
			case 3:
				return name.contains("Del1_FDS1_multiHop_3") && name.contains("FileGatheringReport");

			default:
				return false;
			}
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File dir = new File(REPORTS_DIR);
		for(int i=0; i<4; i++){
			files[i] = dir.listFiles(new ParamFilenameFilter(i));
			System.out.println(files[i].length+ " files read with strategy " + i+":");
			/*for(int j=0;j<files[i].length; j++){
				System.out.println(files[i][j].getName());
			}
			System.out.println();*/
		}		
		System.out.println((files[0].length + files[1].length + files[2].length + files[3].length)+ " total files read");
		
		double[] tAvgs = new double[4];
		double[] tMins = new double[4];
		double[] tMaxs = new double[4];
		double[] percCompletati = new double[4];
		
		for(int strat = 0; strat<4; strat++){
			int tFilesLetti = 0;
			double tMin = Double.MAX_VALUE;
			double tMax = Double.MIN_VALUE;
			double tSum = 0;
			int completati = 0;
			
			
			for(int ifile=0; ifile< files[strat].length; ifile++){
				File currentFile = files[strat][ifile];
				Scanner scanner = null;
				try {
					scanner = new Scanner(currentFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}		
				
				
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(line.contains(KEY_VIRTUALFILE)){
						tFilesLetti++;
						double tempValue = Double.parseDouble(line.substring(KEY_VIRTUALFILE.length()));
						//System.err.println(tempValue);
						if (tempValue == 0){
							tempValue = 604800;
						}
						else{
							completati++;
						}
						tSum += tempValue;
						if(tempValue > tMax){
							tMax = tempValue;
						}
						if(tempValue < tMin){
							tMin = tempValue;
						}						
					}
				}
				
				scanner.close();
			}
			
			tMins[strat] = tMin;
			tMaxs[strat] = tMax;
			tAvgs[strat] = tSum / tFilesLetti;
			percCompletati[strat] = new Double(completati) / tFilesLetti;
			System.err.println("Min " + tMins[strat]+" - Completati: "+percCompletati[strat]);
			
		}
		
		/* FIles dei dati x i tempi del virtual file */
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_TEMPI_VF));
			out.println("Strat\tNo_delegation\tM2MShare\tDelegation_To_all\tM2MShare_3_hop");
			out.print("Average");
			for(int i=0; i<4; i++){				
				out.print("\t" + tAvgs[i]);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* FIles dei dati x i tempi del virtual file */
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_PERC_COMPLETATA));
			out.println("Strat\tNo_delegation\tM2MShare\tDelegation_To_all\tM2MShare_3_hop");
			out.print("Average");
			for(int i=0; i<4; i++){				
				out.print("\t" + percCompletati[i]);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}



