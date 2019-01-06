import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeJSONWriter {

	/**
	 * Writes several tab <code>\t</code> symbols using the provided {@link Writer}.
	 * @param times The number of times to write the tab symbol
	 * @param writer The writer to use
	 * @throws IOException If the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/***
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 * 
	 * @param element The element to quote
	 * @param writer The writer to use
	 * @throws IOException If the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param Elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to the
	 * specified file.
	 *
	 * @param elements The elements to convert to JSON
	 * @param path The path to the file write to output
	 * @throws IOException If the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers using
	 * the provided {@link Writer} and indentation level.
	 *
	 * @param elements The elements to convert to JSON
	 * @param writer The writer to use
	 * @param level The initial indentation level
	 * @throws IOException If the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer, int level) throws IOException {
		writer.write('[');
		writer.write(System.lineSeparator());
		if (!elements.isEmpty()) {
			for (Integer element : elements.headSet(elements.last())) {
				indent(level + 1, writer);
				writer.write(element.toString());
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			writer.write(elements.last().toString());
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write("]");
	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to the specified
	 * file.
	 *
	 * @param elements The elements to convert to JSON
	 * @param path The path to the file write to output
	 * @throws IOException If the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements The elements to convert to JSON
	 * @param writer The writer to use
	 * @param level The initial indentation level
	 * @throws IOException If the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer, int level) throws IOException {
		writer.write('{');
		writer.write(System.lineSeparator());
		if (!elements.isEmpty()) {
			for (String element : elements.headMap(elements.lastKey()).keySet()) {
				indent(level + 1, writer);
				TreeJSONWriter.quote(element, writer);
				writer.write(": " + elements.get(element));
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			TreeJSONWriter.quote(elements.lastKey(), writer);
			writer.write(": " + elements.get(elements.lastKey()));
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write("}");
	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements The elements to convert to JSON
	 * @param writer The writer to use
	 * @param level The initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {
		writer.write('{');
		writer.write(System.lineSeparator());
		if (!elements.isEmpty()) {
			for (String element : elements.headMap(elements.lastKey()).keySet()) {
				indent(level + 1, writer);
				TreeJSONWriter.quote(element, writer);
				writer.write(": ");
				asArray(elements.get(element), writer, level + 1);
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			TreeJSONWriter.quote(elements.lastKey(), writer);
			writer.write(": ");
			asArray(elements.get(elements.lastKey()), writer, level + 1);
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write("}");
	}
	/**
	 * Make the invertedindex data structure to the JON object
	 * @param elements The elements to convert to JSON
	 * @param  writer The writer to use
	 * @param   level The initial indentation level
	 * @throws IOException IOException if the writer encounters any issues
	 * 
	 */

	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {

		writer.write('{');
		writer.write(System.lineSeparator());
		if (!elements.isEmpty()) {
			for (String element : elements.headMap(elements.lastKey()).keySet()) {
				indent(level + 1, writer);
				TreeJSONWriter.quote(element, writer);
				writer.write(": ");
				asNestedObject(elements.get(element), writer, level); 
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			TreeJSONWriter.quote(elements.lastKey(), writer);
			writer.write(": ");
			asNestedObject(elements.get(elements.lastKey()), writer, level + 1);
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write("}");
	}
	/**
	 * Make the result data structure written into JSON file
	 * @param result The result data structure
	 * @param path The path to the file write to output
	 * @throws IOException IOException if the writer encounters any issues
	 */
	public static void searchNestedObject(TreeMap<String,ArrayList<Result>> result,Path path) throws IOException
	{
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			searchNestedObject(result, writer, 0);
		}
	}
	/**
	 * Make the result data structure written into JSON file
	 * @param result
	 * @param writer The writer to use
	 * @param level The initial indentation level
	 * @throws IOException IOException IOException if the writer encounters any issues
	 */
	public static void searchNestedObject(TreeMap<String,ArrayList<Result>> result,Writer writer, int level) throws IOException
	{
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		writer.write('[');
		writer.write(System.lineSeparator());
			if (!result.isEmpty()) {
				for (String word:result.keySet())
				{
					indent(level + 1, writer);
					writer.write('{');
					writer.write(System.lineSeparator());
					indent(level + 2, writer);
					quote("queries",writer);
					writer.write(": ");
					quote(word,writer);
					writer.write(",");
					writer.write(System.lineSeparator());
					indent(level + 2, writer);
					quote("results",writer);
					writer.write(": ");
					writer.write("[");
					writer.write(System.lineSeparator());
					int count=0; 
					for (Result re:result.get(word))
					{
						if (count<result.get(word).size()-1&&result.get(word).size()>1)
						{
						indent(level + 3, writer);
						writer.write("{");
						writer.write(System.lineSeparator());
						indent(level + 4, writer);
						quote("where",writer);
						writer.write(": ");
						quote(re.getPath(),writer);
						writer.write(",");
						writer.write(System.lineSeparator());
						indent(level + 4, writer);
						quote("count",writer);
						writer.write(": ");
						writer.write(Integer.toString(re.getMatchWord()));
						writer.write(",");
						writer.write(System.lineSeparator());
						indent(level + 4, writer);
						quote("score",writer);
						writer.write(": ");
						writer.write(FORMATTER.format(re.getScore()));
						writer.write(System.lineSeparator());
						indent(level + 3, writer);
						writer.write("}");
						writer.write(",");
						writer.write(System.lineSeparator());
						count++;
						}else
						{
						indent(level + 3, writer);
						writer.write("{");
						writer.write(System.lineSeparator());
						indent(level + 4, writer);
						quote("where",writer);
						writer.write(": ");
						quote(re.getPath(),writer);
						writer.write(",");
						writer.write(System.lineSeparator());
						indent(level + 4, writer);
						quote("count",writer);
						writer.write(": ");
						writer.write(Integer.toString(re.getMatchWord()));
						writer.write(",");
						writer.write(System.lineSeparator());
						indent(level + 4, writer);
						quote("score",writer);
						writer.write(": ");
						writer.write(FORMATTER.format(re.getScore()));
						writer.write(System.lineSeparator());
						indent(level + 3, writer);
						writer.write("}");
						writer.write(System.lineSeparator());
						}
					}
					
					if(word==result.lastKey())
					{
					indent(level + 2, writer);
					writer.write("]");
					writer.write(System.lineSeparator());
					indent(level + 1, writer);
					writer.write("}");		
					writer.write(System.lineSeparator());
					}
					else
					{
						indent(level + 2, writer);
						writer.write("]");
						writer.write(System.lineSeparator());
						indent(level + 1, writer);
						writer.write("}");	
						writer.write(",");	
						writer.write(System.lineSeparator());
					}
				}
			indent(level, writer);
			
			writer.write("]");
		}
	}
	
}