import java.util.*;
import java.io.File;

public class Main {
	public static void main(String[]args) {

		final File folder = new File(args[1]), vocab = new File(args[0]);
		final int nThreads = Integer.parseInt(args[2]);
		Async a = null;
		long start, end, th = 0;

//		Speed test
		for (int i = 0; i < 10; i++) {
			a = new Async(nThreads, vocab, folder);
			start = System.currentTimeMillis();
			a.indexHash();
			end = System.currentTimeMillis();
			th += end - start;
			System.out.println(end - start);
		}
		System.out.println(th / 10);

//		Search test
		Scanner sc = new Scanner(System.in);
		a = new Async(4, vocab, folder);
		a.indexHash();

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
				System.out.println("Elapsed time: " + (end - start) / 1000 + " us.");
			}
			else
				System.out.println("Unknown command. Try again.");
		}
	}
}
