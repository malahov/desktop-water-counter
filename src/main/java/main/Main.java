package main;

import java.util.Locale;
import java.util.logging.Logger;

import view.AppView;

/**
 * @author sergey.malahov
 *
 * Точка входа в программу.
 */
public class Main {
	
	private static final Logger log = Logger.getLogger(Main.class.getName());
	
	private static final String STARTING_MESSAGE = "Программа \"Водоучет\" запускается...";
	
	
	/**
	 * @param args
	 *            Параметры командной строки.
	 */
	public static void main(String[] args) {
		try {
			Locale.setDefault(new Locale("ru", "RU"));
			log.info(STARTING_MESSAGE);
			AppView view = new AppView();
			view.show();			
		} catch (Exception e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}
	}

}
