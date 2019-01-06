import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public interface QueryBuilderInterface {
	/**
	 * It is a method that will choose the exactSearch or partialSearch to do the mutithread search  
	 * @param path The path of file 
	 * @param exactCheck Cheek it is partial search or exactedsearch
	 * @throws FileNotFoundException couldn't find right file path
	 * @throws IOException reader cann't find the right path
	 */
	public void addSearchResult(Path path, boolean exactCheck) throws FileNotFoundException, IOException;
	
	/**
	 * write the result to the JSON file. 
	 * @param output path of file will write to
	 * @throws IOException reader cann't find the right path
	 */
	public void toResultJSON(Path output) throws IOException;

}
