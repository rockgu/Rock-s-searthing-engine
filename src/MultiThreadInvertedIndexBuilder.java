import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class MultiThreadInvertedIndexBuilder { 
	/**
	 * It initialize the number of the thread in the workQueue and call the traverse
	 * method to the muti thread
	 * 
	 * @param directory The path of the file
	 * @param invert    The invertedIndex Data structure
	 * @param thread    The number of the thread
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void traverse(Path directory, ThreadSafeInvertedIndex invert,  WorkQueue queue)
			throws IOException, InterruptedException {
		traverseHelper(directory, invert,queue	);
		queue.finish();
	}

	/**
	 * read all the path of file from the directory recursively. After the got all
	 * the path of file, it will put to the fileReader method
	 * 
	 * @param directory The directory that need to traverse
	 * @param invert    The invertIndex object to pass into fileReader method
	 * @throws IOException When file is not exist
	 * 
	 * @see {@link DirectoryStream}
	 */
	private static void traverseHelper(Path directory, ThreadSafeInvertedIndex invert,WorkQueue queue) throws IOException {
		if (Files.isDirectory(directory)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
				Iterator<Path> list = listing.iterator();
				Path currentFile;
				while (list.hasNext()) {
					currentFile = list.next();
					traverseHelper(currentFile, invert, queue);
				}
			}
		} else {
			String path1 = directory.toString().toLowerCase();
			if (path1.endsWith(".text") || path1.endsWith(".txt")) {
				queue.execute(new fileReaderTask(directory, invert));
			}
		}
	}

	/**
	 * The inner class to use mutlithread to execute the fileReader method
	 * 
	 * @author rockgu
	 *
	 */
	public static class fileReaderTask implements Runnable {
		private Path directory;
		private InvertedIndex invert;

		public fileReaderTask(Path directory, InvertedIndex invert) {
			this.directory = directory;
			this.invert = invert;
		}

		@Override
		public void run() {
			InvertedIndex temp = new InvertedIndex();
			try {
				InvertedIndexBuilder.fileReader(directory, temp);
				this.invert.addAll(temp);
			} catch (FileNotFoundException e) {
				System.out.println("The writer can't write to the file " + directory);
			} catch (IOException e) {
				System.out.println("The writer can't write to the file " + directory);
			}
		}

	}

}
