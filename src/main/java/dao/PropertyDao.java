package dao;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import exception.DBException;
import exception.WaterCounterException;

/**
 * @author sergey.malahov
 *
 * Dao для доступа к настройкам приложения, 
 * таким как параметры почты и информация о пользователе.
 */
public class PropertyDao {
	
	private static final Logger log = Logger.getLogger(PropertyDao.class.getName());
	
	private SimpleJdbcInsert insert;
	
	private JdbcTemplate sql;
	
	public PropertyDao(DataSource dataSource) throws WaterCounterException{
		sql = new JdbcTemplate(dataSource);		
		insert = new SimpleJdbcInsert(dataSource)
			.withSchemaName("APP").withTableName("PROPERTIES")
			.usingGeneratedKeyColumns("ID");
		
		createTables();	
	}
	
	private static final String get_props_sql = "select name, value from properties";
	
	public Map<String, String> getProperties() throws DBException {		
		PropertyMapper pm = new PropertyMapper();		
		Map<String, String> result = new HashMap<String, String>();
		
		try {
			List<Property> props = sql.query(get_props_sql, pm);
			for(Property prop : props) {
				result.put(prop.getName(), prop.getValue());
			}
			return result;
		} catch (Exception e) {
			log.severe(e.getMessage());
			DBException ex = new DBException(e);
			ex.setCode("select_value_fail");
			throw ex;
		}
	}

	public void saveProperties(Map<String, String> properties) throws DBException {
		for(Entry<String, String> entry : properties.entrySet()) {
			Property prop = getProperty(entry.getKey());
			if(prop == null) {
				addProperty(entry.getKey(), entry.getValue());
			} else {
				prop.setValue(entry.getValue());
				updateProperty(prop.getName(), entry.getValue());
			}			
		}
	}

	private static final String create_properties_sql = "create table properties " +
			"(id int not null GENERATED ALWAYS AS IDENTITY " +
			"(START WITH 1, INCREMENT BY 1) CONSTRAINT properties_pk primary key, " +
			"name varchar(255) not null CONSTRAINT un_name unique, value varchar(1000) not null)";
	
	private void createTables() throws WaterCounterException {
		try {
			DatabaseMetaData md = sql.getDataSource().getConnection().getMetaData();
			ResultSet rs = md.getTables(null, "APP", "PROPERTIES", null);
			if(!rs.next()) {
				sql.execute(create_properties_sql);
			} 
		} catch (SQLException e) {
			log.severe(e.getMessage());
			WaterCounterException ex = new WaterCounterException(e);
			ex.setCode("tables_create_fail");
			throw ex;
		}		
	}
	
	private void addProperty(String name, String value) throws DBException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("NAME", name);
		params.put("VALUE", value);
		try {
			insert.execute(params);			
		} catch (Exception e) {
			log.severe(e.getMessage());
			DBException ex = new DBException(e);
			ex.setCode("insert_value_fail");
			throw ex;
		}
	}
	 
	private static final String get_prop_sql = 
			"select name, value from properties where name like ?";
	
	private Property getProperty(String name) throws DBException {
		PropertyMapper pm = new PropertyMapper();
		Property result = null;		
		try {
			List<Property> props = sql.query(get_prop_sql, pm, name);
			if(props != null && props.size() > 0) {
				result = new Property();
				result.setName(props.get(0).getName());
				result.setValue(props.get(0).getValue());
			} 
			return result;
		} catch (Exception e) {
			log.severe(e.getMessage());
			DBException ex = new DBException(e);
			ex.setCode("get_value_fail");
			throw ex;
		}
	}
	
	private static final String update_prop_sql = 
			"update properties set value = ? where name like ?";
	
	private void updateProperty(String name, String newValue) throws DBException {
		try {
			sql.update(update_prop_sql, newValue, name);			
		} catch (Exception e) {
			log.severe(e.getMessage());
			DBException ex = new DBException(e);
			ex.setCode("update_value_fail");
			throw ex;
		}
	}
	
	/**
	 * @author sergey.malahov
	 *
	 * Класс представляет отдельное свойство
	 */
	private class Property {
		
		private String name;
		
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	
	private class PropertyMapper implements RowMapper<PropertyDao.Property> {

		@Override
		public Property mapRow(ResultSet rs, int rowNum) throws SQLException {
			Property prop = new Property();
			prop.setName(rs.getString(1));
			prop.setValue(rs.getString(2));
			return prop;
		}		
	}
}
