import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;
import java.io.File;
import java.util.regex.*;

public class Async {

	public Map<String, Map<String, Integer>> dictionary;
	public ArrayList<ParserAsync> parsers;
	public final File vocab;
	public final File folder;
	public final int nThreads;

	public Async(int nThreads, final File vocab, final File folder)	{

		this.vocab = vocab;
		this.folder = folder;
		this.nThreads = nThreads;
	}

	public void indexHash()	{
		dictionary = new ConcurrentHashMap<String, Map<String, Integer>>(100000, (float)0.75, nThreads);
//		fillVocabulary();
		FileProcessor fp = new FileProcessor();
		fp.listFilesForFolder(folder);
//		System.out.println(fp.files.size());

		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		ArrayList<Future> futures = new ArrayList<>();
		parsers = new ArrayList<>();

		try {

			for (int i = 0; i < nThreads; i++)
				futures.add(executor.submit(new Async.ParserAsync(new ArrayList<File>(fp.files.subList(i * fp.files.size() / nThreads, (i + 1) * fp.files.size() / nThreads)))));

			for (Future future : futures)
				future.get();

			executor.shutdown();

//			System.out.println("Total: " + dictionary.size());
		}
		catch (Exception e){}
	}

	public void indexTree()	{
		dictionary = new ConcurrentSkipListMap<>();
//		fillVocabulary();
		FileProcessor fp = new FileProcessor();
		fp.listFilesForFolder(folder);
//		System.out.println(fp.files.size());

		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		ArrayList<Future> futures = new ArrayList<>();
		parsers = new ArrayList<>();

		try {

			for (int i = 0; i < nThreads; i++)
				futures.add(executor.submit(new ParserAsync(new ArrayList<File>(fp.files.subList(i * fp.files.size() / nThreads, (i + 1) * fp.files.size() / nThreads)))));

			for (Future future : futures)
				future.get();

			executor.shutdown();

//			System.out.println("Total: " + dictionary.size());
		}
		catch (Exception e){}
	}

	public void fillVocabulary()	{
		try {
			Scanner vocabScanner = new Scanner(vocab);
			while (vocabScanner.hasNextLine()) {
				dictionary.put(vocabScanner.nextLine(), new HashMap<>());
			}
			vocabScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void search(String request)  {
		String[] words = request.trim().toLowerCase().split(" +");
		Set<String> result = new HashSet<>(dictionary.get(words[0]).keySet());
		for (String word : words)	{
			result.retainAll(dictionary.get(word).keySet());
		}
		for (String id : result)	{
			System.out.println(id + ".txt");
		}
	}

	class ParserAsync implements Runnable {

		ArrayList<File> files;

		public ParserAsync(ArrayList<File> files)	{
			this.files = files;
		}

		public String getDocID(File file)	{
			if (file != null)
			{
				String id = file.getName().split(".txt")[0];
				return id;
			}
			return null;
		}

		public ArrayList<String> parse(File file)	{
			try {
				Scanner sc = new Scanner(file);
				String temp;
				ArrayList<String> words = new ArrayList<>();

				while (sc.hasNextLine())	{

					temp = sc.nextLine();
					temp = temp.replaceAll("<br />"," ");

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
			long start = System.currentTimeMillis();
			ArrayList<String> temp;
			Map<String, Integer> oldV, newV;

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
			long end = System.currentTimeMillis();
			System.out.println(end - start);
		}
	}

}
