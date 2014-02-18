package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import model.MailInfo;
import model.UserInfo;
import model.Value;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import util.ReportBuilder;
import util.ReportSender;
import dao.PropertyDao;
import dao.ValueDao;
import exception.WaterCounterException;

/**
 * @author sergey.malahov
 * 
 * Класс должен инициализировать 
 * единственный экземпляр реализации контроллера приложения и возвращать его.
 *
 */
public class ControllerProvider {
	
	private static IController impl;
	
	public static IController getController() throws WaterCounterException {
		if(impl == null) {
			initControllerImpl();
		}
		return impl;
	}

	private ControllerProvider() {
		
	}
	
	private static void initControllerImpl() throws WaterCounterException {
		String driverName = "org.apache.derby.jdbc.EmbeddedDriver";
		String connectionURL = "jdbc:derby:counterDB;create=true";		
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(driverName);
		ds.setUrl(connectionURL);
		impl = new Controller(ds);
	}
	
	/**
	 * @author sergey.malahov
	 * 
	 * Реализация контроллера приложения.
	 *
	 */
	private static class Controller implements IController {
		
		private static final String NO_PREV_VALUE = "Нет предыдущего показания, " +
				"невозможно определить период и расход воды.";

		private ValueDao valueDao;
		
		private PropertyDao propertyDao;
		
		@Override
		public void addValue(Value value) throws WaterCounterException{
			valueDao.add(value);		
		}
		
		@Override
		public void updateValue(Value value) throws WaterCounterException {
			valueDao.updateValue(value);			
		}
		
		@Override
		public List<Value> getValues() throws WaterCounterException{
			return valueDao.getWaterValues();
		}
		
		@Override
		public Value getPrevValue(Value currentValue)
				throws WaterCounterException {
			List<Value> values = getValues();
			boolean getThis = false;
			for(Value value : values) {
				if(getThis) {
					return value;
				} else {
					if (value.getDate().equals(currentValue.getDate())) {
						getThis = true;
					}
				}
			}
			throw new WaterCounterException(NO_PREV_VALUE);
		}
		
		@Override
		public Value getLastValue() throws WaterCounterException {
			Value result = null;
			List<Value> values = getValues();
			if(values != null && !values.isEmpty()) {
				result = values.get(0);
			}
			return result;
		}
		
		@Override
		public void sendValue(Value value, MailInfo mailInfo, UserInfo userInfo)
				throws WaterCounterException {			
			// получить предъдущее значение счетчика
			Value prevValue = getPrevValue(value);
			
			// сгенерировать текст письма
			
			// создать генератор
			ReportBuilder reporter = new ReportBuilder(userInfo, value, prevValue);
				
			// сгенерировать текст
			String message = reporter.buildReport();
			
			// отправить текст
				
			// создать отправителя
			ReportSender sender = new ReportSender(mailInfo);
				
			// отправить текст
			sender.send(message);
			// если письмо отправлено, то пометить значение как переданное
			value.setSended(true);			
			// и сохранить его в БД
			updateValue(value);			
		}
		
		@Override
		public UserInfo getUserInfo() throws WaterCounterException {
			Map<String, String> props = propertyDao.getProperties();
			UserInfo result = new UserInfo();
			result.setFio(props.get(UserInfo.FLAT_HOLDER_PROPERTY));
			result.setAddress(props.get(UserInfo.FLAT_ADDRESS_PROPERTY));
			return result;
		}
		
		@Override
		public void saveUserInfo(UserInfo userInfo) throws WaterCounterException {
			Map<String, String> props = new HashMap<String, String>();
			props.put(UserInfo.FLAT_HOLDER_PROPERTY, userInfo.getFio());
			props.put(UserInfo.FLAT_ADDRESS_PROPERTY, userInfo.getAddress());
			propertyDao.saveProperties(props);
		}
		
		@Override
		public MailInfo getMailInfo() throws WaterCounterException {
			Map<String, String> props = propertyDao.getProperties();
			MailInfo result = new MailInfo();
			result.setSmtpServer(props.get(MailInfo.SMTP_PROPERTY));
			result.setPort(props.get(MailInfo.PORT_PROPERTY));
			result.setAccount(props.get(MailInfo.ACCOUNT_PROPERTY));
			result.setPassword(props.get(MailInfo.PASSWORD_PROPERTY));
			result.setRecipient(props.get(MailInfo.RECIPIENT_PROPERTY));
			return result;
		}
		
		@Override
		public void saveMailInfo(MailInfo mailInfo) throws WaterCounterException {
			Map<String, String> props = new HashMap<String, String>();
			props.put(MailInfo.SMTP_PROPERTY, mailInfo.getSmtpServer());
			props.put(MailInfo.PORT_PROPERTY, mailInfo.getPort());
			props.put(MailInfo.ACCOUNT_PROPERTY, mailInfo.getAccount());
			props.put(MailInfo.PASSWORD_PROPERTY, mailInfo.getPassword());
			props.put(MailInfo.RECIPIENT_PROPERTY, mailInfo.getRecipient());
			propertyDao.saveProperties(props);
		}

		private Controller(DataSource ds) throws WaterCounterException {
			valueDao = new ValueDao(ds);
			propertyDao = new PropertyDao(ds);
		}
	}
}
