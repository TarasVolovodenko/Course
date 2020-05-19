import java.io.FileDescriptor;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.io.File;

public class Main {
	public static void main(String[]args)
	{
		FileProcessor fp = new FileProcessor();
		final File folder = new File("/home/taras/study/parallel/aclImdb/");

		fp.listFilesForFolder(folder);
		System.out.println(fp.files);
	}
}
