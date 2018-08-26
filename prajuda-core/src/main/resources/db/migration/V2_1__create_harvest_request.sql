CREATE TABLE harvest_request (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    service_source_id       VARCHAR(255)    NOT NULL,
    created_at              DATETIME        NOT NULL,
    started_at              DATETIME        NULL,
    completed_at            DATETIME        NULL,
    harvest_type            VARCHAR(255)    NOT NULL,
    CONSTRAINT pk_harvest_request PRIMARY KEY (id)
);