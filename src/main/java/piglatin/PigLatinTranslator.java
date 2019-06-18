package piglatin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PigLatinTranslator {

	private static final Logger LOGGER = LogManager.getLogger(PigLatinTranslator.class.getName());

	private final static String PUNCT_PATTERN = "\\p{Punct}";
	private final static String UPPER_PATTERN = "\\p{Upper}";

	public void translate(File inputFile, File outputFile) {
		LOGGER.info("Start translate string (word, sentence, or paragraph) into “pig-latin”...");

		StringBuilder pigLineBuilder, pigWordBuilder;
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(outputFile.getAbsolutePath());
			Scanner lineScanner = new Scanner(inputFile);
			String lineDelimiter = "";
			while (lineScanner.hasNextLine()) {
				String line = lineScanner.nextLine();

				pigLineBuilder = new StringBuilder();
				pigLineBuilder.append(lineDelimiter);
				lineDelimiter = System.getProperty("line.separator");
				pigLineBuilder.append(isParagraph(line) ? "\t" : "");

				String inLineDelimiter = "";
				Scanner wordScanner = new Scanner(line);
				while (wordScanner.hasNext()) {
					pigWordBuilder = new StringBuilder();
					String inWordDelimiter = "";
					for (String word : getWordListByHyphen(wordScanner.next())) {
						pigWordBuilder.append(inWordDelimiter);
						inWordDelimiter = "-";
						pigWordBuilder.append(canModify(word) ? getPigLatin(word) : word);
					}

					pigLineBuilder.append(inLineDelimiter);
					inLineDelimiter = " ";
					pigLineBuilder.append(pigWordBuilder.toString());
				}
				wordScanner.close();

				fileWriter.write(pigLineBuilder.toString());
			}

			lineScanner.close();

		} catch (FileNotFoundException e1) {
			LOGGER.error("Cannot read from input file", e1);
			
		} catch (IOException e2) {
			LOGGER.error("Cannot write to output file", e2);
			
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException e3) {
				LOGGER.warn("Problem close file writer", e3);
			}
		}

		LOGGER.info("Finish translate.");
	}

	private String getPigLatin(String word) {
		StringBuilder result = new StringBuilder(word.toLowerCase().replaceAll(PUNCT_PATTERN, ""));
		if (isConsonant(word)) {
			result.append("way");
		} else {
			result.insert(result.length(), result.charAt(0));
			result.append("ay");
			result.deleteCharAt(0);
		}

		adjustPunctuation(word, result);
		adjustCapitalization(word, result);

		return result.toString();
	}

	private List<String> getWordListByHyphen(String word) {
		List<String> wordList = new ArrayList<>();
		if (word.contains("-")) {
			wordList.addAll(Arrays.asList(word.split("-")));
		} else {
			wordList.add(word);
		}
		return wordList;
	}

	private boolean canModify(String word) {
		return !word.replaceAll(PUNCT_PATTERN + "*$", "").endsWith("way") && (isConsonant(word) || isVowel(word));
	}

	private boolean isConsonant(String word) {
		return Pattern.compile("^[aeiou]", Pattern.CASE_INSENSITIVE).matcher(word).find();
	}

	private boolean isVowel(String word) {
		return Pattern.compile("^[bcdfghjklmnpqrstvwxyz]", Pattern.CASE_INSENSITIVE).matcher(word).find();
	}

	private boolean isParagraph(String line) {
		return Pattern.compile("^\\p{Blank}").matcher(line).find();
	}

	private void adjustPunctuation(String word, StringBuilder result) {
		adjustWord(new StringBuilder(word).reverse().toString(), result.reverse(), PUNCT_PATTERN);
		result.reverse();
	}

	private void adjustCapitalization(String word, StringBuilder result) {
		adjustWord(word, result, UPPER_PATTERN);
	}

	private void adjustWord(String word, StringBuilder result, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(word);
		while (matcher.find()) {
			int index = matcher.start();
			if (regex.equals(UPPER_PATTERN)) {
				result.setCharAt(index, Character.toUpperCase(result.charAt(index)));
			} else if (regex.equals(PUNCT_PATTERN)) {
				result.insert(index, matcher.group());
			}
		}
	}
}
