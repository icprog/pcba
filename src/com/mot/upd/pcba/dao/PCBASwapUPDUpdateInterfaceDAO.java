/**
 * 
 */
package com.mot.upd.pcba.dao;

import com.mot.upd.pcba.pojo.PCBASerialNoUPdateQueryInput;
import com.mot.upd.pcba.pojo.PCBASerialNoUPdateResponse;

/**
 * @author rviswa
 *
 */
public interface PCBASwapUPDUpdateInterfaceDAO {
	

	PCBASerialNoUPdateResponse serialNumberInfo(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput);

	int checkValidSerialNoIn(String SerialNoIn);
	PCBASerialNoUPdateResponse validateGppId(String serialNoIn,String serialNoOut);
	PCBASerialNoUPdateResponse validateProtocol(String serialNoIn,String serialNoOut);
	public void sendAnEmail(String serialNoIn,String serialNoOut,String emailMessageCode,String emailMessage);
	PCBASerialNoUPdateResponse calculateNoOfMACAddressForNormalCase(String serialNoIn,String serialNoOut);
	PCBASerialNoUPdateResponse calculateNoOfMACAddress(String serialNoOut,String dualSerialNoOut,String triSerialNoOut);
	public String getStatus(String serialNoIn);
	
	
	

}
