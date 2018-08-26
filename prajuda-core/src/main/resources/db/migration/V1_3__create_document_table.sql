CREATE TABLE praj_document (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    content         MEDIUMTEXT      NOT NULL,
    tag             VARCHAR(255)    NOT NULL,
    path            VARCHAR(4000)   NOT NULL,
    service_id      BIGINT          NULL,
    service_name    VARCHAR(255)    NULL,
    CONSTRAINT pk_praj_document PRIMARY KEY (id),
    CONSTRAINT fk_document_service
        FOREIGN KEY (service_id)
        REFERENCES praj_service (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
    );