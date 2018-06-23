package com.ztesoft.oss.crm.server.storage.dao.extractor;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class BlobResultSetExtractor implements ResultSetExtractor {

	private Map<String, String> lobColMap = new HashMap<String, String>();

	public BlobResultSetExtractor(String[] lobColumns) {
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
						Blob blob = rs.getBlob(md.getColumnName(i+1));
						//考虑到informix数据库,blob字段可能会写一个null,导致空指针异常
						if(blob== null){
							m.put(md.getColumnName(i+1), new String("".getBytes(), "UTF-8"));
						}
						m.put(md.getColumnName(i+1), new String(blob.getBytes(1, (int) blob.length()), "UTF-8"));
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
