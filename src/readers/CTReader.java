package readers;

import graph.Aptamer;
import graph.IndexBase;
import graph.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CTReader {

	/**
	 *
	 * @param filename
	 *            Path to a ct file to open
	 * @throws FileNotFoundException
	 *             If it cannot file the file provided
	 * @throws IOException
	 *             If a row doesn't have 6 elements.
	 */
	public static Aptamer readCTFile(String filename) throws IOException {
		Scanner scan;
		try {
			scan = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Could not find CT file: " + filename);
		}

		Aptamer a = readAptamerFromScanner(scan);

		scan.close();

		return a;

	}

	/**
	 *
	 * @param filename
	 *            Path to a ct file to open
	 * @throws FileNotFoundException
	 *             If it cannot file the file provided
	 * @throws IOException
	 *             If a row doesn't have 6 elements.
	 */
	public static List<Aptamer> readCTListFile(String filename) throws IOException {
		Scanner scan;
		try {
			scan = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Could not find CT file: " + filename);
		}

		List<Aptamer> as = new ArrayList<>();
		while (scan.hasNextInt()) {
			as.add(readAptamerFromScanner(scan));
			while(scan.hasNextLine() & !scan.hasNextInt()){
				scan.nextLine(); //Throw away lines I don't care about.
			}
		}

		scan.close();

		return as;

	}

	private static Aptamer readAptamerFromScanner(Scanner scan) throws IOException {

		int numLines = scan.nextInt();
		scan.nextLine(); // read the rest of the line. Throw it away.

		Aptamer a = new Aptamer(numLines);
		for (int i = 0; i < numLines; i++) {
			String[] line = scan.nextLine().split("\\s+");
			if (line.length != 6) {
				throw new IOException("Found row of length " + line.length + " on line " + (i + 1));
			}
			// 1: Idx 2:Base 3:5' 4:3'5:paired 6:historicalIdx
			int idx = Integer.parseInt(line[0]) - 1;
			int five = Integer.parseInt(line[2]) - 1;
			int three = Integer.parseInt(line[3]) - 1;
			int hbond = Integer.parseInt(line[4]) - 1;
			if (idx != i) {
				throw new IOException("Row idx " + line[0] + " did not contain numbers as needed");
			}
			Node n = new IndexBase(line[1], idx, five, three, hbond);
			a.addBase(i,n);
		}

		return a;
	}

	public static void main(String[] args) throws IOException {
		List<Aptamer> a = readCTListFile("/local/scratch/aptamer/somect.ct");
		System.out.println("Read in " + a.size() + " aptamers");
		for(Aptamer aa: a){
			aa.simplify();
			System.out.println(aa.toString());
		}
	}
}
