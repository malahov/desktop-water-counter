package view;

import java.util.Calendar;
import java.util.Date;

import model.Value;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import util.DateUtil;
import exception.FormatException;

/**
 * @author sergey.malahov
 * 
 *         Диалог ввода показаний счетчиков.
 */
public class IndicationDialog extends Dialog {

	public static final int OK_BUTTON = 0;

	public static final int CANCEL_BUTTON = 1;

	private static final int MIN_WIDTH = 200;

	private static final int H_INDENT = 20;

	private static final int BUTTON_WIDTH = 75;

	private static final int BUTTON_SPAN = 2;

	private static final String CAPTION = "Ввод показаний счетчиков";

	private static final String COLD_LABEL = "Холодная вода";

	private static final String HOT_LABEL = "Горячая вода";

	private static final String SEND_LABEL = "Отправлено ранее";

	private static final String EMPTY_COLD_VALUE = "Не введены данные по холодной воде.";

	private static final String EMPTY_HOT_VALUE = "Не введены данные по горячей воде.";

	private static final String OK_BUTTON_TEXT = "ОК";

	private static final String CANCEL_BUTTON_TEXT = "Отмена";

	private static final int LAYOUT_COLUMN_COUNT = 2;

	private static final String ERROR_FORMAT_COLD_VALUE = "Не верный формат данных по холодной воде";

	private static final String ERROR_FORMAT_HOT_VALUE = "Не верный формат данных по горячей воде";

	private int result = CANCEL_BUTTON;

	private DateTime dateTime;

	private Text cold, hot;

	private Button send;

	private Value value, prevValue;

	Shell shell;

	public IndicationDialog(Shell parent) {
		super(parent);
	}

	public int open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(CAPTION);

		init(shell);

		shell.pack();

		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return result;

	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Value getPrevValue() {
		return prevValue;
	}

	public void setPrevValue(Value prevValue) {
		this.prevValue = prevValue;
	}

	private void init(Composite composite) {
		GridLayout layout = new GridLayout(LAYOUT_COLUMN_COUNT, false);
		composite.setLayout(layout);

		Label label = new Label(composite, SWT.NONE);
		label.setText("Дата");

		dateTime = new DateTime(composite, SWT.BORDER | SWT.DATE
				| SWT.DROP_DOWN);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalIndent = H_INDENT;
		dateTime.setLayoutData(gd);
		Date date = value.getDate();
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			dateTime.setDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
		}

		label = new Label(composite, SWT.NONE);
		label.setText(COLD_LABEL);

		cold = new Text(composite, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalIndent = H_INDENT;
		cold.setLayoutData(gd);
		cold.setFocus();
		Double coldValue = value.getColdValue();
		if (coldValue != null) {
			cold.setText(coldValue.toString());
		}

		label = new Label(composite, SWT.NONE);
		label.setText(HOT_LABEL);

		hot = new Text(composite, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalIndent = H_INDENT;
		hot.setLayoutData(gd);
		Double hotValue = value.getHotValue();
		if (hotValue != null) {
			hot.setText(hotValue.toString());
		}

		send = new Button(composite, SWT.CHECK);
		send.setText(SEND_LABEL);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = LAYOUT_COLUMN_COUNT;
		send.setLayoutData(gd);
		send.setSelection(value.isSended());

		createButtoms(composite);

	}

	private void createButtoms(Composite composite) {
		Composite area = new Composite(composite, SWT.FILL);
		GridData gd = new GridData();
		gd.horizontalSpan = BUTTON_SPAN;
		gd.horizontalAlignment = SWT.RIGHT;
		area.setLayoutData(gd);

		RowLayout layout = new RowLayout();
		layout.pack = false;
		area.setLayout(layout);

		Button ok = new Button(area, SWT.PUSH);
		RowData rd = new RowData();
		rd.width = BUTTON_WIDTH;
		ok.setLayoutData(rd);
		ok.setText(OK_BUTTON_TEXT);

		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				value.setDate(getDate());
				value.setSended(getSend());
				try {
					value.setColdValue(getCold());
					value.setHotValue(getHot());
				} catch (FormatException ex) {
					MessageHelper.showWarningMessage(
							IndicationDialog.this.shell, ex.getMessage());
					return;
				}

				if (isValueCorrect()) {
					result = OK_BUTTON;
					shell.close();
				}
			}
		});

		shell.setDefaultButton(ok);

		Button cancel = new Button(area, SWT.PUSH);
		rd = new RowData();
		rd.width = BUTTON_WIDTH;
		cancel.setLayoutData(rd);
		cancel.setText(CANCEL_BUTTON_TEXT);

		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = CANCEL_BUTTON;
				shell.close();
			}
		});
	}

	private boolean isValueCorrect() {
		boolean correct = true;
		if (prevValue != null) {
			if (!value.getDate().after(prevValue.getDate())) {
				MessageHelper.showWarningMessage(
						IndicationDialog.this.shell,
						"Дата должна быть больше "
								+ DateUtil.dateToString(prevValue.getDate()));
				dateTime.setFocus();
				correct = false;
			} else if (value.getColdValue() < prevValue.getColdValue()) {
				MessageHelper.showWarningMessage(IndicationDialog.this.shell,
						"Показания счетчика холодной воды не может быть меньше "
								+ Double.toString(prevValue.getColdValue()));
				cold.setFocus();
				correct = false;
			} else if (value.getHotValue() < prevValue.getHotValue()) {
				MessageHelper.showWarningMessage(IndicationDialog.this.shell,
						"Показания счетчика горячей воды не может быть меньше "
								+ Double.toString(prevValue.getHotValue()));
				hot.setFocus();
				correct = false;
			}
		}
		return correct;
	}

	private Double getCold() throws FormatException {
		if (cold.getText().isEmpty()) {
			cold.setFocus();
			FormatException ex = new FormatException(EMPTY_COLD_VALUE);
			ex.setCode("empty_cold_value");
			throw ex;
		}
		try {
			String preStr = cold.getText().trim().replace(",", ".");
			return Double.valueOf(preStr);
		} catch (NumberFormatException e) {
			cold.setFocus();
			FormatException ex = new FormatException(ERROR_FORMAT_COLD_VALUE);
			ex.setCode("error_format");
			throw ex;
		}
	}

	private Double getHot() throws FormatException {
		if (hot.getText().isEmpty()) {
			hot.setFocus();
			FormatException ex = new FormatException(EMPTY_HOT_VALUE);
			ex.setCode("empty_hot_value");
			throw ex;
		}
		try {
			String preStr = hot.getText().trim().replace(",", ".");
			return Double.valueOf(preStr);
		} catch (NumberFormatException e) {
			hot.setFocus();
			FormatException ex = new FormatException(ERROR_FORMAT_HOT_VALUE);
			ex.setCode("error_format");
			throw ex;
		}
	}

	private Date getDate() {
		return DateUtil.getStartDay(dateTime.getYear(), dateTime.getMonth(),
				dateTime.getDay());
	}

	private boolean getSend() {
		return send.getSelection();
	}

}
