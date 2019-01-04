import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SizeChecker {
	public static List<File> files=new LinkedList<File>();
	  public static String DownlodFolder = "/Users/rarumalla/Telugu Songs/";

public static void main(String[] args) {
		listf(DownlodFolder,files);
		int k=0;
		for (int i = 0; i < files.size(); i++) {
			if((files.get(i).length()/ (1024*1024)<=2)){
				k++;
			System.out.println(files.get(i).getName() +"   "+(files.get(i).length()/ (1024*1024)));
		//	files.get(i).delete();
			
			}
		}
		System.out.println(files.size() +"  "+k);
}

public static void listf(String directoryName, List<File> files) {
    File directory = new File(directoryName);

    // Get all files from a directory.
    File[] fList = directory.listFiles();
    if(fList != null)
        for (File file : fList) {      
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }


}

