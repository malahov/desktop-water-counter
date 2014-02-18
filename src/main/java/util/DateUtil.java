package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Утилитарный класс для работы с дадами.
 * 
 */
public final class DateUtil {

	private static final String DEFAULT_PATTERN = "dd.MM.yyyy";

	/**
	 * Вернет начало дня, то есть дату со временем 0 часов, 0 минут, 0 секунд, 0
	 * милисекунд.
	 * 
	 * @param date
	 *            Исходная дата.
	 * @return Дата с нулевым временем
	 */
	public static Date getStartDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Вернет начало дня, то есть дату со временем 0 часов, 0 минут, 0 секунд, 0
	 * милисекунд.
	 * 
	 * @param year
	 *            Год (нумерация начинается с 1)
	 * @param month
	 *            Месяц (нумерация начинается с 0)
	 * @param day
	 *            День месяца (нумерация начинается с 1)
	 * @return Дата с нулевым временем
	 */
	public static Date getStartDay(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();		
		
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Вернет конец дня, то есть дату со временем 23 часа, 59 минут, 59 секунд,
	 * 999 милисекунд.
	 * 
	 * @param date
	 *            Исходная дата.
	 * @return Дата со временем 23:59:59:999
	 */
	public static Date getEndDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		return calendar.getTime();
	}

	/**
	 * Вернет строковое представление даты в формате дд.мм.гггг
	 * 
	 * @param date
	 *            Дата
	 * @return Строковое представление даты
	 */
	public static String dateToString(Date date) {
		return dateToString(date, DEFAULT_PATTERN);
	}

	/**
	 * Вернет строковое представление даты указанном формате
	 * 
	 * @param date
	 *            Дата
	 * @param pattern
	 *            Формат представление даты
	 * @return Строковое представление даты
	 */
	public static String dateToString(Date date, String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	private DateUtil() {

	}

}
