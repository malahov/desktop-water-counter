package model;

/**
 * @author sergey.malahov
 *
 * Класс содержит информацию об 
 * ответственном квартиросъемщике и адресе квартиры.
 */
public class UserInfo {
	
	public static final String FLAT_HOLDER_PROPERTY = "FLAT_HOLDER_PROPERTY";

	public static final String FLAT_ADDRESS_PROPERTY = "FLAT_ADDRESS_PROPERTY";

	private String fio;

	private String address;

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
