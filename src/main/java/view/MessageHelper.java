package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author sergey.malahov
 *
 * Класс помогает показывать пользователю сообщения об ошибках и предупреждения.
 */
public class MessageHelper {
	
	private static final String CAPTION_ERROR_MESSAGE = "Ошибка!";
	
	private static final String CAPTION_WARNING_MESSAGE = "Предупреждение!";
	
	public static void showErrorMessage(Shell shell, String message) {
		MessageBox m = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		m.setText(CAPTION_ERROR_MESSAGE);
		m.setMessage(message);
		m.open();
	}
	
	public static void showWarningMessage(Shell shell, String message) {
		MessageBox m = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		m.setText(CAPTION_WARNING_MESSAGE);
		m.setMessage(message);
		m.open();
	}
	
	public static void showInfoMessage(Shell shell, String message) {
		
	}
}
