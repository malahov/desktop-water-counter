package controller;

import java.util.List;

import model.MailInfo;
import model.UserInfo;
import model.Value;
import exception.WaterCounterException;

/**
 * @author malah
 *
 * Интерфейс контроллера приложения.
 */
public interface IController {
	
	void addValue(Value value) throws WaterCounterException;
	
	void updateValue(Value value) throws WaterCounterException;
	
	List<Value> getValues() throws WaterCounterException;
	
	Value getPrevValue(Value currentValue) throws WaterCounterException;
	
	Value getLastValue() throws WaterCounterException;

	UserInfo getUserInfo() throws WaterCounterException;
	
	void saveUserInfo(UserInfo userInfo) throws WaterCounterException;
	
	MailInfo getMailInfo() throws WaterCounterException;
	
	void saveMailInfo(MailInfo mailInfo) throws WaterCounterException;
	
	void sendValue(Value value, MailInfo mailInfo, UserInfo userInfo) throws WaterCounterException;

}
