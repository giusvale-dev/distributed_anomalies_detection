CREATE TABLE IF NOT EXISTS member (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255),
  surname VARCHAR(255),
  enabled BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS authority (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  authority_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS member_authority (
  member_id BIGINT NOT NULL,
  authority_id BIGINT NOT NULL,
  PRIMARY KEY (member_id, authority_id),
  FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
  FOREIGN KEY (authority_id) REFERENCES authority (id) ON DELETE CASCADE
);