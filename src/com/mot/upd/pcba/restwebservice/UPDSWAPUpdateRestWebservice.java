package com.mot.upd.pcba.restwebservice;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.constants.PCBADataDictionary;
import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.dao.PCBASwapUPDUpdateInterfaceDAO;
import com.mot.upd.pcba.dao.PCBASwapUPDUpdateOracleDAO;
import com.mot.upd.pcba.dao.PCBASwapUPDUpdateSQLDAO;
import com.mot.upd.pcba.pojo.PCBASerialNoUPdateQueryInput;
import com.mot.upd.pcba.pojo.PCBASerialNoUPdateResponse;
import com.mot.upd.pcba.utils.DBUtil;
import com.mot.upd.pcba.utils.MEIDException;

/**
 * @author rviswa
 *
 */
@Path("/swapUpdateRS")
public class UPDSWAPUpdateRestWebservice {
	private static Logger logger = Logger.getLogger(UPDSWAPUpdateRestWebservice.class);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response swapSerialNOData(PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) throws MEIDException{


		PCBASerialNoUPdateResponse pcbaSerialNoUPdateResponse = new PCBASerialNoUPdateResponse();

		boolean isMissing=false;		
		boolean isValidSerialNoOut=false;
		boolean isValidDualSerialNo=false;
		boolean isValidTriSerialNo=false;
		boolean isValidSerial=false;
		boolean isValidSerialIn=false;
		boolean isValidDualSerialIn=false;
		boolean isValidTriSerialIn=false;
		boolean isValidSntypeCheck=false;
		boolean isValidDualSerialNoType=false;
		boolean isValidRepaireDate=false;
		boolean isValidValidation =false;
		boolean isValidation =false;
		boolean isTriSerialNoType=false;

		//String updConfig = null;
		String updConfig = PCBADataDictionary.UPDCONFIG;;
		try {
			updConfig = DBUtil.dbConfigCheck();
		}catch (NamingException e) {

			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG + e);
		} catch (SQLException e) {
			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.NO_DATASOURCE_FOUND);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_DISPATCH_SERIAL_MSG + e);
		}
		PCBASwapUPDUpdateInterfaceDAO pcbaSwapUPDUpdateInterfaceDAO =null;

		if(updConfig!=null && updConfig.equals(PCBADataDictionary.DBCONFIG)){
			pcbaSwapUPDUpdateInterfaceDAO = new PCBASwapUPDUpdateOracleDAO();
			//logger.info("inside------------ oracle logic");
		}else{
			pcbaSwapUPDUpdateInterfaceDAO = new PCBASwapUPDUpdateSQLDAO();
			//logger.info("inside------------- Mysql logic");
		}

		//Check for Mandatory SerialNoin Check
		isMissing =validateSerialNoInCheck(pCBASerialNoUPdateQueryInput);
		if(isMissing){
			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.MANDATORY_SERIALNO_IN_CODE);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.PCBA_INPUT_PARAM_MISSING);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
		}

		//Check Valid DB serialNoIn check
		if(pCBASerialNoUPdateQueryInput.getSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getSerialNoIn().equals(""))){

			String statusOfSerialNoIn = null;
			try {
				statusOfSerialNoIn = DBUtil.checkValidSerialNumber(pCBASerialNoUPdateQueryInput.getSerialNoIn(),"SerialNoIn");
			} catch (MEIDException e) {
				pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.INVALID+"SerialNOIn");
				pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.INVALID_SERIAL_NO_CODE);
			}

			if(statusOfSerialNoIn.length() == 15){
				pCBASerialNoUPdateQueryInput.setSerialNoIn(statusOfSerialNoIn);
			}else{
				pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.INVALID_SERIAL_NO_CODE);
				pcbaSerialNoUPdateResponse.setResponseMessage(statusOfSerialNoIn);
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

			}
		}

		//check if SerialNoOut is valid
		isValidSerialNoOut =validateSerialNoOut(pCBASerialNoUPdateQueryInput);
		if(isValidSerialNoOut){
			pcbaSerialNoUPdateResponse.setResponseCode(""+ServiceMessageCodes.NEW_SERIAL_NO_NOT_FOUND);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.SERIALNOOUT_MSG);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

		}


		//mandatory filed check for SN Type
		isValidSntypeCheck=validateMandatroySNType(pCBASerialNoUPdateQueryInput);

		if(isValidSntypeCheck){

			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.SERIALNOTYPE_CODE);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.SERIALNOTYPE_MSG);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
		}


		//check if sn type is valid
		isValidSerial=validateSNType(pCBASerialNoUPdateQueryInput);

		if(!isValidSerial){
			pcbaSerialNoUPdateResponse.setResponseCode(""+ServiceMessageCodes.INVALID_SN_TYPE);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.INVALID_SN_TYPE_MSG);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
		}

		//check repaireDate validation check

		isValidRepaireDate = ValidRepaireDate(pCBASerialNoUPdateQueryInput);
		if(isValidRepaireDate){
			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.REPAIREDATE_CODE);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.REPAIREDATE_MSG);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

		}
		// added one more mandatory field validation on 08 july 2015
		isValidValidation = ValidValidation(pCBASerialNoUPdateQueryInput);
		if(isValidValidation){
			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.VALIDATION_CODE);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.VALIDATION_MSG);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
		}

		isValidation = Validation(pCBASerialNoUPdateQueryInput);
		if(!isValidation){
			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.VALID_VALIDATION_CODE);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.VALID_VALIDATION_MSG);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
		}

		//Check SerialIn and SerialOut different
		isValidSerialIn=validateNormalSerialNoIn(pCBASerialNoUPdateQueryInput);
		if(!isValidSerialIn){
			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.SERIAL_IN_OUT_DIFF);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.SERIAL_IN_OUT_DIFF_MSG);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

		}
		//Check SerialIn,SerialOut,DualSerialNoIn and DualSerialNoOut are Different
		isValidDualSerialIn = validateDualSerialNoIn(pCBASerialNoUPdateQueryInput);
		if(!isValidDualSerialIn){
			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.DUAL_SERIAL_IN_OUT_DIFF);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.DUAL_SERIAL_IN_OUT_DIFF_MSG);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

		}
		//Check DualSerialNOTypeCheck
		if((pCBASerialNoUPdateQueryInput.getDualSerialNoIn()!=null && !pCBASerialNoUPdateQueryInput.getDualSerialNoIn().equals("")) &&
				(pCBASerialNoUPdateQueryInput.getDualSerialNoOut()!=null && !pCBASerialNoUPdateQueryInput.getDualSerialNoOut().equals("")) &&
				(pCBASerialNoUPdateQueryInput.getDualSerialNoType()!=null && !pCBASerialNoUPdateQueryInput.getDualSerialNoType().equals(""))){

			isValidDualSerialNoType = validateDualSerialNoType(pCBASerialNoUPdateQueryInput);
			if(!isValidDualSerialNoType){
				pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.DUAL_SERIALNOTYPE_CODE);
				pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.DUAL_SERIALNOTYPE_MSG);
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

			}

		}

		// Check SerialIn,SerialOut,DualSerialNoIn,DualSerialNoOut,TriSerialNoIn and TriSerialNoOut are Different
		isValidTriSerialIn = validateTriSerialNoIn(pCBASerialNoUPdateQueryInput);
		if(!isValidTriSerialIn){
			pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.TRI_SERIAL_IN_OUT_DIFF);
			pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.TRI_SERIAL_IN_OUT_DIFF_MSG);
			return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

		}


		//Check Valid DualSerialNoIn
		if(pCBASerialNoUPdateQueryInput.getDualSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getDualSerialNoIn().equals(""))){
			String statusOfDualSerialNoIn = DBUtil.checkValidSerialNumber(pCBASerialNoUPdateQueryInput.getDualSerialNoIn(),"DualSerialNoIn");
			if(statusOfDualSerialNoIn.length() == 15){
				pCBASerialNoUPdateQueryInput.setDualSerialNoIn(statusOfDualSerialNoIn);
			}else{
				pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.INVALID_SERIAL_NO_CODE);
				pcbaSerialNoUPdateResponse.setResponseMessage(statusOfDualSerialNoIn);
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

			}
		}


		// check if DualSerialNo is valid

		if((pCBASerialNoUPdateQueryInput.getDualSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getDualSerialNoIn().equals(""))) 
				|| (pCBASerialNoUPdateQueryInput.getDualSerialNoOut()!=null  && !(pCBASerialNoUPdateQueryInput.getDualSerialNoOut().equals("")))){


			isValidDualSerialNo = validateDualSerialNo(pCBASerialNoUPdateQueryInput);
			if(isValidDualSerialNo){
				pcbaSerialNoUPdateResponse.setResponseCode(""+ServiceMessageCodes.DUAL_SERIAL_NOT_FOUND);
				pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.DUAL_SERIAL_NOT_FOUND_MSG);
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
			}
		}

		if(pCBASerialNoUPdateQueryInput.getDualSerialNoIn()!=null &&!(pCBASerialNoUPdateQueryInput.getDualSerialNoIn().equals(""))){
			int dualSerialCount = pcbaSwapUPDUpdateInterfaceDAO.checkValidSerialNoIn(pCBASerialNoUPdateQueryInput.getDualSerialNoIn());
			int triSerilalCount=0;
			if(pCBASerialNoUPdateQueryInput.getTriSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getTriSerialNoIn().equals(""))){
				triSerilalCount = pcbaSwapUPDUpdateInterfaceDAO.checkValidSerialNoIn(pCBASerialNoUPdateQueryInput.getDualSerialNoIn());
			}

			if(dualSerialCount!=2 && triSerilalCount!=3){

				// Not Eligible For Dual Sim Case
				//July 08 2015
				pcbaSwapUPDUpdateInterfaceDAO.sendAnEmail(pCBASerialNoUPdateQueryInput.getDualSerialNoIn(),pCBASerialNoUPdateQueryInput.getDualSerialNoOut(),ServiceMessageCodes.DUAL_SERIAL_NO_CODE,ServiceMessageCodes.DUAL_SERIAL_NO_CODE_MSG);

				pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.DUAL_SERIAL_NO_CODE);
				pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.DUAL_SERIAL_NO_CODE_MSG);
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
			}

		}

		//Check Valid TriSerialNoIn
		if(pCBASerialNoUPdateQueryInput.getTriSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getTriSerialNoIn().equals(""))){
			String statusOfTriSerialNoIn = DBUtil.checkValidSerialNumber(pCBASerialNoUPdateQueryInput.getTriSerialNoIn(),"TriSerialNoIn");
			if(statusOfTriSerialNoIn.length() == 15){
				pCBASerialNoUPdateQueryInput.setTriSerialNoIn(statusOfTriSerialNoIn);
			}else{
				pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.INVALID_SERIAL_NO_CODE);
				pcbaSerialNoUPdateResponse.setResponseMessage(statusOfTriSerialNoIn);
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

			}
		}

		// check if TriSerialNo is valid

		if((pCBASerialNoUPdateQueryInput.getTriSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getTriSerialNoIn().equals(""))) || 
				(pCBASerialNoUPdateQueryInput.getTriSerialNoOut()!=null && !(pCBASerialNoUPdateQueryInput.getTriSerialNoOut().equals(""))) || 
				pCBASerialNoUPdateQueryInput.getTriSerialNoType()!=null && !(pCBASerialNoUPdateQueryInput.getTriSerialNoType().equals(""))){

			isValidTriSerialNo=validateTriSerialNo(pCBASerialNoUPdateQueryInput);
			if(isValidTriSerialNo){
				pcbaSerialNoUPdateResponse.setResponseCode(""+ServiceMessageCodes.TRI_SERIAL_NOT_FOUND);
				pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.TRI_SERIAL_NOT_FOUND_MSG);
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
			}


		}

		//check TriSerialNoType
		if((pCBASerialNoUPdateQueryInput.getTriSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getTriSerialNoIn().equals(""))) || 
				(pCBASerialNoUPdateQueryInput.getTriSerialNoOut()!=null && !(pCBASerialNoUPdateQueryInput.getTriSerialNoOut().equals("")))){

			isTriSerialNoType = validateTriSerialNoType(pCBASerialNoUPdateQueryInput);

			if(!isTriSerialNoType){
				pcbaSerialNoUPdateResponse.setResponseCode(ServiceMessageCodes.TRI_SERIALNOTYPE_CODE);
				pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.TRI_SERIALNOTYPE_MSG);
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();

			}
		}

		if(pCBASerialNoUPdateQueryInput.getTriSerialNoIn()!=null &&!(pCBASerialNoUPdateQueryInput.getTriSerialNoIn().equals(""))){
			int triSerialCount = pcbaSwapUPDUpdateInterfaceDAO.checkValidSerialNoIn(pCBASerialNoUPdateQueryInput.getTriSerialNoIn());
			if(triSerialCount!=3){
				// Not Eligible For Tri Sim Case
				//July 08 2015
				pcbaSwapUPDUpdateInterfaceDAO.sendAnEmail(pCBASerialNoUPdateQueryInput.getTriSerialNoIn(),pCBASerialNoUPdateQueryInput.getTriSerialNoOut(),ServiceMessageCodes.TRI_SERIAL_NO_CODE,ServiceMessageCodes.TRI_SERIAL_NO_CODE_MSG);

				pcbaSerialNoUPdateResponse.setResponseCode(""+ServiceMessageCodes.TRI_SERIAL_NO_CODE);
				pcbaSerialNoUPdateResponse.setResponseMessage(ServiceMessageCodes.TRI_SERIAL_NO_CODE_MSG);
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
			}			
		}



		//GPP_ID Validation For IMEI Start From 08 July 2015 for Normal case
		String serialNoStatus = pcbaSwapUPDUpdateInterfaceDAO.getStatus(pCBASerialNoUPdateQueryInput.getSerialNoIn());
		//logger.info("+++++++++++++ACT/BTL++++++++++++++++"+serialNoStatus+"serial in---"+pCBASerialNoUPdateQueryInput.getSerialNoIn());
		if((serialNoStatus != null && serialNoStatus.startsWith("ACT")) || (serialNoStatus != null && serialNoStatus.startsWith("BTL"))){
		//	logger.info("+++++++++++++ACT/BTL++++++++++++++++");
			if((pCBASerialNoUPdateQueryInput.getValidation() != null) && 
					(!pCBASerialNoUPdateQueryInput.getValidation().equals("") )&& 
					(pCBASerialNoUPdateQueryInput.getValidation().equals(PCBADataDictionary.YES))){

				// GPPID Validation for IMEI Normal Case
			//	logger.info("+++++++++++++YES++++++++++++++++");

				if(pCBASerialNoUPdateQueryInput.getSerialNoType()!=null && pCBASerialNoUPdateQueryInput.getSerialNoType().equals("IMEI")){
				//	logger.info("+++++++++++++IMEI++++++++++++++++");
					pcbaSerialNoUPdateResponse = pcbaSwapUPDUpdateInterfaceDAO.validateGppId(pCBASerialNoUPdateQueryInput.getSerialNoIn(),pCBASerialNoUPdateQueryInput.getSerialNoOut());
				//	logger.info("+++++++++++++pcbaSerialNoUPdateResponse++++++++++++++++" +pcbaSerialNoUPdateResponse);
					if(pcbaSerialNoUPdateResponse.getResponseCode()!=null && !pcbaSerialNoUPdateResponse.getResponseCode().equals("")){
						return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
					}
				}

				//Protocol validation for MEID Normal Case

				if(pCBASerialNoUPdateQueryInput.getSerialNoType()!=null && pCBASerialNoUPdateQueryInput.getSerialNoType().equals("MEID")){
					pcbaSerialNoUPdateResponse = pcbaSwapUPDUpdateInterfaceDAO.validateProtocol(pCBASerialNoUPdateQueryInput.getSerialNoIn(),pCBASerialNoUPdateQueryInput.getSerialNoOut());
					if(pcbaSerialNoUPdateResponse.getResponseCode()!=null && !pcbaSerialNoUPdateResponse.getResponseCode().equals("")){
						return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
					}
				}

				// Calculate no of mac address for SerialNoIn and SerialNoOut for Normal Case

				if((pCBASerialNoUPdateQueryInput.getSerialNoIn()!=null && !pCBASerialNoUPdateQueryInput.getSerialNoIn().equals("")) && 
						(pCBASerialNoUPdateQueryInput.getSerialNoOut()!=null && !pCBASerialNoUPdateQueryInput.getSerialNoOut().equals(""))){
					pcbaSerialNoUPdateResponse = pcbaSwapUPDUpdateInterfaceDAO.calculateNoOfMACAddressForNormalCase(pCBASerialNoUPdateQueryInput.getSerialNoIn(),pCBASerialNoUPdateQueryInput.getSerialNoOut());

					/*if(pcbaSerialNoUPdateResponse.getResponseCode()!=null && !pcbaSerialNoUPdateResponse.getResponseCode().equals("")){
						return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
					    }*/
				}

			}
		}


		// Dual Case GPPID Validation July 08 2015
		String dualSerialNoStatus = pcbaSwapUPDUpdateInterfaceDAO.getStatus(pCBASerialNoUPdateQueryInput.getDualSerialNoIn());

		if((dualSerialNoStatus != null && dualSerialNoStatus.startsWith("ACT")) || (dualSerialNoStatus != null && dualSerialNoStatus.startsWith("BTL"))){

			if((pCBASerialNoUPdateQueryInput.getValidation() != null)&& 
					(!pCBASerialNoUPdateQueryInput.getValidation().equals("")) && 
					(pCBASerialNoUPdateQueryInput.getValidation().equals(PCBADataDictionary.YES))){

				//GPPID validation for IMEI Dual SIM Case
				if(pCBASerialNoUPdateQueryInput.getDualSerialNoType()!=null && pCBASerialNoUPdateQueryInput.getDualSerialNoType().equals("IMEI")){
					pcbaSerialNoUPdateResponse = pcbaSwapUPDUpdateInterfaceDAO.validateGppId(pCBASerialNoUPdateQueryInput.getDualSerialNoIn(),pCBASerialNoUPdateQueryInput.getDualSerialNoOut());
					if(pcbaSerialNoUPdateResponse.getResponseCode()!=null && !pcbaSerialNoUPdateResponse.getResponseCode().equals("")){
						return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
					}
				}

				//Protocol validation for MEID Dual SIM Case
				if(pCBASerialNoUPdateQueryInput.getDualSerialNoType()!=null && pCBASerialNoUPdateQueryInput.getDualSerialNoType().equals("MEID")){
					pcbaSerialNoUPdateResponse = pcbaSwapUPDUpdateInterfaceDAO.validateProtocol(pCBASerialNoUPdateQueryInput.getDualSerialNoIn(),pCBASerialNoUPdateQueryInput.getDualSerialNoOut());
					if(pcbaSerialNoUPdateResponse.getResponseCode()!=null && !pcbaSerialNoUPdateResponse.getResponseCode().equals("")){
						return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
					}
				}			

				// Calculate no of mac address for SerialNoIn and SerialNoOut for Dual SIM Case
				if((pCBASerialNoUPdateQueryInput.getDualSerialNoIn()!=null && !pCBASerialNoUPdateQueryInput.getDualSerialNoIn().equals("")) && 
						(pCBASerialNoUPdateQueryInput.getDualSerialNoOut()!=null && !pCBASerialNoUPdateQueryInput.getDualSerialNoOut().equals(""))){
					pcbaSerialNoUPdateResponse = pcbaSwapUPDUpdateInterfaceDAO.calculateNoOfMACAddress(pCBASerialNoUPdateQueryInput.getSerialNoOut(),pCBASerialNoUPdateQueryInput.getDualSerialNoOut(),null);

					/*if(pcbaSerialNoUPdateResponse.getResponseCode()!=null && !pcbaSerialNoUPdateResponse.getResponseCode().equals("")){
						return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
					   }*/
				}

			}
		}

		// Tri Sim  Case GPPID Validation July 08 2015
		String triSerialNoStatus = pcbaSwapUPDUpdateInterfaceDAO.getStatus(pCBASerialNoUPdateQueryInput.getTriSerialNoIn());

		if((triSerialNoStatus != null && triSerialNoStatus.startsWith("ACT")) || (triSerialNoStatus != null && triSerialNoStatus.startsWith("BTL"))){

			if((pCBASerialNoUPdateQueryInput.getValidation() != null) && 
					(!pCBASerialNoUPdateQueryInput.getValidation().equals("")) && 
					(pCBASerialNoUPdateQueryInput.getValidation().equals(PCBADataDictionary.YES))){

				// GPPID Validation for IMEI Tri SIM Case
				if(pCBASerialNoUPdateQueryInput.getTriSerialNoType()!=null && pCBASerialNoUPdateQueryInput.getTriSerialNoType().equals("IMEI")){
					pcbaSerialNoUPdateResponse = pcbaSwapUPDUpdateInterfaceDAO.validateGppId(pCBASerialNoUPdateQueryInput.getTriSerialNoIn(),pCBASerialNoUPdateQueryInput.getTriSerialNoOut());
					if(pcbaSerialNoUPdateResponse.getResponseCode()!=null && !pcbaSerialNoUPdateResponse.getResponseCode().equals("")){
						return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
					}
				}

				//Protocol Validation for MEID Tri SIM Case
				if(pCBASerialNoUPdateQueryInput.getTriSerialNoType()!=null && pCBASerialNoUPdateQueryInput.getTriSerialNoType().equals("MEID")){
					pcbaSerialNoUPdateResponse = pcbaSwapUPDUpdateInterfaceDAO.validateProtocol(pCBASerialNoUPdateQueryInput.getTriSerialNoIn(),pCBASerialNoUPdateQueryInput.getTriSerialNoOut());
					if(pcbaSerialNoUPdateResponse.getResponseCode()!=null && !pcbaSerialNoUPdateResponse.getResponseCode().equals("")){
						return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
					}
				}

				// Calculate no of mac address for SerialNoIn and SerialNoOut for Tri SIM Case

				if((pCBASerialNoUPdateQueryInput.getTriSerialNoIn()!=null && !pCBASerialNoUPdateQueryInput.getTriSerialNoIn().equals("")) && 
						(pCBASerialNoUPdateQueryInput.getTriSerialNoOut()!=null && !pCBASerialNoUPdateQueryInput.getTriSerialNoOut().equals(""))){
					pcbaSerialNoUPdateResponse = pcbaSwapUPDUpdateInterfaceDAO.calculateNoOfMACAddress(pCBASerialNoUPdateQueryInput.getSerialNoOut(),pCBASerialNoUPdateQueryInput.getDualSerialNoOut(),pCBASerialNoUPdateQueryInput.getTriSerialNoOut());

					/*if(pcbaSerialNoUPdateResponse.getResponseCode()!=null && !pcbaSerialNoUPdateResponse.getResponseCode().equals("")){
				return Response.status(200).entity(pcbaSerialNoUPdateResponse).build();
			   }*/
				}

			}
		}

		PCBASerialNoUPdateResponse response = pcbaSwapUPDUpdateInterfaceDAO.serialNumberInfo(pCBASerialNoUPdateQueryInput);

		return Response.status(200).entity(response).build();

	}



	private boolean validateTriSerialNoType(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if(pCBASerialNoUPdateQueryInput.getTriSerialNoType().trim().equals(PCBADataDictionary.IMEI) || pCBASerialNoUPdateQueryInput.getTriSerialNoType().trim().equals(PCBADataDictionary.MEID)){
			return true;
		}

		return false;

	}

	private boolean ValidRepaireDate(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if(pCBASerialNoUPdateQueryInput.getRepairdate()==null){
			return true;
		}
		if(pCBASerialNoUPdateQueryInput.getRepairdate().equals("")){
			return true;
		}

		return false;
	}

	private boolean ValidValidation(PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if(pCBASerialNoUPdateQueryInput.getValidation()==null){
			return true;
		}
		if(pCBASerialNoUPdateQueryInput.getValidation().equals("")){
			return true;
		}
		return false;
	}

	private boolean Validation(PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if(pCBASerialNoUPdateQueryInput.getValidation().trim().equals(PCBADataDictionary.YES) || pCBASerialNoUPdateQueryInput.getValidation().trim().equals(PCBADataDictionary.NO)){
			return true;
		}
		return false;
	}

	private boolean validateDualSerialNoType(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub

		if(pCBASerialNoUPdateQueryInput.getDualSerialNoType().trim().equals(PCBADataDictionary.IMEI) || pCBASerialNoUPdateQueryInput.getDualSerialNoType().trim().equals(PCBADataDictionary.MEID)){
			return true;
		}

		return false;
	}



	private boolean validateNormalSerialNoIn(PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if((pCBASerialNoUPdateQueryInput.getSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getSerialNoIn().equals(""))) && 
				(pCBASerialNoUPdateQueryInput.getSerialNoOut()!=null && !(pCBASerialNoUPdateQueryInput.getSerialNoOut().equals("")))){

			Set<String > set = new HashSet<String>();

			set.add(pCBASerialNoUPdateQueryInput.getSerialNoIn());
			set.add(pCBASerialNoUPdateQueryInput.getSerialNoOut());

			if(set.size()==2){
				return true;
			}else{
				return false;
			}

		}
		return true;
	}

	private boolean validateDualSerialNoIn(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {

		if((pCBASerialNoUPdateQueryInput.getDualSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getDualSerialNoIn().equals(""))) && 
				(pCBASerialNoUPdateQueryInput.getDualSerialNoOut()!=null && !(pCBASerialNoUPdateQueryInput.getDualSerialNoOut().equals("")))){

			Set<String > set = new HashSet<String>();

			set.add(pCBASerialNoUPdateQueryInput.getSerialNoIn());
			set.add(pCBASerialNoUPdateQueryInput.getSerialNoOut());
			set.add(pCBASerialNoUPdateQueryInput.getDualSerialNoIn());
			set.add(pCBASerialNoUPdateQueryInput.getDualSerialNoOut());
			if(set.size()==4){
				return true;
			}else{
				return false;
			}
		}
		return true;
	}

	private boolean validateTriSerialNoIn(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {

		if((pCBASerialNoUPdateQueryInput.getTriSerialNoIn()!=null && !(pCBASerialNoUPdateQueryInput.getTriSerialNoIn().equals(""))) && 
				(pCBASerialNoUPdateQueryInput.getTriSerialNoOut()!=null && !(pCBASerialNoUPdateQueryInput.getTriSerialNoOut().equals("")))){

			Set<String > set = new HashSet<String>();

			set.add(pCBASerialNoUPdateQueryInput.getSerialNoIn());
			set.add(pCBASerialNoUPdateQueryInput.getSerialNoOut());
			set.add(pCBASerialNoUPdateQueryInput.getDualSerialNoIn());
			set.add(pCBASerialNoUPdateQueryInput.getDualSerialNoOut());
			set.add(pCBASerialNoUPdateQueryInput.getTriSerialNoIn());
			set.add(pCBASerialNoUPdateQueryInput.getTriSerialNoOut());
			if(set.size()==6){
				return true;
			}else{
				return false;
			}
		}
		return true;
	}	

	// TODO Auto-generated method stub



	private boolean validateSNType(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if(pCBASerialNoUPdateQueryInput.getSerialNoType().trim().equals(PCBADataDictionary.IMEI) || pCBASerialNoUPdateQueryInput.getSerialNoType().trim().equals(PCBADataDictionary.MEID)){
			return true;
		}
		return false;
	}


	private boolean validateTriSerialNo(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if(pCBASerialNoUPdateQueryInput.getTriSerialNoIn()==null || pCBASerialNoUPdateQueryInput.getTriSerialNoOut()==null || pCBASerialNoUPdateQueryInput.getDualSerialNoIn()==null || pCBASerialNoUPdateQueryInput.getDualSerialNoOut()==null || pCBASerialNoUPdateQueryInput.getTriSerialNoType()==null){
			return true;
		}
		if(pCBASerialNoUPdateQueryInput.getTriSerialNoIn().equals("") || pCBASerialNoUPdateQueryInput.getTriSerialNoOut().equals("") || pCBASerialNoUPdateQueryInput.getDualSerialNoIn().equals("") || pCBASerialNoUPdateQueryInput.getDualSerialNoOut().equals("") || pCBASerialNoUPdateQueryInput.getTriSerialNoType().equals("")){
			return true;
		}


		return false;
	}



	private boolean validateDualSerialNo(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if(pCBASerialNoUPdateQueryInput.getDualSerialNoIn()==null || 
				pCBASerialNoUPdateQueryInput.getDualSerialNoOut()==null || 
				pCBASerialNoUPdateQueryInput.getDualSerialNoType()==null){
			return true;
		}
		if(pCBASerialNoUPdateQueryInput.getDualSerialNoIn().equals("") ||
				pCBASerialNoUPdateQueryInput.getDualSerialNoOut().equals("") || 
				pCBASerialNoUPdateQueryInput.getDualSerialNoType().equals("")){
			return true;
		}
		return false;
	}



	private boolean validateSerialNoOut(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if(pCBASerialNoUPdateQueryInput.getSerialNoOut() == null || pCBASerialNoUPdateQueryInput.getSerialNoOut().equals("")){
			return true;
		}
		return false;
	}

	private boolean validateSerialNoInCheck(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		// TODO Auto-generated method stub
		if(pCBASerialNoUPdateQueryInput.getSerialNoIn()==null){
			return true;
		}
		if(pCBASerialNoUPdateQueryInput.getSerialNoIn().equals("")){
			return true;
		}

		return false;
	}
	private boolean validateMandatroySNType(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		if(pCBASerialNoUPdateQueryInput.getSerialNoType()==null){
			return true;
		}
		if(pCBASerialNoUPdateQueryInput.getSerialNoType().equals("")){
			return true;
		}

		return false;
	}

}
