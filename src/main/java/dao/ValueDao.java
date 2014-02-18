package dao;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import model.Value;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.JdbcUtils;

import exception.DBException;
import exception.WaterCounterException;

/**
 * @author sergey.malahov
 * 
 * 
 */
public class ValueDao {

	private static final Logger log = Logger
			.getLogger(ValueDao.class.getName());

	private SimpleJdbcInsert insert;

	private JdbcTemplate sql;

	public ValueDao(DataSource dataSource) throws WaterCounterException {
		sql = new JdbcTemplate(dataSource);
		insert = new SimpleJdbcInsert(dataSource).withSchemaName("APP")
				.withTableName("WATER_VALUE").usingGeneratedKeyColumns("ID");

		createTables();
	}

	private static final String create_value_sql = "create table water_value "
			+ "(id int not null GENERATED ALWAYS AS IDENTITY "
			+ "(START WITH 1, INCREMENT BY 1) CONSTRAINT water_value_pk primary key, "
			+ "hot_value float not null, cold_value float not null, value_date date not null, "
			+ "sended boolean not null default false)";

	private void createTables() throws WaterCounterException {
		try {
			DatabaseMetaData md = sql.getDataSource().getConnection()
					.getMetaData();
			ResultSet rs = md.getTables(null, "APP", "WATER_VALUE", null);
			if (!rs.next()) {
				sql.execute(create_value_sql);
			}
		} catch (SQLException e) {
			log.severe(e.getMessage());
			WaterCounterException ex = new WaterCounterException(e);
			ex.setCode("tables_create_fail");
			throw ex;
		}
	}

	public void add(Value value) throws DBException {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("HOT_VALUE", value.getHotValue());
		params.put("COLD_VALUE", value.getColdValue());
		params.put("VALUE_DATE", value.getDate());
		params.put("SENDED", value.isSended());
		try {
			insert.execute(params);
		} catch (Exception e) {
			log.severe(e.getMessage());
			DBException ex = new DBException(e);
			ex.setCode("insert_value_fail");
			throw ex;
		}
	}

	private static final String get_values_sql = "select hot_value, cold_value, "
			+ "value_date, sended, id from APP.WATER_VALUE order by value_date desc";

	public List<Value> getWaterValues() throws DBException {

		RowMapper<Value> rm = new RowMapper<Value>() {
			@Override
			public Value mapRow(ResultSet rs, int row) throws SQLException {
				Value res = new Value();
				res.setHotValue((Double) JdbcUtils.getResultSetValue(rs, 1,
						Double.class));
				res.setColdValue((Double) JdbcUtils.getResultSetValue(rs, 2,
						Double.class));
				Date date = new Date(((Timestamp) JdbcUtils.getResultSetValue(
						rs, 3, Timestamp.class)).getTime());
				res.setDate(date);
				res.setSended((Boolean) JdbcUtils.getResultSetValue(rs, 4,
						Boolean.class));
				res.setId((Integer) JdbcUtils.getResultSetValue(rs, 5,
						Integer.class));
				return res;
			}
		};

		try {
			return sql.query(get_values_sql, rm);
		} catch (Exception e) {
			log.severe(e.getMessage());
			DBException ex = new DBException(e);
			ex.setCode("insert_value_fail");
			throw ex;
		}
	}

	private static final String update_value_sql = "update APP.WATER_VALUE set hot_value = ?, "
			+ "cold_value = ?, value_date = ?, sended = ? where id = ?";

	public void updateValue(Value value) throws DBException {
		try {
			sql.update(update_value_sql, value.getHotValue(), value.getColdValue(),
					value.getDate(), value.isSended(), value.getId());			
		} catch (Exception e) {
			log.severe(e.getMessage());
			DBException ex = new DBException(e);
			ex.setCode("update_value_fail");
			throw ex;
		}
	}

}
