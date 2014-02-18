package view;

import java.util.Map;

import model.MailInfo;
import model.UserInfo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import exception.FormatException;

/**
 * @author sergey.malahov
 *
 * Диалог для ввода настроек почты, и данных пользователя.
 */
public class PreferencesDialog extends Dialog {
	
	public static final int OK_BUTTON = 0;
	
	public static final int CANCEL_BUTTON = 1;
	
	private static final String EMPTY = "";

	private static final String CLIENT_TAB_HEADER = "Данные о потребителе";

	private static final String MAIL_TAB_HEADER = "Данные почты";

	private static final String FLAT_HOLDER_LABEL = "ФИО ответственного квартиросъемщика";

	private static final String FLAT_ADDRESS_LABEL = "Адрес и телефон";

	private static final String SMTP_LABEL = "Адрес SMTP сервера";

	private static final String PORT_LABEL = "Порт";

	private static final String ACCOUNT_LABEL = "Почтовый ящик";

	private static final String PASSWORD_LABEL = "Пароль";
	
	private static final int BUTTON_WIDTH = 75;

	private static final String OK_BUTTON_TEXT = "ОК";

	private static final String CANCEL_BUTTON_TEXT = "Отмена";

	private static final int MIN_WIDTH = 200;

	private static final int H_INDENT = 20;

	private static final int LAYOUT_COLUMN_COUNT = 2;

	private static final String EMPTY_FIO_VALUE = "Не заполнено поле ФИО";

	private static final String EMPTY_ADDRESS_VALUE = "Не заполнено поле Адрес";

	private static final String RECIPIENT_LABEL = "Электронная почта получателя";
	
	private Text fio, address, smtpServer, port, account, password, recipient;
	
	private int result = CANCEL_BUTTON;
	
	private Shell shell;
	
	private UserInfo userInfo;
	
	private MailInfo mailInfo;

	public PreferencesDialog(Shell parent, UserInfo user, MailInfo mail) {
		super(parent);
		this.userInfo = user;
		this.mailInfo = mail;
	}
	
	public int open(Map<String, String> properties) {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Настройки");
		
		init(shell);
		
		fillProperties(properties);
		
		shell.pack();
		
		shell.open();	
		
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()){
				display.sleep();
			}
		}		
		
		return result;
		
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	public MailInfo getMailInfo() {
		return mailInfo;
	}

	private void init(Composite composite) {
		
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		
		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		
		TabItem clientTab = new TabItem(tabFolder, SWT.BORDER);
		Composite clientComposite = new Composite(tabFolder, SWT.NONE);
		clientTab.setControl(clientComposite);
		clientTab.setText(CLIENT_TAB_HEADER);
		initClientComposite(clientComposite);

		TabItem mailTab = new TabItem(tabFolder, SWT.NONE);
		Composite mailComposite = new Composite(tabFolder, SWT.NONE);
		mailTab.setControl(mailComposite);
		mailTab.setText(MAIL_TAB_HEADER);
		initMailComposite(mailComposite);
		
		createButtoms(composite);
	}

	private void fillProperties(Map<String, String> properties) {
		String text = userInfo.getFio();
		fio.setText(text == null ? EMPTY : text);
		
		text = userInfo.getAddress();
		address.setText(text == null ? EMPTY : text);
		
		text = mailInfo.getSmtpServer();
		smtpServer.setText(text == null ? EMPTY : text);
		
		text = mailInfo.getPort();
		port.setText(text == null ? EMPTY : text);
		
		text = mailInfo.getAccount();
		account.setText(text == null ? EMPTY : text);
		
		text = mailInfo.getPassword();
		password.setText(text == null ? EMPTY : text);	
		
		text = mailInfo.getRecipient();
		recipient.setText(text == null ? EMPTY : text);
	}

	private void initMailComposite(Composite mailComposite) {
		
		GridLayout layout = new GridLayout(2, true);
		mailComposite.setLayout(layout);		
		
		Label label  = new Label(mailComposite, SWT.NONE);
		label.setText(SMTP_LABEL);
		
		smtpServer = new Text(mailComposite, SWT.BORDER);	
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalIndent = H_INDENT;
		smtpServer.setLayoutData(gd);
		
		label  = new Label(mailComposite, SWT.NONE);
		label.setText(PORT_LABEL);
		
		port = new Text(mailComposite, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalIndent = H_INDENT;
		port.setLayoutData(gd);
		
		label  = new Label(mailComposite, SWT.NONE);
		label.setText(ACCOUNT_LABEL);
		
		account = new Text(mailComposite, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalIndent = H_INDENT;
		account.setLayoutData(gd);
		
		label  = new Label(mailComposite, SWT.NONE);
		label.setText(PASSWORD_LABEL);
		
		password = new Text(mailComposite, SWT.BORDER);
		password.setEchoChar('*');
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalIndent = H_INDENT;
		password.setLayoutData(gd);
		
		label = new Label(mailComposite, SWT.NONE);
		label.setText(RECIPIENT_LABEL);
		
		recipient = new Text(mailComposite, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalIndent = H_INDENT;
		recipient.setLayoutData(gd);
		
	}

	private void initClientComposite(Composite clientComposite) {
		// Создать layout из 2-х колонок
		GridLayout layout = new GridLayout(LAYOUT_COLUMN_COUNT, true);
		clientComposite.setLayout(layout);
		
		// Создать метку + поле ввода для ФИО
		Label label  = new Label(clientComposite, SWT.NONE);
		label.setText(FLAT_HOLDER_LABEL);
		
		fio = new Text(clientComposite, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalIndent = H_INDENT;
		fio.setLayoutData(gd);
		
		// Создать метку + поле ввода для адреса с телефоном.
		label  = new Label(clientComposite, SWT.NONE);
		label.setText(FLAT_ADDRESS_LABEL);
		
		address = new Text(clientComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumWidth = MIN_WIDTH;
		gd.horizontalSpan = LAYOUT_COLUMN_COUNT;
		address.setLayoutData(gd);
	}
	
	private void createButtoms(Composite composite) {
		Composite area = new Composite(composite, SWT.FILL);
		GridData gd = new GridData();	
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
				try {
					prepareProperties();									
				} catch (FormatException ex) {
					MessageHelper.showWarningMessage(PreferencesDialog.this.shell,
							ex.getMessage());
					return;
				}
				result = OK_BUTTON;
				shell.close();
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

	private void prepareProperties() throws FormatException {
		checkUserInfo();
		userInfo.setFio(fio.getText());
		userInfo.setAddress(address.getText());		
		
		mailInfo.setSmtpServer(smtpServer.getText());
		mailInfo.setPort(port.getText());
		mailInfo.setAccount(account.getText());
		mailInfo.setPassword(password.getText());
		mailInfo.setRecipient(recipient.getText());
	}

	private void checkUserInfo() throws FormatException {
		if(fio.getText().isEmpty()) {
			TabFolder tabFolder = (TabFolder) fio.getParent().getParent();
			tabFolder.setSelection(0);
			fio.setFocus();
			FormatException e = new FormatException(EMPTY_FIO_VALUE);
			e.setCode("empty_fio_value");
			throw e;
		} else if(address.getText().isEmpty()) {
			TabFolder tabFolder = (TabFolder) fio.getParent().getParent();
			tabFolder.setSelection(0);
			address.setFocus();			
			FormatException e = new FormatException(EMPTY_ADDRESS_VALUE);
			e.setCode("empty_address_value");
			throw e;
		} 		
	}
}
