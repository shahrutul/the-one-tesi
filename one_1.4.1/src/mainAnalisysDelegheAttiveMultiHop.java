import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;


public class mainAnalisysDelegheAttiveMultiHop {
	private static final String REPORTS_DIR = "reports/multiHopPerc";
	private static final String OUTPUT_FILE = "reports/datiFinali/datiDelegheAttiveMultiHop.dat";
	
	private static final String LOG_SUFFIX = "FileGatheringLog.txt";
	private static final String VF_CREATED_LOG = "1.0000 VirtualFile created in ";
	private static final String NEW_DELEGATION_LOG = "PendingDownload ";
	private static final String FWD_COMPLETE_LOG = "DownloadFWD returned";
	private static final String DOWN_EXPIRED_LOG = "PendingDownload expired";
	private static final String FWD_EXPIRED_LOG = "DownloadFWD expired";
	
	//private static final String REQUESTER_NODE = "A32";
	private static final int HOURS_ANALIZED = 168;

	private static final int[] percs = {10,25,50,75,100};
	private static File[][][] files = new File[percs.length][3][];
	
	private static final String[] stratLabels = {"M2MShare_1_hop", "M2MShare_2_hop", "M2MShare_3_hop"};
	

	private static class ParamFilenameFilter implements FilenameFilter {
		private int strategy;

		public ParamFilenameFilter(int i) {
			strategy = i;
		}

		@Override
		public boolean accept(File dir, String name) {
			
			switch (strategy) {
			case 0:
				return name.contains("Del1_FDS1_multiHop_1")
						&& name.contains(LOG_SUFFIX);
			case 1:
				return name.contains("Del1_FDS1_multiHop_2")
						&& name.contains(LOG_SUFFIX);
			case 2:
				return name.contains("Del1_FDS1_multiHop_3")
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

		for(int perc = 0; perc < percs.length; perc++){
			File subdir = new File(REPORTS_DIR + File.separatorChar+ percs[perc]+"perc");
			System.err.println(subdir.getAbsolutePath());
			for(int strat=0; strat<3; strat++){
				files[perc][strat] = subdir.listFiles(new ParamFilenameFilter(strat));
				System.out.println(files[perc][strat].length+ " files read with strategy " + strat+":");					
			}
			System.out.println();

		}
		System.out
		.println((files[0].length + files[1].length + files[2].length)
				+ " total files read");

		int[][][] totAvgs = new int[percs.length][3][HOURS_ANALIZED];

		for(int pop=0; pop<percs.length; pop++){
			for (int strat = 0; strat < 3; strat++) {
				int tFilesLetti = 0;
				int sum = 0;
				int[] dataThisStratSum = new int[HOURS_ANALIZED];
				System.err.println("strat "+strat);

				for (int ifile = 0; ifile < files[pop][strat].length; ifile++) {

					File currentFile = files[pop][strat][ifile];
					Scanner scanner = null;
					try {
						scanner = new Scanner(currentFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					
					String requesterNode = null;
					String newDelegation = NEW_DELEGATION_LOG;
					
					int delegated = 0;
					int completed = 0;

					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();
						if (line == null || line.equals("")) {
							continue;
						}
						if(line.contains(VF_CREATED_LOG)){
							String reqTemp = line.substring(VF_CREATED_LOG.length());
							requesterNode = reqTemp.substring(0, reqTemp.indexOf(' '));
							//newDelegation += requesterNode;
						}
						
						double time = Double.parseDouble(line.substring(0,
								line.indexOf(' ')));
						
						int ora = (int) (time / 3600);
						if (ora > HOURS_ANALIZED) {
							continue;
						}
						
						if(line.contains(DOWN_EXPIRED_LOG) ||
								line.contains(FWD_COMPLETE_LOG) ||
								line.contains(FWD_EXPIRED_LOG)){
							completed ++;
							for(int i=ora; i<HOURS_ANALIZED; i++){
								dataThisStratSum[i] --;							
							}
						}
						else
						if(line.contains(newDelegation) && !line.contains(DOWN_EXPIRED_LOG)){
							delegated++;
							for(int i=ora; i<HOURS_ANALIZED; i++){
								dataThisStratSum[i] ++;							
							}
						}
						
						
						
					}
					scanner.close();
					
					tFilesLetti++;
							
					if(strat == 0){
						System.err.println("completate "+completed+"/"+delegated+ " - "+ currentFile.getName());
					}
					
					
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
					totAvgs[pop][strat][i] = dataThisStratSum[i] / tFilesLetti;
					if(totAvgs[pop][strat][i]<0){
						totAvgs[pop][strat][i] = 0;
					}
					System.out.print(totAvgs[pop][strat][i] + " ");
				}
				System.out.println();
			}
		}

		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE));
			out.println("Time");
			for(int perc = 0; perc<percs.length; perc++){
				for(int i=0; i<3; i++){	
					out.print("\t" + stratLabels[i]+"_"+percs[perc]);
				}
			}
			out.println();
			
			for(int h = 0; h<HOURS_ANALIZED; h++){				
				out.print(h);
				for(int perc = 0; perc<percs.length; perc++){
					for(int strat=0; strat<3; strat++){	
						out.print("\t" + (totAvgs[perc][strat][h]));
					}
				}
				out.println();
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}

}
