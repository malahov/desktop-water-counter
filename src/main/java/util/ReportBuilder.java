package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import java.util.logging.Logger;

import model.UserInfo;
import model.Value;
import exception.FormatException;
import exception.WaterCounterException;

/**
 * Класс предназначен для постороения отчета о водопотреблении за месяц. Формат
 * отчета - HTML.
 * 
 * @author sergey.malahov
 */
public class ReportBuilder {

	private static final Logger log = Logger.getLogger(ReportBuilder.class
			.getName());

	private static final String ADDRESS = ":address";

	private static final String USER = ":user";

	private static final String PREV_DATE = ":prevDate";

	private static final String CURRENT_DATE = ":currentDate";

	private static final String COLD_PREV = ":coldPrev";

	private static final String COLD_CURRENT = ":coldCurrent";

	private static final String COLD_CONSUMPTION = ":coldConsumption";

	private static final String HOT_PREV = ":hotPrev";

	private static final String HOT_CURRENT = ":hotCurrent";

	private static final String HOT_CONSUMPTION = ":hotConsumption";

	private static final String TEMPLATE_PATH = "templates/template";

	private static final String DECIMAL_FORMAT = "00000.000";

	private static final String EMPTY_FIO_MESSAGE = "Не заполнено ФИО "
			+ "ответственного квартиросъемщика. "
			+ "Укажите его в настройки программы.";

	private static final String EMPTY_ADDRESS_MESSAGE = "Не заполнен "
			+ "адрес квартиры. Укажите его в настройки программы.";

	private UserInfo userInfo;

	private Value currentValue, prevValue;

	public ReportBuilder(UserInfo userInfo, Value currentValue, Value prevValue) {
		this.userInfo = userInfo;
		this.currentValue = currentValue;
		this.prevValue = prevValue;
	}

	public String buildReport() throws WaterCounterException {
		
		checkUserInfo(userInfo);

		String template = readTemplate();

		String report = prepareReport(template);

		return report;
	}

	private String readTemplate() throws WaterCounterException {
		BufferedReader br = null;
		try {
			InputStream is = new FileInputStream(new File(TEMPLATE_PATH));
			br = new BufferedReader(new InputStreamReader(is));
			StringBuilder builder = new StringBuilder();
			String nextLine = null;
			while ((nextLine = br.readLine()) != null) {
				builder.append(nextLine);
			}

			return builder.toString();

		} catch (FileNotFoundException e) {
			log.severe(e.getMessage());
			throw new WaterCounterException(e);
		} catch (IOException e) {
			log.severe(e.getMessage());
			throw new WaterCounterException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.severe(e.getMessage());
					throw new WaterCounterException(e);
				}
			}
		}
	}

	private String prepareReport(String template) throws WaterCounterException {
		try {
			String result = template.replace(ADDRESS, userInfo.getAddress());
			result = result.replace(USER, userInfo.getFio());

			result = result.replace(PREV_DATE,
					DateUtil.dateToString(prevValue.getDate()));
			result = result.replace(CURRENT_DATE,
					DateUtil.dateToString(currentValue.getDate()));

			DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);

			result = result.replace(COLD_PREV,
					df.format(prevValue.getColdValue()));
			result = result.replace(COLD_CURRENT,
					df.format(currentValue.getColdValue()));
			Double temp = currentValue.getColdValue()
					- prevValue.getColdValue();
			result = result.replace(COLD_CONSUMPTION, df.format(temp));

			result = result.replace(HOT_PREV,
					df.format(prevValue.getHotValue()));
			result = result.replace(HOT_CURRENT,
					df.format(currentValue.getHotValue()));
			temp = currentValue.getHotValue() - prevValue.getHotValue();
			result = result.replace(HOT_CONSUMPTION, df.format(temp));

			return result;
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new WaterCounterException(e);
		}
	}

	/**
	 * Проверит заполнены ли необходимые поля объекта.
	 * 
	 * @param info
	 *            Объект класса {@link UserInfo}
	 * @throws FormatException
	 *             Если не заполнено необходимое поле объекта.
	 */
	private void checkUserInfo(UserInfo info) throws FormatException {
		if (info.getFio() == null || info.getFio().isEmpty()) {
			throw new FormatException(EMPTY_FIO_MESSAGE);
		}
		if (info.getAddress() == null || info.getAddress().isEmpty()) {
			throw new FormatException(EMPTY_ADDRESS_MESSAGE);
		}
	}

}
