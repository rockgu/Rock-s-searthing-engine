public class Result implements Comparable<Result> {
	private final int total;
	private final String path;
	private int matches;

	/**
	 * The constructor of the result object and initialize.
	 * 
	 * @param totalWord The total word of each file
	 * @param matchWord total match word.
	 * @param path      The path of each file.
	 */
	public Result(int totalWord, int matchWord, String path) {
		this.total = totalWord;
		this.matches = matchWord;
		this.path = path;
	}

	/**
	 * It will compare the Result object.
	 */
	@Override
	public int compareTo(Result other) {

		int result = Double.compare(other.getScore(), this.getScore());
		if (result == 0) {
			result = Integer.compare(other.getMatchWord(), this.getMatchWord());
			if (result == 0) {
				return String.CASE_INSENSITIVE_ORDER.compare(this.path, other.path);
			}
			return result;
		}
		return result;
	}

	/**
	 * It will return the total word
	 * 
	 * @return total Word in result object.
	 */
	public int totalWord() {
		return total;
	}

	/**
	 * It will return score
	 * 
	 * @return score of the result
	 */
	public double getScore() {
		return (double) matches / total;
	}

	/**
	 * It will return the path of the result object.
	 * 
	 * @return Path of the result object
	 */
	public String getPath() {
		return path;
	}

	/**
	 * It will return all the match world
	 * 
	 * @return all the matches word
	 */
	public int getMatchWord() {
		return matches;
	}

	/**
	 * It will update total matchWord
	 * 
	 * @param matches Total match word
	 */
	public void updateMatches(int matches) {
		this.matches += matches;
	}
}