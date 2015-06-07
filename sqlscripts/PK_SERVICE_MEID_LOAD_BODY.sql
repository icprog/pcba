--------------------------------------------------------
--  File created - Friday-June-05-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package Body PK_SERVICE_MEID_LOAD
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "UPD"."PK_SERVICE_MEID_LOAD" 
AS

/******************************************************************************
   NAME:       PK_SERVICE_MEID_LOAD
   PURPOSE:    To load meid serial number into UPD.UPD_SERVICE_MEID table 
                allocated for service center.

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        23-OCT-2013    Joshi         1. Created this package.
******************************************************************************/

PROCEDURE UPD_SVC_MEID_LOAD(
v_request_id IN VARCHAR2, status OUT VARCHAR2)
AS
  v_meid_sn VARCHAR2(50);
  v_protocol_name VARCHAR2(50);
  v_akey1_type VARCHAR2(50);
  v_akey1_value VARCHAR2(50);
  v_akey2_type VARCHAR2(50);
  v_akey2_value VARCHAR2(50);
  v_master_sublock_type VARCHAR2(50);
  v_master_sublock_value VARCHAR2(50);
  v_onetime_sublock_type VARCHAR2(50);
  v_onetime_sublock_value VARCHAR2(50);
  v_pESN VARCHAR2(50);
  v_meid_decimal VARCHAR2(50) := 'TBD';
  v_upd_reqid VARCHAR2(50);
  v_requester VARCHAR2(50);
  v_generation_date DATE;
  v_pmdid VARCHAR2(50);
  v_customer VARCHAR2(50);
  v_prog_facility VARCHAR2(50) := 'Service Center';
  v_build_type VARCHAR2(50) := 'PROD';
  v_product_name VARCHAR2(100);
  v_created_by VARCHAR2(50) :='SVC LOAD';
  v_created_date DATE := sysdate;
  v_updated_by VARCHAR2(50) := 'SVC LOAD';
  v_updated_date DATE := sysdate;
  
  v_start_meid VARCHAR2(50);
  v_end_meid VARCHAR2(50);

  v_meid_tac varchar(10);
  v_meid_num varchar(10);
  v_meid_dec_tac varchar(15);
  v_meid_dec_num varchar(10);
  v_meid_dec varchar(20);
  l_line VARCHAR2(32000);
  l_fileWriteStream UTL_FILE.FILE_TYPE;
  l_dirpath VARCHAR2 (50) := 'PCBA_DIR';
  --/mot/test/upd/com/mot/pcs/upd/data/pcba_load
  l_pcbaMEIDFileName VARCHAR2 (50);
   l_currentDate VARCHAR2 (50);
   
--  CURSOR gsc_meid IS 
--  SELECT START_MEID, END_MEID,EASY_KEY
--  FROM UPD.UPD_MEID_SUMMARY 
--  where factory_id = 'SVCCENTER' and request_id = v_request_id;
-- modified for pcba process
   CURSOR gsc_meid IS 
   SELECT START_MEID, END_MEID,EASY_KEY, FACTORY_ID
   FROM UPD.UPD_MEID_SUMMARY 
   --where factory_id in('SVCCENTER','PCBAPGMAGT') and request_id = v_request_id;
   where factory_id in('SVCCENTER','PCBAPGMAGT') and request_id = v_request_id;

  
  
  CURSOR each_meid(start_meid IN VARCHAR2, end_meid IN VARCHAR2) IS 
  SELECT * 
  FROM UPD.UPD_MEID 
  where serial_no >= start_meid and serial_no <= end_meid;
  
BEGIN
    l_currentDate :=TO_CHAR(SYSDATE,'YYYY_MM_DD');
    l_pcbaMEIDFileName:= 'UPD_PCBA_PGM_MEID_LOAD_'||l_currentDate||'.dat';
     --dbms_output.put_line('Creating a file:'||l_pcbaMEIDFileName);
  FOR MEID_REC IN gsc_meid
  LOOP
    
    
    SELECT SERIAL_NO 
      INTO V_START_MEID 
    FROM UPD.UPD_MEID 
    WHERE SERIAL_NO LIKE MEID_REC.START_MEID||'%';
    
    -- dbms_output.put_line(v_request_id||' from plsqlblock'||V_START_MEID);
    
    SELECT SERIAL_NO 
      INTO V_END_MEID 
    FROM UPD.UPD_MEID 
    where serial_no like MEID_REC.end_meid||'%';
    
    -- dbms_output.put_line(v_request_id||' from plsqlblock'||V_END_MEID);
    
    -- dbms_output.put_line(v_request_id||' from plsqlblock'||MEID_REC.easy_key);
        
    SELECT SUBSTR(GPPD_ID,1,4),CUSTOMER,UPD_MARKET_MODEL 
      INTO v_pmdid,v_customer,v_product_name
    FROM upd.UPD_MEID_REQUEST_DETAIL 
    where easy_key = MEID_REC.easy_key;
    
    -- dbms_output.put_line(v_request_id||' from plsqlblock'||v_customer);
       
    IF (v_customer IS NULL OR v_customer ='') 
    THEN
      v_customer := 'ANY';
    END IF;
    
   
    FOR c_each_meid IN each_meid(start_meid => v_start_meid, end_meid => v_end_meid)
    LOOP
      
      v_meid_sn := c_each_meid.serial_no;
      
      SELECT protocol,PESN,request_id,last_mod_user,generation_date 
        INTO v_protocol_name,v_pESN,v_upd_reqid,v_requester,v_generation_date
      FROM upd.upd_meid 
      WHERE serial_no = v_meid_sn;
      
      BEGIN
        SELECT ATTRIBUTE_CATEGORY,ATTRIBUTE_VALUE 
          INTO v_akey1_type,v_akey1_value 
        FROM upd.upd_meid_attr 
        WHERE serial_no = v_meid_sn AND attribute_type = 'AKEY1'; -- AKEY1 type and value
      EXCEPTION 
      WHEN NO_DATA_FOUND THEN
        v_akey1_type := NULL;
        v_akey1_value := NULL;
      END; 
    --  dbms_output.put_line('2');
      BEGIN
        SELECT ATTRIBUTE_CATEGORY,ATTRIBUTE_VALUE 
          INTO v_akey2_type,v_akey2_value 
        FROM upd.upd_meid_attr 
        WHERE serial_no = v_meid_sn AND attribute_type = 'AKEY2'; -- AKEY2 type and value
      EXCEPTION 
      WHEN NO_DATA_FOUND THEN
        v_akey2_type := NULL;
        v_akey2_value := NULL;
      END; 
     -- dbms_output.put_line('3');
      BEGIN
        SELECT ATTRIBUTE_CATEGORY,ATTRIBUTE_VALUE 
          INTO v_master_sublock_type,v_master_sublock_value  
        FROM upd.upd_meid_attr 
        WHERE serial_no = v_meid_sn and attribute_type = 'MASTERSUBLOCK';
      EXCEPTION 
      WHEN NO_DATA_FOUND THEN
        v_master_sublock_type := NULL;
        v_master_sublock_value := NULL;
      END; 
     -- dbms_output.put_line('4');
      BEGIN
        SELECT ATTRIBUTE_CATEGORY,ATTRIBUTE_VALUE 
          INTO v_onetime_sublock_type,v_onetime_sublock_value 
        FROM upd.upd_meid_attr 
        WHERE serial_no = v_meid_sn and attribute_type = 'ONETIMESUBLOCK';
      EXCEPTION 
      WHEN NO_DATA_FOUND THEN
        v_onetime_sublock_type := NULL;
        v_onetime_sublock_value := NULL;
      END; 
      
      v_meid_tac := SUBSTR(v_meid_sn,1,8);
      v_meid_num := SUBSTR(v_meid_sn,9,6);
      v_meid_dec_tac := HEXTODECIMAL(v_meid_tac);
      v_meid_dec_num := HEXTODECIMAL(v_meid_num);
      v_meid_dec := LPAD(v_meid_dec_num,8,'0');
      v_meid_decimal := v_meid_dec_tac||v_meid_dec;
      
        
      BEGIN
        IF MEID_REC.FACTORY_ID='SVCCENTER' THEN  
        INSERT INTO UPD.UPD_SERVICE_MEID
        (MEID_SN,PROTOCOL_NAME,
        AKEY1_TYPE,AKEY1_VALUE,
        AKEY2_TYPE,AKEY2_VALUE,
        MASTER_SUBLOCK_TYPE,MASTER_SUBLOCK_CODE,
        ONETIME_SBLOCK_TYPE,ONETIME_SBLOCK_CODE,
        PESN,MEID_DECIMAL,UPD_REQUESTID,REQUESTER,GENERATION_DATE,GPPD_ID,CUSTOMER,
        PROGRAMMING_FACILITY,BUILD_TYPE,PRODUCT_NAME,
        DISPATCH_STATUS,
        CREATED_BY,CREATED_TIMESTAMP,UPDATED_BY,UPDATED_TIMESTAMP
        )
        VALUES(v_meid_sn,v_protocol_name,
        v_akey1_type,v_akey1_value,
        v_akey2_type,v_akey2_value,
        v_master_sublock_type,v_master_sublock_value,
        v_onetime_sublock_type,v_onetime_sublock_value,
        v_pESN,v_meid_decimal,v_upd_reqid,v_requester,v_generation_date,v_pmdid,v_customer,
        v_prog_facility,v_build_type,v_product_name,
        'N',
        v_created_by,v_created_date,v_updated_by,v_updated_date
        );
        update UPD.UPD_MEID_SUMMARY set update_status='Y' where request_id = v_request_id;
        COMMIT;
        status:='SUCCESS';
        ELSE
       -- dbms_output.put_line('1');
        v_prog_facility := 'PCBAPGMAGT';
        v_created_by  :='PCBAPGMAGT';
        v_updated_by :='PCBAPGMAGT';
        INSERT INTO UPD.UPD_PCBA_PGM_MEID
        (SERIAL_NO,PROTOCOL_NAME,
        AKEY1_TYPE,AKEY1_VALUE,
        AKEY2_TYPE,AKEY2_VALUE,
        MASTER_SUBLOCK_TYPE,MASTER_SUBLOCK_CODE,
        ONETIME_SBLOCK_TYPE,ONETIME_SBLOCK_CODE,
        PESN,MEID_DECIMAL,REQUEST_ID,REQUESTER,GENERATION_DATE,GPPD_ID,CUSTOMER,
        PROGRAMMING_FACILITY,BUILD_TYPE,PRODUCT_NAME,
        DISPATCH_STATUS,
        CREATED_BY,CREATED_DATETIME,LAST_MOD_BY,LAST_MOD_DATETIME,
        PGM_DATE,PGM_STATUS,SWAP_DATE
        )
        VALUES(v_meid_sn,v_protocol_name,
        v_akey1_type,v_akey1_value,
        v_akey2_type,v_akey2_value,
        v_master_sublock_type,v_master_sublock_value,
        v_onetime_sublock_type,v_onetime_sublock_value,
        v_pESN,v_meid_decimal,v_upd_reqid,v_requester,v_generation_date,v_pmdid,v_customer,
        v_prog_facility,v_build_type,v_product_name,
        'N',
        v_created_by,v_created_date,v_updated_by,v_updated_date,
        null,'N',null
        );
       --  dbms_output.put_line('2');
         update UPD.UPD_MEID_SUMMARY set update_status='Y' where request_id = v_request_id;
          COMMIT;
        status:='SUCCESS';
       --  dbms_output.put_line('3');
        END IF;
      EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
        --If the record already present then continue with next MEID.

        status:='DUPLICATE VALUE EXISTS';

        update UPD.UPD_MEID_SUMMARY set update_status='Y' where request_id = v_request_id;
        COMMIT;
        NULL;
        
      END;

    END LOOP;
     
    COMMIT;
  END LOOP;
     ----file writing
    BEGIN
     
     l_fileWriteStream := UTL_FILE.FOPEN (l_dirpath, l_pcbaMEIDFileName, 'w', 32000);
    FOR PCBAMEID IN (SELECT * FROM UPD_PCBA_PGM_MEID) LOOP
        --  UTL_FILE.PUT_LINE (l_fileWriteStream,PCBAMEID.SERIAL_NO ||'|'||PCBAMEID.PROTOCOL_NAME);
         UTL_FILE.PUT_LINE (l_fileWriteStream, PCBAMEID.SERIAL_NO  || '|' || PCBAMEID.PROTOCOL_NAME  || '|' || PCBAMEID.AKEY1_TYPE   || '|'  || PCBAMEID.AKEY1_VALUE  || '|' 
|| PCBAMEID.AKEY2_TYPE   || '|' || PCBAMEID.AKEY2_VALUE  || '|'  || PCBAMEID.MASTER_SUBLOCK_TYPE || '|' || PCBAMEID.MASTER_SUBLOCK_CODE  || '|' 
|| PCBAMEID.ONETIME_SBLOCK_TYPE  || '|' || PCBAMEID.ONETIME_SBLOCK_CODE  || '|' || PCBAMEID.PESN   || '|' || PCBAMEID.MEID_DECIMAL  || '|' 
|| PCBAMEID.REQUEST_ID || '|' || PCBAMEID.REQUESTER || '|' || PCBAMEID.GENERATION_DATE || '|'  || PCBAMEID.GPPD_ID  || '|'              
|| PCBAMEID.CUSTOMER  || '|' || PCBAMEID.PROGRAMMING_FACILITY  || '|'  || PCBAMEID.BUILD_TYPE  || '|' || PCBAMEID.PRODUCT_NAME || '|'  
|| PCBAMEID.DISPATCH_DATE || '|' || PCBAMEID.DISPATCH_STATUS || '|' || PCBAMEID.RSD_ID     || '|' || PCBAMEID.MASC_ID  || '|'                 
|| PCBAMEID.CLIENT_REQUEST_DATETIME || '|'  || PCBAMEID.CREATED_BY || '|' || PCBAMEID.CREATED_DATETIME || '|' || PCBAMEID.LAST_MOD_BY || '|' 
|| PCBAMEID.LAST_MOD_DATETIME || '|' || PCBAMEID.PGM_DATE || '|'  || PCBAMEID.PGM_STATUS    || '|' || PCBAMEID.SWAP_DATE    );
    END LOOP;
      UTL_FILE.fclose(l_fileWriteStream);
      EXCEPTION
        WHEN UTL_FILE.internal_error THEN
        DBMS_OUTPUT.PUT_LINE ('UTL_FILE: An internal error occurred.');
        UTL_FILE.FCLOSE_ALL;
   END;

  EXCEPTION
  WHEN OTHERS THEN
  status:= 'ERROR';
    NULL;

END UPD_SVC_MEID_LOAD;

END PK_SERVICE_MEID_LOAD;

/
