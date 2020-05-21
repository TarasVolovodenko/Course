import java.io.FileDescriptor;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.io.File;

public class Main {
	public static void main(String[]args) {

		final File folder = new File(args[1]), vocab = new File(args[0]);

//		Speed test
		Async a = null;
		for (int nThreads = 128; nThreads <= 1024; nThreads *= 2) {
			a = new Async(nThreads, vocab, folder);
			long start = System.currentTimeMillis();
			a.indexHash();
			long mid = System.currentTimeMillis();
			a.indexTree();
			long end = System.currentTimeMillis();
			System.out.println((mid - start) + "\t" + (end - mid));
		}

	}
}
