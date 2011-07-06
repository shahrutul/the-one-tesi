import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class mainAnalisysDelegheAttive {
	private static final String REPORTS_DIR = "reports/redundancy2";
	private static final String OUTPUT_FILE = "reports/datiFinali/datiDelegheAttive.dat";
	private static final String LOG_SUFFIX = "FileGatheringLog.txt";
	private static final String REQUESTER_NODE = "A32";
	private static final String NEW_DELEGATION_LOG = "PendingDownload "+ REQUESTER_NODE;
	private static final String FWD_COMPLETE_LOG = "DownloadFWD returned";
	private static final String DOWN_EXPIRED_LOG = "PendingDownload expired";
	private static final String FWD_EXPIRED_LOG = "DownloadFWD expired";
	
	
	private static final int HOURS_ANALIZED = 168;

	private static File[][] files = new File[3][];


	private static class ParamFilenameFilter implements FilenameFilter {
		private int strategy;

		public ParamFilenameFilter(int i) {
			strategy = i;
		}

		@Override
		public boolean accept(File dir, String name) {
			
			switch (strategy) {
			case 0:
				return name.contains("Del0")
						&& name.contains(LOG_SUFFIX);
			case 1:
				return name.contains("Del1")
						&& name.contains(LOG_SUFFIX);
			case 2:
				return name.contains("Del2")
						&& name.contains(LOG_SUFFIX);

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
		for (int i = 0; i < 3; i++) {
			files[i] = dir.listFiles(new ParamFilenameFilter(i));
			System.out.println(files[i].length + " files read with strategy "
					+ i + ":");
		}
		System.out
				.println((files[0].length + files[1].length + files[2].length)
						+ " total files read");

		int[][] totAvgs = new int[3][HOURS_ANALIZED];

		for (int strat = 0; strat < 3; strat++) {
			int tFilesLetti = 0;
			int sum = 0;
			int[] dataThisStratSum = new int[HOURS_ANALIZED];
			System.err.println("strat "+strat);

			for (int ifile = 0; ifile < files[strat].length; ifile++) {
				
				File currentFile = files[strat][ifile];
				Scanner scanner = null;
				try {
					scanner = new Scanner(currentFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line == null || line.equals("")) {
						continue;
					}
					double time = Double.parseDouble(line.substring(0,
							line.indexOf(' ')));
					
					int ora = (int) (time / 3600);
					if (ora > HOURS_ANALIZED) {
						continue;
					}
					
					if(line.contains(NEW_DELEGATION_LOG)){
						for(int i=ora; i<HOURS_ANALIZED; i++){
							dataThisStratSum[i] ++;							
						}
					}
					else if(line.contains(DOWN_EXPIRED_LOG) ||
							line.contains(FWD_COMPLETE_LOG) ||
							line.contains(FWD_EXPIRED_LOG)){
						for(int i=ora; i<HOURS_ANALIZED; i++){
							dataThisStratSum[i] --;							
						}
					}
					
					
					
				}
				scanner.close();
				
				tFilesLetti++;
				/*
				for(String host: dataInNode.keySet()){
					if(!host.equals("E479")){
						continue;
					}
					double[] dataArray = dataInNode.get(host);
					System.out.print(host+":\t");
					for (int i = 0; i < HOURS_ANALIZED; i++) {
						System.out.print(dataArray[i]+" ");
					}
					System.out.println();
				}*/
				
			}
			
			for (int i = 0; i < dataThisStratSum.length; i++) {
				totAvgs[strat][i] = dataThisStratSum[i] / tFilesLetti;
				if(totAvgs[strat][i]<0){
					totAvgs[strat][i] = 0;
				}
				System.out.print(totAvgs[strat][i] + " ");
			}
			System.out.println();
		}

		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE));
			out.println("Time\tNo_delegation\tM2MShare\tDelegation_to_all");
			for(int h = 0; h<HOURS_ANALIZED; h++){				
				out.print(h);
				for(int strat=0; strat<3; strat++){	
					out.print("\t" + (totAvgs[strat][h]));
				}
				out.println();
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}

}
