/**
 * 
 */
package exception;

/**
 * @author sergey.malahov
 *
 * Исключение приложения показывает, 
 * что проблемы произошли на уровне БД.
 */
public class DBException extends WaterCounterException {

	private static final long serialVersionUID = 4956027822267937783L;
	
	public DBException() {
		super();
	}
	
	public DBException(String message) {
		super(message);
	}
	
	public DBException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DBException(Throwable cause) {
		super(cause);
	}
}
