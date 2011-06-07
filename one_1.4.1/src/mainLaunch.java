import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;


public class mainLaunch {
	
	private static final String REPORTS_DIR = "reports/cluster";
	private static final String SEED_FILE = "seedsCluster.txt";
	private static final String SEED_SETTING_FILE = "seedSettings.txt";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* Read the seeds */
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(SEED_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		Vector<Integer> seeds = new Vector<Integer>();
		while (scanner.hasNextLine()) {
			try {
				seeds.add(scanner.nextInt());
			}catch (Exception e){}
		}
		System.err.println("Read "+seeds.size()+" seeds.");
		
		int fileGenIndex = 0, movementIndex = 0;
		boolean validSeeds = false;
		
		/* Read the file list of reports */
		File folder = new File(REPORTS_DIR);

		/* Start generating configuration seeds */
		while(!validSeeds){
			final String matchString = "FG"+seeds.get(fileGenIndex)+"_MM"+seeds.get(movementIndex);
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.contains(matchString);
				}
			};

			if(folder.list(filter).length == 0){
				validSeeds = true;
			}
			else{
				/* next combination of seeds */
				System.err.println("Combination "+matchString+" already used");
				if(fileGenIndex == movementIndex && movementIndex == seeds.size()){
					System.err.println("All seed combinations tested!");
					return;
				}

				if(movementIndex <  seeds.size()){
					movementIndex++;
					fileGenIndex++;
				}
			}
		}
		/* Found a valid seeds combination */
		System.err.println("Valid combination: "+seeds.get(fileGenIndex)+"_"+seeds.get(movementIndex));

		try {
			PrintWriter out = new PrintWriter(new FileWriter(SEED_SETTING_FILE));
			out.println("FilesGenerator.rngSeed = "+seeds.get(fileGenIndex));
			out.println("MovementModel.rngSeed = "+seeds.get(movementIndex));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
