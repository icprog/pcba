/**
 * 
 */
package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.pojo.PCBAProgramQueryInput;
import com.mot.upd.pcba.pojo.PCBAProgramResponse;
import com.mot.upd.pcba.utils.DBUtil;


/**
 * @author rviswa
 *
 */
public class UPDSerialSuccessFailureSQLDAO implements UPDSerialSuccessFailureInterfaceDAO{
	private static Logger logger = Logger.getLogger(UPDSerialSuccessFailureSQLDAO.class);


	private DataSource ds;
	private Connection con = null;
	private PreparedStatement pstmt = null;

	PCBAProgramResponse response = new PCBAProgramResponse();

	public PCBAProgramResponse updateIMEIStatusSuccess(PCBAProgramQueryInput pcbaProgramQueryInput){

		try {

			ds = DBUtil.getMySqlDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in updateIMEIStatusSuccess:"+e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}

		try {
			// Get database connection
			con = DBUtil.getConnection(ds);
			con.setAutoCommit(false);

			StringBuffer IMEIStatusSuccess_SQL=new StringBuffer();
			IMEIStatusSuccess_SQL.append("update upd.upd_lock_code  set LAST_MOD_BY='pcba_pgm_success',");

			// ATTRIBUTE_25
			if(pcbaProgramQueryInput.getMsl()!=null && !(pcbaProgramQueryInput.getMsl().equals(""))){
				IMEIStatusSuccess_SQL.append("motorola_master='"+pcbaProgramQueryInput.getMsl()+"',");
			}

			// ATTRIBUTE_28
			if(pcbaProgramQueryInput.getOtksl()!=null && !(pcbaProgramQueryInput.getOtksl().equals(""))){
				IMEIStatusSuccess_SQL.append("motorola_onetime='"+pcbaProgramQueryInput.getOtksl()+"',");
			}

			// ATTRIBUTE_29
			if(pcbaProgramQueryInput.getServicePassCode()!=null && !(pcbaProgramQueryInput.getServicePassCode().equals(""))){
				IMEIStatusSuccess_SQL.append("service_password='"+pcbaProgramQueryInput.getServicePassCode()+"',");
			}


			IMEIStatusSuccess_SQL.append("LAST_MOD_DATETIME=NOW() WHERE serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'");
			pstmt = con.prepareStatement(IMEIStatusSuccess_SQL.toString());
			pstmt.execute();
			logger.info("IMEI MY SQL Query:"+IMEIStatusSuccess_SQL);

			pstmt =null;

			String handsetType = "update upd.upd_device_config  set handset_type='GSM',LAST_MOD_DATETIME=NOW(),LAST_MOD_BY='pcba_pgm_success' WHERE serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(handsetType);
			pstmt.execute();

			logger.info("IMEI Status Success handsetType SQL Query :"+handsetType);

			pstmt=null;

			String MYSQL_QueryIMEI ="update upd.upd_pcba_pgm_imei  set PGM_DATE=NOW(),PGM_STATUS='pcba_pgm_success' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(MYSQL_QueryIMEI);
			pstmt.execute();
			logger.info("IMEIStatusSuccess-MY SQLQueryIMEI:"+MYSQL_QueryIMEI);

			pstmt=null;

			String MYSQL_QueryMEID ="update upd.upd_pcba_pgm_meid  set PGM_DATE=NOW(),PGM_STATUS='pcba_pgm_success' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(MYSQL_QueryMEID);
			pstmt.execute();
			logger.info("IMEIStatusSuccess-MY SQLQueryMEID:"+MYSQL_QueryMEID);

			con.commit();

			response.setSerialNO(pcbaProgramQueryInput.getSerialNO());
			response.setResponseCode(ServiceMessageCodes.OLD_SN_SUCCESS);
			response.setResponseMessage(ServiceMessageCodes.IMEI_SUCCES_MSG);

		}catch(Exception e){

			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.info("Update IMEIStatusSuccess error:"+e);
			response.setResponseCode(""+ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG);
		}
		finally{			
			DBUtil.connectionClosed(con, pstmt);

		}

		return response;			

	}

	public PCBAProgramResponse updateIMEIStatusFailure(PCBAProgramQueryInput pcbaProgramQueryInput){

		try {

			ds = DBUtil.getMySqlDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in updateIMEIStatusFailure:"+e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}

		try {
			// Get database connection
			con = DBUtil.getConnection(ds);
			con.setAutoCommit(false);

			StringBuffer IMEIStatusFailure_SQL=new StringBuffer();
			//july2015 enhancement for intermediate update 
			String status ="";
			if(pcbaProgramQueryInput.getStatus()!=null && !pcbaProgramQueryInput.getStatus().equals("") && pcbaProgramQueryInput.getStatus().equals("F")){
				status ="pcba_pgm_failure";
			}else if(pcbaProgramQueryInput.getStatus()!=null && !pcbaProgramQueryInput.getStatus().equals("") && pcbaProgramQueryInput.getStatus().equals("I")){
				status ="pcba_pgm_Update(I)";
			}
			//july2015 enhancement for intermediate update 
			IMEIStatusFailure_SQL.append("update upd.upd_lock_code  set LAST_MOD_BY='"+status+"',");

			// ATTRIBUTE_25
			if(pcbaProgramQueryInput.getMsl()!=null && !(pcbaProgramQueryInput.getMsl().equals(""))){
				IMEIStatusFailure_SQL.append("motorola_master='"+pcbaProgramQueryInput.getMsl()+"',");
			}

			// ATTRIBUTE_28
			if(pcbaProgramQueryInput.getOtksl()!=null && !(pcbaProgramQueryInput.getOtksl().equals(""))){
				IMEIStatusFailure_SQL.append("motorola_onetime='"+pcbaProgramQueryInput.getOtksl()+"',");
			}

			// ATTRIBUTE_29
			if(pcbaProgramQueryInput.getServicePassCode()!=null && !(pcbaProgramQueryInput.getServicePassCode().equals(""))){
				IMEIStatusFailure_SQL.append("service_password='"+pcbaProgramQueryInput.getServicePassCode()+"',");
			}


			IMEIStatusFailure_SQL.append("LAST_MOD_DATETIME=NOW() WHERE serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'");
			pstmt = con.prepareStatement(IMEIStatusFailure_SQL.toString());
			pstmt.execute();
			logger.info("IMEI MY SQL Query:"+IMEIStatusFailure_SQL);

			pstmt =null;

			String handsetType = "update upd.upd_device_config  set handset_type='GSM',LAST_MOD_DATETIME=NOW(),LAST_MOD_BY='pcba_pgm_failure' WHERE serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(handsetType);
			pstmt.execute();

			logger.info("IMEI Status Failure handsetType SQL Query :"+handsetType);

			pstmt=null;
			//july2015 enhancement for intermediate update 
			//String MYSQL_QueryIMEI ="update upd.upd_pcba_pgm_imei  set PGM_DATE=NOW(),PGM_STATUS='pcba_pgm_failure' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			String MYSQL_QueryIMEI ="update upd.upd_pcba_pgm_imei  set PGM_DATE=NOW(),PGM_STATUS='"+status+"' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(MYSQL_QueryIMEI);
			pstmt.execute();
			logger.info("IMEIStatusFailure-MY SQLQueryIMEI:"+MYSQL_QueryIMEI);

			pstmt = null;
			//july2015 enhancement for intermediate update 
			//String MYSQL_QueryMEID ="update upd.upd_pcba_pgm_meid  set PGM_DATE=NOW(),PGM_STATUS='pcba_pgm_failure' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			String MYSQL_QueryMEID ="update upd.upd_pcba_pgm_meid  set PGM_DATE=NOW(),PGM_STATUS='"+status+"' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(MYSQL_QueryMEID);
			pstmt.execute();
			logger.info("IMEIStatusFailure-MY SQLQueryMEID:"+MYSQL_QueryMEID);
			//july2015 enhancement for intermediate update 
			

			con.commit();
			//july2015 enhancement for intermediate update 
			//response.setSerialNO(pcbaProgramQueryInput.getSerialNO());
		//	response.setResponseCode(""+ServiceMessageCodes.IMEI_FAILURE);
		//	response.setResponseMessage(ServiceMessageCodes.IMEI_FAILURE_MSG);
			if((pcbaProgramQueryInput.getStatus()!=null && !pcbaProgramQueryInput.getStatus().equals("")) && (pcbaProgramQueryInput.getStatus().equalsIgnoreCase("F"))){
				response.setSerialNO(pcbaProgramQueryInput.getSerialNO());
				response.setResponseCode(""+ServiceMessageCodes.IMEI_FAILURE);
				response.setResponseMessage(ServiceMessageCodes.IMEI_FAILURE_MSG);
			}
			//july2015 enhancement for intermediate update 
			StringBuffer intermediteValues = new StringBuffer();

			if(pcbaProgramQueryInput.getMsl()!=null && !pcbaProgramQueryInput.getMsl().equals("")){
				intermediteValues.append(" Msl value:"+pcbaProgramQueryInput.getMsl());
			}
			if(pcbaProgramQueryInput.getOtksl()!=null && !pcbaProgramQueryInput.getOtksl().equals("")){
				intermediteValues.append(" Otksl value:"+pcbaProgramQueryInput.getOtksl());
			}
			if(pcbaProgramQueryInput.getServicePassCode()!=null && !pcbaProgramQueryInput.getServicePassCode().equals("")){
				intermediteValues.append(" ServicePassCode value:"+pcbaProgramQueryInput.getServicePassCode());
			}
			
			if((pcbaProgramQueryInput.getStatus()!=null && !pcbaProgramQueryInput.getStatus().equals("")) && (pcbaProgramQueryInput.getStatus().equalsIgnoreCase("I"))){

				if((pcbaProgramQueryInput.getMsl()!=null && !pcbaProgramQueryInput.getMsl().equals("")) 
						|| (pcbaProgramQueryInput.getOtksl()!=null && !pcbaProgramQueryInput.getOtksl().equals(""))
						|| (pcbaProgramQueryInput.getServicePassCode()!=null && !pcbaProgramQueryInput.getServicePassCode().equals(""))){

					response.setSerialNO(pcbaProgramQueryInput.getSerialNO());
					response.setResponseCode(""+ServiceMessageCodes.IMEI_INTER_FAILURE);
					response.setResponseMessage(ServiceMessageCodes.LOCK_CODE_MSG+intermediteValues);
				}else{
					response.setSerialNO(pcbaProgramQueryInput.getSerialNO());
					response.setResponseCode(""+ServiceMessageCodes.IMEI_INTER_CODE_FAILURE);
					response.setResponseMessage(ServiceMessageCodes.LOCK_INTER_CODE_MSG);
				}
			}

		}catch(Exception e){

			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.info("Update IMEIStatusFailure error:"+e);
			response.setResponseCode(""+ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG);
		}
		finally{			
			DBUtil.connectionClosed(con, pstmt);

		}

		return response;
	}
	public PCBAProgramResponse updateMEIDStatusSuccess(PCBAProgramQueryInput pcbaProgramQueryInput){
		try {

			ds = DBUtil.getMySqlDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in updateMEIDStatusSuccess:"+e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}
		try{
			// Get database connection
			con = DBUtil.getConnection(ds);

			con.setAutoCommit(false);

			StringBuffer sb=new StringBuffer();
			String MEIDStatusSuccess_SQL="update upd.upd_lock_code set LAST_MOD_BY='pcba_pgm_success',LAST_MOD_DATETIME=NOW() WHERE serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(MEIDStatusSuccess_SQL);
			pstmt.execute();

			logger.info("MEID MY SQL Query:"+sb.toString());

			pstmt =null;

			String handsetType = "update upd.upd_device_config  set handset_type='CDMA',LAST_MOD_DATETIME=NOW(),LAST_MOD_BY='pcba_pgm_success' WHERE serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(handsetType);
			pstmt.execute();

			logger.info("MEID Status Success MY SQL Query:"+handsetType);

			pstmt = null;

			String MYSQL_QueryIMEI ="update upd.upd_pcba_pgm_imei  set PGM_DATE=NOW(),PGM_STATUS='pcba_pgm_success' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(MYSQL_QueryIMEI);
			pstmt.execute();
			logger.info("MEIDStatusSuccess-MY SQLQueryIMEI:"+MYSQL_QueryIMEI);

			pstmt = null;

			String MYSQL_QueryMEID ="update upd.upd_pcba_pgm_meid  set PGM_DATE=NOW(),PGM_STATUS='pcba_pgm_success' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(MYSQL_QueryMEID);
			pstmt.execute();
			logger.info("MEIDStatusSuccess - MY SQLQueryMEID:"+MYSQL_QueryMEID);

			con.commit();

			response.setSerialNO(pcbaProgramQueryInput.getSerialNO());
			response.setResponseCode(ServiceMessageCodes.OLD_SN_SUCCESS);
			response.setResponseMessage(ServiceMessageCodes.MEID_SUCCES_MSG);


		}catch(Exception e){

			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			logger.info("Update MEIDStatusSuccess error:"+e);
			response.setResponseCode(""+ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG+e.getMessage());
		}
		finally{			
			DBUtil.connectionClosed(con, pstmt);			
		}

		return response;

	}

	public PCBAProgramResponse updateMEIDStatusFailure(PCBAProgramQueryInput pcbaProgramQueryInput){
		try {

			ds = DBUtil.getMySqlDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in updateMEIDStatusFailure:"+e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}
		try{
			// Get database connection
			con = DBUtil.getConnection(ds);

			con.setAutoCommit(false);

			String MEIDStatusFailure_SQL="update upd.upd_lock_code  set LAST_MOD_BY='pcba_pgm_failure',LAST_MOD_DATETIME=NOW() WHERE serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";

			pstmt = con.prepareStatement(MEIDStatusFailure_SQL);
			pstmt.execute();

			logger.info("updateMEIDStatusFailure MY SQL Query:"+MEIDStatusFailure_SQL);

			pstmt =null;

			String handsetType = "update upd.upd_device_config  set handset_type='CDMA',LAST_MOD_DATETIME=NOW(),LAST_MOD_BY='pcba_pgm_failure' WHERE serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(handsetType);
			pstmt.execute();

			logger.info("MEID Status Failure MY SQL Query:"+handsetType);

			pstmt = null;

			String MYSQL_QueryIMEI ="update upd.upd_pcba_pgm_imei  set PGM_DATE=NOW(),PGM_STATUS='pcba_pgm_failure' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(MYSQL_QueryIMEI);
			pstmt.execute();
			logger.info("MEIDStatusFailure-MY SQLQueryIMEI:"+MYSQL_QueryIMEI);

			pstmt = null;

			String MYSQL_QueryMEID ="update upd.upd_pcba_pgm_meid  set PGM_DATE=NOW(),PGM_STATUS='pcba_pgm_failure' where serial_no='"+pcbaProgramQueryInput.getSerialNO()+"'";
			pstmt = con.prepareStatement(MYSQL_QueryMEID);
			pstmt.execute();
			logger.info("MEIDStatusFailure-SQLQueryMEID:"+MYSQL_QueryMEID);

			con.commit();

			response.setSerialNO(pcbaProgramQueryInput.getSerialNO());
			response.setResponseCode(""+ServiceMessageCodes.MEID_FAILURE);
			response.setResponseMessage(ServiceMessageCodes.MEID_FAILURE_MSG);

		}catch(Exception e){

			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			logger.info("Update MEIDStatusSuccess error:"+e);
			response.setResponseCode(""+ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG);

		}
		finally{
			DBUtil.connectionClosed(con, pstmt);			
		}

		return response;

	}

}
