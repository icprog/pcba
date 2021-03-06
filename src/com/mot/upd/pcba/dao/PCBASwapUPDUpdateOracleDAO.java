/**
 * 
 */
package com.mot.upd.pcba.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.constants.ServiceMessageCodes;
import com.mot.upd.pcba.pojo.PCBASerialNoUPdateQueryInput;
import com.mot.upd.pcba.pojo.PCBASerialNoUPdateResponse;
import com.mot.upd.pcba.utils.DBUtil;
import com.mot.upd.pcba.utils.InitProperty;
import com.mot.upd.pcba.utils.MailUtil;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author rviswa
 * 
 */
public class PCBASwapUPDUpdateOracleDAO implements
PCBASwapUPDUpdateInterfaceDAO {
	private static Logger logger = Logger
			.getLogger(PCBASwapUPDUpdateOracleDAO.class);

	private DataSource ds;
	private Connection con = null;
	private Connection connection = null;
	private PreparedStatement preparedStmt = null;
	private PreparedStatement pstmt = null;
	private PreparedStatement pstmt1 = null;
	private PreparedStatement prestmt = null;
	private ResultSet rs = null;

	PCBASerialNoUPdateResponse response = new PCBASerialNoUPdateResponse();

	StringBuffer SQLQuery = new StringBuffer();

	public PCBASerialNoUPdateResponse serialNumberInfo(
			PCBASerialNoUPdateQueryInput pCBASerialNoUPdateQueryInput) {
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:" + e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}

		try {
			// get database connection
			con = DBUtil.getConnection(ds);

			StringBuffer sb = new StringBuffer();

			sb.append("select  SERIAL_NO, REQUEST_ID, REGION_ID, SYSTEM_ID, ATTRIBUTE_01, ATTRIBUTE_02, ATTRIBUTE_03, ATTRIBUTE_04, ATTRIBUTE_05,ATTRIBUTE_06,  ATTRIBUTE_07,  ATTRIBUTE_08,");
			sb.append("ATTRIBUTE_09, ATTRIBUTE_10,   ATTRIBUTE_11,  ATTRIBUTE_12,  ATTRIBUTE_13,  ATTRIBUTE_14, ATTRIBUTE_15,  ATTRIBUTE_16,  ATTRIBUTE_17,  ATTRIBUTE_18,  ATTRIBUTE_19,");
			sb.append("ATTRIBUTE_20,  ATTRIBUTE_21,  ATTRIBUTE_22,  ATTRIBUTE_23,  ATTRIBUTE_24,  ATTRIBUTE_34, ATTRIBUTE_35,  ATTRIBUTE_37,  ATTRIBUTE_38,  ATTRIBUTE_39,  ATTRIBUTE_40,");
			sb.append("ATTRIBUTE_41,  ATTRIBUTE_42,  ATTRIBUTE_43,  ATTRIBUTE_44,  ATTRIBUTE_45,  ATTRIBUTE_46, ATTRIBUTE_47,  ATTRIBUTE_48,  ATTRIBUTE_49,  ATTRIBUTE_50,  ATTRIBUTE_51,");
			sb.append("ATTRIBUTE_52,  ATTRIBUTE_53,  ATTRIBUTE_54,  ATTRIBUTE_55,  ATTRIBUTE_56,  ATTRIBUTE_57, ATTRIBUTE_58,  ATTRIBUTE_59,  ATTRIBUTE_60,  ATTRIBUTE_61,  ATTRIBUTE_62,");
			sb.append("ATTRIBUTE_63,  ATTRIBUTE_64,  ATTRIBUTE_65,  ATTRIBUTE_66,  ATTRIBUTE_67,  ATTRIBUTE_68, ATTRIBUTE_69,  ATTRIBUTE_70,  ATTRIBUTE_71,  ATTRIBUTE_72,  ATTRIBUTE_73,");
			sb.append("ATTRIBUTE_74,  ATTRIBUTE_75,  ATTRIBUTE_76,  ATTRIBUTE_77,  ATTRIBUTE_78,  ATTRIBUTE_79, ATTRIBUTE_80,  ATTRIBUTE_81,  ATTRIBUTE_82,  ATTRIBUTE_84,  ATTRIBUTE_85,");
			sb.append("ATTRIBUTE_86,  ATTRIBUTE_87,  ATTRIBUTE_88,  ATTRIBUTE_89,  ATTRIBUTE_90,  ATTRIBUTE_91, ATTRIBUTE_92,  ATTRIBUTE_93,  ATTRIBUTE_94,  ATTRIBUTE_95,  ATTRIBUTE_96,");
			sb.append("ATTRIBUTE_97,  ATTRIBUTE_98,  ATTRIBUTE_99,  ATTRIBUTE_100, ATTRIBUTE_101, ATTRIBUTE_105,ATTRIBUTE_106, ATTRIBUTE_107, ATTRIBUTE_108, ATTRIBUTE_109, ATTRIBUTE_110,");
			sb.append("ATTRIBUTE_111, ATTRIBUTE_112, ATTRIBUTE_113, ATTRIBUTE_117, ATTRIBUTE_118, ATTRIBUTE_114,ATTRIBUTE_115, ATTRIBUTE_116, ATTRIBUTE_119, ATTRIBUTE_120, ATTRIBUTE_121,");
			sb.append("ATTRIBUTE_122, ATTRIBUTE_123 from upd.UPD_SN_REPOS  where serial_no=?");

			logger.info("Before Shipment Data Reading SQL:"+sb.toString());

			preparedStmt = con.prepareStatement(sb.toString());
			preparedStmt.setString(1,
					pCBASerialNoUPdateQueryInput.getSerialNoIn());
			rs = preparedStmt.executeQuery();

			//logger.info("After Shipment Data Reading SQL:"+sb.toString());

			if (rs.next()) {

				String serialOutStatus = getStatus(pCBASerialNoUPdateQueryInput.getSerialNoOut());
				connection = DBUtil.getConnection(ds);
				connection.setAutoCommit(false);

				if((serialOutStatus!=null && serialOutStatus.startsWith("VOI")) || 
						(serialOutStatus!=null && serialOutStatus.startsWith("ACT")) ||
						(serialOutStatus!=null && serialOutStatus.startsWith("BTL"))){					

					String serialNoOfstatus=rs.getString("ATTRIBUTE_37");


					if((serialNoOfstatus != null && serialNoOfstatus.startsWith("ACT")) || (serialNoOfstatus != null && serialNoOfstatus.startsWith("BTL"))){

						SQLQuery.append("update upd.UPD_SN_REPOS SET REQUEST_ID=?,REGION_ID=?,SYSTEM_ID=?,ATTRIBUTE_01=?,ATTRIBUTE_02=?,ATTRIBUTE_03=?,ATTRIBUTE_04=?,ATTRIBUTE_05=?,ATTRIBUTE_06=?,ATTRIBUTE_07=?,ATTRIBUTE_08=?,");
						SQLQuery.append("ATTRIBUTE_09=?,  ATTRIBUTE_10=?,  ATTRIBUTE_11=?,  ATTRIBUTE_12=?,  ATTRIBUTE_13=?,  ATTRIBUTE_14=?, ATTRIBUTE_15=?,  ATTRIBUTE_16=?,  ATTRIBUTE_17=?,  ATTRIBUTE_18=?,  ATTRIBUTE_19=?,");
						SQLQuery.append("ATTRIBUTE_20=?,  ATTRIBUTE_21=?,  ATTRIBUTE_22=?,  ATTRIBUTE_23=?,  ATTRIBUTE_24=?,  ATTRIBUTE_34=?, ATTRIBUTE_35=?,  ATTRIBUTE_37=?,  ATTRIBUTE_38=?,  ATTRIBUTE_39=?,  ATTRIBUTE_40=?,");
						SQLQuery.append("ATTRIBUTE_41=?,  ATTRIBUTE_42=?,  ATTRIBUTE_43=?,  ATTRIBUTE_44=?,  ATTRIBUTE_45=?,  ATTRIBUTE_46=?, ATTRIBUTE_47=?,  ATTRIBUTE_48=?,  ATTRIBUTE_49=?,  ATTRIBUTE_50=?,  ATTRIBUTE_51=?,");
						SQLQuery.append("ATTRIBUTE_52=?,  ATTRIBUTE_53=?,  ATTRIBUTE_54=?,  ATTRIBUTE_55=?,  ATTRIBUTE_56=?,  ATTRIBUTE_57=?, ATTRIBUTE_58=?,  ATTRIBUTE_59=?,  ATTRIBUTE_60=?,  ATTRIBUTE_61=?,  ATTRIBUTE_62=?,");
						SQLQuery.append("ATTRIBUTE_63=?,  ATTRIBUTE_64=?,  ATTRIBUTE_65=?,  ATTRIBUTE_66=?,  ATTRIBUTE_67=?,  ATTRIBUTE_68=?, ATTRIBUTE_69=?,  ATTRIBUTE_70=?,  ATTRIBUTE_71=?,  ATTRIBUTE_72=?,  ATTRIBUTE_73=?,");
						SQLQuery.append("ATTRIBUTE_74=?,  ATTRIBUTE_75=?,  ATTRIBUTE_76=?,  ATTRIBUTE_77=?,  ATTRIBUTE_78=?,  ATTRIBUTE_79=?, ATTRIBUTE_80=?,  ATTRIBUTE_81=?,  ATTRIBUTE_82=?,  ATTRIBUTE_84=?,  ATTRIBUTE_85=?,");
						SQLQuery.append("ATTRIBUTE_86=?,  ATTRIBUTE_87=?,  ATTRIBUTE_88=?,  ATTRIBUTE_89=?,  ATTRIBUTE_90=?,  ATTRIBUTE_91=?, ATTRIBUTE_92=?,  ATTRIBUTE_93=?,  ATTRIBUTE_94=?,  ATTRIBUTE_95=?,  ATTRIBUTE_96=?,");
						SQLQuery.append("ATTRIBUTE_97=?,  ATTRIBUTE_98=?,  ATTRIBUTE_99=?,  ATTRIBUTE_100=?, ATTRIBUTE_101=?, ATTRIBUTE_105=?,ATTRIBUTE_106=?, ATTRIBUTE_107=?, ATTRIBUTE_108=?, ATTRIBUTE_109=?, ATTRIBUTE_110=?,");
						SQLQuery.append("ATTRIBUTE_111=?, ATTRIBUTE_112=?, ATTRIBUTE_113=?, ATTRIBUTE_117=?, ATTRIBUTE_118=?, ATTRIBUTE_114=?,ATTRIBUTE_115=?, ATTRIBUTE_116=?, ATTRIBUTE_119=?, ATTRIBUTE_120=?, ATTRIBUTE_121=?,");
						SQLQuery.append("ATTRIBUTE_122=?, ATTRIBUTE_123=?, LAST_MOD_BY='pcba_pgm_SwapUpdate',LAST_MOD_DATETIME=sysdate  where SERIAL_NO=?");


						logger.info("Before Inserting Shipment Table SQL"+SQLQuery.toString());
						pstmt = connection.prepareStatement(SQLQuery.toString());

						if(rs.getString("REQUEST_ID")!=null && !(rs.getString("REQUEST_ID").equals(""))){
							pstmt.setString(1, rs.getString("REQUEST_ID"));
						}else{
							pstmt.setString(1,null);
						}

						if(rs.getString("REGION_ID")!=null && !(rs.getString("REGION_ID").equals(""))){
							pstmt.setString(2, rs.getString("REGION_ID"));
						}else{
							pstmt.setString(2, null);
						}

						if(rs.getString("SYSTEM_ID")!=null && !(rs.getString("SYSTEM_ID").equals(""))){
							pstmt.setString(3, rs.getString("SYSTEM_ID"));
						}else{
							pstmt.setString(3, null);
						}

						if(rs.getString("ATTRIBUTE_01")!=null && !(rs.getString("ATTRIBUTE_01").equals(""))){
							pstmt.setString(4, rs.getString("ATTRIBUTE_01"));
						}else{
							pstmt.setString(4, null);
						}					

						if(rs.getDate("ATTRIBUTE_02")!=null && !(rs.getDate("ATTRIBUTE_02").equals(""))){
							pstmt.setDate(5, rs.getDate("ATTRIBUTE_02"));
						}else{
							pstmt.setDate(5, null);
						}

						if(rs.getString("ATTRIBUTE_03")!=null && !(rs.getString("ATTRIBUTE_03").equals(""))){
							pstmt.setString(6, rs.getString("ATTRIBUTE_03"));
						}else{
							pstmt.setString(6, null);
						}

						if(rs.getString("ATTRIBUTE_04")!=null && !(rs.getString("ATTRIBUTE_04").equals(""))){
							pstmt.setString(7, rs.getString("ATTRIBUTE_04"));
						}else{
							pstmt.setString(7, null);
						}

						if(rs.getString("ATTRIBUTE_05")!=null && !(rs.getString("ATTRIBUTE_05").equals(""))){
							pstmt.setString(8, rs.getString("ATTRIBUTE_05"));
						}else{
							pstmt.setString(8, null);
						}

						if(rs.getString("ATTRIBUTE_06")!=null && !(rs.getString("ATTRIBUTE_06").equals(""))){
							pstmt.setString(9, rs.getString("ATTRIBUTE_06"));
						}else{
							pstmt.setString(9, null);
						}

						if(rs.getString("ATTRIBUTE_07")!=null && !(rs.getString("ATTRIBUTE_07").equals(""))){
							pstmt.setString(10, rs.getString("ATTRIBUTE_07"));
						}else{
							pstmt.setString(10, null);
						}

						if(rs.getString("ATTRIBUTE_08")!=null && !(rs.getString("ATTRIBUTE_08").equals(""))){
							pstmt.setString(11, rs.getString("ATTRIBUTE_08"));
						}else{
							pstmt.setString(11, null);
						}

						if(rs.getString("ATTRIBUTE_09")!=null && !(rs.getString("ATTRIBUTE_09").equals(""))){
							pstmt.setString(12, rs.getString("ATTRIBUTE_09"));
						}else{
							pstmt.setString(12,null);
						}

						if(rs.getTimestamp("ATTRIBUTE_10")!=null && !(rs.getTimestamp("ATTRIBUTE_10").equals(""))){
							pstmt.setTimestamp(13, rs.getTimestamp("ATTRIBUTE_10"));
						}else{
							pstmt.setTimestamp(13, null);
						}

						if(rs.getString("ATTRIBUTE_11")!=null && !(rs.getString("ATTRIBUTE_11").equals(""))){
							pstmt.setString(14, rs.getString("ATTRIBUTE_11"));
						}else{
							pstmt.setString(14,null);
						}

						if(rs.getString("ATTRIBUTE_12")!=null && !(rs.getString("ATTRIBUTE_12").equals(""))){
							pstmt.setString(15, rs.getString("ATTRIBUTE_12"));
						}else{
							pstmt.setString(15, null);
						}

						if(rs.getString("ATTRIBUTE_13")!=null && !(rs.getString("ATTRIBUTE_13").equals(""))){
							pstmt.setString(16, rs.getString("ATTRIBUTE_13"));
						}else{
							pstmt.setString(16,null);
						}

						if(rs.getString("ATTRIBUTE_14")!=null && !(rs.getString("ATTRIBUTE_14").equals(""))){
							pstmt.setString(17, rs.getString("ATTRIBUTE_14"));
						}else{
							pstmt.setString(17,null);
						}

						if(rs.getString("ATTRIBUTE_15")!=null && !(rs.getString("ATTRIBUTE_15").equals(""))){
							pstmt.setString(18, rs.getString("ATTRIBUTE_15"));
						}else{
							pstmt.setString(18,null);
						}

						if(rs.getString("ATTRIBUTE_16")!=null && !(rs.getString("ATTRIBUTE_16").equals(""))){
							pstmt.setString(19, rs.getString("ATTRIBUTE_16"));
						}else{
							pstmt.setString(19,null);
						}

						if(rs.getString("ATTRIBUTE_17")!=null && !(rs.getString("ATTRIBUTE_17").equals(""))){
							pstmt.setString(20, rs.getString("ATTRIBUTE_17"));
						}else{
							pstmt.setString(20,null);
						}

						if(rs.getDate("ATTRIBUTE_18")!=null && !(rs.getDate("ATTRIBUTE_18").equals(""))){
							pstmt.setDate(21, rs.getDate("ATTRIBUTE_18"));
						}else{
							pstmt.setDate(21,null);
						}

						if(rs.getString("ATTRIBUTE_19")!=null && !(rs.getString("ATTRIBUTE_19").equals(""))){
							pstmt.setString(22, rs.getString("ATTRIBUTE_19"));
						}else{
							pstmt.setString(22,null);
						}

						if(rs.getString("ATTRIBUTE_20")!=null && !(rs.getString("ATTRIBUTE_20").equals(""))){
							pstmt.setString(23, rs.getString("ATTRIBUTE_20"));
						}else{
							pstmt.setString(23,null);
						}

						if(rs.getString("ATTRIBUTE_21")!=null && !(rs.getString("ATTRIBUTE_21").equals(""))){
							pstmt.setString(24, rs.getString("ATTRIBUTE_21"));
						}else{
							pstmt.setString(24,null);
						}

						if(rs.getString("ATTRIBUTE_22")!=null && !(rs.getString("ATTRIBUTE_22").equals(""))){
							pstmt.setString(25, rs.getString("ATTRIBUTE_22"));
						}else{
							pstmt.setString(25,null);
						}

						if(rs.getString("ATTRIBUTE_23")!=null && !(rs.getString("ATTRIBUTE_23").equals(""))){
							pstmt.setString(26, rs.getString("ATTRIBUTE_23"));
						}else{
							pstmt.setString(26,null);
						}

						if(rs.getString("ATTRIBUTE_24")!=null && !(rs.getString("ATTRIBUTE_24").equals(""))){
							pstmt.setString(27, rs.getString("ATTRIBUTE_24"));
						}else{
							pstmt.setString(27,null);
						}
						String lockCode = rs.getString("ATTRIBUTE_34");

						if(rs.getString("ATTRIBUTE_34")!=null && !(rs.getString("ATTRIBUTE_34").equals(""))){
							pstmt.setString(28, rs.getString("ATTRIBUTE_34"));
						}else{
							pstmt.setString(28,null);
						}

						if(rs.getString("ATTRIBUTE_35")!=null && !(rs.getString("ATTRIBUTE_35").equals(""))){
							pstmt.setString(29, rs.getString("ATTRIBUTE_35"));
						}else{
							pstmt.setString(29,null);
						}

						Date curDate = new Date();
						SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
						String DateToStr = format.format(curDate);

						if(serialNoOfstatus!=null && serialNoOfstatus.startsWith("ACT")){
							pstmt.setString(30, "ACT       " + DateToStr);// Status
						}

						if(serialNoOfstatus!=null && serialNoOfstatus.startsWith("BTL")){
							pstmt.setString(30, "BTL       " + DateToStr);// Status
						}

						if(rs.getString("ATTRIBUTE_38")!=null && !(rs.getString("ATTRIBUTE_38").equals(""))){
							pstmt.setString(31, rs.getString("ATTRIBUTE_38"));
						}else{
							pstmt.setString(31,null);
						}

						if(rs.getString("ATTRIBUTE_39")!=null && !(rs.getString("ATTRIBUTE_39").equals(""))){
							pstmt.setString(32, rs.getString("ATTRIBUTE_39"));
						}else{
							pstmt.setString(32,null);
						}

						if(rs.getString("ATTRIBUTE_40")!=null && !(rs.getString("ATTRIBUTE_40").equals(""))){
							pstmt.setString(33, rs.getString("ATTRIBUTE_40"));
						}else{
							pstmt.setString(33,null);
						}

						/*if(rs.getString("ATTRIBUTE_41")!=null && !(rs.getString("ATTRIBUTE_41").equals(""))){
						pstmt.setString(34, rs.getString("ATTRIBUTE_41"));
					}else{
						pstmt.setString(34,null);
					}*/

						// ATTRIBUTE_41 related as follows
						if(pCBASerialNoUPdateQueryInput.getSerialNoOut()!=null && !(pCBASerialNoUPdateQueryInput.getSerialNoOut().equals(""))){
							pstmt.setString(34, pCBASerialNoUPdateQueryInput.getSerialNoOut());
						}else{
							pstmt.setString(34,null);
						}

						if(rs.getString("ATTRIBUTE_42")!=null && !(rs.getString("ATTRIBUTE_42").equals(""))){
							pstmt.setString(35, rs.getString("ATTRIBUTE_42"));
						}else{
							pstmt.setString(35,null);
						}

						if(rs.getString("ATTRIBUTE_43")!=null && !(rs.getString("ATTRIBUTE_43").equals(""))){
							pstmt.setString(36, rs.getString("ATTRIBUTE_43"));
						}else{
							pstmt.setString(36,null);
						}

						if(rs.getString("ATTRIBUTE_44")!=null && !(rs.getString("ATTRIBUTE_44").equals(""))){
							pstmt.setString(37, rs.getString("ATTRIBUTE_44"));
						}else{
							pstmt.setString(37,null);
						}

						if(rs.getDate("ATTRIBUTE_45")!=null && !(rs.getDate("ATTRIBUTE_45").equals(""))){
							pstmt.setDate(38, rs.getDate("ATTRIBUTE_45"));
						}else{
							pstmt.setString(38,null);
						}

						if(rs.getString("ATTRIBUTE_46")!=null && !(rs.getString("ATTRIBUTE_46").equals(""))){
							pstmt.setString(39, rs.getString("ATTRIBUTE_46"));
						}else{
							pstmt.setString(39,null);
						}

						if(rs.getString("ATTRIBUTE_47")!=null && !(rs.getString("ATTRIBUTE_47").equals(""))){
							pstmt.setString(40, rs.getString("ATTRIBUTE_47"));
						}else{
							pstmt.setString(40,null);
						}

						if(rs.getString("ATTRIBUTE_48")!=null && !(rs.getString("ATTRIBUTE_48").equals(""))){
							pstmt.setString(41, rs.getString("ATTRIBUTE_48"));
						}else{
							pstmt.setString(41,null);
						}

						if(rs.getDate("ATTRIBUTE_49")!=null && !(rs.getDate("ATTRIBUTE_49").equals(""))){
							pstmt.setDate(42, rs.getDate("ATTRIBUTE_49"));
						}else{
							pstmt.setDate(42,null);
						}

						if(rs.getDate("ATTRIBUTE_50")!=null && !(rs.getDate("ATTRIBUTE_50").equals(""))){
							pstmt.setDate(43, rs.getDate("ATTRIBUTE_50"));
						}else{
							pstmt.setDate(43,null);
						}

						if(rs.getDate("ATTRIBUTE_51")!=null && !(rs.getDate("ATTRIBUTE_51").equals(""))){
							pstmt.setDate(44, rs.getDate("ATTRIBUTE_51"));
						}else{
							pstmt.setDate(44,null);
						}

						if(rs.getDate("ATTRIBUTE_52")!=null && !(rs.getDate("ATTRIBUTE_52").equals(""))){
							pstmt.setDate(45, rs.getDate("ATTRIBUTE_52"));
						}else{
							pstmt.setDate(45,null);
						}

						if(rs.getDate("ATTRIBUTE_53")!=null && !(rs.getDate("ATTRIBUTE_53").equals(""))){
							pstmt.setDate(46, rs.getDate("ATTRIBUTE_53"));
						}else{
							pstmt.setDate(46,null);
						}

						pstmt.setLong(47, rs.getLong("ATTRIBUTE_54")); // Number
						pstmt.setLong(48, rs.getLong("ATTRIBUTE_55")); // Number
						pstmt.setLong(49, rs.getLong("ATTRIBUTE_56")); // Number
						pstmt.setLong(50, rs.getLong("ATTRIBUTE_57"));// Number

						if(rs.getString("ATTRIBUTE_58")!=null && !(rs.getString("ATTRIBUTE_58").equals(""))){
							pstmt.setString(51, rs.getString("ATTRIBUTE_58"));
						}else{
							pstmt.setString(51,null);
						}

						if(rs.getString("ATTRIBUTE_59")!=null && !(rs.getString("ATTRIBUTE_59").equals(""))){
							pstmt.setString(52, rs.getString("ATTRIBUTE_59"));
						}else{
							pstmt.setString(52,null);
						}

						if(rs.getString("ATTRIBUTE_60")!=null && !(rs.getString("ATTRIBUTE_60").equals(""))){
							pstmt.setString(53, rs.getString("ATTRIBUTE_60"));
						}else{
							pstmt.setString(53,null);
						}

						if(rs.getString("ATTRIBUTE_61")!=null && !(rs.getString("ATTRIBUTE_61").equals(""))){
							pstmt.setString(54, rs.getString("ATTRIBUTE_61"));
						}else{
							pstmt.setString(54,null);
						}

						if(rs.getString("ATTRIBUTE_62")!=null && !(rs.getString("ATTRIBUTE_62").equals(""))){
							pstmt.setString(55, rs.getString("ATTRIBUTE_62"));
						}else{
							pstmt.setString(55,null);
						}

						if(rs.getString("ATTRIBUTE_63")!=null && !(rs.getString("ATTRIBUTE_63").equals(""))){
							pstmt.setString(56, rs.getString("ATTRIBUTE_63"));
						}else{
							pstmt.setString(56,null);
						}

						if(rs.getString("ATTRIBUTE_64")!=null && !(rs.getString("ATTRIBUTE_64").equals(""))){
							pstmt.setString(57, rs.getString("ATTRIBUTE_64"));
						}else{
							pstmt.setString(57,null);
						}

						if(rs.getString("ATTRIBUTE_65")!=null && !(rs.getString("ATTRIBUTE_65").equals(""))){
							pstmt.setString(58, rs.getString("ATTRIBUTE_65"));
						}else{
							pstmt.setString(58,null);
						}

						if(rs.getString("ATTRIBUTE_66")!=null && !(rs.getString("ATTRIBUTE_66").equals(""))){
							pstmt.setString(59, rs.getString("ATTRIBUTE_66"));
						}else{
							pstmt.setString(59,null);
						}

						if(rs.getString("ATTRIBUTE_67")!=null && !(rs.getString("ATTRIBUTE_67").equals(""))){
							pstmt.setString(60, rs.getString("ATTRIBUTE_67"));
						}else{
							pstmt.setString(60,null);
						}

						if(rs.getString("ATTRIBUTE_68")!=null && !(rs.getString("ATTRIBUTE_68").equals(""))){
							pstmt.setString(61, rs.getString("ATTRIBUTE_68"));
						}else{
							pstmt.setString(61,null);
						}

						if(rs.getString("ATTRIBUTE_69")!=null && !(rs.getString("ATTRIBUTE_69").equals(""))){
							pstmt.setString(62, rs.getString("ATTRIBUTE_69"));
						}else{
							pstmt.setString(62,null);
						}

						if(rs.getString("ATTRIBUTE_70")!=null && !(rs.getString("ATTRIBUTE_70").equals(""))){
							pstmt.setString(63, rs.getString("ATTRIBUTE_70"));
						}else{
							pstmt.setString(63,null);
						}

						if(rs.getString("ATTRIBUTE_71")!=null && !(rs.getString("ATTRIBUTE_71").equals(""))){
							pstmt.setString(64, rs.getString("ATTRIBUTE_71"));
						}else{
							pstmt.setString(64,null);
						}

						if(rs.getString("ATTRIBUTE_72")!=null && !(rs.getString("ATTRIBUTE_72").equals(""))){
							pstmt.setString(65, rs.getString("ATTRIBUTE_72"));
						}else{
							pstmt.setString(65,null);
						}

						if(rs.getString("ATTRIBUTE_73")!=null && !(rs.getString("ATTRIBUTE_73").equals(""))){
							pstmt.setString(66, rs.getString("ATTRIBUTE_73"));
						}else{
							pstmt.setString(66,null);
						}

						if(rs.getString("ATTRIBUTE_74")!=null && !(rs.getString("ATTRIBUTE_74").equals(""))){
							pstmt.setString(67, rs.getString("ATTRIBUTE_74"));
						}else{
							pstmt.setString(67,null);
						}

						if(rs.getString("ATTRIBUTE_75")!=null && !(rs.getString("ATTRIBUTE_75").equals(""))){
							pstmt.setString(68, rs.getString("ATTRIBUTE_75"));
						}else{
							pstmt.setString(68,null);
						}

						if(rs.getString("ATTRIBUTE_76")!=null && !(rs.getString("ATTRIBUTE_76").equals(""))){
							pstmt.setString(69, rs.getString("ATTRIBUTE_76"));
						}else{
							pstmt.setString(69,null);
						}

						if(rs.getString("ATTRIBUTE_77")!=null && !(rs.getString("ATTRIBUTE_77").equals(""))){
							pstmt.setString(70, rs.getString("ATTRIBUTE_77"));
						}else{
							pstmt.setString(70,null);
						}

						if(rs.getString("ATTRIBUTE_78")!=null && !(rs.getString("ATTRIBUTE_78").equals(""))){
							pstmt.setString(71, rs.getString("ATTRIBUTE_78"));
						}else{
							pstmt.setString(71,null);
						}

						if(rs.getDate("ATTRIBUTE_79")!=null && !(rs.getDate("ATTRIBUTE_79").equals(""))){
							pstmt.setDate(72, rs.getDate("ATTRIBUTE_79"));
						}else{
							pstmt.setDate(72,null);
						}

						if(rs.getString("ATTRIBUTE_80")!=null && !(rs.getString("ATTRIBUTE_80").equals(""))){
							pstmt.setString(73, rs.getString("ATTRIBUTE_80"));
						}else{
							pstmt.setString(73,null);
						}

						if(rs.getDate("ATTRIBUTE_81")!=null && !(rs.getDate("ATTRIBUTE_81").equals(""))){
							pstmt.setDate(74, rs.getDate("ATTRIBUTE_81"));
						}else{
							pstmt.setDate(74,null);
						}

						if(rs.getString("ATTRIBUTE_82")!=null && !(rs.getString("ATTRIBUTE_82").equals(""))){
							pstmt.setString(75, rs.getString("ATTRIBUTE_82"));
						}else{
							pstmt.setString(75,null);
						}

						if(rs.getString("ATTRIBUTE_84")!=null && !(rs.getString("ATTRIBUTE_84").equals(""))){
							pstmt.setString(76, rs.getString("ATTRIBUTE_84"));
						}else{
							pstmt.setString(76,null);
						}

						if(rs.getString("ATTRIBUTE_85")!=null && !(rs.getString("ATTRIBUTE_85").equals(""))){
							pstmt.setString(77, rs.getString("ATTRIBUTE_85"));
						}else{
							pstmt.setString(77,null);
						}

						if(rs.getString("ATTRIBUTE_86")!=null && !(rs.getString("ATTRIBUTE_86").equals(""))){
							pstmt.setString(78, rs.getString("ATTRIBUTE_86"));
						}else{
							pstmt.setString(78,null);
						}

						if(rs.getString("ATTRIBUTE_87")!=null && !(rs.getString("ATTRIBUTE_87").equals(""))){
							pstmt.setString(79, rs.getString("ATTRIBUTE_87"));
						}else{
							pstmt.setString(79,null);
						}

						if(rs.getString("ATTRIBUTE_88")!=null && !(rs.getString("ATTRIBUTE_88").equals(""))){
							pstmt.setString(80, rs.getString("ATTRIBUTE_88"));
						}else{
							pstmt.setString(80,null);
						}

						if(rs.getString("ATTRIBUTE_89")!=null && !(rs.getString("ATTRIBUTE_89").equals(""))){
							pstmt.setString(81, rs.getString("ATTRIBUTE_89"));
						}else{
							pstmt.setString(81,null);
						}

						if(rs.getString("ATTRIBUTE_90")!=null && !(rs.getString("ATTRIBUTE_90").equals(""))){
							pstmt.setString(82, rs.getString("ATTRIBUTE_90"));
						}else{
							pstmt.setString(82,null);
						}

						if(rs.getString("ATTRIBUTE_91")!=null && !(rs.getString("ATTRIBUTE_91").equals(""))){
							pstmt.setString(83, rs.getString("ATTRIBUTE_91"));
						}else{
							pstmt.setString(83,null);
						}

						if(rs.getString("ATTRIBUTE_92")!=null && !(rs.getString("ATTRIBUTE_92").equals(""))){
							pstmt.setString(84, rs.getString("ATTRIBUTE_92"));
						}else{
							pstmt.setString(84,null);
						}

						if(rs.getString("ATTRIBUTE_93")!=null && !(rs.getString("ATTRIBUTE_93").equals(""))){
							pstmt.setString(85, rs.getString("ATTRIBUTE_93"));
						}else{
							pstmt.setString(85,null);
						}

						if(rs.getString("ATTRIBUTE_94")!=null && !(rs.getString("ATTRIBUTE_94").equals(""))){
							pstmt.setString(86, rs.getString("ATTRIBUTE_94"));
						}else{
							pstmt.setString(86,null);
						}

						if(rs.getString("ATTRIBUTE_95")!=null && !(rs.getString("ATTRIBUTE_95").equals(""))){
							pstmt.setString(87, rs.getString("ATTRIBUTE_95"));
						}else{
							pstmt.setString(87,null);
						}

						if(rs.getString("ATTRIBUTE_96")!=null && !(rs.getString("ATTRIBUTE_96").equals(""))){
							pstmt.setString(88, rs.getString("ATTRIBUTE_96"));
						}else{
							pstmt.setString(88,null);
						}

						if(rs.getString("ATTRIBUTE_97")!=null && !(rs.getString("ATTRIBUTE_97").equals(""))){
							pstmt.setString(89, rs.getString("ATTRIBUTE_97"));
						}else{
							pstmt.setString(89,null);
						}

						if(rs.getString("ATTRIBUTE_98")!=null && !(rs.getString("ATTRIBUTE_98").equals(""))){
							pstmt.setString(90, rs.getString("ATTRIBUTE_98"));
						}else{
							pstmt.setString(90,null);
						}

						if(rs.getString("ATTRIBUTE_99")!=null && !(rs.getString("ATTRIBUTE_99").equals(""))){
							pstmt.setString(91, rs.getString("ATTRIBUTE_99"));
						}else{
							pstmt.setString(91,null);
						}

						if(rs.getString("ATTRIBUTE_100")!=null && !(rs.getString("ATTRIBUTE_100").equals(""))){
							pstmt.setString(92, rs.getString("ATTRIBUTE_100"));
						}else{
							pstmt.setString(92,null);
						}

						if(rs.getString("ATTRIBUTE_101")!=null && !(rs.getString("ATTRIBUTE_101").equals(""))){
							pstmt.setString(93, rs.getString("ATTRIBUTE_101"));
						}else{
							pstmt.setString(93,null);
						}

						if(rs.getString("ATTRIBUTE_105")!=null && !(rs.getString("ATTRIBUTE_105").equals(""))){
							pstmt.setString(94, rs.getString("ATTRIBUTE_105"));
						}else{
							pstmt.setString(94,null);
						}

						if(rs.getString("ATTRIBUTE_106")!=null && !(rs.getString("ATTRIBUTE_106").equals(""))){
							pstmt.setString(95, rs.getString("ATTRIBUTE_106"));
						}else{
							pstmt.setString(95,null);
						}

						if(rs.getString("ATTRIBUTE_107")!=null && !(rs.getString("ATTRIBUTE_107").equals(""))){
							pstmt.setString(96, rs.getString("ATTRIBUTE_107"));
						}else{
							pstmt.setString(96,null);
						}

						if(rs.getString("ATTRIBUTE_108")!=null && !(rs.getString("ATTRIBUTE_108").equals(""))){
							pstmt.setString(97, rs.getString("ATTRIBUTE_108"));
						}else{
							pstmt.setString(97,null);
						}

						if(rs.getString("ATTRIBUTE_109")!=null && !(rs.getString("ATTRIBUTE_109").equals(""))){
							pstmt.setString(98, rs.getString("ATTRIBUTE_109"));
						}else{
							pstmt.setString(98,null);
						}

						if(rs.getString("ATTRIBUTE_110")!=null && !(rs.getString("ATTRIBUTE_110").equals(""))){
							pstmt.setString(99, rs.getString("ATTRIBUTE_110"));
						}else{
							pstmt.setString(99,null);
						}

						if(rs.getString("ATTRIBUTE_111")!=null && !(rs.getString("ATTRIBUTE_111").equals(""))){
							pstmt.setString(100, rs.getString("ATTRIBUTE_111"));
						}else{
							pstmt.setString(100,null);
						}

						if(rs.getString("ATTRIBUTE_112")!=null && !(rs.getString("ATTRIBUTE_112").equals(""))){
							pstmt.setString(101, rs.getString("ATTRIBUTE_112"));
						}else{
							pstmt.setString(101,null);
						}

						if(rs.getString("ATTRIBUTE_113")!=null && !(rs.getString("ATTRIBUTE_113").equals(""))){
							pstmt.setString(102, rs.getString("ATTRIBUTE_113"));
						}else{
							pstmt.setString(102,null);
						}

						if(rs.getDate("ATTRIBUTE_117")!=null && !(rs.getDate("ATTRIBUTE_117").equals(""))){
							pstmt.setDate(103, rs.getDate("ATTRIBUTE_117"));
						}else{
							pstmt.setDate(103,null);
						}

						pstmt.setLong(104, rs.getLong("ATTRIBUTE_118"));// Number

						if(rs.getDate("ATTRIBUTE_114")!=null && !(rs.getDate("ATTRIBUTE_114").equals(""))){
							pstmt.setDate(105, rs.getDate("ATTRIBUTE_114"));
						}else{
							pstmt.setDate(105,null);
						}

						if(rs.getDate("ATTRIBUTE_115")!=null && !(rs.getDate("ATTRIBUTE_115").equals(""))){
							pstmt.setDate(106, rs.getDate("ATTRIBUTE_115"));
						}else{
							pstmt.setDate(106,null);
						}

						if(rs.getString("ATTRIBUTE_116")!=null && !(rs.getString("ATTRIBUTE_116").equals(""))){
							pstmt.setString(107, rs.getString("ATTRIBUTE_116"));
						}else{
							pstmt.setString(107,null);
						}

						if(rs.getString("ATTRIBUTE_119")!=null && !(rs.getString("ATTRIBUTE_119").equals(""))){
							pstmt.setString(108, rs.getString("ATTRIBUTE_119"));
						}else{
							pstmt.setString(108,null);
						}

						if(rs.getString("ATTRIBUTE_120")!=null && !(rs.getString("ATTRIBUTE_120").equals(""))){
							pstmt.setString(109, rs.getString("ATTRIBUTE_120"));
						}else{
							pstmt.setString(109,null);
						}


						if(rs.getString("ATTRIBUTE_121")!=null && !(rs.getString("ATTRIBUTE_121").equals(""))){
							pstmt.setString(110, rs.getString("ATTRIBUTE_121"));
						}else{
							pstmt.setString(110,null);
						}

						if(rs.getString("ATTRIBUTE_122")!=null && !(rs.getString("ATTRIBUTE_122").equals(""))){
							pstmt.setString(111, rs.getString("ATTRIBUTE_122"));
						}else{
							pstmt.setString(111, null);
						}

						if(rs.getString("ATTRIBUTE_123")!=null && !(rs.getString("ATTRIBUTE_123").equals(""))){
							pstmt.setString(112, rs.getString("ATTRIBUTE_123"));
						}else{
							pstmt.setString(112,null);
						}

						pstmt.setString(113, pCBASerialNoUPdateQueryInput.getSerialNoOut());

						boolean status = pstmt.execute();

						logger.info("After Inserting Shipment Table SQL"+SQLQuery.toString());

						//Update SwapDate in MEID Table.
						pstmt = null;
						String serialMEIDupdate="update upd.upd_pcba_pgm_meid set SWAP_DATE=sysdate,OLD_SERIAL_NO='"+pCBASerialNoUPdateQueryInput.getSerialNoIn()+"' where serial_no='"+pCBASerialNoUPdateQueryInput.getSerialNoOut()+"'";
						pstmt = connection.prepareStatement(serialMEIDupdate);
						pstmt.execute();

						logger.info("serialNormalCase MEIDupdate SQL:"+serialMEIDupdate);

						//Update swapDate in IMEI Table 
						pstmt = null;
						String serialIMEIupdate="update upd.upd_pcba_pgm_imei set SWAP_DATE=sysdate,OLD_SERIAL_NO='"+pCBASerialNoUPdateQueryInput.getSerialNoIn()+"' where serial_no='"+pCBASerialNoUPdateQueryInput.getSerialNoOut()+"'";
						pstmt = connection.prepareStatement(serialIMEIupdate);
						pstmt.execute();

						logger.info("serialNormalCase IMEIupdate SQL:"+serialIMEIupdate);

						if (!status) {
							// commented for pcba july 2015 release	
							//String updateOldserialNOStatus = "update upd.UPD_SN_REPOS set ATTRIBUTE_34='"+lockCode+"SCR',ATTRIBUTE_37='SCR       "
							String updateOldserialNOStatus = "update upd.UPD_SN_REPOS set ATTRIBUTE_34='"+lockCode+"SCR',ATTRIBUTE_37='VOI       "
									+ DateToStr
									+ "',ATTRIBUTE_41='"+pCBASerialNoUPdateQueryInput.getSerialNoOut()+"',LAST_MOD_BY='pcba_pgm_SwapUpdate',LAST_MOD_DATETIME=sysdate where serial_no='"
									+ pCBASerialNoUPdateQueryInput.getSerialNoIn()
									+ "'";
							pstmt1 = connection
									.prepareStatement(updateOldserialNOStatus);
							pstmt1.execute();
							logger.info("Updating Status in Shipment Table:"+updateOldserialNOStatus);

						}

						response.setResponseCode(ServiceMessageCodes.OLD_SN_SUCCESS);
						response.setResponseMessage(ServiceMessageCodes.OLD_SERIAL_FOUND_SUCCSS_MSG);
					}else{
						sendEmail(pCBASerialNoUPdateQueryInput.getSerialNoIn(),pCBASerialNoUPdateQueryInput.getSerialNoOut(),connection,prestmt);
						response.setResponseCode(ServiceMessageCodes.EMAIL_MSG_CODE);
						response.setResponseMessage(ServiceMessageCodes.EMAIL_MSG);
						connection.commit();
					}
				}else{
					response.setResponseCode(ServiceMessageCodes.SERIAL_NO_OUT_MSG_CODE);
					response.setResponseMessage(ServiceMessageCodes.SERIAL_NO_OUT_MSG);

				}
			} else {

				response.setResponseCode(""+ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND_IN_SHIPMENT_TABLE);
				response.setResponseMessage(ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND_IN_SHIPMENT_TABLE_MSG);
				return response;

			}

			// Above its normal case

			// Dual Case

			if (pCBASerialNoUPdateQueryInput.getDualSerialNoIn() != null)

			{
				if (!(pCBASerialNoUPdateQueryInput.getDualSerialNoIn().trim() == "")) {

					String DualStatus = getStatus(pCBASerialNoUPdateQueryInput.getDualSerialNoIn());
					if((DualStatus!=null && DualStatus.startsWith("ACT")) || (DualStatus!=null && DualStatus.startsWith("BTL"))){


						String dualSerialOutStatus = getStatus(pCBASerialNoUPdateQueryInput.getDualSerialNoOut());

						if((dualSerialOutStatus!=null && dualSerialOutStatus.startsWith("VOI")) || 
								(dualSerialOutStatus!=null && dualSerialOutStatus.startsWith("ACT")) || 
								(dualSerialOutStatus!=null && dualSerialOutStatus.startsWith("BTL"))){

							connection = updateReferenceTable(
									pCBASerialNoUPdateQueryInput.getSerialNoIn(),
									pCBASerialNoUPdateQueryInput.getSerialNoOut(),
									connection);

							connection = updateBasedOnSerial(
									pCBASerialNoUPdateQueryInput.getDualSerialNoIn(),
									pCBASerialNoUPdateQueryInput.getDualSerialNoOut(),
									ds, connection);

							connection = updateReferenceTable(
									pCBASerialNoUPdateQueryInput.getDualSerialNoIn(),
									pCBASerialNoUPdateQueryInput.getDualSerialNoOut(),
									connection);

						}else{
							response.setResponseCode(ServiceMessageCodes.SERIAL_NO_OUT_MSG_CODE);
							response.setResponseMessage(ServiceMessageCodes.SERIAL_NO_OUT_MSG);
						}

					}else{						
						connection.rollback();
						sendEmail(pCBASerialNoUPdateQueryInput.getDualSerialNoIn(),pCBASerialNoUPdateQueryInput.getDualSerialNoOut(),connection,prestmt);
						response.setResponseCode(ServiceMessageCodes.EMAIL_MSG_CODE);
						response.setResponseMessage(ServiceMessageCodes.EMAIL_MSG);
						connection.commit();
						//return response;


					}
				}

			}

			// Tri Case
			if (pCBASerialNoUPdateQueryInput.getTriSerialNoIn() != null) {

				if (!(pCBASerialNoUPdateQueryInput.getTriSerialNoIn().trim() == "")) {

					String triStatus = getStatus(pCBASerialNoUPdateQueryInput.getTriSerialNoIn());

					if((triStatus!=null && triStatus.startsWith("ACT")) || (triStatus!=null && triStatus.startsWith("BTL"))){

						String triSerialOutStatus = getStatus(pCBASerialNoUPdateQueryInput.getTriSerialNoOut());
						if((triSerialOutStatus!=null && triSerialOutStatus.startsWith("VOI")) || 
								(triSerialOutStatus!=null && triSerialOutStatus.startsWith("ACT")) ||
								(triSerialOutStatus!=null && triSerialOutStatus.startsWith("BTL"))){

							connection = updateBasedOnSerial(
									pCBASerialNoUPdateQueryInput.getTriSerialNoIn(),
									pCBASerialNoUPdateQueryInput.getTriSerialNoOut(),
									ds, connection);
							connection = updateReferenceTable(
									pCBASerialNoUPdateQueryInput.getTriSerialNoIn(),
									pCBASerialNoUPdateQueryInput.getTriSerialNoOut(),
									connection);
						}else{

							response.setResponseCode(ServiceMessageCodes.SERIAL_NO_OUT_MSG_CODE);
							response.setResponseMessage(ServiceMessageCodes.SERIAL_NO_OUT_MSG);

						}
					}else{

						connection.rollback();
						sendEmail(pCBASerialNoUPdateQueryInput.getTriSerialNoIn(),pCBASerialNoUPdateQueryInput.getTriSerialNoOut(),connection,prestmt);
						response.setResponseCode(ServiceMessageCodes.EMAIL_MSG_CODE);
						response.setResponseMessage(ServiceMessageCodes.EMAIL_MSG);
						connection.commit();
						return response;
					}

				}
			}

			// dualConnection.commit();
			connection.commit();
			connection.close();

		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setResponseCode(""+ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG
					+ e.getMessage());
		} finally {
			DBUtil.closeConnection(con, preparedStmt, rs);
			DBUtil.connectionClosed(connection, pstmt);	
			try{
				if(pstmt1!=null){
					pstmt1.close();
				}
				if(prestmt!=null){
					prestmt.close();
				}
			}catch(SQLException e){
				logger.error(e.getMessage());
				response.setResponseCode(""+ServiceMessageCodes.SQL_EXCEPTION);
				response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG+ e.getMessage());
			}
		}

		return response;
	}

	// Dual Case and
	// Tri Case
	public Connection updateBasedOnSerial(String serialNoIn,
			String serialNoOut, DataSource ds, Connection connection2)
					throws Exception {

		Connection innerselectcon = null;
		PreparedStatement pst = null;
		PreparedStatement pstUpdate = null;
		ResultSet rs = null;

		try {
			// get database connection
			innerselectcon = DBUtil.getConnection(ds);

			StringBuffer sbuffer = new StringBuffer();
			StringBuffer SQLInnerQuery = new StringBuffer();

			sbuffer.append("select  SERIAL_NO, REQUEST_ID, REGION_ID, SYSTEM_ID, ATTRIBUTE_01, ATTRIBUTE_02, ATTRIBUTE_03, ATTRIBUTE_04, ATTRIBUTE_05,ATTRIBUTE_06,  ATTRIBUTE_07,  ATTRIBUTE_08,");
			sbuffer.append("ATTRIBUTE_09, ATTRIBUTE_10,   ATTRIBUTE_11,  ATTRIBUTE_12,  ATTRIBUTE_13,  ATTRIBUTE_14, ATTRIBUTE_15,  ATTRIBUTE_16,  ATTRIBUTE_17,  ATTRIBUTE_18,  ATTRIBUTE_19,");
			sbuffer.append("ATTRIBUTE_20,  ATTRIBUTE_21,  ATTRIBUTE_22,  ATTRIBUTE_23,  ATTRIBUTE_24,  ATTRIBUTE_34, ATTRIBUTE_35,  ATTRIBUTE_37,  ATTRIBUTE_38,  ATTRIBUTE_39,  ATTRIBUTE_40,");
			sbuffer.append("ATTRIBUTE_41,  ATTRIBUTE_42,  ATTRIBUTE_43,  ATTRIBUTE_44,  ATTRIBUTE_45,  ATTRIBUTE_46, ATTRIBUTE_47,  ATTRIBUTE_48,  ATTRIBUTE_49,  ATTRIBUTE_50,  ATTRIBUTE_51,");
			sbuffer.append("ATTRIBUTE_52,  ATTRIBUTE_53,  ATTRIBUTE_54,  ATTRIBUTE_55,  ATTRIBUTE_56,  ATTRIBUTE_57, ATTRIBUTE_58,  ATTRIBUTE_59,  ATTRIBUTE_60,  ATTRIBUTE_61,  ATTRIBUTE_62,");
			sbuffer.append("ATTRIBUTE_63,  ATTRIBUTE_64,  ATTRIBUTE_65,  ATTRIBUTE_66,  ATTRIBUTE_67,  ATTRIBUTE_68, ATTRIBUTE_69,  ATTRIBUTE_70,  ATTRIBUTE_71,  ATTRIBUTE_72,  ATTRIBUTE_73,");
			sbuffer.append("ATTRIBUTE_74,  ATTRIBUTE_75,  ATTRIBUTE_76,  ATTRIBUTE_77,  ATTRIBUTE_78,  ATTRIBUTE_79, ATTRIBUTE_80,  ATTRIBUTE_81,  ATTRIBUTE_82,  ATTRIBUTE_84,  ATTRIBUTE_85,");
			sbuffer.append("ATTRIBUTE_86,  ATTRIBUTE_87,  ATTRIBUTE_88,  ATTRIBUTE_89,  ATTRIBUTE_90,  ATTRIBUTE_91, ATTRIBUTE_92,  ATTRIBUTE_93,  ATTRIBUTE_94,  ATTRIBUTE_95,  ATTRIBUTE_96,");
			sbuffer.append("ATTRIBUTE_97,  ATTRIBUTE_98,  ATTRIBUTE_99,  ATTRIBUTE_100, ATTRIBUTE_101, ATTRIBUTE_105,ATTRIBUTE_106, ATTRIBUTE_107, ATTRIBUTE_108, ATTRIBUTE_109, ATTRIBUTE_110,");
			sbuffer.append("ATTRIBUTE_111, ATTRIBUTE_112, ATTRIBUTE_113, ATTRIBUTE_117, ATTRIBUTE_118, ATTRIBUTE_114,ATTRIBUTE_115, ATTRIBUTE_116, ATTRIBUTE_119, ATTRIBUTE_120, ATTRIBUTE_121,");
			sbuffer.append("ATTRIBUTE_122, ATTRIBUTE_123 from upd.UPD_SN_REPOS  where serial_no=?");

			logger.info(" Before Dual Shipment Table SQL:"+sbuffer.toString());

			pst = innerselectcon.prepareStatement(sbuffer.toString());
			pst.setString(1, serialNoIn);
			rs = pst.executeQuery();

			logger.info(" After Dual Shipment Table SQL:"+sbuffer.toString());

			if (rs.next()) {

				SQLInnerQuery.append("update upd.UPD_SN_REPOS SET REQUEST_ID=?,REGION_ID=?,SYSTEM_ID=?,ATTRIBUTE_01=?,ATTRIBUTE_02=?,ATTRIBUTE_03=?,ATTRIBUTE_04=?,ATTRIBUTE_05=?,ATTRIBUTE_06=?,ATTRIBUTE_07=?,ATTRIBUTE_08=?,");
				SQLInnerQuery.append("ATTRIBUTE_09=?,  ATTRIBUTE_10=?,  ATTRIBUTE_11=?,  ATTRIBUTE_12=?,  ATTRIBUTE_13=?,  ATTRIBUTE_14=?, ATTRIBUTE_15=?,  ATTRIBUTE_16=?,  ATTRIBUTE_17=?,  ATTRIBUTE_18=?,  ATTRIBUTE_19=?,");
				SQLInnerQuery.append("ATTRIBUTE_20=?,  ATTRIBUTE_21=?,  ATTRIBUTE_22=?,  ATTRIBUTE_23=?,  ATTRIBUTE_24=?,  ATTRIBUTE_34=?, ATTRIBUTE_35=?,  ATTRIBUTE_37=?,  ATTRIBUTE_38=?,  ATTRIBUTE_39=?,  ATTRIBUTE_40=?,");
				SQLInnerQuery.append("ATTRIBUTE_41=?,  ATTRIBUTE_42=?,  ATTRIBUTE_43=?,  ATTRIBUTE_44=?,  ATTRIBUTE_45=?,  ATTRIBUTE_46=?, ATTRIBUTE_47=?,  ATTRIBUTE_48=?,  ATTRIBUTE_49=?,  ATTRIBUTE_50=?,  ATTRIBUTE_51=?,");
				SQLInnerQuery.append("ATTRIBUTE_52=?,  ATTRIBUTE_53=?,  ATTRIBUTE_54=?,  ATTRIBUTE_55=?,  ATTRIBUTE_56=?,  ATTRIBUTE_57=?, ATTRIBUTE_58=?,  ATTRIBUTE_59=?,  ATTRIBUTE_60=?,  ATTRIBUTE_61=?,  ATTRIBUTE_62=?,");
				SQLInnerQuery.append("ATTRIBUTE_63=?,  ATTRIBUTE_64=?,  ATTRIBUTE_65=?,  ATTRIBUTE_66=?,  ATTRIBUTE_67=?,  ATTRIBUTE_68=?, ATTRIBUTE_69=?,  ATTRIBUTE_70=?,  ATTRIBUTE_71=?,  ATTRIBUTE_72=?,  ATTRIBUTE_73=?,");
				SQLInnerQuery.append("ATTRIBUTE_74=?,  ATTRIBUTE_75=?,  ATTRIBUTE_76=?,  ATTRIBUTE_77=?,  ATTRIBUTE_78=?,  ATTRIBUTE_79=?, ATTRIBUTE_80=?,  ATTRIBUTE_81=?,  ATTRIBUTE_82=?,  ATTRIBUTE_84=?,  ATTRIBUTE_85=?,");
				SQLInnerQuery.append("ATTRIBUTE_86=?,  ATTRIBUTE_87=?,  ATTRIBUTE_88=?,  ATTRIBUTE_89=?,  ATTRIBUTE_90=?,  ATTRIBUTE_91=?, ATTRIBUTE_92=?,  ATTRIBUTE_93=?,  ATTRIBUTE_94=?,  ATTRIBUTE_95=?,  ATTRIBUTE_96=?,");
				SQLInnerQuery.append("ATTRIBUTE_97=?,  ATTRIBUTE_98=?,  ATTRIBUTE_99=?,  ATTRIBUTE_100=?, ATTRIBUTE_101=?, ATTRIBUTE_105=?,ATTRIBUTE_106=?, ATTRIBUTE_107=?, ATTRIBUTE_108=?, ATTRIBUTE_109=?, ATTRIBUTE_110=?,");
				SQLInnerQuery.append("ATTRIBUTE_111=?, ATTRIBUTE_112=?, ATTRIBUTE_113=?, ATTRIBUTE_117=?, ATTRIBUTE_118=?, ATTRIBUTE_114=?,ATTRIBUTE_115=?, ATTRIBUTE_116=?, ATTRIBUTE_119=?, ATTRIBUTE_120=?, ATTRIBUTE_121=?,");
				SQLInnerQuery.append("ATTRIBUTE_122=?, ATTRIBUTE_123=?, LAST_MOD_BY='pcba_pgm_SwapUpdate',LAST_MOD_DATETIME=sysdate  where SERIAL_NO=?");

				logger.info(" Before Shipment Table Insert SQL:"+SQLInnerQuery.toString());

				pstUpdate = connection2.prepareStatement(SQLInnerQuery
						.toString());


				if(rs.getString("REQUEST_ID")!=null && !(rs.getString("REQUEST_ID").equals(""))){
					pstUpdate.setString(1, rs.getString("REQUEST_ID"));
				}else{
					pstUpdate.setString(1,null);
				}

				if(rs.getString("REGION_ID")!=null && !(rs.getString("REGION_ID").equals(""))){
					pstUpdate.setString(2, rs.getString("REGION_ID"));
				}else{
					pstUpdate.setString(2, null);
				}

				if(rs.getString("SYSTEM_ID")!=null && !(rs.getString("SYSTEM_ID").equals(""))){
					pstUpdate.setString(3, rs.getString("SYSTEM_ID"));
				}else{
					pstUpdate.setString(3, null);
				}

				if(rs.getString("ATTRIBUTE_01")!=null && !(rs.getString("ATTRIBUTE_01").equals(""))){
					pstUpdate.setString(4, rs.getString("ATTRIBUTE_01"));
				}else{
					pstUpdate.setString(4, null);
				}					

				if(rs.getDate("ATTRIBUTE_02")!=null && !(rs.getDate("ATTRIBUTE_02").equals(""))){
					pstUpdate.setDate(5, rs.getDate("ATTRIBUTE_02"));
				}else{
					pstUpdate.setDate(5, null);
				}

				if(rs.getString("ATTRIBUTE_03")!=null && !(rs.getString("ATTRIBUTE_03").equals(""))){
					pstUpdate.setString(6, rs.getString("ATTRIBUTE_03"));
				}else{
					pstUpdate.setString(6, null);
				}

				if(rs.getString("ATTRIBUTE_04")!=null && !(rs.getString("ATTRIBUTE_04").equals(""))){
					pstUpdate.setString(7, rs.getString("ATTRIBUTE_04"));
				}else{
					pstUpdate.setString(7, null);
				}

				if(rs.getString("ATTRIBUTE_05")!=null && !(rs.getString("ATTRIBUTE_05").equals(""))){
					pstUpdate.setString(8, rs.getString("ATTRIBUTE_05"));
				}else{
					pstUpdate.setString(8, null);
				}

				if(rs.getString("ATTRIBUTE_06")!=null && !(rs.getString("ATTRIBUTE_06").equals(""))){
					pstUpdate.setString(9, rs.getString("ATTRIBUTE_06"));
				}else{
					pstUpdate.setString(9, null);
				}

				if(rs.getString("ATTRIBUTE_07")!=null && !(rs.getString("ATTRIBUTE_07").equals(""))){
					pstUpdate.setString(10, rs.getString("ATTRIBUTE_07"));
				}else{
					pstUpdate.setString(10, null);
				}

				if(rs.getString("ATTRIBUTE_08")!=null && !(rs.getString("ATTRIBUTE_08").equals(""))){
					pstUpdate.setString(11, rs.getString("ATTRIBUTE_08"));
				}else{
					pstUpdate.setString(11, null);
				}

				if(rs.getString("ATTRIBUTE_09")!=null && !(rs.getString("ATTRIBUTE_09").equals(""))){
					pstUpdate.setString(12, rs.getString("ATTRIBUTE_09"));
				}else{
					pstUpdate.setString(12,null);
				}

				if(rs.getTimestamp("ATTRIBUTE_10")!=null && !(rs.getTimestamp("ATTRIBUTE_10").equals(""))){
					pstUpdate.setTimestamp(13, rs.getTimestamp("ATTRIBUTE_10"));
				}else{
					pstUpdate.setTimestamp(13, null);
				}

				if(rs.getString("ATTRIBUTE_11")!=null && !(rs.getString("ATTRIBUTE_11").equals(""))){
					pstUpdate.setString(14, rs.getString("ATTRIBUTE_11"));
				}else{
					pstUpdate.setString(14,null);
				}

				if(rs.getString("ATTRIBUTE_12")!=null && !(rs.getString("ATTRIBUTE_12").equals(""))){
					pstUpdate.setString(15, rs.getString("ATTRIBUTE_12"));
				}else{
					pstUpdate.setString(15, null);
				}

				if(rs.getString("ATTRIBUTE_13")!=null && !(rs.getString("ATTRIBUTE_13").equals(""))){
					pstUpdate.setString(16, rs.getString("ATTRIBUTE_13"));
				}else{
					pstUpdate.setString(16,null);
				}

				if(rs.getString("ATTRIBUTE_14")!=null && !(rs.getString("ATTRIBUTE_14").equals(""))){
					pstUpdate.setString(17, rs.getString("ATTRIBUTE_14"));
				}else{
					pstUpdate.setString(17,null);
				}

				if(rs.getString("ATTRIBUTE_15")!=null && !(rs.getString("ATTRIBUTE_15").equals(""))){
					pstUpdate.setString(18, rs.getString("ATTRIBUTE_15"));
				}else{
					pstUpdate.setString(18,null);
				}

				if(rs.getString("ATTRIBUTE_16")!=null && !(rs.getString("ATTRIBUTE_16").equals(""))){
					pstUpdate.setString(19, rs.getString("ATTRIBUTE_16"));
				}else{
					pstUpdate.setString(19,null);
				}

				if(rs.getString("ATTRIBUTE_17")!=null && !(rs.getString("ATTRIBUTE_17").equals(""))){
					pstUpdate.setString(20, rs.getString("ATTRIBUTE_17"));
				}else{
					pstUpdate.setString(20,null);
				}

				if(rs.getDate("ATTRIBUTE_18")!=null && !(rs.getDate("ATTRIBUTE_18").equals(""))){
					pstUpdate.setDate(21, rs.getDate("ATTRIBUTE_18"));
				}else{
					pstUpdate.setDate(21,null);
				}

				if(rs.getString("ATTRIBUTE_19")!=null && !(rs.getString("ATTRIBUTE_19").equals(""))){
					pstUpdate.setString(22, rs.getString("ATTRIBUTE_19"));
				}else{
					pstUpdate.setString(22,null);
				}

				if(rs.getString("ATTRIBUTE_20")!=null && !(rs.getString("ATTRIBUTE_20").equals(""))){
					pstUpdate.setString(23, rs.getString("ATTRIBUTE_20"));
				}else{
					pstUpdate.setString(23,null);
				}

				if(rs.getString("ATTRIBUTE_21")!=null && !(rs.getString("ATTRIBUTE_21").equals(""))){
					pstUpdate.setString(24, rs.getString("ATTRIBUTE_21"));
				}else{
					pstUpdate.setString(24,null);
				}

				if(rs.getString("ATTRIBUTE_22")!=null && !(rs.getString("ATTRIBUTE_22").equals(""))){
					pstUpdate.setString(25, rs.getString("ATTRIBUTE_22"));
				}else{
					pstUpdate.setString(25,null);
				}

				if(rs.getString("ATTRIBUTE_23")!=null && !(rs.getString("ATTRIBUTE_23").equals(""))){
					pstUpdate.setString(26, rs.getString("ATTRIBUTE_23"));
				}else{
					pstUpdate.setString(26,null);
				}

				if(rs.getString("ATTRIBUTE_24")!=null && !(rs.getString("ATTRIBUTE_24").equals(""))){
					pstUpdate.setString(27, rs.getString("ATTRIBUTE_24"));
				}else{
					pstUpdate.setString(27,null);
				}
				String lockCode = rs.getString("ATTRIBUTE_34");

				if(rs.getString("ATTRIBUTE_34")!=null && !(rs.getString("ATTRIBUTE_34").equals(""))){
					pstUpdate.setString(28, rs.getString("ATTRIBUTE_34"));
				}else{
					pstUpdate.setString(28,null);
				}

				if(rs.getString("ATTRIBUTE_35")!=null && !(rs.getString("ATTRIBUTE_35").equals(""))){
					pstUpdate.setString(29, rs.getString("ATTRIBUTE_35"));
				}else{
					pstUpdate.setString(29,null);
				}

				Date curDate = new Date();
				SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
				String DateToStr = format.format(curDate);

				String serialNoInStatus=rs.getString("ATTRIBUTE_37");

				if(serialNoInStatus!=null && serialNoInStatus.startsWith("ACT")){
					pstUpdate.setString(30, "ACT       " + DateToStr);// Status
				}

				if(serialNoInStatus!=null && serialNoInStatus.startsWith("BTL")){
					pstUpdate.setString(30, "BTL       " + DateToStr);// Status
				}


				if(rs.getString("ATTRIBUTE_38")!=null && !(rs.getString("ATTRIBUTE_38").equals(""))){
					pstUpdate.setString(31, rs.getString("ATTRIBUTE_38"));
				}else{
					pstUpdate.setString(31,null);
				}

				if(rs.getString("ATTRIBUTE_39")!=null && !(rs.getString("ATTRIBUTE_39").equals(""))){
					pstUpdate.setString(32, rs.getString("ATTRIBUTE_39"));
				}else{
					pstUpdate.setString(32,null);
				}

				if(rs.getString("ATTRIBUTE_40")!=null && !(rs.getString("ATTRIBUTE_40").equals(""))){
					pstUpdate.setString(33, rs.getString("ATTRIBUTE_40"));
				}else{
					pstUpdate.setString(33,null);
				}

				/*if(rs.getString("ATTRIBUTE_41")!=null && !(rs.getString("ATTRIBUTE_41").equals(""))){
					pstUpdate.setString(34, rs.getString("ATTRIBUTE_41"));
				}else{
					pstUpdate.setString(34,null);
				}*/

				// ATTRIBUTE_41 as follows
				if(serialNoOut!=null && !(serialNoOut.equals(""))){
					pstUpdate.setString(34,serialNoOut);
				}else{
					pstUpdate.setString(34,null);
				}

				if(rs.getString("ATTRIBUTE_42")!=null && !(rs.getString("ATTRIBUTE_42").equals(""))){
					pstUpdate.setString(35, rs.getString("ATTRIBUTE_42"));
				}else{
					pstUpdate.setString(35,null);
				}

				if(rs.getString("ATTRIBUTE_43")!=null && !(rs.getString("ATTRIBUTE_43").equals(""))){
					pstUpdate.setString(36, rs.getString("ATTRIBUTE_43"));
				}else{
					pstUpdate.setString(36,null);
				}

				if(rs.getString("ATTRIBUTE_44")!=null && !(rs.getString("ATTRIBUTE_44").equals(""))){
					pstUpdate.setString(37, rs.getString("ATTRIBUTE_44"));
				}else{
					pstUpdate.setString(37,null);
				}

				if(rs.getDate("ATTRIBUTE_45")!=null && !(rs.getDate("ATTRIBUTE_45").equals(""))){
					pstUpdate.setDate(38, rs.getDate("ATTRIBUTE_45"));
				}else{
					pstUpdate.setString(38,null);
				}

				if(rs.getString("ATTRIBUTE_46")!=null && !(rs.getString("ATTRIBUTE_46").equals(""))){
					pstUpdate.setString(39, rs.getString("ATTRIBUTE_46"));
				}else{
					pstUpdate.setString(39,null);
				}

				if(rs.getString("ATTRIBUTE_47")!=null && !(rs.getString("ATTRIBUTE_47").equals(""))){
					pstUpdate.setString(40, rs.getString("ATTRIBUTE_47"));
				}else{
					pstUpdate.setString(40,null);
				}

				if(rs.getString("ATTRIBUTE_48")!=null && !(rs.getString("ATTRIBUTE_48").equals(""))){
					pstUpdate.setString(41, rs.getString("ATTRIBUTE_48"));
				}else{
					pstUpdate.setString(41,null);
				}

				if(rs.getDate("ATTRIBUTE_49")!=null && !(rs.getDate("ATTRIBUTE_49").equals(""))){
					pstUpdate.setDate(42, rs.getDate("ATTRIBUTE_49"));
				}else{
					pstUpdate.setDate(42,null);
				}

				if(rs.getDate("ATTRIBUTE_50")!=null && !(rs.getDate("ATTRIBUTE_50").equals(""))){
					pstUpdate.setDate(43, rs.getDate("ATTRIBUTE_50"));
				}else{
					pstUpdate.setDate(43,null);
				}

				if(rs.getDate("ATTRIBUTE_51")!=null && !(rs.getDate("ATTRIBUTE_51").equals(""))){
					pstUpdate.setDate(44, rs.getDate("ATTRIBUTE_51"));
				}else{
					pstUpdate.setDate(44,null);
				}

				if(rs.getDate("ATTRIBUTE_52")!=null && !(rs.getDate("ATTRIBUTE_52").equals(""))){
					pstUpdate.setDate(45, rs.getDate("ATTRIBUTE_52"));
				}else{
					pstUpdate.setDate(45,null);
				}

				if(rs.getDate("ATTRIBUTE_53")!=null && !(rs.getDate("ATTRIBUTE_53").equals(""))){
					pstUpdate.setDate(46, rs.getDate("ATTRIBUTE_53"));
				}else{
					pstUpdate.setDate(46,null);
				}

				pstUpdate.setLong(47, rs.getLong("ATTRIBUTE_54")); // Number
				pstUpdate.setLong(48, rs.getLong("ATTRIBUTE_55")); // Number
				pstUpdate.setLong(49, rs.getLong("ATTRIBUTE_56")); // Number
				pstUpdate.setLong(50, rs.getLong("ATTRIBUTE_57"));// Number

				if(rs.getString("ATTRIBUTE_58")!=null && !(rs.getString("ATTRIBUTE_58").equals(""))){
					pstUpdate.setString(51, rs.getString("ATTRIBUTE_58"));
				}else{
					pstUpdate.setString(51,null);
				}

				if(rs.getString("ATTRIBUTE_59")!=null && !(rs.getString("ATTRIBUTE_59").equals(""))){
					pstUpdate.setString(52, rs.getString("ATTRIBUTE_59"));
				}else{
					pstUpdate.setString(52,null);
				}

				if(rs.getString("ATTRIBUTE_60")!=null && !(rs.getString("ATTRIBUTE_60").equals(""))){
					pstUpdate.setString(53, rs.getString("ATTRIBUTE_60"));
				}else{
					pstUpdate.setString(53,null);
				}

				if(rs.getString("ATTRIBUTE_61")!=null && !(rs.getString("ATTRIBUTE_61").equals(""))){
					pstUpdate.setString(54, rs.getString("ATTRIBUTE_61"));
				}else{
					pstUpdate.setString(54,null);
				}

				if(rs.getString("ATTRIBUTE_62")!=null && !(rs.getString("ATTRIBUTE_62").equals(""))){
					pstUpdate.setString(55, rs.getString("ATTRIBUTE_62"));
				}else{
					pstUpdate.setString(55,null);
				}

				if(rs.getString("ATTRIBUTE_63")!=null && !(rs.getString("ATTRIBUTE_63").equals(""))){
					pstUpdate.setString(56, rs.getString("ATTRIBUTE_63"));
				}else{
					pstUpdate.setString(56,null);
				}

				if(rs.getString("ATTRIBUTE_64")!=null && !(rs.getString("ATTRIBUTE_64").equals(""))){
					pstUpdate.setString(57, rs.getString("ATTRIBUTE_64"));
				}else{
					pstUpdate.setString(57,null);
				}

				if(rs.getString("ATTRIBUTE_65")!=null && !(rs.getString("ATTRIBUTE_65").equals(""))){
					pstUpdate.setString(58, rs.getString("ATTRIBUTE_65"));
				}else{
					pstUpdate.setString(58,null);
				}

				if(rs.getString("ATTRIBUTE_66")!=null && !(rs.getString("ATTRIBUTE_66").equals(""))){
					pstUpdate.setString(59, rs.getString("ATTRIBUTE_66"));
				}else{
					pstUpdate.setString(59,null);
				}

				if(rs.getString("ATTRIBUTE_67")!=null && !(rs.getString("ATTRIBUTE_67").equals(""))){
					pstUpdate.setString(60, rs.getString("ATTRIBUTE_67"));
				}else{
					pstUpdate.setString(60,null);
				}

				if(rs.getString("ATTRIBUTE_68")!=null && !(rs.getString("ATTRIBUTE_68").equals(""))){
					pstUpdate.setString(61, rs.getString("ATTRIBUTE_68"));
				}else{
					pstUpdate.setString(61,null);
				}

				if(rs.getString("ATTRIBUTE_69")!=null && !(rs.getString("ATTRIBUTE_69").equals(""))){
					pstUpdate.setString(62, rs.getString("ATTRIBUTE_69"));
				}else{
					pstUpdate.setString(62,null);
				}

				if(rs.getString("ATTRIBUTE_70")!=null && !(rs.getString("ATTRIBUTE_70").equals(""))){
					pstUpdate.setString(63, rs.getString("ATTRIBUTE_70"));
				}else{
					pstUpdate.setString(63,null);
				}

				if(rs.getString("ATTRIBUTE_71")!=null && !(rs.getString("ATTRIBUTE_71").equals(""))){
					pstUpdate.setString(64, rs.getString("ATTRIBUTE_71"));
				}else{
					pstUpdate.setString(64,null);
				}

				if(rs.getString("ATTRIBUTE_72")!=null && !(rs.getString("ATTRIBUTE_72").equals(""))){
					pstUpdate.setString(65, rs.getString("ATTRIBUTE_72"));
				}else{
					pstUpdate.setString(65,null);
				}

				if(rs.getString("ATTRIBUTE_73")!=null && !(rs.getString("ATTRIBUTE_73").equals(""))){
					pstUpdate.setString(66, rs.getString("ATTRIBUTE_73"));
				}else{
					pstUpdate.setString(66,null);
				}

				if(rs.getString("ATTRIBUTE_74")!=null && !(rs.getString("ATTRIBUTE_74").equals(""))){
					pstUpdate.setString(67, rs.getString("ATTRIBUTE_74"));
				}else{
					pstUpdate.setString(67,null);
				}

				if(rs.getString("ATTRIBUTE_75")!=null && !(rs.getString("ATTRIBUTE_75").equals(""))){
					pstUpdate.setString(68, rs.getString("ATTRIBUTE_75"));
				}else{
					pstUpdate.setString(68,null);
				}

				if(rs.getString("ATTRIBUTE_76")!=null && !(rs.getString("ATTRIBUTE_76").equals(""))){
					pstUpdate.setString(69, rs.getString("ATTRIBUTE_76"));
				}else{
					pstUpdate.setString(69,null);
				}

				if(rs.getString("ATTRIBUTE_77")!=null && !(rs.getString("ATTRIBUTE_77").equals(""))){
					pstUpdate.setString(70, rs.getString("ATTRIBUTE_77"));
				}else{
					pstUpdate.setString(70,null);
				}

				if(rs.getString("ATTRIBUTE_78")!=null && !(rs.getString("ATTRIBUTE_78").equals(""))){
					pstUpdate.setString(71, rs.getString("ATTRIBUTE_78"));
				}else{
					pstUpdate.setString(71,null);
				}

				if(rs.getDate("ATTRIBUTE_79")!=null && !(rs.getDate("ATTRIBUTE_79").equals(""))){
					pstUpdate.setDate(72, rs.getDate("ATTRIBUTE_79"));
				}else{
					pstUpdate.setDate(72,null);
				}

				if(rs.getString("ATTRIBUTE_80")!=null && !(rs.getString("ATTRIBUTE_80").equals(""))){
					pstUpdate.setString(73, rs.getString("ATTRIBUTE_80"));
				}else{
					pstUpdate.setString(73,null);
				}

				if(rs.getDate("ATTRIBUTE_81")!=null && !(rs.getDate("ATTRIBUTE_81").equals(""))){
					pstUpdate.setDate(74, rs.getDate("ATTRIBUTE_81"));
				}else{
					pstUpdate.setDate(74,null);
				}

				if(rs.getString("ATTRIBUTE_82")!=null && !(rs.getString("ATTRIBUTE_82").equals(""))){
					pstUpdate.setString(75, rs.getString("ATTRIBUTE_82"));
				}else{
					pstUpdate.setString(75,null);
				}

				if(rs.getString("ATTRIBUTE_84")!=null && !(rs.getString("ATTRIBUTE_84").equals(""))){
					pstUpdate.setString(76, rs.getString("ATTRIBUTE_84"));
				}else{
					pstUpdate.setString(76,null);
				}

				if(rs.getString("ATTRIBUTE_85")!=null && !(rs.getString("ATTRIBUTE_85").equals(""))){
					pstUpdate.setString(77, rs.getString("ATTRIBUTE_85"));
				}else{
					pstUpdate.setString(77,null);
				}

				if(rs.getString("ATTRIBUTE_86")!=null && !(rs.getString("ATTRIBUTE_86").equals(""))){
					pstUpdate.setString(78, rs.getString("ATTRIBUTE_86"));
				}else{
					pstUpdate.setString(78,null);
				}

				if(rs.getString("ATTRIBUTE_87")!=null && !(rs.getString("ATTRIBUTE_87").equals(""))){
					pstUpdate.setString(79, rs.getString("ATTRIBUTE_87"));
				}else{
					pstUpdate.setString(79,null);
				}

				if(rs.getString("ATTRIBUTE_88")!=null && !(rs.getString("ATTRIBUTE_88").equals(""))){
					pstUpdate.setString(80, rs.getString("ATTRIBUTE_88"));
				}else{
					pstUpdate.setString(80,null);
				}

				if(rs.getString("ATTRIBUTE_89")!=null && !(rs.getString("ATTRIBUTE_89").equals(""))){
					pstUpdate.setString(81, rs.getString("ATTRIBUTE_89"));
				}else{
					pstUpdate.setString(81,null);
				}

				if(rs.getString("ATTRIBUTE_90")!=null && !(rs.getString("ATTRIBUTE_90").equals(""))){
					pstUpdate.setString(82, rs.getString("ATTRIBUTE_90"));
				}else{
					pstUpdate.setString(82,null);
				}

				if(rs.getString("ATTRIBUTE_91")!=null && !(rs.getString("ATTRIBUTE_91").equals(""))){
					pstUpdate.setString(83, rs.getString("ATTRIBUTE_91"));
				}else{
					pstUpdate.setString(83,null);
				}

				if(rs.getString("ATTRIBUTE_92")!=null && !(rs.getString("ATTRIBUTE_92").equals(""))){
					pstUpdate.setString(84, rs.getString("ATTRIBUTE_92"));
				}else{
					pstUpdate.setString(84,null);
				}

				if(rs.getString("ATTRIBUTE_93")!=null && !(rs.getString("ATTRIBUTE_93").equals(""))){
					pstUpdate.setString(85, rs.getString("ATTRIBUTE_93"));
				}else{
					pstUpdate.setString(85,null);
				}

				if(rs.getString("ATTRIBUTE_94")!=null && !(rs.getString("ATTRIBUTE_94").equals(""))){
					pstUpdate.setString(86, rs.getString("ATTRIBUTE_94"));
				}else{
					pstUpdate.setString(86,null);
				}

				if(rs.getString("ATTRIBUTE_95")!=null && !(rs.getString("ATTRIBUTE_95").equals(""))){
					pstUpdate.setString(87, rs.getString("ATTRIBUTE_95"));
				}else{
					pstUpdate.setString(87,null);
				}

				if(rs.getString("ATTRIBUTE_96")!=null && !(rs.getString("ATTRIBUTE_96").equals(""))){
					pstUpdate.setString(88, rs.getString("ATTRIBUTE_96"));
				}else{
					pstUpdate.setString(88,null);
				}

				if(rs.getString("ATTRIBUTE_97")!=null && !(rs.getString("ATTRIBUTE_97").equals(""))){
					pstUpdate.setString(89, rs.getString("ATTRIBUTE_97"));
				}else{
					pstUpdate.setString(89,null);
				}

				if(rs.getString("ATTRIBUTE_98")!=null && !(rs.getString("ATTRIBUTE_98").equals(""))){
					pstUpdate.setString(90, rs.getString("ATTRIBUTE_98"));
				}else{
					pstUpdate.setString(90,null);
				}

				if(rs.getString("ATTRIBUTE_99")!=null && !(rs.getString("ATTRIBUTE_99").equals(""))){
					pstUpdate.setString(91, rs.getString("ATTRIBUTE_99"));
				}else{
					pstUpdate.setString(91,null);
				}

				if(rs.getString("ATTRIBUTE_100")!=null && !(rs.getString("ATTRIBUTE_100").equals(""))){
					pstUpdate.setString(92, rs.getString("ATTRIBUTE_100"));
				}else{
					pstUpdate.setString(92,null);
				}

				if(rs.getString("ATTRIBUTE_101")!=null && !(rs.getString("ATTRIBUTE_101").equals(""))){
					pstUpdate.setString(93, rs.getString("ATTRIBUTE_101"));
				}else{
					pstUpdate.setString(93,null);
				}

				if(rs.getString("ATTRIBUTE_105")!=null && !(rs.getString("ATTRIBUTE_105").equals(""))){
					pstUpdate.setString(94, rs.getString("ATTRIBUTE_105"));
				}else{
					pstUpdate.setString(94,null);
				}

				if(rs.getString("ATTRIBUTE_106")!=null && !(rs.getString("ATTRIBUTE_106").equals(""))){
					pstUpdate.setString(95, rs.getString("ATTRIBUTE_106"));
				}else{
					pstUpdate.setString(95,null);
				}

				if(rs.getString("ATTRIBUTE_107")!=null && !(rs.getString("ATTRIBUTE_107").equals(""))){
					pstUpdate.setString(96, rs.getString("ATTRIBUTE_107"));
				}else{
					pstUpdate.setString(96,null);
				}

				if(rs.getString("ATTRIBUTE_108")!=null && !(rs.getString("ATTRIBUTE_108").equals(""))){
					pstUpdate.setString(97, rs.getString("ATTRIBUTE_108"));
				}else{
					pstUpdate.setString(97,null);
				}

				if(rs.getString("ATTRIBUTE_109")!=null && !(rs.getString("ATTRIBUTE_109").equals(""))){
					pstUpdate.setString(98, rs.getString("ATTRIBUTE_109"));
				}else{
					pstUpdate.setString(98,null);
				}

				if(rs.getString("ATTRIBUTE_110")!=null && !(rs.getString("ATTRIBUTE_110").equals(""))){
					pstUpdate.setString(99, rs.getString("ATTRIBUTE_110"));
				}else{
					pstUpdate.setString(99,null);
				}

				if(rs.getString("ATTRIBUTE_111")!=null && !(rs.getString("ATTRIBUTE_111").equals(""))){
					pstUpdate.setString(100, rs.getString("ATTRIBUTE_111"));
				}else{
					pstUpdate.setString(100,null);
				}

				if(rs.getString("ATTRIBUTE_112")!=null && !(rs.getString("ATTRIBUTE_112").equals(""))){
					pstUpdate.setString(101, rs.getString("ATTRIBUTE_112"));
				}else{
					pstUpdate.setString(101,null);
				}

				if(rs.getString("ATTRIBUTE_113")!=null && !(rs.getString("ATTRIBUTE_113").equals(""))){
					pstUpdate.setString(102, rs.getString("ATTRIBUTE_113"));
				}else{
					pstUpdate.setString(102,null);
				}

				if(rs.getDate("ATTRIBUTE_117")!=null && !(rs.getDate("ATTRIBUTE_117").equals(""))){
					pstUpdate.setDate(103, rs.getDate("ATTRIBUTE_117"));
				}else{
					pstUpdate.setDate(103,null);
				}

				pstUpdate.setLong(104, rs.getLong("ATTRIBUTE_118"));// Number

				if(rs.getDate("ATTRIBUTE_114")!=null && !(rs.getDate("ATTRIBUTE_114").equals(""))){
					pstUpdate.setDate(105, rs.getDate("ATTRIBUTE_114"));
				}else{
					pstUpdate.setDate(105,null);
				}

				if(rs.getDate("ATTRIBUTE_115")!=null && !(rs.getDate("ATTRIBUTE_115").equals(""))){
					pstUpdate.setDate(106, rs.getDate("ATTRIBUTE_115"));
				}else{
					pstUpdate.setDate(106,null);
				}

				if(rs.getString("ATTRIBUTE_116")!=null && !(rs.getString("ATTRIBUTE_116").equals(""))){
					pstUpdate.setString(107, rs.getString("ATTRIBUTE_116"));
				}else{
					pstUpdate.setString(107,null);
				}

				if(rs.getString("ATTRIBUTE_119")!=null && !(rs.getString("ATTRIBUTE_119").equals(""))){
					pstUpdate.setString(108, rs.getString("ATTRIBUTE_119"));
				}else{
					pstUpdate.setString(108,null);
				}

				if(rs.getString("ATTRIBUTE_120")!=null && !(rs.getString("ATTRIBUTE_120").equals(""))){
					pstUpdate.setString(109, rs.getString("ATTRIBUTE_120"));
				}else{
					pstUpdate.setString(109,null);
				}


				if(rs.getString("ATTRIBUTE_121")!=null && !(rs.getString("ATTRIBUTE_121").equals(""))){
					pstUpdate.setString(110, rs.getString("ATTRIBUTE_121"));
				}else{
					pstUpdate.setString(110,null);
				}

				if(rs.getString("ATTRIBUTE_122")!=null && !(rs.getString("ATTRIBUTE_122").equals(""))){
					pstUpdate.setString(111, rs.getString("ATTRIBUTE_122"));
				}else{
					pstUpdate.setString(111, null);
				}

				if(rs.getString("ATTRIBUTE_123")!=null && !(rs.getString("ATTRIBUTE_123").equals(""))){
					pstUpdate.setString(112, rs.getString("ATTRIBUTE_123"));
				}else{
					pstUpdate.setString(112,null);
				}
				pstUpdate.setString(113, serialNoOut);

				boolean status = pstUpdate.execute();

				logger.info(" After Shipment Table Insert SQL:"+SQLInnerQuery.toString());

				//Update SwapDate in MEID Table.
				pstUpdate = null;
				String serialMEIDupdate="update upd.upd_pcba_pgm_meid set SWAP_DATE=sysdate,OLD_SERIAL_NO='"+serialNoIn+"' where serial_no='"+serialNoOut+"'";
				pstUpdate = connection2.prepareStatement(serialMEIDupdate);
				pstUpdate.execute();

				logger.info("serialMEIDupdate SQL:"+serialMEIDupdate);

				//Update swapDate in IMEI Table 
				pstUpdate = null;
				String serialIMEIupdate="update upd.upd_pcba_pgm_imei set SWAP_DATE=sysdate,OLD_SERIAL_NO='"+serialNoIn+"' where serial_no='"+serialNoOut+"'";
				pstUpdate = connection2.prepareStatement(serialIMEIupdate);
				pstUpdate.execute();

				logger.info("serialIMEIupdate SQL:"+serialIMEIupdate);

				if (!status) {
					// commented for pcba july 2015 release	
					//String updateOldserialNOStatus = "update upd.UPD_SN_REPOS set ATTRIBUTE_34='"+lockCode+"SCR',ATTRIBUTE_37='SCR       "
					String updateOldserialNOStatus = "update upd.UPD_SN_REPOS set ATTRIBUTE_34='"+lockCode+"SCR',ATTRIBUTE_37='VOI       "
							+ DateToStr
							+ "',ATTRIBUTE_41='"+serialNoOut+"',LAST_MOD_BY='pcba_pgm_SwapUpdate',LAST_MOD_DATETIME=sysdate where serial_no='"
							+ serialNoIn
							+ "'";
					pstmt1 = connection2
							.prepareStatement(updateOldserialNOStatus);
					pstmt1.execute();

					logger.info("Status update Shipment Table Insert SQL:"+updateOldserialNOStatus);

				}

				response.setResponseCode(ServiceMessageCodes.OLD_SN_SUCCESS);
				response.setResponseMessage(ServiceMessageCodes.OLD_SERIAL_FOUND_SUCCSS_MSG);


			} else {

				response.setResponseCode(""+ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND_IN_SHIPMENT_TABLE);
				response.setResponseMessage(ServiceMessageCodes.OLD_SERIAL_NO_NOT_FOUND_IN_SHIPMENT_TABLE_MSG);				

			}

		} catch (Exception e) {
			// innerupdatecon.rollback();
			e.printStackTrace();
			throw e;

		} finally {
			DBUtil.closeConnection(innerselectcon, pst, rs);
			if (pstUpdate != null) {
				pstUpdate.close();
			}
		}

		return connection2;
	}

	public Connection updateReferenceTable(String serialNoIn,
			String serialNoOut, Connection con) throws SQLException, Exception {
		Connection innerselectcon = null;
		PreparedStatement pst = null;
		PreparedStatement pstUpdate = null;
		ResultSet rs = null;

		StringBuffer sbuffer = new StringBuffer();
		StringBuffer SQLInnerQuery = new StringBuffer();
		sbuffer.append("SELECT SERIAL_NO,REFERENCE_KEY,STATUS FROM upd.upd_sn_repos_ref WHERE SERIAL_NO=? AND STATUS IS NULL");
		try {
			innerselectcon = DBUtil.getConnection(ds);
			pst = innerselectcon.prepareStatement(sbuffer.toString());
			pst.setString(1, serialNoIn);
			rs = pst.executeQuery();

			logger.info("Reading Data Inside Shipment table(Status) SQL:"+sbuffer.toString());


			if (rs.next()) {

				SQLInnerQuery
				.append("INSERT INTO upd.upd_sn_repos_ref (SERIAL_NO,REFERENCE_KEY,CREATED_BY,CREATION_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME) VALUES (?,?,'pcba_pgm_SwapUpdate',sysdate,'pcba_pgm',sysdate)");
				pstUpdate = con.prepareStatement(SQLInnerQuery.toString());
				pstUpdate.setString(1, serialNoOut);
				pstUpdate.setString(2, rs.getString("REFERENCE_KEY"));
				boolean status = pstUpdate.execute();

				logger.info("Update Status SQL:"+SQLInnerQuery.toString());

				if (!status) {
					// Commentted code for pcba july 2015 release
					//String updateOldserialNOStatus = "update upd.upd_sn_repos_ref set STATUS='SCR'  where serial_no='"
					String updateOldserialNOStatus = "update upd.upd_sn_repos_ref set STATUS='VOI'  where serial_no='"
							+ serialNoIn + "'";
					pstmt1 = con.prepareStatement(updateOldserialNOStatus);
					pstmt1.execute();

					logger.info("SQL Status:"+updateOldserialNOStatus);
				}

			} else {
				throw new Exception("Serial not found in UPD_SN_REPO_REF table");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw e;
		}finally{
			DBUtil.closeConnection(innerselectcon, pst, rs);
			if(pstUpdate!=null){
				pstUpdate.close();
			}
		}

		return con;
	}

	@Override
	public int checkValidSerialNoIn(String SerialNoIn) {
		// TODO Auto-generated method stub
		Connection conn1=null;
		Connection conn2=null;
		PreparedStatement pstmt1=null;
		PreparedStatement pstmt2=null;
		ResultSet rs1=null;
		ResultSet rs2=null;
		int referenceKeyCount =0;
		// TODO Auto-generated method stub
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:" + e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG+e.getMessage());

		}

		try {
			// get database connection
			conn1 = DBUtil.getConnection(ds);
			String query="select Attribute_99 from upd.UPD_SN_REPOS where serial_no=?";
			pstmt1 = conn1.prepareStatement(query);
			pstmt1.setString(1,SerialNoIn);
			rs1 = pstmt1.executeQuery();

			logger.info("Status of Serial No:"+query);

			String referenceKey = null;
			String referenceKeyQuery="select count(*) from upd.upd_sn_repos_ref where status is null and reference_key=?";

			if (rs1.next()) {
				referenceKey=rs1.getString("Attribute_99");
				if(referenceKey!=null && !(referenceKey.equals(""))){

					conn2 = DBUtil.getConnection(ds);
					pstmt2 = conn2.prepareStatement(referenceKeyQuery);
					pstmt2.setString(1, referenceKey);
					rs2 = pstmt2.executeQuery();

					logger.info("Reading Count of Serial No:"+referenceKeyQuery);

					if(rs2.next()){
						referenceKeyCount = rs2.getInt(1);
					}


				}
			}

		}catch(Exception e){
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG+e.getMessage());

		}finally{
			DBUtil.closeConnection(conn1, pstmt1, rs1);
			DBUtil.closeConnection(conn2, pstmt2,rs2);
		}
		return referenceKeyCount;
	}

	public String getStatus(String serialNoIn){

		Connection connection1=null;
		PreparedStatement preparedStatement1=null;
		ResultSet resultSet1=null;
		String status=null;
		// TODO Auto-generated method stub
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:" + e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG+e.getMessage());

		}

		try {
			// get database connection
			connection1 = DBUtil.getConnection(ds);
			String query="select ATTRIBUTE_37 from upd.UPD_SN_REPOS where serial_no=?";
			preparedStatement1 = connection1.prepareStatement(query);
			preparedStatement1.setString(1,serialNoIn);
			resultSet1 = preparedStatement1.executeQuery();
			if(resultSet1.next()){
				status =resultSet1.getString("ATTRIBUTE_37");
			}

			logger.info("Status of Serial No in Shipment:"+query);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBUtil.closeConnection(connection1, preparedStatement1, resultSet1);
		}
		return status;
	}

	public Connection sendEmail(String serialNoIn,String serialNoOut,Connection innerselectcon,PreparedStatement prestmt){
		try{

			StringBuffer stb = new StringBuffer();
			stb.append("insert into upd.shipment_notavail_sn(SERIAL_NO_IN,SERIAL_NO_OUT,CREATED_BY,CREATION_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,STATUS) values(?,?,?,?,?,?,?)");
			prestmt = innerselectcon.prepareStatement(stb.toString());
			prestmt.setString(1,serialNoIn);
			prestmt.setString(2,serialNoOut);
			prestmt.setString(3, "PCBA_PGM");
			prestmt.setDate(4,
					new java.sql.Date(System.currentTimeMillis()));
			prestmt.setString(5, "PCBA_PGM");
			prestmt.setDate(6,
					new java.sql.Date(System.currentTimeMillis()));
			prestmt.setString(7, "S");
			prestmt.execute();

			logger.info(" SQL Query:"+stb);

			MailUtil.sendEmail(serialNoIn,serialNoOut);


		}catch(Exception e){
			e.printStackTrace();
		}	
		return innerselectcon;
	}

	// Start July 08 2015

	@Override
	public PCBASerialNoUPdateResponse validateGppId(String serialNoIn,String serialNoOut) {
		// TODO Auto-generated method stub

		Connection connection1=null;
		PreparedStatement preparedStatement1=null;
		ResultSet resultSet1=null;
		Connection connection2=null;
		PreparedStatement preparedStatement2=null;
		ResultSet resultSet2=null;
		String serialNoInGppId=null;
		String serialNoOutGppId=null;

		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:" + e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}

		try {

			// get database connection
			connection1 = DBUtil.getConnection(ds);
			String query="select ATTRIBUTE_120 from upd.UPD_SN_REPOS where serial_no=?";
			preparedStatement1 = connection1.prepareStatement(query);
			preparedStatement1.setString(1,serialNoIn);
			resultSet1 = preparedStatement1.executeQuery();
			//System.out.println("query:"+query);
			if(resultSet1.next()){
				serialNoInGppId =resultSet1.getString("ATTRIBUTE_120");
			}

			//System.out.println("query:"+query);

			connection2 = DBUtil.getConnection(ds);
			String query1="select ATTRIBUTE_120 from upd.UPD_SN_REPOS where serial_no=?";
			preparedStatement2 = connection2.prepareStatement(query1);
			preparedStatement2.setString(1,serialNoOut);
			resultSet2 = preparedStatement2.executeQuery();
			//System.out.println("query1:"+query1);
			if(resultSet2.next()){
				serialNoOutGppId =resultSet2.getString("ATTRIBUTE_120");
			}

			if((serialNoInGppId!=null && !serialNoInGppId.equals("")) && (serialNoOutGppId != null && !serialNoOutGppId.equals(""))){
				if(serialNoInGppId.equals(serialNoOutGppId)){
					// Continue i.e serial_In and Serial_Out Having same GPPD_ID
					response.setResponseCode(null);
					response.setResponseMessage(null);
				}else{
					sendAnEmail(serialNoIn,serialNoOut,ServiceMessageCodes.GPP_ID_EMAIL_MSG_CODE,ServiceMessageCodes.GPP_ID_EMAIL_MSG);
					response.setResponseCode(ServiceMessageCodes.GPP_ID_EMAIL_MSG_CODE);
					response.setResponseMessage(ServiceMessageCodes.GPP_ID_EMAIL_MSG);
				}

			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG+ e.getMessage());
		}finally{
			DBUtil.closeConnection(connection1, preparedStatement1, resultSet1);
			DBUtil.closeConnection(connection2, preparedStatement2, resultSet2);
		}
		return response;	
	}

	@Override
	public PCBASerialNoUPdateResponse validateProtocol(String serialNoIn,String serialNoOut) {
		// TODO Auto-generated method stub

		Connection connection3=null;
		PreparedStatement preparedStatement3=null;
		ResultSet resultSet3=null;
		Connection connection4=null;
		PreparedStatement preparedStatement4=null;
		ResultSet resultSet4=null;
		String serialNoInProtocol=null;
		String serialNoOutProtocol=null;

		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:" + e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}

		try {

			// get database connection
			connection3 = DBUtil.getConnection(ds);
			String query="select PROTOCOL from UPD.UPD_MEID where serial_no=?";
			preparedStatement3 = connection3.prepareStatement(query);
			preparedStatement3.setString(1,serialNoIn);
			resultSet3 = preparedStatement3.executeQuery();

			if(resultSet3.next()){
				serialNoInProtocol =resultSet3.getString("PROTOCOL");
			}

			connection4 = DBUtil.getConnection(ds);
			String query1="select PROTOCOL from UPD.UPD_MEID where serial_no=?";
			preparedStatement4 = connection4.prepareStatement(query1);
			preparedStatement4.setString(1,serialNoOut);
			resultSet4 = preparedStatement4.executeQuery();

			if(resultSet4.next()){
				serialNoOutProtocol =resultSet4.getString("PROTOCOL");
			}

			if((serialNoInProtocol!=null && !serialNoInProtocol.equals("")) && (serialNoOutProtocol != null && !serialNoOutProtocol.equals(""))){
				if(serialNoInProtocol.equals(serialNoOutProtocol)){
					// Continue i.e serial_In and Serial_Out Having same PROTOCOL
					response.setResponseCode(null);
					response.setResponseMessage(null);
				}else{
					sendAnEmail(serialNoIn,serialNoOut,ServiceMessageCodes.PROTOCOL_EMAIL_MSG_CODE,ServiceMessageCodes.PROTOCOL_EMAIL_MSG);
					response.setResponseCode(ServiceMessageCodes.PROTOCOL_EMAIL_MSG_CODE);
					response.setResponseMessage(ServiceMessageCodes.PROTOCOL_EMAIL_MSG);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG+ e.getMessage());
		}finally{
			DBUtil.closeConnection(connection3, preparedStatement3, resultSet3);
			DBUtil.closeConnection(connection4, preparedStatement4, resultSet4);
		}
		return response;	
	} 

	@Override
	public PCBASerialNoUPdateResponse calculateNoOfMACAddressForNormalCase(String serialNoIn, String serialNoOut) {
		// TODO Auto-generated method stub

		Connection connection5=null;
		PreparedStatement preparedStatement5=null;
		ResultSet resultSet5=null;
		Connection connection6=null;
		PreparedStatement preparedStatement6=null;
		ResultSet resultSet6=null;

		String serialNoInUlma=null;
		String serialNoInWlan=null;
		String serialNoInwlan2=null;
		String serialNoInwlan3=null;
		String serialNoInwlan4=null;

		String serialNoOutUlma=null;
		String serialNoOutWlan=null;
		String serialNoOutwlan2=null;
		String serialNoOutwlan3=null;
		String serialNoOutwlan4=null;
		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:" + e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}

		try {

			// get database connection
			connection5 = DBUtil.getConnection(ds);
			String query="select attribute_36,attribute_83,attribute_102,attribute_103,attribute_104 from UPD.upd_sn_repos where serial_no=?";
			preparedStatement5 = connection5.prepareStatement(query);
			preparedStatement5.setString(1,serialNoIn);
			resultSet5 = preparedStatement5.executeQuery();
			int count1=0;
			int count2=0;
			if(resultSet5.next()){
				serialNoInUlma = resultSet5.getString("attribute_36");
				serialNoInWlan = resultSet5.getString("attribute_83");
				serialNoInwlan2= resultSet5.getString("attribute_102");
				serialNoInwlan3= resultSet5.getString("attribute_103");
				serialNoInwlan4= resultSet5.getString("attribute_104");

				if(serialNoInUlma!=null && !serialNoInUlma.equals("")){
					count1++;
				}
				if(serialNoInWlan!=null && !serialNoInWlan.equals("")){
					count1++;
				}
				if(serialNoInwlan2!=null && !serialNoInwlan2.equals("")){
					count1++;
				}
				if(serialNoInwlan3!=null && !serialNoInwlan3.equals("")){
					count1++;
				}
				if(serialNoInwlan4!=null && !serialNoInwlan4.equals("")){
					count1++;
				}
				System.out.println("count1:"+count1);
			}

			connection6 = DBUtil.getConnection(ds);
			String query1="select attribute_36,attribute_83,attribute_102,attribute_103,attribute_104 from UPD.upd_sn_repos where serial_no=?";
			preparedStatement6 = connection6.prepareStatement(query1);
			preparedStatement6.setString(1,serialNoOut);
			resultSet6 = preparedStatement6.executeQuery();

			if(resultSet6.next()){

				serialNoOutUlma = resultSet6.getString("attribute_36");
				serialNoOutWlan = resultSet6.getString("attribute_83");
				serialNoOutwlan2= resultSet6.getString("attribute_102");
				serialNoOutwlan3= resultSet6.getString("attribute_103");
				serialNoOutwlan4= resultSet6.getString("attribute_104");

				if(serialNoOutUlma!=null && !serialNoOutUlma.equals("")){
					count2++;
				}
				if(serialNoOutWlan!=null && !serialNoOutWlan.equals("")){
					count2++;
				}
				if(serialNoOutwlan2!=null && !serialNoOutwlan2.equals("")){
					count2++;
				}
				if(serialNoOutwlan3!=null && !serialNoOutwlan3.equals("")){
					count2++;
				}if(serialNoOutwlan4!=null && !serialNoOutwlan4.equals("")){
					count2++;
				}
				System.out.println("count2:"+count2);
			}

			if(count1 == count2){
				// Continue i.e serial_In and Serial_Out Having same calculateNoOfULMA/MAC Address
				response.setResponseCode(null);
				response.setResponseMessage(null);			

			}else{

				sendAnEmail(serialNoIn,serialNoOut,ServiceMessageCodes.MAC_ULMA_EMAIL_MSG_CODE,ServiceMessageCodes.MAC_ULMA_EMAIL_MSG);
				response.setResponseCode(ServiceMessageCodes.MAC_ULMA_EMAIL_MSG_CODE);
				response.setResponseMessage(ServiceMessageCodes.MAC_ULMA_EMAIL_MSG);			

			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG+ e.getMessage());
		}finally{
			DBUtil.closeConnection(connection5, preparedStatement5, resultSet5);
			DBUtil.closeConnection(connection6, preparedStatement6, resultSet6);
		}
		return response;	
	}
	@Override
	public PCBASerialNoUPdateResponse calculateNoOfMACAddress(String serialNoOut, String dualSerialNoOut, String triSerialNoOut) {
		// TODO Auto-generated method stub

		Connection conn1=null;
		PreparedStatement prestmt1=null;
		ResultSet rs1=null;

		Connection conn2=null;
		PreparedStatement prestmt2=null;
		ResultSet rs2=null;

		Connection conn3=null;
		PreparedStatement prestmt3=null;
		ResultSet rs3=null;

		String serialNoOutUlma=null;
		String serialNoOutWlan=null;
		String serialNoOutwlan2=null;
		String serialNoOutwlan3=null;
		String serialNoOutwlan4=null;

		String dualSerialNoOutUlma=null;
		String dualSerialNoOutWlan=null;
		String dualSerialNoOutwlan2=null;
		String dualSerialNoOutwlan3=null;
		String dualSerialNoOutwlan4=null;

		String triSerialNoOutUlma=null;
		String triSerialNoOutWlan=null;
		String triSerialNoOutwlan2=null;
		String triSerialNoOutwlan3=null;
		String triSerialNoOutwlan4=null;


		try {

			ds = DBUtil.getOracleDataSource();
		} catch (NamingException e) {
			logger.info("Data source not found in MEID:" + e);
			response.setResponseCode(""+ServiceMessageCodes.NO_DATASOURCE_FOUND);
			response.setResponseMessage(ServiceMessageCodes.NO_DATASOURCE_FOUND_FOR_SERIAL_NO_MSG);
			return response;
		}

		try {

			// get database connection
			conn1 = DBUtil.getConnection(ds);
			String query="select attribute_36,attribute_83,attribute_102,attribute_103,attribute_104 from UPD.upd_sn_repos where serial_no=?";
			prestmt1 = conn1.prepareStatement(query);
			prestmt1.setString(1,serialNoOut);
			rs1 = prestmt1.executeQuery();
			int count1=0;
			int count2=0;
			int count3=0;

			if(rs1.next()){
				serialNoOutUlma = rs1.getString("attribute_36");
				serialNoOutWlan = rs1.getString("attribute_83");
				serialNoOutwlan2= rs1.getString("attribute_102");
				serialNoOutwlan3= rs1.getString("attribute_103");
				serialNoOutwlan4= rs1.getString("attribute_104");

				if(serialNoOutUlma!=null && !serialNoOutUlma.equals("")){
					count1++;
				}
				if(serialNoOutWlan!=null && !serialNoOutWlan.equals("")){
					count1++;
				}
				if(serialNoOutwlan2!=null && !serialNoOutwlan2.equals("")){
					count1++;
				}
				if(serialNoOutwlan3!=null && !serialNoOutwlan3.equals("")){
					count1++;
				}
				if(serialNoOutwlan4!=null && !serialNoOutwlan4.equals("")){
					count1++;
				}
				//System.out.println("count1:"+count1);
			}

			conn2 = DBUtil.getConnection(ds);
			String query1="select attribute_36,attribute_83,attribute_102,attribute_103,attribute_104 from UPD.upd_sn_repos where serial_no=?";
			prestmt2 = conn2.prepareStatement(query1);
			prestmt2.setString(1,dualSerialNoOut);
			rs2 = prestmt2.executeQuery();

			if(rs2.next()){

				dualSerialNoOutUlma = rs2.getString("attribute_36");
				dualSerialNoOutWlan = rs2.getString("attribute_83");
				dualSerialNoOutwlan2= rs2.getString("attribute_102");
				dualSerialNoOutwlan3= rs2.getString("attribute_103");
				dualSerialNoOutwlan4= rs2.getString("attribute_104");

				if(dualSerialNoOutUlma!=null && !dualSerialNoOutUlma.equals("")){
					count2++;
				}
				if(dualSerialNoOutWlan!=null && !dualSerialNoOutWlan.equals("")){
					count2++;
				}
				if(dualSerialNoOutwlan2!=null && !dualSerialNoOutwlan2.equals("")){
					count2++;
				}
				if(dualSerialNoOutwlan3!=null && !dualSerialNoOutwlan3.equals("")){
					count2++;
				}if(dualSerialNoOutwlan4!=null && !dualSerialNoOutwlan4.equals("")){
					count2++;
				}
				//System.out.println("count2:"+count2);
			}

			String UpdateQuery="update UPD.upd_sn_repos set attribute_36=?,attribute_83=?,attribute_102=?,attribute_103=?,attribute_104=? where serial_no=?";

			if(triSerialNoOut!=null && !triSerialNoOut.equals("")){
				conn3 = DBUtil.getConnection(ds);
				String query2="select attribute_36,attribute_83,attribute_102,attribute_103,attribute_104 from UPD.upd_sn_repos where serial_no=?";
				prestmt3 = conn3.prepareStatement(query2);
				prestmt3.setString(1,triSerialNoOut);
				rs3 = prestmt3.executeQuery();
				//System.out.println("Query2:"+query2);
				if(rs3.next()){

					triSerialNoOutUlma = rs3.getString("attribute_36");
					triSerialNoOutWlan = rs3.getString("attribute_83");
					triSerialNoOutwlan2= rs3.getString("attribute_102");
					triSerialNoOutwlan3= rs3.getString("attribute_103");
					triSerialNoOutwlan4= rs3.getString("attribute_104");

					if(triSerialNoOutUlma!=null && !triSerialNoOutUlma.equals("")){
						count3++;
					}
					if(triSerialNoOutWlan!=null && !triSerialNoOutWlan.equals("")){
						count3++;
					}
					if(triSerialNoOutwlan2!=null && !triSerialNoOutwlan2.equals("")){
						count3++;
					}
					if(triSerialNoOutwlan3!=null && !triSerialNoOutwlan3.equals("")){
						count3++;
					}if(triSerialNoOutwlan4!=null && !triSerialNoOutwlan4.equals("")){
						count3++;
					}
					//System.out.println("count3:"+count3);
				}	

				if(count1== count2 && count2== count3){
					// Continue i.e Serial_Out,dualSerial_Out and triSerial_Out Having same calculateNoOfULMA/MAC Address
					response.setResponseCode(null);
					response.setResponseMessage(null);
				}else if(count1<count2){

					conn1 = DBUtil.getConnection(ds);
					prestmt1 = conn1.prepareStatement(UpdateQuery);
					prestmt1.setString(1,dualSerialNoOutUlma);
					prestmt1.setString(2,dualSerialNoOutWlan);
					prestmt1.setString(3,dualSerialNoOutwlan2);
					prestmt1.setString(4,dualSerialNoOutwlan3);
					prestmt1.setString(5,dualSerialNoOutwlan4);
					prestmt1.setString(6,serialNoOut);
					prestmt1.execute();

					sendAnEmailForULMAorMACKAddressNotMatch(serialNoOut,dualSerialNoOut,null,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);
					response.setResponseCode(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE);
					response.setResponseMessage(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);

				}else if(count2<count3){
					conn2 = DBUtil.getConnection(ds);
					prestmt2 = conn2.prepareStatement(UpdateQuery);
					prestmt2.setString(1,triSerialNoOutUlma);
					prestmt2.setString(2,triSerialNoOutWlan);
					prestmt2.setString(3,triSerialNoOutwlan2);
					prestmt2.setString(4,triSerialNoOutwlan3);
					prestmt2.setString(5,triSerialNoOutwlan4);
					prestmt2.setString(6,dualSerialNoOut);
					prestmt2.execute();

					sendAnEmailForULMAorMACKAddressNotMatch(null,dualSerialNoOut,triSerialNoOut,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);
					response.setResponseCode(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE);
					response.setResponseMessage(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);


				}else if(count3<count1){
					conn3 = DBUtil.getConnection(ds);
					prestmt3 = conn3.prepareStatement(UpdateQuery);
					prestmt3.setString(1,serialNoOutUlma);
					prestmt3.setString(2,serialNoOutWlan);
					prestmt3.setString(3,serialNoOutwlan2);
					prestmt3.setString(4,serialNoOutwlan3);
					prestmt3.setString(5,serialNoOutwlan4);
					prestmt3.setString(6,triSerialNoOut);
					prestmt3.execute();

					sendAnEmailForULMAorMACKAddressNotMatch(serialNoOut,null,triSerialNoOut,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);
					response.setResponseCode(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE);
					response.setResponseMessage(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);


				}else if(count3<count2){

					Connection conn4=null;
					PreparedStatement prestmt4=null;
					try{
						conn4 = DBUtil.getConnection(ds);
						prestmt4 = conn4.prepareStatement(UpdateQuery);
						prestmt4.setString(1,dualSerialNoOutUlma);
						prestmt4.setString(2,dualSerialNoOutWlan);
						prestmt4.setString(3,dualSerialNoOutwlan2);
						prestmt4.setString(4,dualSerialNoOutwlan3);
						prestmt4.setString(5,dualSerialNoOutwlan4);
						prestmt4.setString(6,triSerialNoOut);
						prestmt4.execute();

						sendAnEmailForULMAorMACKAddressNotMatch(null,dualSerialNoOut,triSerialNoOut,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);
						response.setResponseCode(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE);
						response.setResponseMessage(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);

					}catch(Exception e){
						e.printStackTrace();
						logger.error(e.getMessage());
						response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
						response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG+ e.getMessage());

					}finally{
						DBUtil.connectionClosed(conn4,prestmt4);
					}



				}else if(count2<count1){

					Connection conn5=null;
					PreparedStatement prestmt5=null;
					try{						
						conn5 = DBUtil.getConnection(ds);
						prestmt5 = conn5.prepareStatement(UpdateQuery);
						prestmt5.setString(1,serialNoOutUlma);
						prestmt5.setString(2,serialNoOutWlan);
						prestmt5.setString(3,serialNoOutwlan2);
						prestmt5.setString(4,serialNoOutwlan3);
						prestmt5.setString(5,serialNoOutwlan4);
						prestmt5.setString(6,dualSerialNoOut);
						prestmt5.execute();

						sendAnEmailForULMAorMACKAddressNotMatch(serialNoOut,dualSerialNoOut,null,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);
						response.setResponseCode(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE);
						response.setResponseMessage(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);

					}catch(Exception e){
						e.printStackTrace();
						logger.error(e.getMessage());
						response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
						response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG+ e.getMessage());

					}finally{
						DBUtil.connectionClosed(conn5,prestmt5);
					}
				}else if(count1<count3){
					Connection conn6=null;
					PreparedStatement prestmt6=null;
					try{
						conn6 = DBUtil.getConnection(ds);
						prestmt6 = conn6.prepareStatement(UpdateQuery);
						prestmt6.setString(1,triSerialNoOutUlma);
						prestmt6.setString(2,triSerialNoOutWlan);
						prestmt6.setString(3,triSerialNoOutwlan2);
						prestmt6.setString(4,triSerialNoOutwlan3);
						prestmt6.setString(5,triSerialNoOutwlan4);
						prestmt6.setString(6,serialNoOut);
						prestmt6.execute();

						sendAnEmailForULMAorMACKAddressNotMatch(serialNoOut,null,triSerialNoOut,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);
						response.setResponseCode(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE);
						response.setResponseMessage(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);

					}catch(Exception e){
						e.printStackTrace();
						logger.error(e.getMessage());
						response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
						response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG+ e.getMessage());

					}finally{
						DBUtil.connectionClosed(conn6,prestmt6);
					}


				}

			} else if(count1 == count2){
				// Continue i.e Serial_Out and dualSerial_Out Having same calculateNoOfULMA/MAC Address
				response.setResponseCode(null);
				response.setResponseMessage(null);
			}else if(count1>count2){
				conn1 = DBUtil.getConnection(ds);
				prestmt1 = conn1.prepareStatement(UpdateQuery);
				prestmt1.setString(1,serialNoOutUlma);
				prestmt1.setString(2,serialNoOutWlan);
				prestmt1.setString(3,serialNoOutwlan2);
				prestmt1.setString(4,serialNoOutwlan3);
				prestmt1.setString(5,serialNoOutwlan4);
				prestmt1.setString(6,dualSerialNoOut);
				prestmt1.execute();

				sendAnEmailForULMAorMACKAddressNotMatch(serialNoOut,dualSerialNoOut,null,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);
				response.setResponseCode(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE);
				response.setResponseMessage(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);


			}else{
				conn2 = DBUtil.getConnection(ds);
				prestmt2 = conn2.prepareStatement(UpdateQuery);
				prestmt2.setString(1,dualSerialNoOutUlma);
				prestmt2.setString(2,dualSerialNoOutWlan);
				prestmt2.setString(3,dualSerialNoOutwlan2);
				prestmt2.setString(4,dualSerialNoOutwlan3);
				prestmt2.setString(5,dualSerialNoOutwlan4);
				prestmt2.setString(6,serialNoOut);
				prestmt2.execute();

				sendAnEmailForULMAorMACKAddressNotMatch(serialNoOut,dualSerialNoOut,null,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE,ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);
				response.setResponseCode(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE);
				response.setResponseMessage(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG);
			}



		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			response.setResponseCode(ServiceMessageCodes.SQL_EXCEPTION);
			response.setResponseMessage(ServiceMessageCodes.SQL_EXCEPTION_MSG+ e.getMessage());
		}finally{
			DBUtil.closeConnection(conn1, prestmt1, rs1);
			DBUtil.closeConnection(conn2, prestmt2, rs2);
			DBUtil.closeConnection(conn3, prestmt3, rs3);
		}
		return response;	
	}
	public static void sendAnEmailForULMAorMACKAddressNotMatch(String serialNoOut,String dualSerialNoOut,String triSerialNoOut,String emailMessageCode,String emailMessage){

		// TODO Auto-generated method stub
		PropertyResourceBundle bundle = InitProperty.getProperty("pcbaMail.properties");

		logger.info("logger information sendEmail start");
		final String USER_NAME = bundle.getString("username");
		final String server=bundle.getString("server");
		final String portNO=bundle.getString("portNO");
		final String RECIPIENT=bundle.getString("recipient");
		String subject="";
		
		if(emailMessageCode!= null && emailMessageCode.equals(ServiceMessageCodes.ULMA_MAC_EMAIL_MSG_CODE)){
			subject=bundle.getString("emailULMASubject");
		}else{
			subject=bundle.getString("emailSubject");
		}

		String body=bundle.getString("newEmailBody");

		if(((serialNoOut!=null && !serialNoOut.equals("")) && (dualSerialNoOut!=null && !dualSerialNoOut.equals("")))){
			body = MessageFormat.format(body, serialNoOut,dualSerialNoOut,emailMessageCode,emailMessage);
		}else if(((dualSerialNoOut!=null && !dualSerialNoOut.equals("")) && (triSerialNoOut!=null && !triSerialNoOut.equals("")))){
			body = MessageFormat.format(body, dualSerialNoOut,triSerialNoOut,emailMessageCode,emailMessage);
		}else if(((triSerialNoOut!=null && !triSerialNoOut.equals("")) && (serialNoOut!=null && !serialNoOut.equals("")))){
			body = MessageFormat.format(body, triSerialNoOut,serialNoOut,emailMessageCode,emailMessage);
		}else if(((triSerialNoOut!=null && !triSerialNoOut.equals("")) && (dualSerialNoOut!=null && !dualSerialNoOut.equals("")))){
			body = MessageFormat.format(body, triSerialNoOut,dualSerialNoOut,emailMessageCode,emailMessage);
		}else if(((dualSerialNoOut!=null && !dualSerialNoOut.equals("")) && (serialNoOut!=null && !serialNoOut.equals("")))){
			body = MessageFormat.format(body, dualSerialNoOut,serialNoOut,emailMessageCode,emailMessage);
		}else if(((serialNoOut!=null && !serialNoOut.equals("")) && (triSerialNoOut!=null && !triSerialNoOut.equals("")))){
			body = MessageFormat.format(body, serialNoOut,triSerialNoOut,emailMessageCode,emailMessage);
		}



		String from = USER_NAME;
		logger.info("from --------------------" + from);
		String[] to = { RECIPIENT }; // list of recipient email addresses

		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", server);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.port", portNO);
		props.put("mail.smtp.auth", "false");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(from));
			InternetAddress[] toAddress = new InternetAddress[to.length];

			// To get the array of addresses
			for( int i = 0; i < to.length; i++ ) {
				toAddress[i] = new InternetAddress(to[i]);
			}

			for( int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			}

			message.setSubject(subject);  
			message.setContent(body, "text/html");
			Transport transport = session.getTransport("smtp");
			transport.connect(server, from);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("Sent an E-Mail successfully....");
		}
		catch (AddressException ae) {
			logger.info("Reading AddressException"+ae.getMessage());
			ae.printStackTrace();
		}
		catch (MessagingException me) {
			logger.info("Reading MessagingException:"+me.getMessage());
			me.printStackTrace();
		}



	}
	@Override
	public void sendAnEmail(String serialNoIn, String serialNoOut,String emailMessageCode, String emailMessage) {
		// TODO Auto-generated method stub
		PropertyResourceBundle bundle = InitProperty.getProperty("pcbaMail.properties");

		logger.info("logger information sendEmail start");
		final String USER_NAME = bundle.getString("username");
		final String server=bundle.getString("server");
		final String portNO=bundle.getString("portNO");
		final String RECIPIENT=bundle.getString("recipient");
		String subject="";
		
		if(emailMessageCode!=null && emailMessageCode.equals(ServiceMessageCodes.MAC_ULMA_EMAIL_MSG_CODE)){
			subject=bundle.getString("emailULMASubject");
		}else{
			subject=bundle.getString("emailSubject");
		}

		String body=bundle.getString("emailBody");
		body = MessageFormat.format(body, serialNoIn,serialNoOut,emailMessageCode,emailMessage);


		String from = USER_NAME;
		logger.info("from --------------------" + from);
		String[] to = { RECIPIENT }; // list of recipient email addresses

		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", server);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.port", portNO);
		props.put("mail.smtp.auth", "false");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(from));
			InternetAddress[] toAddress = new InternetAddress[to.length];

			// To get the array of addresses
			for( int i = 0; i < to.length; i++ ) {
				toAddress[i] = new InternetAddress(to[i]);
			}

			for( int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			}

			message.setSubject(subject);  
			message.setContent(body, "text/html");
			Transport transport = session.getTransport("smtp");
			transport.connect(server, from);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("Sent an E-Mail successfully....");
		}
		catch (AddressException ae) {
			logger.info("Reading AddressException"+ae.getMessage());
			ae.printStackTrace();
		}
		catch (MessagingException me) {
			logger.info("Reading MessagingException:"+me.getMessage());
			me.printStackTrace();
		}

	}
}
