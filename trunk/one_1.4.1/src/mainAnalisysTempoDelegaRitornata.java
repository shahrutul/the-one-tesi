
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class mainAnalisysTempoDelegaRitornata {
	private static final String REPORTS_DIR = "reports/cluster";
	private static final String OUTPUT_FILE_TEMPI_RITORNO_DELEGHE = "reports/datiTempiRitornoDeleghe.dat";
	private static final String KEY_FWD_RETURNED = "DownloadFWD returned";
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
				return name.contains("Del0") && name.contains("FileGatheringLog");
			case 1:
				return name.contains("Del1_FDS1") && name.contains("FT3") && name.contains("FileGatheringLog");
			case 2:
				return name.contains("Del2") && name.contains("FileGatheringLog");

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
			/*
			for(int j=0;j<files[i].length; j++){
				System.out.println(files[i][j].getName());
			}
			System.out.println();*/
		}		
		System.out.println((files[0].length + files[1].length + files[2].length)+ " total files read");
		
		double[] tAvgs = new double[3];
		double[] tMins = new double[3];
		double[] tMaxs = new double[3];
		
		for(int strat = 0; strat<3; strat++){
			int tFilesLetti = 0;
			double tMin = Double.MAX_VALUE;
			double tMax = Double.MIN_VALUE;
			double tSum = 0;
			
			for(int ifile=0; ifile< files[strat].length; ifile++){
				File currentFile = files[strat][ifile];
				Scanner scanner = null;
				try {
					scanner = new Scanner(currentFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}		
				boolean fileValido = false;
				int delegheRitornateFile = 0;
				int tSumFile = 0;
				
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(line.contains(KEY_FWD_RETURNED)){
						tFilesLetti++;
						delegheRitornateFile++;
						fileValido = true;
						//double tempValue = new Scanner(line).nextDouble();
						
						double tempValue = Double.parseDouble(line.substring(0,line.indexOf(' ')));
						//System.err.println(tempValue);
						tSumFile += tempValue;
						tSum += tempValue;
						if(tempValue > tMax){
							tMax = tempValue;
						}
						if(tempValue < tMin){
							tMin = tempValue;
						}						
					}
				}/*
				if(fileValido){
					tFilesLetti++;
					tSum += tSumFile / delegheRitornateFile;
				}*/
				scanner.close();
			}
			
			tMins[strat] = tMin;
			tMaxs[strat] = tMax;
			tAvgs[strat] = tSum / tFilesLetti;
		}
		
		/* FIles dei dati x i tempi del virtual file */
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_TEMPI_RITORNO_DELEGHE));
			out.println("Strat\tM2MShare\tDelegation_To_all");
			out.print(labels[0]);
			for(int i=1; i<3; i++){				
				out.print("\t" + tAvgs[i]);
			}
			out.println();
			out.print(labels[1]);
			for(int i=1; i<3; i++){				
				out.print("\t" + tMins[i]);
			}
			out.println();
			out.print(labels[2]);
			for(int i=1; i<3; i++){				
				out.print("\t" + tMaxs[i]);
			}
			out.println();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}



