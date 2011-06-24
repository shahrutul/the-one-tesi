
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class mainAnalisysRidondanzaData {
	private static final String REPORTS_DIR = "reports/cluster";
	private static final String OUTPUT_FILE = "reports/datiRidondanzaDati.dat";
	private static final String REQUESTER_NODE = "A32";
	
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
				return name.contains("Del0") && name.contains("DataTransferLog");
			case 1:
				return name.contains("Del1_FDS1") && name.contains("FT3") && name.contains("DataTransferLog");
			case 2:
				return name.contains("Del2") && name.contains("DataTransferLog");

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
		
		long[][] totAvgs = new long[3][20];
		
		for(int strat = 0; strat<3; strat++){
			int tFilesLetti = 0;
			double sum = 0;
			
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
					if(line == null || line.equals("")){
						continue;
					}						
					double time = Double.parseDouble(line.substring(0, line.indexOf('\t')));
					
					line = line.substring(line.indexOf('\t')+1);					
					String source = line.substring(0, line.indexOf('\t'));
					
					line = line.substring(line.indexOf('\t')+1);
					String dest = line.substring(0, line.indexOf('\t'));
					line = line.substring(line.indexOf('\t')+1);
					long data = Long.parseLong(line);
					//System.err.println(""+time+"-"+source+"-"+dest+"-"+data);
					if(dest.equals(REQUESTER_NODE) || source.equals(REQUESTER_NODE)){
						continue;
					}
				}
				scanner.close();
			}
		}
		
		/* FIles dei dati x i tempi del virtual file 
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE));
			out.println("Strat\tM2MShare\tDelegation_To_all");
			out.print(labels[0]);
			for(int i=1; i<3; i++){				
				out.print("\t" + totAvgs[i]);
			}
			out.println();
			out.print(labels[1]);
			for(int i=1; i<3; i++){				
				out.print("\t" + totMins[i]);
			}
			out.println();
			out.print(labels[2]);
			for(int i=1; i<3; i++){				
				out.print("\t" + totMaxs[i]);
			}
			out.println();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
}



