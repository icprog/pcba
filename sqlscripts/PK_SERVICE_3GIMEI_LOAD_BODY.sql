--------------------------------------------------------
--  File created - Friday-June-05-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package Body PK_SERVICE_3GIMEI_LOAD
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "UPD"."PK_SERVICE_3GIMEI_LOAD" 
AS

/******************************************************************************
   NAME:       PK_SERVICE_3GIMEI_LOAD
   PURPOSE:    To load IMEI serial number into UPD.UPD_SERVICE_3GIMEI table
                allocated for service center.

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        23-OCT-2013    Joshi         1. Created this package.
******************************************************************************/

PROCEDURE UPD_SVC_3GIMEI_LOAD(
v_req_id IN UPD.UPD_IMEITAC_RANGE_DETAILS.REQUEST_ID%TYPE,status out varchar2)
AS
v_start_imei UPD.UPD_IMEITAC_RANGE_DETAILS.TAC_START_RANGE%TYPE;
v_end_imei UPD.UPD_IMEITAC_RANGE_DETAILS.TAC_END_RANGE%TYPE;
v_gppd_id UPD_IMEITAC_REQ_PRIMARY_ATTR.GPPD_ID%TYPE;
v_prd_mkt_name UPD.UPD_IMEITAC_NPI_DETAILS.PRODUCT_MKT_NAME%TYPE;
v_prd_model_no UPD.UPD_IMEITAC_NPI_DETAILS.PRODUCT_MODEL_NO%TYPE;
v_prd_int_name UPD.UPD_IMEITAC_NPI_DETAILS.PRODUCT_INT_NAME%TYPE;
v_requester_id UPD.UPD_IMEITAC_MASTER1.REQUESTOR_ID%TYPE;
v_gen_date UPD.UPD_IMEITAC_RANGE_DETAILS.LAST_MOD_DATETIME%TYPE;
v_cust UPD.UPD_IMEITAC_REQ_PRIMARY_ATTR.CUSTOMER%TYPE;
v_ship_region UPD.UPD_IMEITAC_REQ_PRIMARY_ATTR.CUST_SHIPTO_REGIONS%TYPE;
v_imei_sn UPD.UPD_SERVICE_3GIMEI.IMEI_SN%TYPE;

v_prog_facility VARCHAR2(50) := 'Service Center';
--v_prog_facility VARCHAR2(50) := 'PCBA_PGM_AGENT';
v_build_type VARCHAR2(50) := 'PROD';
v_created_by VARCHAR2(50) :='SVC LOAD';
v_created_date DATE := sysdate;
v_updated_by VARCHAR2(50) := 'SVC LOAD';
v_updated_date DATE := sysdate;
l_line VARCHAR2(32000);
l_fileWriteStream UTL_FILE.FILE_TYPE;
l_dirpath VARCHAR2 (50) := 'PCBA_DIR';
--/mot/test/upd/com/mot/pcs/upd/data/pcba_load
l_pcbaIMEIFileName VARCHAR2 (50);

 l_currentDate VARCHAR2 (50);
v_imei UPD.UPD_IMEITAC_RANGE_DETAILS.TAC_START_RANGE%TYPE;

v_odd           integer       := 0;
v_even          integer       := 0;
v_two_times     integer       := 0;
v_sum           integer       := 0;

CURSOR svc_tac IS
SELECT *
FROM UPD.UPD_IMEITAC_RANGE_DETAILS
WHERE facility in('SVCCENTER','PCBAPGMAGT') AND TAC_ASSIGNED IS NOT NULL AND REQUEST_ID = v_req_id;
--WHERE facility in('PCBA_PGM_AGENT') AND TAC_ASSIGNED IS NOT NULL AND REQUEST_ID = v_req_id;
--24QFZN


BEGIN
--raise_application_error(-20100, 100||' from plsqlblock');

  FOR tac_rec IN svc_tac
  LOOP
   -- v_req_id := tac_rec.request_id;
    v_start_imei := LPAD(tac_rec.tac_start_range,14,0);
    v_end_imei := LPAD(tac_rec.tac_end_range,14,0);
    v_gen_date := tac_rec.last_mod_datetime;
     
    SELECT gppd_id,customer,cust_shipto_regions
      INTO v_gppd_id,v_cust,v_ship_region
    FROM UPD.UPD_IMEITAC_REQ_PRIMARY_ATTR
    WHERE REQUEST_ID = v_req_id;

    IF (v_cust IS NULL OR v_cust ='')
    THEN
      v_cust := 'ANY';
    END IF;

    SELECT product_model_no,product_mkt_name,product_int_name
      INTO v_prd_model_no,v_prd_mkt_name,v_prd_int_name
    FROM UPD.UPD_IMEITAC_NPI_DETAILS
    WHERE gppd_id = v_gppd_id;

    SELECT REQUESTOR_ID
      INTO v_requester_id
    FROM UPD.UPD_IMEITAC_MASTER1
    WHERE REQUEST_ID = v_req_id;

    v_imei := v_start_imei;
      l_currentDate :=TO_CHAR(SYSDATE,'YYYY_MM_DD');
      l_pcbaIMEIFileName:= 'UPD_PCBA_PGM_IMEI_LOAD_'||l_currentDate||'.dat';
    --  dbms_output.put_line('Creating a file:'||l_pcbaIMEIFileName);
    WHILE (v_imei <= v_end_imei)
    LOOP

      v_odd         := 0;
      v_even        := 0;
      FOR j IN 1..7
      LOOP
        v_odd :=v_odd+to_number(subStr(v_imei,(j-1)*2+1,1));
        v_two_times  :=to_number(subStr(v_imei,j*2,1))*2;
        IF(v_two_times >=10)
        THEN
          v_two_times :=v_two_times -9;
        END IF;
        v_even := v_even+v_two_times;
      END LOOP;
      v_sum := v_odd+v_even;
      v_sum := mod(v_sum,10);
      IF(v_sum =0)
      THEN
        v_sum := 10;
      END IF;
      v_imei_sn := v_imei||to_char(10-v_sum);
      BEGIN
         -- DBMS_OUTPUT.PUT_LINE(v_imei_sn || v_prd_mkt_name ||v_prd_model_no ||v_req_id||v_requester_id||v_gppd_id||v_cust||v_prog_facility||v_build_type||v_prd_int_name||v_created_by||v_created_date||v_updated_by||v_updated_date);
       IF tac_rec.facility ='SVCCENTER' THEN 
        INSERT INTO UPD.UPD_SERVICE_3GIMEI
        (IMEI_SN,MARKET_NAME,MODEL_NUMBER,
         UPD_REQUESTID,REQUESTER,GENERATION_DATE,GPPD_ID,CUSTOMER,
         PROGRAMMING_FACILITY,BUILD_TYPE,PRODUCT_NAME,SHIPPING_REGIONS,
         DISPATCH_STATUS,
         CREATED_BY,CREATED_TIMESTAMP,UPDATED_BY,UPDATED_TIMESTAMP
        )
        VALUES(v_imei_sn,v_prd_mkt_name,v_prd_model_no,
        v_req_id,v_requester_id,v_gen_date,v_gppd_id,v_cust,
        v_prog_facility,v_build_type,v_prd_int_name,v_ship_region,
        'N',
        v_created_by,v_created_date,v_updated_by,v_updated_date
        );
        update UPD.UPD_IMEITAC_RANGE_DETAILS set update_status='Y' where request_id = v_req_id;
           COMMIT;
           status :='SUCCESS';
        ELSE 
       --   dbms_output.put_line('1');
          --dbms_output.put_line(v_prd_mkt_name);
          v_prog_facility := 'PCBA_PGM_AGENT';
          v_created_by  :='PCBAPGMAGT';
          v_updated_by :='PCBAPGMAGT ';
          insert into UPD.UPD_PCBA_PGM_IMEI (SERIAL_NO, MARKET_NAME,MODEL_NUMBER,REQUEST_ID,REQUESTER,GENERATION_DATE,GPPD_ID,                
          CUSTOMER,PROGRAMMING_FACILITY,BUILD_TYPE,PRODUCT_NAME,DISPATCH_DATE,DISPATCH_STATUS,        
          RSD_ID,MASC_ID,CLIENT_REQUEST_DATETIME,CREATED_BY,CREATED_DATETIME,LAST_MOD_BY,            
          LAST_MOD_DATETIME,PGM_DATE,PGM_STATUS,SWAP_DATE) values (v_imei_sn,v_prd_mkt_name,v_prd_model_no,
          v_req_id,v_requester_id,sysdate,v_gppd_id,v_cust,
          v_prog_facility,v_build_type,v_prd_int_name,null,
          'N','NULL','NULL',null,
          v_created_by,v_created_date,v_updated_by,v_updated_date,null,'N',null);
          --dbms_output.put_line('2');
          update UPD.UPD_IMEITAC_RANGE_DETAILS set update_status='Y' where request_id = v_req_id;
            COMMIT;
           status :='SUCCESS';
           
          END IF;
      EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
        --If the record already present then continue with next MEID.
      --   dbms_output.put_line(v_req_id||' from plsqlblock'||v_imei_sn);
         status :='DUPLICATE IMEI SERIAL NUMBER EXISTS';
         update UPD.UPD_IMEITAC_RANGE_DETAILS set update_status='Y' where request_id = v_req_id;
         COMMIT;
        NULL;
        
      END;

      v_imei := v_imei+1;
       
          
    END LOOP;
  END LOOP;
  ----file writing
    BEGIN
    l_fileWriteStream := UTL_FILE.FOPEN (l_dirpath, l_pcbaIMEIFileName, 'w', 32000);
    FOR PCBAIMEI IN (SELECT * FROM UPD_PCBA_PGM_IMEI) LOOP
          --UTL_FILE.PUT_LINE (l_fileWriteStream,PCBAIMEI.SERIAL_NO ||'|'||PCBAIMEI.MARKET_NAME);
        UTL_FILE.PUT_LINE (l_fileWriteStream, PCBAIMEI.SERIAL_NO || '|' || PCBAIMEI.MARKET_NAME || '|' || PCBAIMEI.MODEL_NUMBER  || '|' || PCBAIMEI.REQUEST_ID || '|' || PCBAIMEI.REQUESTER  || '|' || PCBAIMEI.GENERATION_DATE || '|' || PCBAIMEI.GPPD_ID || '|' || PCBAIMEI.CUSTOMER   
          || '|' || PCBAIMEI.PROGRAMMING_FACILITY  || '|' || PCBAIMEI.BUILD_TYPE  || '|' || PCBAIMEI.PRODUCT_NAME   || '|'  || PCBAIMEI.DISPATCH_DATE  || '|'  || PCBAIMEI.DISPATCH_STATUS || '|' || PCBAIMEI.RSD_ID     || '|'  || PCBAIMEI.MASC_ID   || '|'  || PCBAIMEI.CLIENT_REQUEST_DATETIME
          || '|'  || PCBAIMEI.CREATED_BY || '|'    || PCBAIMEI.CREATED_DATETIME  || '|' || PCBAIMEI.LAST_MOD_BY  || '|'  || PCBAIMEI.LAST_MOD_DATETIME  || '|' || PCBAIMEI.PGM_DATE  || '|' || PCBAIMEI.PGM_STATUS   || '|' || PCBAIMEI.SWAP_DATE );
    END LOOP;
      UTL_FILE.fclose(l_fileWriteStream);
       EXCEPTION
        WHEN UTL_FILE.internal_error THEN
        DBMS_OUTPUT.PUT_LINE ('UTL_FILE: An internal error occurred.');
        UTL_FILE.FCLOSE_ALL;
 END;
  ----file writng
   
  EXCEPTION
  WHEN OTHERS THEN
    NULL;
END UPD_SVC_3GIMEI_LOAD;

END PK_SERVICE_3GIMEI_LOAD;

/
