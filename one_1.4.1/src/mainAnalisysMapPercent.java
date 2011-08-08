
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;



public class mainAnalisysMapPercent {
	private static final String REPORTS_DIR = "reports/datiFinali/multiHopPerc/";
	private static final String CONTROL_FILE = "controlSet.dat";
	private static final String OUTPUTS_FILE = "reports/datiFinali/multiHopPerc/mapPerc.dat";
	private static File[] files;
	private static final int[] PERCs = {10,25,50,75,100};
	
	private static final int initialWidth = 1000;
	private static final int initialHeight = 800;	
	private static final int finalWidth = 50;
	private static final int finalHeight = 40;
	private static final int xStep = initialWidth / finalWidth;
	private static final int yStep = initialHeight / finalHeight;
	private static final int PRINT_MULTIPLIER = 18;
	private static final String[] stratLabels = {"M2MShare_1_hop", "M2MShare_2_hop", "M2MShare_3_hop"};
	
	private static class ParamFilenameFilter implements FilenameFilter{		
		
		@Override
		public boolean accept(File dir, String name) {
			return  name.contains("perc.dat");
		}
		
	}
	
	/**
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		File dir = new File(REPORTS_DIR);	
		files = dir.listFiles(new ParamFilenameFilter());
		File controlFile = dir.listFiles(new FilenameFilter() {			
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(CONTROL_FILE);
			}
		})[0];
		
		int totalFiles = files.length;
		System.out.println(totalFiles+ " files read, 1 control file");
		
		long controlValue = getScore(controlFile);
		System.out.println(controlFile.getName()+": "+controlValue);
		

		int totalFilesRead = 0;

		long[][] sumValues = new long[finalWidth][finalHeight];
		int filesRead = 0;
		double[][] mapAvgs = new double[PERCs.length][3];

		for(int p=0; p<PERCs.length; p++){
			for(int hop=1; hop<=3; hop++){
				totalFilesRead++;
				File currentFile = new File(REPORTS_DIR + "M2MShare_"+hop+"_hop_"+PERCs[p]+"perc.dat");
				long tempScore = getScore(currentFile);
				double percScore = 100*((double)tempScore/(double)controlValue);
				System.out.println((100*totalFilesRead/totalFiles)+"% - "+
						currentFile.getName()+": "+tempScore+"/"+controlValue + " ("+percScore+"%)");
				mapAvgs[p][hop-1] = percScore;
			}
		}
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUTS_FILE));
			out.print("percReDelegation");
			for(int i=0; i<3; i++){	
				out.print("\t" + stratLabels[i]);
			}
			out.println();
			for(int perc = 0; perc<PERCs.length; perc++){
				
				out.print(PERCs[perc]);
				for(int i=0; i<3; i++){	
					out.print("\t" + (mapAvgs[perc][i]));
				}
				out.println();
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		for(int ifile=0; ifile< files.length; ifile++){
			File currentFile = files[ifile];
			System.out.println((100*totalFilesRead/totalFiles)+"% - "+currentFile.getName());
			
			filesRead++;
			totalFilesRead++;
			
		}

		// faccio la media totale 
		for(int x=0; x<finalWidth; x++){
			for(int y=0; y<finalHeight; y++){
				sumValues[x][y] = sumValues[x][y] / filesRead;
			}
		}*/

/*
		try {
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUTS_DIR +"controlSet.dat"));
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
		}*/

		System.out.println("100% - Complete!");

	}
	
	private static long getScore (File f){
		long val = 0;
		ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>();
		Scanner input = null;
		try {
			input = new Scanner(f);
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
				if(temp>0){
					val++;
				}

			}
			a.add(col);
		}
		input.close();
		return val;
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



