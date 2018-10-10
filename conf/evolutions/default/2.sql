# --- !Ups
CREATE TABLE IF NOT EXISTS tmp_recruitments (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  store_id INT(11) NOT NULL,
  display_occupation_id INT(11) NOT NULL,
  display_payment_type_id INT(11) NOT NULL,
  display_payment_from INT(11) NOT NULL,
  display_payment_to INT(11),
  title VARCHAR(255) NOT NULL,
  pr TEXT NOT NULL,
  work_info TEXT NOT NULL,
  payment_info TEXT NOT NULL,
  working_hours_info TEXT NOT NULL,
  holiday_info TEXT NOT NULL,
  requirement_info TEXT NOT NULL,
  treatment_info TEXT NOT NULL,
  entry_method_info TEXT NOT NULL,
  line_url VARCHAR(255) NOT NULL,
  foreign key(store_id) references stores(id),
  foreign key(display_occupation_id) references occupations(id),
  foreign key(display_payment_type_id) references payment_types(id),
  UNIQUE(store_id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE tmp_recruitments;


# --- !Ups
CREATE TABLE IF NOT EXISTS tmp_recruitment_occupation (
  tmp_recruitment_id INT(11) NOT NULL,
  occupation_id INT(11) NOT NULL,
  foreign key(tmp_recruitment_id) references tmp_recruitments(id),
  foreign key(occupation_id) references occupations(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE tmp_recruitment_occupation;


# --- !Ups
CREATE TABLE IF NOT EXISTS tmp_recruitment_specific_tag (
  tmp_recruitment_id INT(11) NOT NULL,
  specific_tag_id INT(11) NOT NULL,
  foreign key(tmp_recruitment_id) references tmp_recruitments(id),
  foreign key(specific_tag_id) references specific_tags(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE tmp_recruitment_specific_tag;


# --- !Ups
CREATE TABLE IF NOT EXISTS tmp_recruitment_photos (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  tmp_recruitment_id INT(11) NOT NULL,
  resource_name VARCHAR(255) NOT NULL,
  foreign key(tmp_recruitment_id) references tmp_recruitments(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE tmp_recruitment_photos;
