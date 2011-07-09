import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class mainAnalisysOrdinaMovimento {
	private static final String REPORTS_FILE = 
			"reports/datiFinali/FG1008653149_MM1008653149_Del1_FDS1_multiHop_3_M2MShareMovementReport.txt";
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
				
		File currentFile = new File(REPORTS_FILE);
		Scanner scanner = null;
		try {
			scanner = new Scanner(currentFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Vector<Vector<String>> vects = new Vector<Vector<String>>();
		vects.add(new Vector<String>());
		vects.add(new Vector<String>());
		vects.add(new Vector<String>());
		vects.add(new Vector<String>());

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			int strat = Integer.parseInt(line.substring(line.length()-1));
			vects.get(strat).add(line);
		}
		scanner.close();

		try {
			PrintWriter out = new PrintWriter(new FileWriter(REPORTS_FILE));
			for(int str = 3; str>=0; str--){	
				System.err.println(str + " -> " + vects.get(str).size());
				for(int i=0; i<vects.get(str).size(); i++){	
					out.println(vects.get(str).get(i));
				}
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}

}
