package com.selesse.apps.winescanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import android.util.Log;

public class FileManager {
	public final static String WINEDB_LOCATION = "/sdcard/winescanner";
	public final static String WINEDB_FILE = "/wineDB";
	public static ArrayList<Wine> m_wine;

	public static ArrayList<Wine> loadWine() {
		ArrayList<Wine> wines = new ArrayList<Wine>();

		File file = new File(WINEDB_LOCATION + WINEDB_FILE);

		if (file.exists()) {

			try {
				Scanner in = new Scanner(file);
				in.useDelimiter("\\||\n");

				while (in.hasNext()) {
					Wine wine = new Wine();
					wine.setBarcode(in.next());
					wine.setName(in.next());
					wine.setCountry(in.next());
					wine.setYear(in.next());
					wine.setDescription(in.next());
					wine.setRating(in.next());
					wine.setPrice(in.next());
					wine.setImageURL(in.next());
					String comment = in.nextLine();
					if (comment.startsWith("|null"))
						wine.setComment("null");
					else
						wine.setComment(comment.substring(1,
								comment.length()));

					Log.v("WineScanner", "Checking in " + wine.toString());

					wines.add(wine);
				}

			} catch (FileNotFoundException e) {
				Log.v("WineScanner",
						"File not found (shouldn't ever get here though)");
				e.printStackTrace();
			}

		}

		// otherwise, file doesn't exist - we return an empty array list
		Collections.sort(wines);
		m_wine = wines;
		return wines;

	}

	public static void deleteWine(int position) {

		Wine w = m_wine.remove(position);
		String deleteCode = w.getBarcode();

		try {
			File file = new File(WINEDB_LOCATION + WINEDB_FILE);
			Scanner in = new Scanner(file);

			ArrayList<String> fileContents = new ArrayList<String>();
			while (in.hasNext()) {
				String buffer = in.nextLine();
				String barcode = buffer.substring(0, buffer.indexOf('|'));
				if (barcode.equals(deleteCode))
					continue;
				fileContents.add(buffer);
			}

			PrintWriter out = new PrintWriter(file);

			for (String line : fileContents)
				out.println(line);

			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void addWine(Wine newWine) {
		m_wine.add(newWine);

		File file = new File(WINEDB_LOCATION);
		if (!file.exists())
			file.mkdir();

		file = new File(WINEDB_LOCATION + WINEDB_FILE);

		try {
			PrintWriter out = new PrintWriter(file);

			for (Wine w : m_wine) {
				out.print(w.getBarcode() + "|");
				out.print(w.getName() + "|");
				out.print(w.getCountry() + "|");
				out.print(w.getYear() + "|");
				out.print(w.getDescription() + "|");
				out.print(w.getRating() + "|");
				out.print(w.getPrice() + "|");
				out.print(w.getImageURL() + "|");
				out.println(w.getComment());
				out.flush();
			}

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
