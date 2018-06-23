package com.ztesoft.iom.manage.rest.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClobResultSetExtractor implements ResultSetExtractor {

	private Map<String, String> lobColMap = new HashMap<String, String>();

	public ClobResultSetExtractor(String[] lobColumns) {
		if(lobColumns != null) {
			for(String s : lobColumns) {
				lobColMap.put(s.toUpperCase(), "1");
			}
		}
	}

	public Object extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		ResultSetMetaData md = rs.getMetaData();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while(rs.next()){
			Map<String, Object> m = new HashMap<String, Object>();
			for(int i=0;i<md.getColumnCount();i++){
				if(lobColMap.get(md.getColumnName(i+1).toUpperCase()) != null) { //lob字段
					try{
						Clob blob = rs.getClob(md.getColumnName(i+1));
						//考虑到informix数据库,blob字段可能会写一个null,导致空指针异常
						if(blob== null){
							m.put(md.getColumnName(i+1), "");
						} else {
							m.put(md.getColumnName(i+1), blob.getSubString(1, (int) blob.length()));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					m.put(md.getColumnName(i+1), rs.getObject(md.getColumnName(i+1)));
				}
			}
			list.add(m);
		}
		return list;
	}

}
