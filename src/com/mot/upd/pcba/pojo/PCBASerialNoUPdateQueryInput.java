/**
 * 
 */
package com.mot.upd.pcba.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author rviswa
 *
 */
public class PCBASerialNoUPdateQueryInput implements Serializable{

	private String clientIP;
	private String mascID;
	private String serialNoIn;
	private String serialNoOut;
	private String serialNoType;
	private String dualSerialNoIn;
	private String dualSerialNoOut;
	private String dualSerialNoType;
	private String triSerialNoIn;
	private String triSerialNoOut;
	private String triSerialNoType;
	private Date   repairdate;
	private String iccId;
	private String cit;
	private String apc;
	private String transModel;
	private String custModel;
	private String mktModel;
	private String itemCode;
	private String intelControlKey;
	private String validation;


	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	public String getMascID() {
		return mascID;
	}
	public void setMascID(String mascID) {
		this.mascID = mascID;
	}
	public String getSerialNoIn() {
		return serialNoIn;
	}
	public void setSerialNoIn(String serialNoIn) {
		this.serialNoIn = serialNoIn;
	}
	public String getSerialNoOut() {
		return serialNoOut;
	}
	public void setSerialNoOut(String serialNoOut) {
		this.serialNoOut = serialNoOut;
	}
	public String getSerialNoType() {
		return serialNoType;
	}
	public void setSerialNoType(String serialNoType) {
		this.serialNoType = serialNoType;
	}
	public String getDualSerialNoIn() {
		return dualSerialNoIn;
	}
	public void setDualSerialNoIn(String dualSerialNoIn) {
		this.dualSerialNoIn = dualSerialNoIn;
	}
	public String getDualSerialNoOut() {
		return dualSerialNoOut;
	}
	public void setDualSerialNoOut(String dualSerialNoOut) {
		this.dualSerialNoOut = dualSerialNoOut;
	}
	public String getDualSerialNoType() {
		return dualSerialNoType;
	}
	public void setDualSerialNoType(String dualSerialNoType) {
		this.dualSerialNoType = dualSerialNoType;
	}
	public String getTriSerialNoIn() {
		return triSerialNoIn;
	}
	public void setTriSerialNoIn(String triSerialNoIn) {
		this.triSerialNoIn = triSerialNoIn;
	}
	public String getTriSerialNoOut() {
		return triSerialNoOut;
	}
	public void setTriSerialNoOut(String triSerialNoOut) {
		this.triSerialNoOut = triSerialNoOut;
	}
	public String getTriSerialNoType() {
		return triSerialNoType;
	}
	public void setTriSerialNoType(String triSerialNoType) {
		this.triSerialNoType = triSerialNoType;
	}
	public Date getRepairdate() {
		return repairdate;
	}
	public void setRepairdate(Date repairdate) {
		this.repairdate = repairdate;
	}
	public String getIccId() {
		return iccId;
	}
	public void setIccId(String iccId) {
		this.iccId = iccId;
	}
	public String getCit() {
		return cit;
	}
	public void setCit(String cit) {
		this.cit = cit;
	}
	public String getApc() {
		return apc;
	}
	public void setApc(String apc) {
		this.apc = apc;
	}
	public String getTransModel() {
		return transModel;
	}
	public void setTransModel(String transModel) {
		this.transModel = transModel;
	}
	public String getCustModel() {
		return custModel;
	}
	public void setCustModel(String custModel) {
		this.custModel = custModel;
	}
	public String getMktModel() {
		return mktModel;
	}
	public void setMktModel(String mktModel) {
		this.mktModel = mktModel;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getIntelControlKey() {
		return intelControlKey;
	}
	public void setIntelControlKey(String intelControlKey) {
		this.intelControlKey = intelControlKey;
	}
	public String getValidation() {
		return validation;
	}
	public void setValidation(String validation) {
		this.validation = validation;
	}


}
