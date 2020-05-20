import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.io.File;
import java.util.regex.*;

public class Parser implements Callable<Map<String, Map<Integer, Integer>>> {
	ArrayList<File> files;
	public Parser(ArrayList<File> files)	{
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
			ArrayList<String> words = new ArrayList<String>();
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
	public Map<String, Map<Integer, Integer>> call() throws Exception {
		Map<String, Map<Integer, Integer>> dict = new HashMap<String, Map<Integer, Integer>>();
		ArrayList<String> temp;
		Map<Integer, Integer> t;
		for (File file : files)	{
			temp = parse(file);
			for (String word : temp) {
				t = dict.get(word);
				if (t == null)
					t = new HashMap<Integer, Integer>();
				t.computeIfAbsent(getDocID(file), k -> 0);
				t.compute(getDocID(file), (k, v) -> v + 1);
				dict.put(word, t);
			}
		}
		return dict;
	}
}
