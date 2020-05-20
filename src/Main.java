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

		int nThreads = 20;

		fp.listFilesForFolder(folder);
		System.out.println(fp.files.size());
		ArrayList<Parser> parsers = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		ArrayList<Map<String, Map<Integer, Integer>>> results = new ArrayList<>();


		try {

			for (int i = 0; i < nThreads; i++)
				parsers.add(new Parser(new ArrayList<File>(fp.files.subList(i * fp.files.size() / nThreads, (i + 1) * fp.files.size() / nThreads))));

			for (Future<Map<String, Map<Integer, Integer>>> result : executor.invokeAll(parsers)) {
				results.add(result.get());
			}

			executor.shutdown();


			int s = 0;
			for (Map<String, Map<Integer, Integer>> m : results)
			{
				s += m.size();
				System.out.println(m.size());
			}
			System.out.println("Total: " + s);
		}
		catch (Exception e){}
	}
}
