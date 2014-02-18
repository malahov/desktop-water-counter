package model;

import java.util.Date;

/**
 * @author sergey.malahov
 * 
 * Класс содержит информацию о показаниях 
 * счетчиков горячей и холодной воды на конкретную дату.
 */
public class Value {
	
	private Integer id;

	private Double coldValue;

	private Double hotValue;
	
	private boolean sended; 

	private Date date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getColdValue() {
		return coldValue;
	}

	public void setColdValue(Double coldValue) {
		this.coldValue = coldValue;
	}

	public Double getHotValue() {
		return hotValue;
	}

	public void setHotValue(Double hotValue) {
		this.hotValue = hotValue;
	}

	public boolean isSended() {
		return sended;
	}

	public void setSended(boolean sended) {
		this.sended = sended;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
