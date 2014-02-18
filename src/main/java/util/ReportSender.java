package util;

import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import model.MailInfo;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import exception.FormatException;
import exception.WaterCounterException;

/**
 * Класс осуществляет отпправку письма по электронной почте.
 * 
 * @author sergey.malahov
 */
public class ReportSender {


	private static final Logger log = Logger.getLogger(ReportSender.class
			.getName());

	private static final String ENCODING = "UTF-8";

	/**
	 * Приоритет письма; 1-максимальный, 5-минимальный.
	 */
	private static final int MAX_PRIORITY = 1;

	/**
	 * Запрашивать отчет о прочтении.
	 */
	private static final String SEND_READING_REPORT_TO = "Disposition-Notification-To";

	/**
	 * Тема письма. 
	 */
	private static final String MESSAGE_SUBJECT = "test water counter";
	
	private static final String EMPTY_ACCOUNT_MESSAGE = "Не заполнен почтовый "
			+ "ящик. Укажите его в настройках программы.";

	private static final String EMPTY_PORT_MESSAGE = "Не заполнен порт SMTP "
			+ "сервера. Укажите его в настройках программы.";

	private static final String EMPTY_SMTP_MESSAGE = "Не заполнен адрес SMTP "
			+ "сервера. Укажите его в настройках программы.";

	private static final String EMPTY_RECIPIENT_MESSAGE = "Не заполнена "
			+ "электронная почта получателя. Укажите ее в настройках программы.";

	private MailInfo mailInfo;

	public ReportSender(MailInfo mailInfo) {
		this.mailInfo = mailInfo;
	}

	/**
	 * Создает и отправляет электронное письми.
	 * 
	 * @param message
	 *            Текст письма в формате HTML.
	 * @throws WaterCounterException
	 *             Если что то пошло не так.
	 */
	public void send(String message) throws WaterCounterException {
		
		checkMailInfo(mailInfo);

		JavaMailSender sender = createSender();

		MimeMessage mimeMessage = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, ENCODING);

		try {
			helper.setSubject(MESSAGE_SUBJECT);
			helper.setText(message, true);
			helper.setTo(mailInfo.getRecipient());
			helper.setFrom(mailInfo.getAccount());
			helper.setPriority(MAX_PRIORITY);
			helper.setReplyTo(mailInfo.getAccount());
			helper.getMimeMessage().setHeader(SEND_READING_REPORT_TO,
					mailInfo.getAccount());
			sender.send(helper.getMimeMessage());
		} catch (MailException e) {
			log.severe(e.getMessage());
			throw new WaterCounterException(e);
		} catch (MessagingException e) {
			log.severe(e.getMessage());
			throw new WaterCounterException(e);
		}
	}

	private JavaMailSender createSender() {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(mailInfo.getSmtpServer());
		sender.setPort(Integer.parseInt(mailInfo.getPort()));
		sender.setPassword(mailInfo.getPassword());
		sender.setUsername(mailInfo.getAccount());
		sender.setDefaultEncoding(ENCODING);
		return sender;
	}

	/**
	 * Проверит заполнены ли необходимые поля объекта.
	 * 
	 * @param info
	 *            Объект класса {@link MailInfo}
	 * @throws FormatException
	 *             Если не заполнено необходимое поле объекта.
	 */
	private void checkMailInfo(MailInfo info) throws FormatException {
		if (info.getAccount() == null || info.getAccount().isEmpty()) {
			throw new FormatException(EMPTY_ACCOUNT_MESSAGE);
		}
		if (info.getPort() == null || info.getPort().isEmpty()) {
			throw new FormatException(EMPTY_PORT_MESSAGE);
		}
		if (info.getSmtpServer() == null || info.getSmtpServer().isEmpty()) {
			throw new FormatException(EMPTY_SMTP_MESSAGE);
		}
		if (info.getRecipient() == null || info.getRecipient().isEmpty()) {
			throw new FormatException(EMPTY_RECIPIENT_MESSAGE);
		}
	}
}
