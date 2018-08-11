CREATE TABLE praj_service (
    id                      BIGINT              NOT NULL AUTO_INCREMENT,
    name                    VARCHAR( 255)       NOT NULL,
    url                     VARCHAR(4000)       NOT NULL,
    harvester_type_id       VARCHAR( 255)       NOT NULL,
    repo_info_uri           VARCHAR(4000)       NOT NULL,
    repo_info_branch        VARCHAR( 255)       NOT NULL,
    repo_info_last_modified DATETIME            NOT NULL,
    repo_info_last_tag      VARCHAR( 255)       NULL,
    repo_info_username      VARCHAR( 255)       NOT NULL,
    repo_info_password      VARCHAR( 255)       NOT NULL,
    document_dir            VARCHAR( 255)       NOT NULL,
    PRIMARY KEY (id)
);


INSERT INTO praj_service (name, url, harvester_type_id,
repo_info_uri, repo_info_branch, repo_info_last_modified, repo_info_last_tag, repo_info_username, repo_info_password,
 document_dir)
 VALUES('XUXU', 'hshshshsh', '1',
 '1', '2', '2017-01-01', '4', '5', '6',
 '/storage');

