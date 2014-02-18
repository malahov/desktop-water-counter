package exception;

/**
 * @author sergey.malahov
 *
 * Класс описывает базовое исключение приложения.
 */
public class WaterCounterException extends Exception {
	
	private static final long serialVersionUID = 7790605274093279130L;
	
	private String code;
	
	public WaterCounterException() {
		super();
	}
	
	public WaterCounterException(String message) {
		super(message);
	}
	
	public WaterCounterException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public WaterCounterException(Throwable cause) {
		super(cause);
	}

	/**
	 * Код ошибки.
	 * @return
	 */
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
