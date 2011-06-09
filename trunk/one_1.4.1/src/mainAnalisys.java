
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class mainAnalisys {
	private static final String REPORTS_DIR = "reports/cluster/delega";
	private static final String OUTPUT_FILE_TEMPI_VF = "reports/datiTempiVF.dat";
	private static final String OUTPUT_FILE_DATA = "reports/datiTotalData.dat";
	private static final String OUTPUT_FILE_DELEGHE = "reports/datiDeleghe.dat";
	private static final String OUTPUT_FILE_DELEGHE_TOTALI = "reports/datiDelegheTotali.dat";
	private static final String KEY_VIRTUALFILE = "First VirtualFile satisfied:\t";
	private static final String KEY_DATA = "Total data:\t";
	private static final String KEY_DELEGHE = "VirtualFile delegated:\t";
	private static final String KEY_RITORNATE = "DownloadFWDs returned:\t";
	private static String[] labels = {"Average","Min","Max"};
	private static File[][] files = new File[3][];
	 
	
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
				return name.contains("Del1_FDS1") && name.contains("FT3") && name.contains("FileGatheringReport");
			case 2:
				return name.contains("Del2") && name.contains("FileGatheringReport");

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
		for(int i=0; i<3; i++){
			files[i] = dir.listFiles(new ParamFilenameFilter(i));
			System.out.println(files[i].length+ " files read with strategy " + i+":");
			for(int j=0;j<files[i].length; j++){
				System.out.println(files[i][j].getName());
			}
			System.out.println();
		}		
		System.out.println((files[0].length + files[1].length + files[2].length)+ " total files read");
		
		double[] tAvgs = new double[3];
		double[] tMins = new double[3];
		double[] tMaxs = new double[3];
		
		int[] dataAvgs = new int[3];
		int[] dataMins = new int[3];
		int[] dataMaxs = new int[3];
		
		double[] returnedAvgs = new double[3];
		
		double[] delegheAvgs = new double[3];
		double[] delegheMins = new double[3];
		double[] delegheMaxs = new double[3];
		
		for(int strat = 0; strat<3; strat++){
			int tFilesLetti = 0;
			double tMin = Double.MAX_VALUE;
			double tMax = Double.MIN_VALUE;
			double tSum = 0;
			
			int dataFilesLetti = 0;
			int dataMin = Integer.MAX_VALUE;
			int dataMax = Integer.MIN_VALUE;
			int dataSum = 0;
			
			int delegheFilesLetti = 0;
			int delegheMin = Integer.MAX_VALUE;
			int delegheMax = Integer.MIN_VALUE;
			int delegheTotali = 0;
			double delegheSum = 0;
			
			for(int ifile=0; ifile< files[strat].length; ifile++){
				File currentFile = files[strat][ifile];
				Scanner scanner = null;
				try {
					scanner = new Scanner(currentFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}		
				
				int deleghe = -1;
				int ritornate = -1;
				
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(line.contains(KEY_VIRTUALFILE)){
						tFilesLetti++;
						double tempValue = Double.parseDouble(line.substring(KEY_VIRTUALFILE.length()));
						//System.err.println(tempValue);
						tSum += tempValue;
						if(tempValue > tMax){
							tMax = tempValue;
						}
						if(tempValue < tMin){
							tMin = tempValue;
						}						
					}
					if(line.contains(KEY_DATA)){
						dataFilesLetti++;
						int tempValue = Integer.parseInt(line.substring(KEY_DATA.length()));
						//System.err.println(tempValue);
						dataSum += tempValue;
						if(tempValue > dataMax){
							dataMax = tempValue;
						}
						if(tempValue < dataMin){
							dataMin = tempValue;
						}						
					}
					if(line.contains(KEY_DELEGHE)){
						deleghe = Integer.parseInt(line.substring(KEY_DELEGHE.length()));
						delegheTotali += deleghe;
						if(deleghe > delegheMax){
							delegheMax = deleghe;
						}
						if(deleghe < delegheMin){
							delegheMin = deleghe;
						}	
					}
					if(line.contains(KEY_RITORNATE)){
						ritornate = Integer.parseInt(line.substring(KEY_RITORNATE.length()));
					}
				}
				
				if(deleghe != -1 && ritornate != -1 && strat != 0){
					delegheFilesLetti++;
					double temp = ((double)ritornate) / ((double)deleghe);
					delegheSum += temp;
					//System.err.println("ritornate "+ ritornate+" su "+ deleghe+ ": " + temp);
				}
			}
			
			tMins[strat] = tMin;
			tMaxs[strat] = tMax;
			tAvgs[strat] = tSum / tFilesLetti;
						
			dataMins[strat] = dataMin;
			dataMaxs[strat] = dataMax;
			dataAvgs[strat] = dataSum / dataFilesLetti;
			
			if(strat != 0){
				delegheMins[strat] = delegheMin;
				delegheMaxs[strat] = delegheMax;
				delegheAvgs[strat] = delegheTotali / delegheFilesLetti;
			}
			
			
			returnedAvgs[strat] = delegheSum / delegheFilesLetti;
		}
		
		/* FIles dei dati x i tempi del virtual file */
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_TEMPI_VF));
			out.println("Strat\tNo_delegation\tM2MShare\tDelegation_To_all");
			out.print(labels[0]);
			for(int i=0; i<3; i++){				
				out.print("\t" + tAvgs[i]);
			}
			out.println();
			out.print(labels[1]);
			for(int i=0; i<3; i++){				
				out.print("\t" + tMins[i]);
			}
			out.println();
			out.print(labels[2]);
			for(int i=0; i<3; i++){				
				out.print("\t" + tMaxs[i]);
			}
			out.println();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* FIles dei dati x i dati trasferiti */
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_DATA));
			out.println("Strat\tM2MShare\tDelegation_To_all");
			out.print(labels[0]);
			for(int i=1; i<3; i++){				
				out.print("\t" + dataAvgs[i]);
			}
			out.println();
			out.print(labels[1]);
			for(int i=1; i<3; i++){				
				out.print("\t" + dataMins[i]);
			}
			out.println();
			out.print(labels[2]);
			for(int i=1; i<3; i++){				
				out.print("\t" + dataMaxs[i]);
			}
			out.println();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* FIles dei dati x le deleghe ritornate */
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_DELEGHE));
			out.println("Strat\tM2MShare\tDelegation_To_all");
			out.print(labels[0]);
			for(int i=1; i<3; i++){				
				out.print("\t" + returnedAvgs[i]);
			}
			out.println();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* FIles dei dati x le deleghe totali */
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_DELEGHE_TOTALI));
			out.println("Strat\tM2MShare\tDelegation_To_all");
			out.print(labels[0]);
			for(int i=1; i<3; i++){				
				out.print("\t" + delegheAvgs[i]);
			}
			out.println();
			out.print(labels[1]);
			for(int i=1; i<3; i++){				
				out.print("\t" + delegheMins[i]);
			}
			out.println();
			out.print(labels[2]);
			for(int i=1; i<3; i++){				
				out.print("\t" + delegheMaxs[i]);
			}
			out.println();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}



