
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class mainAnalisysPopol {
	private static final String REPORTS_DIR = "/home/daniele/the-one-tesi/one_1.4.1/src/reports/freqDiversa";
	private static final String OUTPUT_FILE_TEMPI_VF = "/home/daniele/the-one-tesi/one_1.4.1/src/reports/datiTempiVFDiversaPop.dat";
	/*
	private static final String OUTPUT_FILE_DATA = "/home/daniele/the-one-tesi/one_1.4.1/src/reports/datiTotalData.dat";
	private static final String OUTPUT_FILE_DELEGHE = "/home/daniele/the-one-tesi/one_1.4.1/src/reports/datiDeleghe.dat";
	private static final String OUTPUT_FILE_DELEGHE_TOTALI = "/home/daniele/the-one-tesi/one_1.4.1/src/reports/datiDelegheTotali.dat";
	*/
	private static final String KEY_VIRTUALFILE = "First VirtualFile satisfied:\t";
	/*
	private static final String KEY_DATA = "Total data:\t";
	private static final String KEY_DELEGHE = "VirtualFile delegated:\t";
	private static final String KEY_RITORNATE = "DownloadFWDs returned:\t";
	*/
	private static File[][][] files = new File[8][2][];
	 
	
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
				return name.contains("Del1_FDS1") && name.contains("FileGatheringReport");
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
		/* carico i files */
		for(int pop = 0; pop < 8; pop++){
			int numCopie = 50 + (50*pop);
			File subdir = new File(REPORTS_DIR + File.separatorChar+ numCopie+"files");
			System.err.println(subdir.getAbsolutePath());
			for(int strat=0; strat<2; strat++){
				files[pop][strat] = subdir.listFiles(new ParamFilenameFilter(strat));
				System.out.println(files[pop][strat].length+ " files read with strategy " + strat+":");
				for(int j=0;j<files[pop][strat].length; j++){
					System.out.println(files[pop][strat][j].getName());
				}
				System.out.println();
			}
			
		}				
		
		double[][] tAvgs = new double[8][2];

		for(int pop=0; pop<8; pop++){
			for(int strat = 0; strat<2; strat++){
				int tFilesLetti = 0;
				double tSum = 0;

				for(int ifile=0; ifile< files[pop][strat].length; ifile++){
					File currentFile = files[pop][strat][ifile];
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
							if(tempValue == 0){
								tempValue = 172800;
							}
							tSum += tempValue;
						}
					}

					
				}

				tAvgs[pop][strat] = tSum / tFilesLetti;
				if(tAvgs[pop][strat] > 172800){
					tAvgs[pop][strat] = 172800;
				}
			}
		}
		
		
		
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_TEMPI_VF));
			out.println("NrCopies\tNo_delegation\tM2MShare");
			for(int pop = 0; pop<8; pop++){
				int numCopie = 50 + (50 * pop);
				out.print(numCopie);
				for(int i=0; i<2; i++){	
					out.print("\t" + (tAvgs[pop][i]/3600));
				}
				out.println();
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
