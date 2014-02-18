/**
 * 
 */
package exception;

/**
 * @author sergey.malahov
 *
 * Исключение приложения показывает, 
 * что проблемы произошли при проверке данных, введенных пользователем.
 */
public class FormatException extends WaterCounterException {

	private static final long serialVersionUID = 4956027822267937783L;
	
	public FormatException() {
		super();
	}
	
	public FormatException(String message) {
		super(message);
	}
	
	public FormatException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FormatException(Throwable cause) {
		super(cause);
	}
}
