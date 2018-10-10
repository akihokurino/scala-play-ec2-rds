# --- !Ups
CREATE TABLE IF NOT EXISTS billings (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  published_contract_id INT(11) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  foreign key(published_contract_id) references published_contracts(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE billings;