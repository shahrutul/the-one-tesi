
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class mainAnalisysMultiHopPerc {
	private static final String REPORTS_DIR = "reports/multiHopPerc";
	private static final String OUTPUT_FILE_TEMPI_VF = "reports/datiFinali/datiTempiMultiHopPerc.dat";
	private static final String OUTPUT_FILE_DELEGHE = "reports/datiFinali/datiDelegheMultiHopPerc.dat";
	
	private static final String KEY_VIRTUALFILE = "First VirtualFile satisfied:\t";
	private static final String KEY_DELEGHE = "VirtualFile delegated:\t";
	/*
	private static final String KEY_DATA = "Total data:\t";
	private static final String KEY_RITORNATE = "DownloadFWDs returned:\t";
	*/
	private static final int[] percs = {10,25,50,75,100};
	private static File[][][] files = new File[percs.length][3][];
	private static final String[] stratLabels = {"M2MShare_1_hop", "M2MShare_2_hop", "M2MShare_3_hop"};
	
	
	private static class ParamFilenameFilter implements FilenameFilter{
		private int strategy;
		
		public ParamFilenameFilter(int i) {
			strategy = i;
		}

		@Override
		public boolean accept(File dir, String name) {
			switch (strategy) {
			case 0:
				return name.contains("Del1_FDS1_multiHop_1_FileGatheringReport");
			case 1:
				return name.contains("Del1_FDS1_multiHop_2_FileGatheringReport");
			case 2:
				return name.contains("Del1_FDS1_multiHop_3_FileGatheringReport");

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
		for(int perc = 0; perc < percs.length; perc++){
			File subdir = new File(REPORTS_DIR + File.separatorChar+ percs[perc]+"perc");
			System.err.println(subdir.getAbsolutePath());
			for(int strat=0; strat<3; strat++){
				files[perc][strat] = subdir.listFiles(new ParamFilenameFilter(strat));
				System.out.println(files[perc][strat].length+ " files read with strategy " + strat+":");					
			}
			System.out.println();
			
		}				
		
		double[][] tAvgs = new double[percs.length][3];
		long[][] delegheAvgs = new long[percs.length][3];
		
		for(int pop=0; pop<percs.length; pop++){
			for(int strat = 0; strat<3; strat++){
				int tFilesLetti = 0;
				double tSum = 0;
				long delegheSum = 0;

				for(int ifile=0; ifile< files[pop][strat].length; ifile++){
					File currentFile = files[pop][strat][ifile];
					Scanner scanner = null;
					try {
						scanner = new Scanner(currentFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}		
					tFilesLetti++;

					while (scanner.hasNextLine()) {
						
						String line = scanner.nextLine();
						if(line.contains(KEY_VIRTUALFILE)){
							
							double tempValue = Double.parseDouble(line.substring(KEY_VIRTUALFILE.length()));
							//System.err.println(tempValue);
							if(tempValue == 0){
								tempValue = 604800;
							}
							tSum += tempValue;
						}
						
						if(line.contains(KEY_DELEGHE)){
							long tempValue = Long.parseLong(line.substring(KEY_DELEGHE.length()));
							
							delegheSum += tempValue;
						}
					}

					scanner.close();
				}
				
				delegheAvgs[pop][strat] = delegheSum / tFilesLetti;

				tAvgs[pop][strat] = tSum / tFilesLetti;
				if(tAvgs[pop][strat] > 604800){
					tAvgs[pop][strat] = 604800;
				}
			}
		}
		
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_TEMPI_VF));
			out.print("percReDelegation");
			for(int i=0; i<3; i++){	
				out.print("\t" + stratLabels[i]);
			}
			out.println();
			for(int perc = 0; perc<percs.length; perc++){
				
				out.print(percs[perc]);
				for(int i=0; i<3; i++){	
					out.print("\t" + (tAvgs[perc][i]/3600));
				}
				out.println();
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_DELEGHE));
			out.print("percReDelegation");
			for(int i=0; i<3; i++){	
				out.print("\t" + stratLabels[i]);
			}
			out.println();
			for(int perc = 0; perc<percs.length; perc++){
				
				out.print(percs[perc]);
				for(int i=0; i<3; i++){	
					out.print("\t" + (delegheAvgs[perc][i]));
				}
				out.println();
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
