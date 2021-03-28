package exception;

public class CcomException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CcomException(String message){
		super(message);
	}
	
	public CcomException(Throwable cause)
	{
		super(cause);
	}
	
	public CcomException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
