import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.io.File;

public class FileProcessor {
	public ArrayList<File> files = new ArrayList<File>();
	public ArrayList<File> listFilesForFolder(final File folder) {
		File[]f = folder.listFiles();
		int i = 0;
		int lowerIndex = f.length / 50 * 3, upperIndex = f.length / 50 * 4;
		for (final File fileEntry : f) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			}
			else {
				try {
					i = Integer.parseInt(fileEntry.getName().split("_")[0]);
//				System.out.println(i);
					if (i < upperIndex && i >= lowerIndex) {
						files.add(fileEntry);
					}
				} catch (Exception e)	{}
			}
		}
		return files;
	}
}
