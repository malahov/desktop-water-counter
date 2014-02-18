package model;

/**
 * @author sergey.malahov
 *
 * Класс содержит настройки почты.
 */
public class MailInfo {
	
	public static final String SMTP_PROPERTY = "SMTP_PROPERTY";

	public static final String PORT_PROPERTY = "PORT_PROPERTY";

	public static final String ACCOUNT_PROPERTY = "ACCOUNT_PROPERTY";

	public static final String PASSWORD_PROPERTY = "PASSWORD_PROPERTY";

	public static final String RECIPIENT_PROPERTY = "RECIPIENT_PROPERTY";

	private String smtpServer;
	
	private String port;
	
	private String account;
	
	private String password;
	
	private String recipient;

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
}
