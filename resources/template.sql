set time on define on echo off heading on verify off
WHENEVER SQLERROR EXIT SQL.SQLCODE
--
DEFINE patch_path='&1'
DEFINE patch_file='&2'
DEFINE change_log='&3'
--
-- --------------------------------------------------
-- --               Patch heading
-- --------------------------------------------------
--
INSERT INTO CMTX_PATCH_HISTORY (
  CPHX_ID,
  CPHX_PATCH_FILE,
  CPHX_PATCH_NUMBER,
  CPHX_PATCH_COMMENT,
  CPHX_SCHEMA_NAME,
  CPHX_OP_TIMESTAMP,
  CPHX_OS_USER,
  CPHX_OS_MODULE,
  CPHX_HOST,
  CPHX_USER_IP,
  CPHX_NLS_SESSION,
  CPHX_STATUS,
  CPHX_CHANGE_LOG_FILE)
VALUES (
  CMSX_CPHX_ID.NEXTVAL,
  '&patch_file',
  '',
  'Directory: '||'&patch_path',
  SYS_CONTEXT ('USERENV', 'SESSION_USER'),
  systimestamp,
  SYS_CONTEXT ('USERENV', 'OS_USER'),
  SYS_CONTEXT ('USERENV', 'MODULE'),
  SYS_CONTEXT ('USERENV', 'HOST'),
  SYS_CONTEXT ('USERENV', 'IP_ADDRESS'),
  SYS_CONTEXT ('USERENV', 'LANGUAGE'),
  'R',
  '&change_log'
);
--
COMMIT;
--
SET SQLPROMPT "_USER'@'_CONNECT_IDENTIFIER > "
set time on define on echo off heading on
--
spool &patch_path/&patch_file..log
--
PROMPT  ================================================== 
PROMPT  File info
PROMPT
PROMPT  File name   : &patch_file
PROMPT  Patch number:
PROMPT  File comment: Directory &patch_path
PROMPT
PROMPT  ================================================== 
set echo on
--
-- --------------------------------------------------
-- --               File body
-- --------------------------------------------------


--
-- --------------------------------------------------
-- --               File feedback
-- --------------------------------------------------
PROMPT RECOMPILING ALL INVALID OBJECTS
EXEC DBMS_UTILITY.compile_schema(schema => UPPER(SYS_CONTEXT ('USERENV', 'SESSION_USER')), compile_all => FALSE);
--
PROMPT Mark patch as completed
UPDATE CMTX_PATCH_HISTORY SET
  CPHX_LAST_PATCH_FLAG = '0'
WHERE CPHX_LAST_PATCH_FLAG = '1';
UPDATE CMTX_PATCH_HISTORY SET
  CPHX_STATUS = 'F',
  CPHX_LAST_PATCH_FLAG = '1'
WHERE CPHX_PATCH_FILE = '&patch_file';
--
COMMIT;
--
--
set echo off
SHOW ERRORS
spool off