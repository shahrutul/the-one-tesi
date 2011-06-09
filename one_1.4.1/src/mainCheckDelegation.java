
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;


public class mainCheckDelegation {
	private static final String REPORTS_DIR = "reports/cluster";
	private static final String DELEGA_DIR = "reports/cluster/delega/";
	private static final String NODELEGA_DIR = "reports/cluster/noDelega/";
	private static final String KEY_VIRTUALFILE = "First VirtualFile satisfied:\t";
	private static File[] allFiles;
	private static Vector<File> filesConDelega = new Vector<File>();
	private static Vector<File> filesSenzaDelega = new Vector<File>();
	
	private static class SimulazioneIncompletaException extends Exception{}
	 
	
	private static class ParamFilenameFilter implements FilenameFilter{
		private String match;
		
		public ParamFilenameFilter(File f) {
			match = f.getName().substring(
						f.getName().indexOf("FG"),
						f.getName().indexOf("_Del")-1);
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.contains(match);			
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File dir = new File(REPORTS_DIR);
		allFiles = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				return arg0.isFile() && arg0.length()!=0;
			}
		});		
		System.out.println(allFiles.length+ " total files read");
		
		for(int i=0; i< allFiles.length; i++){
			File currentFile = allFiles[i];
			if(filesConDelega.contains(currentFile) || filesSenzaDelega.contains(currentFile)){
				continue;
			}
			
			File[] filesStessaSim = dir.listFiles(new ParamFilenameFilter(currentFile));
			if(filesStessaSim.length < 3){
				continue;
			}
			
			/*
			System.out.println("File cercato: "+currentFile.getName());
			for(int j=0; j< filesStessaSim.length; j++){
				System.out.println(filesStessaSim[j].getName());
			}
			System.out.println();
			*/
			
			try{
				boolean usaDelega = diversiTempiVF(filesStessaSim);
				if(usaDelega){
					//System.err.println(currentFile.getName()+ " usa delega");
					for(int j =0; j<filesStessaSim.length; j++){
						filesConDelega.add(filesStessaSim[j]);
					}
				}
				else{
					for(int j =0; j<filesStessaSim.length; j++){
						filesSenzaDelega.add(filesStessaSim[j]);
					}
				}
			}catch(SimulazioneIncompletaException e){
				continue;
			}
						
		}
		
		System.out.println(filesConDelega.size()+" nuovi files per analisi");
		for(File f:filesConDelega){
			f.renameTo(new File(DELEGA_DIR+f.getName()));
		}
		for(File f:filesSenzaDelega){
			f.renameTo(new File(NODELEGA_DIR+f.getName()));
		}
	}

	private static boolean diversiTempiVF(File[] files) throws SimulazioneIncompletaException {
		boolean del0Trovato = false;
		boolean del1Trovato = false;
		boolean del2Trovato = false;
		double tempoDel0 = 0;
		double tempoDel1 = 0;
		double tempoDel2 = 0;
		int intervalloMinimo = 2;
		
		for(int i=0; i< files.length; i++){
			File currentFile = files[i];
			double vfTime = getVirtualFileTime(currentFile);
			if(vfTime == -1){
				continue;
			}	
			if(currentFile.getName().contains("Del0")){
				del0Trovato = true;
				tempoDel0 = vfTime;
			}
			if(currentFile.getName().contains("Del1")){
				del1Trovato = true;
				tempoDel1 = vfTime;
			}
			if(currentFile.getName().contains("Del2")){
				del2Trovato = true;
				tempoDel2 = vfTime;
			}			
			
		}
		
		if(!(del0Trovato && del1Trovato && del2Trovato)){
			throw new SimulazioneIncompletaException();
		}
		
		return (tempoDel0 - tempoDel2 - intervalloMinimo) > 0;
	}

	private static double getVirtualFileTime(File currentFile) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(currentFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if(line.contains(KEY_VIRTUALFILE)){
				return Double.parseDouble(line.substring(KEY_VIRTUALFILE.length()));						
			}
		}
		return -1;
	}
	
}



