import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;
import java.io.File;
import java.util.regex.*;

public class Async {
	public ConcurrentSkipListMap<String, Map<Integer, Integer>> dictionary;
	public ArrayList<ParserAsync> parsers;
	public Async(int nThreads)	{

		dictionary = new ConcurrentSkipListMap<>();

		FileProcessor fp = new FileProcessor();
		final File folder = new File("/home/taras/study/parallel/aclImdb/");
		fp.listFilesForFolder(folder);

		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		ArrayList<Future> futures = new ArrayList<>();
		parsers = new ArrayList<>();
		try {

			for (int i = 0; i < nThreads; i++)
				futures.add(executor.submit(new ParserAsync(new ArrayList<File>(fp.files.subList(i * fp.files.size() / nThreads, (i + 1) * fp.files.size() / nThreads)))));

			for (Future future : futures)	{
				future.get();
			}

			executor.shutdown();


			dictionary.forEach((k, v) -> {
				System.out.println(k + " " + v.size());
			});
			System.out.println("Total: " + dictionary.size());
		}
		catch (Exception e){}
	}




	class ParserAsync implements Runnable {
		ArrayList<File> files;
		public ParserAsync(ArrayList<File> files)	{
			this.files = files;
		}

		public int getDocID(File file)	{
			if (file != null)
				return Integer.parseInt(file.getName().split("_")[0] + file.getName().split("_")[1].split(".txt")[0]);
			return 0;
		}

		public ArrayList<String> parse(File file)	{
			try {
				Scanner sc = new Scanner(file);
				String temp;
				ArrayList<String> words = new ArrayList<>();
				while (sc.hasNextLine())	{
					temp = sc.nextLine();
					temp = temp.replaceAll("<br />"," ");
//				words.add(temp);
//				words.addAll(Arrays.asList(temp.split(" +")));
					Pattern pattern =
							Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS
									| Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(temp.toLowerCase());

					while (matcher.find())
						words.add(matcher.group());


				}
				return words;
			}
			catch (FileNotFoundException e) {
				System.out.println("File " + file + " not found.");
			}
			return null;
		}
		@Override
		public void run() {
			ArrayList<String> temp;
			Map<Integer, Integer> oldV, newV;
			Boolean f;
			for (File file : files)	{
				temp = parse(file);
				for (String word : temp) {
					oldV = dictionary.get(word);

					if (oldV == null)	{
						newV = new HashMap<>();
						newV.put(getDocID(file), 1);
						dictionary.putIfAbsent(word, newV);
					}
					else {
						do {
							oldV = dictionary.get(word);
							newV = new HashMap<>(oldV);
							if (newV.containsKey(getDocID(file)))
								newV.computeIfPresent(getDocID(file), (k, v) -> v + 1);
							else
								newV.putIfAbsent(getDocID(file), 1);
						}
						while (!dictionary.replace(word, oldV, newV));
					}
				}
			}
		}
	}

}
