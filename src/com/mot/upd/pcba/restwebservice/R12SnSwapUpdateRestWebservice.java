package com.mot.upd.pcba.restwebservice;


import java.sql.SQLException;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.constants.PCBADataDictionary;
import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.dao.R12SnSwapMySQLDAO;
import com.mot.upd.pcba.dao.R12SnSwapOracleDAO;
import com.mot.upd.pcba.handler.PCBASerialNumberModel;
import com.mot.upd.pcba.pojo.R12SnSwapUpdateQueryInput;
import com.mot.upd.pcba.pojo.R12SnSwapUpdateQueryResult;
import com.mot.upd.pcba.utils.DBUtil;
import com.mot.upd.pcba.utils.MEIDException;
import com.mot.upd.pcba.utils.MeidUtils;


/**
 * @author Quinnox Dev Team
 *
 */
@Path("/serialFetchRS")
public class R12SnSwapUpdateRestWebservice {
	private static final Logger logger = Logger.getLogger(R12SnSwapUpdateRestWebservice.class);
	
	
	@GET
	@Path("/{serialIn}")
	@Produces(MediaType.APPLICATION_JSON)
	/**
     * RestService for fetch the old serial number for given serial input 
     * @param  serialIn
     * @return R12SnSwapUpdateQueryResult Object contains the old serial
     * 		   reference and error details
    
     */	
	public R12SnSwapUpdateQueryResult r12SnSwapUpdateService(@PathParam("serialIn") String serialIn)throws NamingException,SQLException{
		//String serialOut = null;
		//serialIn = "353339060930372";

		logger.info(" Input serial number = " +serialIn);
		PCBASerialNumberModel pCBASerialNumberModel =null;
		R12SnSwapUpdateQueryInput r12UpdateQueryInput = new R12SnSwapUpdateQueryInput();
		R12SnSwapUpdateQueryResult r12UpdateQueryResult = new R12SnSwapUpdateQueryResult();
		
		try {
			r12UpdateQueryResult.setSerialIn(serialIn);
			R12SnSwapOracleDAO r12SwapUpdateOraDAO = new R12SnSwapOracleDAO();
			R12SnSwapMySQLDAO r12SwapUpdateMysqlDAO = new R12SnSwapMySQLDAO();
			String serialSnCheckValue = DBUtil.checkValidSerialNumber(serialIn,"SerialIn");
			
			String serialIndatabase =null;
			String updConfig = "NO";
			try {
                updConfig = DBUtil.dbConfigCheck();
                logger.info("UpdConfig DB Check Status : "+updConfig);
				} catch (NamingException e) {
					r12UpdateQueryResult.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
					r12UpdateQueryResult.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
                                                + e);
				} catch (SQLException e) {
					r12UpdateQueryResult.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
					r12UpdateQueryResult.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG
                                                + e);
				}

			if(updConfig.equals(PCBADataDictionary.DBCONFIG)){
				 serialIndatabase = r12SwapUpdateOraDAO.checkSerialInDB(serialSnCheckValue);
			}else{
				serialIndatabase = r12SwapUpdateMysqlDAO.checkSerialInDB(serialSnCheckValue);
			}
			//String serialSnCheckValue = MeidUtils.validateMEID(serialIn);
			logger.info(" serial available in db  = " + serialIndatabase);
			logger.info(" Request serialIn value from after check process  = " + serialSnCheckValue);
			
			if(serialSnCheckValue!=null && serialSnCheckValue.length()==ServiceMessageCodes.SN_15_DIGIT){
				//PCBASerialNumberModel pCBASerialNumberModel = r12SwapUpdateDAO.fetchR12SerialOutValue(r12UpdateQueryInput.getSerialNO());
				if(serialIndatabase !=null && serialIndatabase !=""){
				
				logger.info("updConfig value : = " + updConfig);
				if(updConfig.equals(PCBADataDictionary.DBCONFIG)){
					 pCBASerialNumberModel = r12SwapUpdateOraDAO.fetchOldestSCROracleValue(serialSnCheckValue);
				}else{
					//r12UpdateQueryResult.setSerialIn(r12UpdateQueryInput.getSerialNO());
					pCBASerialNumberModel = r12SwapUpdateMysqlDAO.fetchOldestSCRMysqlValue(serialSnCheckValue);
				}

				if(pCBASerialNumberModel.getOldSN() !=null ){
					r12UpdateQueryResult.setSerialOut(pCBASerialNumberModel.getOldSN());
					r12UpdateQueryResult.setResponseCode(ServiceMessageCodes.OLD_SN_SUCCESS);
					r12UpdateQueryResult.setResponseMsg(ServiceMessageCodes.OLD_SERIAL_FOUND_SUCCSS_MSG);

				}else{
					//r12UpdateQueryResult.setSerialIn(r12UpdateQueryInput.getSerialNO());
					//r12UpdateQueryResult.setSerialOut(pCBASerialNumberModel.getOldSN());
					r12UpdateQueryResult.setSerialOut(serialSnCheckValue);
					r12UpdateQueryResult.setResponseCode(ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND);
					r12UpdateQueryResult.setResponseMsg(ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND_MSG);
				}
			}else{
					//r12UpdateQueryResult.setSerialOut(serialSnCheckValue);
					r12UpdateQueryResult.setResponseCode(ServiceMessageCodes.R12_SN_NOT_AVAIL_IN_DATABSE);
					r12UpdateQueryResult.setResponseMsg(ServiceMessageCodes.R12_SN_NOT_AVAIL_IN_DATABSE_MSG);
				}
			}else{
				r12UpdateQueryResult.setResponseCode(ServiceMessageCodes.R12_SN_NOT_VALID);
				r12UpdateQueryResult.setResponseMsg(ServiceMessageCodes.SERIAL_NO_NOT_VALID_MSG);
			}
			
			} catch (NamingException e) {
				r12UpdateQueryResult.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
				r12UpdateQueryResult.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG + e);
			}catch (SQLException e) {
				r12UpdateQueryResult.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
				r12UpdateQueryResult.setResponseMsg(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG + e);
			}catch (MEIDException e) {
				r12UpdateQueryResult.setResponseCode(ServiceMessageCodes.R12_SN_NOT_VALID);
				r12UpdateQueryResult.setResponseMsg(ServiceMessageCodes.SERIAL_NO_NOT_VALID_MSG);
			}
			return r12UpdateQueryResult;
		}
		
}
