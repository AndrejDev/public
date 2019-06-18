package piglatin;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PigLatinMain {

	private static final Logger LOGGER = LogManager.getLogger(PigLatinMain.class.getName());

	public static final String FOLDERNAME = "textfiles";
	public static final String INPUT_FILENAME = "inputText.txt";
	public static final String OUTPUT_FILENAME = "outputText.txt";

	public static void main(String[] args) {

		try {
			LOGGER.info("Get input file: " + getFilePath(INPUT_FILENAME));
			File inputFile = new File(getFilePath(INPUT_FILENAME));
			LOGGER.info("Get/Create output file: " + getFilePath(OUTPUT_FILENAME));
			File outputFile = new File(getFilePath(OUTPUT_FILENAME));
			outputFile.createNewFile();
			new PigLatinTranslator().translate(inputFile, outputFile);
		} catch (IOException e) {
			LOGGER.error("Cannot get file", e);
		}

	}

	private static String getFilePath(String fileName) {
		return System.getProperty("user.dir") + System.getProperty("file.separator") + FOLDERNAME
				+ System.getProperty("file.separator") + fileName;
	}

}
