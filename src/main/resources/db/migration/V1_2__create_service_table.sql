CREATE TABLE praj_service (
    id                      BIGINT              NOT NULL AUTO_INCREMENT,
    name                    VARCHAR( 255)       NOT NULL,
    url                     VARCHAR(4000)       NOT NULL,
    harvesterTypeId         VARCHAR( 255)       NOT NULL,
    repo_info_uri           VARCHAR(4000)       NOT NULL,
    repo_info_branch        VARCHAR( 255)       NOT NULL,
    repo_info_last_modified DATETIME            NOT NULL,
    repo_info_last_tag      VARCHAR( 255)       NULL,
    repo_info_username      VARCHAR( 255)       NOT NULL,
    repo_info_password      VARCHAR( 255)       NOT NULL,
    document_dir            VARCHAR( 255)       NOT NULL,
    PRIMARY KEY (id)
);

