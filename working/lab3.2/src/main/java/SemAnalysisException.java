public class SemAnalysisException extends RuntimeException {

	public SemAnalysisException() {
		super();
	}

	public SemAnalysisException(String message) {
		super(message);
	}

	public SemAnalysisException(String message, Throwable cause) {
		super(message, cause);
	}

	public SemAnalysisException(Throwable cause) {
		super(cause);
	}

	public Throwable throwThis() {
		throw this;
	}
}
