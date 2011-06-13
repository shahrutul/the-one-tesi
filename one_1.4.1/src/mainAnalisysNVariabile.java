
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class mainAnalisysNVariabile {
	private static final String REPORTS_DIR = "reports/diversoNePop";
	private static final String OUTPUT_FILE_TEMPI_VF = "reports/datiTempiVF_Fp";
	private static final String KEY_VIRTUALFILE = "First VirtualFile satisfied:\t";
	private static final int[] FPs = {5,10,20,30,50,80};
	private static final int[] Ns = {100,200,400,600,800,1000};
	private static File[][][] files = new File[Ns.length][2][];
	 
	
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
		
		
		/* for the values of FilePopularity */
		for(int FPi=0; FPi<FPs.length; FPi++){
			String popDirName = REPORTS_DIR + File.separatorChar + "FP" + FPs[FPi];
			
			/* carico i files */
			
			for(int Ni = 0; Ni < Ns.length; Ni++){
				File subdir = new File(popDirName + File.separatorChar+ "N"+Ns[Ni]);
				System.err.println(subdir.getAbsolutePath());
				for(int strat=0; strat<2; strat++){
					files[Ni][strat] = subdir.listFiles(new ParamFilenameFilter(strat));
					System.out.println(files[Ni][strat].length+ " files read with strategy " + strat+":");
					/*
					for(int j=0;j<files[Ni][strat].length; j++){
						System.out.println(files[Ni][strat][j].getName());
					}*/
					System.out.println();
				}

			}				

			double[][] tAvgs = new double[Ns.length][2];

			for(int Ni=0; Ni<Ns.length; Ni++){
				for(int strat = 0; strat<2; strat++){
					int tFilesLetti = 0;
					double tSum = 0;

					for(int ifile=0; ifile< files[Ni][strat].length; ifile++){
						File currentFile = files[Ni][strat][ifile];
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

					tAvgs[Ni][strat] = tSum / tFilesLetti;
					if(tAvgs[Ni][strat] > 172800){
						tAvgs[Ni][strat] = 172800;
					}
				}
			}


			
			try {
				String outputFileName = OUTPUT_FILE_TEMPI_VF + FPs[FPi];
				PrintWriter out = new PrintWriter(new FileWriter(outputFileName));
				out.println("N\tNo_delegation\tM2MShare");
				for(int Ni = 0; Ni<Ns.length; Ni++){
					
					out.print(Ns[Ni]);
					for(int i=0; i<2; i++){	
						out.print("\t" + (tAvgs[Ni][i]/3600));
					}
					out.println();
				}

				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
