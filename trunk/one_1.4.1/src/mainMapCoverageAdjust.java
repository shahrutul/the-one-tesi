
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;



public class mainMapCoverageAdjust {
	private static final int[] PERCs = {10,25,50,75,100};
	private static final String REPORTS_DIR = "reports/multiHopPerc/";
	private static final String OUTPUTS_DIR = "reports/datiFinali/multiHopPerc/";
	private static File[][][] files = new File[PERCs.length][5][];
	private static final String[] stratLabels = {"No_delegations","Delegations_to_all", "M2MShare_1_hop", "M2MShare_2_hop", "M2MShare_3_hop"};
	
	private static final int initialWidth = 1000;
	private static final int initialHeight = 800;	
	private static final int finalWidth = 50;
	private static final int finalHeight = 40;
	private static final int xStep = initialWidth / finalWidth;
	private static final int yStep = initialHeight / finalHeight;
	private static final int PRINT_MULTIPLIER = 18;
	
	private static class ParamFilenameFilter implements FilenameFilter{
		private int strategy;
		
		public ParamFilenameFilter(int i) {
			strategy = i;
		}

		@Override
		public boolean accept(File dir, String name) {
			switch (strategy) {
			case 0:
				return name.contains("Del0") && name.contains("M2MShareMapCoverageReport");
			case 1:
				return name.contains("Del2") && name.contains("M2MShareMapCoverageReport");
			case 2:
				return name.contains("Del1_FDS1_multiHop_1_M2MShareMapCoverageReport");
			case 3:
				return name.contains("Del1_FDS1_multiHop_2_M2MShareMapCoverageReport");
			case 4:
				return name.contains("Del1_FDS1_multiHop_3_M2MShareMapCoverageReport");

			default:
				return false;
			}
		}
		
	}
	
	/**
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		int totalFiles = 0;
		for(int p=0; p < PERCs.length; p++){
			File dir = new File(REPORTS_DIR + PERCs[p] + "perc");			
			for(int strat=2; strat<5; strat++){
				files[p][strat] = dir.listFiles(new ParamFilenameFilter(strat));
				totalFiles += files[p][strat].length;
				System.out.println(files[p][strat].length+ " files read with strategy " + strat+":");
			}
		}
		
		int totalFilesRead = 0;
		for(int p=0; p < PERCs.length; p++){
			
			/*solo per multi-hop per adesso*/
			for(int strat=2; strat<5; strat++){

				long[][] sumValues = new long[finalWidth][finalHeight];
				int filesRead = 0;

				for(int ifile=0; ifile< files[strat].length; ifile++){
					File currentFile = files[p][strat][ifile];
					System.out.println((100*totalFilesRead/totalFiles)+"% - "+currentFile.getName());
					ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>();
					Scanner input = null;
					try {
						input = new Scanner(currentFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					while(input.hasNextLine()){
						Scanner colReader = new Scanner(input.nextLine());
						ArrayList col = new ArrayList();
						while(colReader.hasNextInt())
						{
							int temp = colReader.nextInt();
							col.add(temp);

						}
						a.add(col);
					}

					//int[][] finalValues = new int[finalWidth][finalHeight];

					for(int x=0; x<finalWidth; x++){
						for(int y=0; y<finalHeight; y++){
							sumValues[x][y] += maxValues(a, x*xStep, y*yStep);
							//sumValues[x][y] += avgValues(a, x*xStep, y*yStep);
							//System.out.print(finalValues[x][y] + " ");
						}
						//System.out.println();
					}
					//System.out.println();

					filesRead++;
					totalFilesRead++;
					input.close();
				}

				/* faccio la media totale */
				for(int x=0; x<finalWidth; x++){
					for(int y=0; y<finalHeight; y++){
						sumValues[x][y] = sumValues[x][y] / filesRead;
					}
				}


				try {
					PrintWriter out = new PrintWriter(new FileWriter(OUTPUTS_DIR + stratLabels[strat] + "_"+PERCs[p]+"perc.dat"));
					for(int y=0; y<(finalHeight * PRINT_MULTIPLIER); y++){
						for(int x=0; x<(finalWidth * PRINT_MULTIPLIER); x++){
							out.print(sumValues[x / PRINT_MULTIPLIER][y / PRINT_MULTIPLIER] + " ");
						}
						out.println();
					}
					//System.out.println();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}



			}
		}
				
		
	}

	private static int maxValues(ArrayList<ArrayList<Integer>> a, int xinit, int yinit) {
		int max = 0;
		for(int x=xinit; x<(xinit+xStep); x++){
			for(int y=yinit; y<(yinit+yStep); y++){
				int temp = a.get(y).get(x);
				if(temp!=0){
					//System.err.println("max "+temp);
				}
				if(temp > max){
					max = temp;
				}
			}
		}
		return max;
	}
	
	private static int avgValues(ArrayList<ArrayList<Integer>> a, int xinit, int yinit) {
		long sum = 0;
		for(int x=xinit; x<(xinit+xStep); x++){
			for(int y=yinit; y<(yinit+yStep); y++){
				int temp = a.get(y).get(x);
				sum += temp;
			}
		}
		return (int) (sum / (xStep*yStep/10));
	}
	
	private static int sumValues(ArrayList<ArrayList<Integer>> a, int xinit, int yinit) {
		long sum = 0;
		for(int x=xinit; x<(xinit+xStep); x++){
			for(int y=yinit; y<(yinit+yStep); y++){
				int temp = a.get(y).get(x);
				sum += temp;
			}
		}
		return (int) sum / 3;
	}
	
}



