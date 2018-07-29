--altera a syntax para a de mysql para os testes
SET DATABASE SQL SYNTAX MYS TRUE;

DROP SCHEMA prajuda_test IF EXISTS CASCADE;

CREATE SCHEMA prajuda_test AUTHORIZATION DBA;
SET INITIAL SCHEMA prajuda_test;
