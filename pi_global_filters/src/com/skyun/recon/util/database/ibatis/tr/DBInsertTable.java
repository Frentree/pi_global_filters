package com.skyun.recon.util.database.ibatis.tr;

import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.skyun.recon.util.database.ibatis.SqlMapInstance;
import com.skyun.recon.util.database.ibatis.vo.globalFiltersVo;

//Database Insert 
public class DBInsertTable {
	private static Logger logger = LoggerFactory.getLogger(DBInsertTable.class);
	private static SqlMapClient sqlMap = null;

	private int ret = 0;
	private String sexception;

	public DBInsertTable() {
		this.sqlMap = SqlMapInstance.getSqlMapInstance();
	}

	public DBInsertTable(SqlMapClient sqlMap) {
		this.sqlMap = sqlMap;
	}

	public void setDBInsertTable(String trid, Object obj) {

		try {
			if (obj instanceof globalFiltersVo) {
				this.sqlMap.openSession().insert(trid, (globalFiltersVo) obj);
				ret = 1;
				sexception = "OK";
			} else if (obj instanceof HashMap) {
				this.sqlMap.openSession().insert(trid, (HashMap<String, Object>) obj);
				ret = 1;
				sexception = "OK";
			} else {
				logger.info("unknown data type " + obj.getClass().getName());
			}

		} catch (SQLException e) {
			sexception = e.getMessage();
			ret = -1;
			logger.info("DB Error :____________________" + sexception);
		}

	}

	// parameter 없이 쿼리 실행 위해 method 생성 -- shlee // 20200827
	public void setDBInsertTable(String trid) {
		try {
			this.sqlMap.openSession().insert(trid);
			ret = 1;
			sexception = "OK";
		} catch (SQLException e) {
			sexception = e.getMessage();
			ret = -1;
			logger.info("DB Error :____________________" + sexception);
		}
	}
	public String getSexception() {
		return sexception;
	}

	public int getRet() {
		return ret;
	}
	
	public SqlMapClient getSqlclient() {
		return this.sqlMap;
	}

}
