
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;



public class mainMapControlSet {
	private static final String REPORTS_DIR = "reports/controlSet/";
	private static final String OUTPUTS_DIR = "reports/datiFinali/multiHopPerc/";
	private static File[] files;
	
	private static final int initialWidth = 1000;
	private static final int initialHeight = 800;	
	private static final int finalWidth = 50;
	private static final int finalHeight = 40;
	private static final int xStep = initialWidth / finalWidth;
	private static final int yStep = initialHeight / finalHeight;
	private static final int PRINT_MULTIPLIER = 18;
	
	private static class ParamFilenameFilter implements FilenameFilter{		
		
		@Override
		public boolean accept(File dir, String name) {
			return  name.contains("M2MShareMapCoverageControl");
		}
		
	}
	
	/**
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		File dir = new File(REPORTS_DIR);		

		files = dir.listFiles(new ParamFilenameFilter());
		int totalFiles = files.length;
		System.out.println(totalFiles+ " files read");

		int totalFilesRead = 0;

		long[][] sumValues = new long[finalWidth][finalHeight];
		int filesRead = 0;

		for(int ifile=0; ifile< files.length; ifile++){
			File currentFile = files[ifile];
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
		}

		System.out.println("100% - Complete!");

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



