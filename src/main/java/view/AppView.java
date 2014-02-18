package view;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.MailInfo;
import model.UserInfo;
import model.Value;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import util.DateUtil;
import controller.ControllerProvider;
import exception.FormatException;
import exception.WaterCounterException;

/**
 * @author sergey.malahov
 * 
 *         Главное окно программы.
 */
public class AppView {

	private static final String CAPTION = "Водоучет";
	private static final String FILE_MENU = "Файл";
	private static final String ENTER_MENU_ITEM = "Ввод показаний";
	private static final String EDIT_MENU_ITEM = "Изменить";
	private static final String SEND_MENU_ITEM = "Отправить";
	private static final String PREF_MENU_ITEM = "Настройки";
	private static final String EXIT_MENU_ITEM = "Выход";
	private static final String DATE_LABEL = "Даты снятых показаний";
	private static final String CURRENT_DATE_LABEL = "Показания счетчиков на ";
	private static final String YEAR_FORMAT = "yyyy";
	private static final String DECIMAL_FORMAT = "00000.000";
	private static final String STATISTIC_LABEL = "Статистика";
	private static final String COLD_LABEL = "Холодное водоснабжение";
	private static final String HOT_LABEL = "Горячее водоснабжение";
	private static final String ROOT = "Год";
	private static final String ROOT_EMPTY = "Нет введенных показаний.";
	private static final String EMPTY = "";
	private static final String SEND_IMAGE_PATH = "icons/tick.png";
	private static final String CALENDAR_IMAGE_PATH = "icons/calendar.png";
	private static final String EMAIL_IMAGE_PATH = "icons/email.png";

	private static Image sendImage;

	private static Image calendarImage;

	private static Image emailImage;

	private Display display;

	private Shell shell;

	private Tree dateTree;

	private Label currentHot, currentCold, currentDate;

	public AppView() {
		createView();
		createMenu();
		sendImage = new Image(Display.getDefault(), SEND_IMAGE_PATH);
		calendarImage = new Image(Display.getCurrent(), CALENDAR_IMAGE_PATH);
		emailImage = new Image(Display.getCurrent(), EMAIL_IMAGE_PATH);
		createBody();
	}

	public void show() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private void createView() {
		display = new Display();
		shell = new Shell(display);
		shell.setText(CAPTION);
		shell.setSize(600, 450);
	}

	private void createMenu() {
		Menu menu = new Menu(shell, SWT.BAR);

		MenuItem mi = new MenuItem(menu, SWT.CASCADE);
		mi.setText(FILE_MENU);

		Menu fm = new Menu(shell, SWT.DROP_DOWN);
		mi.setMenu(fm);

		MenuItem indcationEnter = new MenuItem(fm, SWT.PUSH);
		indcationEnter.setText(ENTER_MENU_ITEM);
		indcationEnter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IndicationDialog dialog = new IndicationDialog(shell);
				Value value = new Value();
				dialog.setValue(value);
				try {
					Value prev = ControllerProvider.getController()
							.getLastValue();
					dialog.setPrevValue(prev);
				} catch (WaterCounterException ex) {
					MessageHelper.showErrorMessage(shell, ex.getMessage());
					return;
				}
				if (dialog.open() == IndicationDialog.OK_BUTTON) {
					value = dialog.getValue();
					try {
						ControllerProvider.getController().addValue(value);
					} catch (WaterCounterException ex) {
						MessageHelper.showErrorMessage(shell, ex.getMessage());
					}
					refresh();
				}
			}
		});

		new MenuItem(fm, SWT.SEPARATOR);

		MenuItem indcationEdit = new MenuItem(fm, SWT.PUSH);
		indcationEdit.setText(EDIT_MENU_ITEM);
		indcationEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				TreeItem[] selection = dateTree.getSelection();
				if (selection.length == 1
						&& selection[0].getData() instanceof Value) {
					Value value = (Value) selection[0].getData();
					IndicationDialog dialog = new IndicationDialog(shell);
					dialog.setValue(value);
					if (dialog.open() == IndicationDialog.OK_BUTTON) {
						value = dialog.getValue();
						try {
							ControllerProvider.getController().updateValue(
									value);
						} catch (WaterCounterException ex) {
							MessageHelper.showErrorMessage(shell,
									ex.getMessage());
						}
						refresh();
					}
				}
			}
		});

		MenuItem send = new MenuItem(fm, SWT.PUSH);
		send.setText(SEND_MENU_ITEM);
		send.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				TreeItem[] items = dateTree.getSelection();

				if (items.length == 1) {
					TreeItem item = items[0];
					if (item.getData() instanceof Value) {
						Value value = (Value) item.getData();
						try {
							MailInfo mailInfo = ControllerProvider
									.getController().getMailInfo();
							UserInfo userInfo = ControllerProvider
									.getController().getUserInfo();
							ControllerProvider.getController().sendValue(value,
									mailInfo, userInfo);
						} catch (FormatException ex) {
							MessageHelper.showInfoMessage(shell,
									ex.getMessage());
						} catch (WaterCounterException ex) {
							MessageHelper.showErrorMessage(shell,
									ex.getMessage());
						}
						refresh();
					}
				}
			}
		});
		send.setEnabled(false);

		MenuItem preferences = new MenuItem(fm, SWT.PUSH);
		preferences.setText(PREF_MENU_ITEM);
		preferences.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Map<String, String> properties = null;
				UserInfo user = null;
				MailInfo mail = null;
				try {
					user = ControllerProvider.getController().getUserInfo();
					mail = ControllerProvider.getController().getMailInfo();
				} catch (WaterCounterException ex) {
					MessageHelper.showErrorMessage(shell, ex.getMessage());
					return;
				}
				PreferencesDialog dialog = new PreferencesDialog(shell, user,
						mail);
				if (dialog.open(properties) == PreferencesDialog.OK_BUTTON) {
					mail = dialog.getMailInfo();
					user = dialog.getUserInfo();
					try {
						ControllerProvider.getController().saveMailInfo(mail);
						ControllerProvider.getController().saveUserInfo(user);
					} catch (Exception ex) {
						MessageHelper.showErrorMessage(shell, ex.getMessage());
						return;
					}
				}
			}
		});

		new MenuItem(fm, SWT.SEPARATOR);

		MenuItem exit = new MenuItem(fm, SWT.PUSH);
		exit.setText(EXIT_MENU_ITEM);
		exit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		shell.setMenuBar(menu);
	}

	private void createBody() {
		shell.setLayout(new GridLayout(2, false));
		createDateTree();
		createStatisticPane();
	}

	private void createDateTree() {
		Composite composite = new Composite(shell, SWT.NONE);
		GridData gd = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
		composite.setLayoutData(gd);
		composite.setLayout(new GridLayout());

		Label lb = new Label(composite, SWT.NONE);
		lb.setText(DATE_LABEL);

		dateTree = new Tree(composite, SWT.NONE);
		gd = new GridData(SWT.LEFT, SWT.FILL, true, true);
		gd.widthHint = 200;
		dateTree.setLayoutData(gd);
		dateTree.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item instanceof TreeItem) {
					if (e.item.getData() instanceof Value) {
						Value val = (Value) e.item.getData();
						currentDate.setText(CURRENT_DATE_LABEL
								+ DateUtil.dateToString(val
										.getDate()));
						DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
						currentCold.setText(df.format(val.getColdValue()));
						currentHot.setText(df.format(val.getHotValue()));
						currentCold.getParent().layout();

						if (!val.isSended()) {
							activateSend();
							activateEdit();
						} else {
							deactivateSend();
							deactivateEdit();
						}
					} else {
						deactivateSend();
						deactivateEdit();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

			private void deactivateEdit() {
				MenuItem text = shell.getMenuBar().getItem(0);
				MenuItem test = text.getMenu().getItem(2);
				test.setEnabled(false);

			}

			private void activateEdit() {
				MenuItem text = shell.getMenuBar().getItem(0);
				MenuItem test = text.getMenu().getItem(2);
				test.setEnabled(true);
			}

			private void activateSend() {
				MenuItem text = shell.getMenuBar().getItem(0);
				MenuItem test = text.getMenu().getItem(3);
				test.setEnabled(true);
			}

			private void deactivateSend() {
				MenuItem text = shell.getMenuBar().getItem(0);
				MenuItem test = text.getMenu().getItem(3);
				test.setEnabled(false);
			}

		});

		fillDateTree();

		dateTree.select(dateTree.getItem(0));
	}

	private void createStatisticPane() {
		Composite composite = new Composite(shell, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(gd);
		composite.setLayout(new GridLayout());

		Label lb = new Label(composite, SWT.NONE);
		lb.setText(STATISTIC_LABEL);

		Composite data1 = new Composite(composite, SWT.NONE);
		data1.setLayout(new GridLayout(2, true));

		currentDate = new Label(data1, SWT.NONE);
		currentDate.setText(CURRENT_DATE_LABEL);
		gd = new GridData();
		gd.horizontalSpan = 2;
		currentDate.setLayoutData(gd);

		lb = new Label(data1, SWT.NONE);
		lb.setText(COLD_LABEL);
		gd = new GridData();
		gd.horizontalIndent = 30;
		lb.setLayoutData(gd);

		currentCold = new Label(data1, SWT.NONE);
		currentCold.setText(EMPTY);
		gd = new GridData();
		gd.horizontalIndent = 30;
		currentCold.setLayoutData(gd);

		lb = new Label(data1, SWT.NONE);
		lb.setText(HOT_LABEL);
		gd = new GridData();
		gd.horizontalIndent = 30;
		lb.setLayoutData(gd);

		currentHot = new Label(data1, SWT.NONE);
		currentHot.setText(EMPTY);
		gd = new GridData();
		gd.horizontalIndent = 30;
		currentHot.setLayoutData(gd);

		// Composite data2 = new Composite(composite, SWT.NONE);
		// data2.setLayout(new GridLayout(2, false));
		//
		// lb = new Label(data2, SWT.NONE);
		// lb.setText("Расход за период с 12.10.2010 по 22.10.2010");
		// gd = new GridData();
		// gd.horizontalSpan = 2;
		// lb.setLayoutData(gd);
		//
		// lb = new Label(data2, SWT.NONE);
		// lb.setText("Холодная вода");
		// gd = new GridData();
		// gd.horizontalIndent = 30;
		// lb.setLayoutData(gd);
		//
		// lb = new Label(data2, SWT.NONE);
		// lb.setText("00007,292");
		// gd = new GridData();
		// gd.horizontalIndent = 30;
		// lb.setLayoutData(gd);
		//
		// lb = new Label(data2, SWT.NONE);
		// lb.setText("Горячая вода");
		// gd = new GridData();
		// gd.horizontalIndent = 30;
		// lb.setLayoutData(gd);
		//
		// lb = new Label(data2, SWT.NONE);
		// lb.setText("00001,292");
		// gd = new GridData();
		// gd.horizontalIndent = 30;
		// lb.setLayoutData(gd);
	}

	private void fillDateTree() {
		List<Value> values = null;

		try {
			values = ControllerProvider.getController().getValues();
		} catch (WaterCounterException ex) {
			MessageHelper.showErrorMessage(shell, ex.getMessage());
		}

		TreeItem root = new TreeItem(dateTree, SWT.NONE);
		if (values != null && !values.isEmpty()) {

			if (values.get(0).isSended()) {
				enableEnterMenuItem();
			} else {
				disableEnterMenuItem();
			}

			root.setText(ROOT);
			root.setImage(calendarImage);
			
			Map<String, TreeItem> map = new HashMap<String, TreeItem>();
			for (Value value : values) {
				String key = DateUtil.dateToString(value.getDate(), YEAR_FORMAT);
				TreeItem year = null;
				if (map.containsKey(key)) {
					year = map.get(key);
				} else {
					year = new TreeItem(root, SWT.NONE);
					year.setText(key);
					year.setImage(calendarImage);
					map.put(key, year);
				}
				String dateString = DateUtil.dateToString(value.getDate());
				TreeItem date = new TreeItem(year, SWT.NONE);
				date.setText(dateString);
				date.setData(value);
				if (value.isSended()) {
					date.setImage(sendImage);
				} else {
					date.setImage(emailImage);
				}
			}
		} else {
			root.setText(ROOT_EMPTY);
			enableEnterMenuItem();
		}

		expandTree(dateTree);
	}

	private void expandTree(Tree dateTree) {
		for (TreeItem item : dateTree.getItems()) {
			expandTreeItem(item);
		}
	}

	private void expandTreeItem(TreeItem item) {
		if (item.getItemCount() > 0) {
			item.setExpanded(true);
			for (TreeItem child : item.getItems()) {
				expandTreeItem(child);
			}
		}
	}

	private void refresh() {
		dateTree.removeAll();
		fillDateTree();
		currentDate.setText(CURRENT_DATE_LABEL);
		currentCold.setText(EMPTY);
		currentHot.setText(EMPTY);
		currentCold.getParent().layout();
	}

	private void enableEnterMenuItem() {
		MenuItem file = shell.getMenuBar().getItem(0);
		MenuItem enter = file.getMenu().getItem(0);
		enter.setEnabled(true);
	}

	private void disableEnterMenuItem() {
		MenuItem file = shell.getMenuBar().getItem(0);
		MenuItem enter = file.getMenu().getItem(0);
		enter.setEnabled(false);
	}

}
