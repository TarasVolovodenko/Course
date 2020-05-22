import java.util.*;
import java.io.File;

public class Main {
	static ArrayList<File> folders = new ArrayList<>();
	static File vocab = null;
	static int nThreads = 1;
	public static void main(String[]args) {

//		read command line arguments
		try {
			for (int i = 0; i < args.length; i++)	{
				if (args[i].equals("-j"))
					nThreads = Integer.parseInt(args[++i]);
				else if (args[i].equals("-v"))
					vocab = new File(args[++i]);
				else folders.add(new File(args[i]));
			}
			if (folders.size() == 0) {
				System.out.print("Illegal arguments. Try again.\njava Main [-j <threads amount>] [-v <vocabulary-path>]");
				return;
			}
		}
		catch (Exception e)	{
			System.out.print("Illegal arguments. Try again.\njava Main [-j <threads amount>] [-v <vocabulary-path>]");
			return;
		}

		Async a = null;
		long start, end, th = 0;

//		Search test
		Scanner sc = new Scanner(System.in);
		a = new Async(nThreads, vocab, folders);
		System.out.println("Building indexes...");
		a.indexHash();
		System.out.println("Indexing successful.");
		// user interface
		while (true) {

			System.out.println("S -- search\nE -- exit");
			String command = sc.nextLine(), request;

			if (command.toLowerCase().equals("e"))
				return;
			if (command.toLowerCase().equals("s"))	{
				System.out.println("Search request: ");
				request = sc.nextLine();
				start = System.nanoTime();
				a.search(request);
				end = System.nanoTime();
				System.out.println("Elapsed time: " + (double)(end - start) / 1000000 + " ms.");
			}
			else
				System.out.println("Unknown command. Try again.");
		}
	}
	public static void speedTest()	{
		Async a = null;
		long start, end, th = 0;
		for (int i = 0; i < 10; i++) {
			if (vocab == null)
				a = new Async(nThreads, vocab, folders);
			start = System.currentTimeMillis();
			a.indexHash();
			end = System.currentTimeMillis();
			th += end - start;
			System.out.println(end - start);
		}
		System.out.println(th / 10);
	}
}
