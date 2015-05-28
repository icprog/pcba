package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.handler.PCBASerialNumberModel;
import com.mot.upd.pcba.pojo.R12SnSwapUpdateQueryResult;
import com.mot.upd.pcba.utils.DBUtil;
import com.mot.upd.pcba.utils.InitProperty;

public class R12SnSwapMySQLDAO {
	private static Logger logger = Logger.getLogger(R12SnSwapOracleDAO.class);
	PropertyResourceBundle bundle = bundle = InitProperty.getProperty("pcbasqlMySQL.properties");
	/* * Fetching oldest scrapped record for given serial number */
	boolean isValuePresent = false;
    HashMap<String,String> resultMap = new HashMap<String,String>();
    R12SnSwapUpdateQueryResult r12UpdateQueryResult = new R12SnSwapUpdateQueryResult();
    /**
     * Fetch the old serial number for given serial input 
     * @param  serialIn
     * @return      PCBASerialNumberModel Object contains the old serial
     * 				reference
    
     */
	public PCBASerialNumberModel fetchOldestSCRMysqlValue(String serialIn){

		//String serialOut = null;
		DataSource ds;
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String oldSnValue = null;
		PCBASerialNumberModel pCBASerialNumberModel = new PCBASerialNumberModel();
		try {
			
			ds = DBUtil.getMySqlDataSource();
			} catch (NamingException e) {
				pCBASerialNumberModel.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
				pCBASerialNumberModel.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
					+ e.getMessage());
			return pCBASerialNumberModel;
		}
		
		//String updConfig = DBUtil.dbConfigCheck();
		//logger.info("updConfig value : = " + updConfig);
		//if(updConfig.equals(PCBADataDictionary.DBCONFIG)){
			try{
				 
					
				String query = bundle.getString("R12.OlddestSNFetch");
				logger.info("query : = " + query);
				conn  = ds.getConnection();
				logger.info("connection established for mysql: " + conn);
				pstmt = conn.prepareStatement(query); 
				pstmt.setString(1, serialIn);
				
				rs = pstmt.executeQuery();
				HashMap<String, String> map = new  HashMap();
				while(rs.next()){
					map.put(rs.getString(1), rs.getString(2));
				}
				Map<String,String> returnedMap1 = checkOldKey(map, serialIn);
                if(returnedMap1.get("VALUE_FOUND").equals("YES")) {
                	logger.info("Old Serial number found : " + returnedMap1.get("RESULT_SERIAL_NO"));
                	     
                		pCBASerialNumberModel.setOldSN(returnedMap1.get("RESULT_SERIAL_NO"));
                	
                } else {
                			logger.info("Old Serial number not found : ");
                			
                }

				}catch(SQLException e){
					pCBASerialNumberModel.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
					pCBASerialNumberModel.setResponseMsg(ServiceMessageCodes.SQL_EXCEPTION_MSG);
					logger.error("SqlException:::: " +e.getMessage());
				}finally{
					DBUtil.closeConnections(conn, stmt, rs);
				}
		//}
			logger.info("PCBASerialNumberModel : " + pCBASerialNumberModel.toString());
			return pCBASerialNumberModel;
		}
		
	 public Map<String,String> checkOldKey(HashMap<String,String> contentsMap, String inputValue) {
         //Check if the target value is present in data map
         if(contentsMap.containsValue(inputValue)) {
                     //Mark the isValuePresent to 'true'
                     isValuePresent = true;
                     Iterator it = contentsMap.entrySet().iterator();
                     while (it.hasNext()) {
                                 Map.Entry pair = (Map.Entry) it.next();
                                 String currentKey = (String)pair.getKey();
                                 String currentValue = (String)pair.getValue();
                                 //System.out.println(currentKey + " = " + currentValue);
                                 if(!currentKey.equals(inputValue)&&currentValue.equals(inputValue)) {
                                	 checkOldKey(contentsMap, currentKey);
                                 }
                     }
         } // Looped through the map and no more reference identified
         else if (isValuePresent) {
                     //System.out.println("Result::"+inputValue);
                     resultMap.put("RESULT_SERIAL_NO", inputValue);
                     resultMap.put("VALUE_FOUND", "YES");
         } // If the given reference key/value not found in Map

         else {
                     //System.out.println("The Value is not found in our records");
                     resultMap.put("RESULT_SERIAL_NO", inputValue);
                     resultMap.put("VALUE_FOUND", "NO");
         }
        return resultMap;

	 }

}
