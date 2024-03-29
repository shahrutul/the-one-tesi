
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class mainAnalisysDataDifferentFDS {
	private static final String REPORTS_DIR = "reports/diversaFDS-2";
	private static final String OUTPUT_FILE_DATA = "reports/datiDataDiversaFDS_";
	private static final String KEY_DATA = "Total data:\t";
	private static String[] labels = {"Average","Min","Max"};
	private static final int[] fileSizes = {3,10,25};
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
				return name.contains("FDS0") && name.contains("FileGatheringReport");
			case 1:
				return name.contains("FDS1") && name.contains("FileGatheringReport");
			case 2:
				return name.contains("FDS2") && name.contains("FileGatheringReport");

			default:
				return false;
			}
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		for(int fileDimI=0; fileDimI<fileSizes.length; fileDimI++ ){

			File dir = new File(REPORTS_DIR + File.separatorChar + fileSizes[fileDimI]+"MB");
			
			for(int i=0; i<3; i++){
				files[i] = dir.listFiles(new ParamFilenameFilter(i));
				System.out.println(files[i].length+ " files read with strategy " + i+":");
				for(int j=0;j<files[i].length; j++){
					System.out.println(files[i][j].getName());
				}
				System.out.println();
			}		
			System.out.println((files[0].length + files[1].length + files[2].length)+ " total files read");

			long[] dataAvgs = new long[3];
			long[] dataMins = new long[3];
			long[] dataMaxs = new long[3];


			for(int strat = 0; strat<3; strat++){
				
				int dataFilesLetti = 0;
				long dataMin = Integer.MAX_VALUE;
				long dataMax = Integer.MIN_VALUE;
				long dataSum = 0;

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
						
						if(line.contains(KEY_DATA)){
							dataFilesLetti++;
							long tempValue = Long.parseLong(line.substring(KEY_DATA.length()));
							//System.err.println(tempValue);
							dataSum += tempValue;
							if(tempValue > dataMax){
								dataMax = tempValue;
							}
							if(tempValue < dataMin){
								dataMin = tempValue;
							}						
						}
						
					}

					scanner.close();
				}

				dataMins[strat] = dataMin;
				dataMaxs[strat] = dataMax;
				dataAvgs[strat] = dataSum / dataFilesLetti;

			}


			/* FIles dei dati x i dati trasferiti */
			try {
				PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_DATA+fileSizes[fileDimI]+"MB.dat"));
				out.println("Strat\tiM\tM2MShare\trM");
				out.print(labels[0]);
				for(int i=0; i<3; i++){				
					out.print("\t" + dataAvgs[i]);
				}
				out.println();
				out.print(labels[1]);
				for(int i=0; i<3; i++){				
					out.print("\t" + dataMins[i]);
				}
				out.println();
				out.print(labels[2]);
				for(int i=0; i<3; i++){				
					out.print("\t" + dataMaxs[i]);
				}
				out.println();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}



