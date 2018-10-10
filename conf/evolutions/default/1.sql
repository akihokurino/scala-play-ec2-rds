# --- !Ups
CREATE TABLE IF NOT EXISTS admin_roles (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE admin_roles;


# --- !Ups
CREATE TABLE IF NOT EXISTS admin_users (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  role_id INT(11) NOT NULL,
  username VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  foreign key(role_id) references admin_roles(id),
  UNIQUE(email)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE admin_users;


# --- !Ups
CREATE TABLE IF NOT EXISTS users (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  birth_date VARCHAR(255) NOT NULL,
  gender_id INT(11) NOT NULL,
  phone_number VARCHAR(15) NOT NULL,
  email VARCHAR(255) NOT NULL,
  apply_mail_magazine boolean NOT NULL,
  UNIQUE(email)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE users;


# --- !Ups
CREATE TABLE IF NOT EXISTS agencies (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE agencies;


# --- !Ups
CREATE TABLE IF NOT EXISTS agency_admin_user (
  agency_id INT(11) NOT NULL,
  admin_user_id INT(11) NOT NULL,
  foreign key(agency_id) references agencies(id),
  foreign key(admin_user_id) references admin_users(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE agency_admin_user;


# --- !Ups
CREATE TABLE IF NOT EXISTS regions (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(20) NOT NULL,
  name_kana VARCHAR(20) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE regions;


# --- !Ups
CREATE TABLE IF NOT EXISTS prefectures (
  id INT(11) NOT NULL PRIMARY KEY,
  region_id INT(11) NOT NULL,
  name VARCHAR(20) NOT NULL,
  name_kana VARCHAR(20) NOT NULL,
  foreign key(region_id) references regions(id),
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE prefectures;


# --- !Ups
CREATE TABLE IF NOT EXISTS areas (
  id INT(11) NOT NULL PRIMARY KEY,
  prefecture_id INT(11) NOT NULL,
  name VARCHAR(20) NOT NULL,
  foreign key(prefecture_id) references prefectures(id),
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE areas;


# --- !Ups
CREATE TABLE IF NOT EXISTS business_conditions (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(20) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE business_conditions;


# --- !Ups
CREATE TABLE IF NOT EXISTS store_statuses (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE store_statuses;


# --- !Ups
CREATE TABLE IF NOT EXISTS stores (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  agency_id INT(11) NOT NULL,
  admin_user_id INT(11) NOT NULL,
  business_condition_id Int(11) NOT NULL,
  status_id INT(11) NOT NULL,
  prefecture_id INT(11) NOT NULL,
  area_id INT(11),
  requested_date DATETIME NOT NULL,
  name VARCHAR(50) NOT NULL,
  name_kana VARCHAR(50) NOT NULL,
  postal_code VARCHAR(10) NOT NULL,
  address VARCHAR(50) NOT NULL,
  building_name VARCHAR(50) NOT NULL,
  phone_number VARCHAR(15) NOT NULL,
  restaurant_permission_number VARCHAR(100) NOT NULL,
  customs_permission_number VARCHAR(100) NOT NULL,
  manager_name VARCHAR(50) NOT NULL,
  manager_name_kana VARCHAR(50) NOT NULL,
  manager_email VARCHAR(255) NOT NULL,
  manager_sub_email VARCHAR(255) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  foreign key(agency_id) references agencies(id),
  foreign key(admin_user_id) references admin_users(id),
  foreign key(business_condition_id) references business_conditions(id),
  foreign key(status_id) references store_statuses(id),
  foreign key(prefecture_id) references prefectures(id),
  foreign key(area_id) references areas(id),
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE stores;


# --- !Ups
CREATE TABLE IF NOT EXISTS nearest_stations (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  store_id INT(11) NOT NULL,
  route_id INT(11) NOT NULL,
  station_id INT(11) NOT NULL,
  foreign key(store_id) references stores(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE nearest_stations;


# --- !Ups
CREATE TABLE IF NOT EXISTS store_admin_user (
  store_id INT(11) NOT NULL,
  admin_user_id INT(11) NOT NULL,
  foreign key(store_id) references stores(id),
  foreign key(admin_user_id) references admin_users(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE store_admin_user;


# --- !Ups
CREATE TABLE IF NOT EXISTS published_contract_statuses (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE published_contract_statuses;


# --- !Ups
CREATE TABLE IF NOT EXISTS published_contract_types (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE published_contract_types;


# --- !Ups
CREATE TABLE IF NOT EXISTS published_contracts (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  store_id INT(11) NOT NULL,
  type_id INT(11) NOT NULL,
  status_id INT(11) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  billing_amount INT(11) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  foreign key(store_id) references stores(id),
  foreign key(type_id) references published_contract_types(id),
  foreign key(status_id) references published_contract_statuses(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE published_contracts;


# --- !Ups
CREATE TABLE IF NOT EXISTS published_plans (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE published_plans;


# --- !Ups
CREATE TABLE IF NOT EXISTS published_plan_contracts (
  id INT(11) NOT NULL PRIMARY KEY,
  plan_id INT(11) NOT NULL,
  foreign key(id) references published_contracts(id),
  foreign key(plan_id) references published_plans(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE published_plan_contracts;


# --- !Ups
CREATE TABLE IF NOT EXISTS published_options (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE published_options;


# --- !Ups
CREATE TABLE IF NOT EXISTS published_option_contracts (
  id INT(11) NOT NULL PRIMARY KEY,
  option_id INT(11) NOT NULL,
  foreign key(id) references published_contracts(id),
  foreign key(option_id) references published_options(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE published_option_contracts;


# --- !Ups
CREATE TABLE IF NOT EXISTS occupations (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(20) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE occupations;


# --- !Ups
CREATE TABLE IF NOT EXISTS recruitment_statuses (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE recruitment_statuses;


# --- !Ups
CREATE TABLE IF NOT EXISTS specific_tags (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE specific_tags;


# --- !Ups
CREATE TABLE IF NOT EXISTS payment_types (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE payment_types;


# --- !Ups
CREATE TABLE IF NOT EXISTS recruitments (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  store_id INT(11) NOT NULL,
  status_id INT(11) NOT NULL,
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
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  foreign key(store_id) references stores(id),
  foreign key(status_id) references recruitment_statuses(id),
  foreign key(display_occupation_id) references occupations(id),
  foreign key(display_payment_type_id) references payment_types(id),
  UNIQUE(store_id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE recruitments;


# --- !Ups
CREATE TABLE IF NOT EXISTS recruitment_occupation (
  recruitment_id INT(11) NOT NULL,
  occupation_id INT(11) NOT NULL,
  foreign key(recruitment_id) references recruitments(id),
  foreign key(occupation_id) references occupations(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE recruitment_occupation;


# --- !Ups
CREATE TABLE IF NOT EXISTS recruitment_specific_tag (
  recruitment_id INT(11) NOT NULL,
  specific_tag_id INT(11) NOT NULL,
  foreign key(recruitment_id) references recruitments(id),
  foreign key(specific_tag_id) references specific_tags(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE recruitment_specific_tag;


# --- !Ups
CREATE TABLE IF NOT EXISTS recruitment_photos (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  recruitment_id INT(11) NOT NULL,
  resource_name VARCHAR(255) NOT NULL,
  foreign key(recruitment_id) references recruitments(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE recruitment_photos;


# --- !Ups
CREATE TABLE IF NOT EXISTS option_ads (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  store_id INT(11) NOT NULL,
  option_id INT(11) NOT NULL,
  occupation_id INT(11) NOT NULL,
  resource_name VARCHAR(255) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  created_at DATETIME NOT NULL,
  foreign key(store_id) references stores(id),
  foreign key(option_id) references published_options(id),
  foreign key(occupation_id) references occupations(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE option_ads;


# --- !Ups
CREATE TABLE IF NOT EXISTS entries (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  recruitment_id INT(11) NOT NULL,
  foreign key(user_id) references users(id),
  foreign key(recruitment_id) references recruitments(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE entries;


# --- !Ups
CREATE TABLE IF NOT EXISTS entry_occupation (
  entry_id INT(11) NOT NULL,
  occupation_id INT(11) NOT NULL,
  foreign key(entry_id) references entries(id),
  foreign key(occupation_id) references occupations(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE entry_occupation;


# --- !Ups
CREATE TABLE IF NOT EXISTS question_statuses (
  id INT(11) NOT NULL PRIMARY KEY,
  name VARCHAR(10) NOT NULL,
  UNIQUE(name)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE question_statuses;


# --- !Ups
CREATE TABLE IF NOT EXISTS questions (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  status_id INT (11) NOT NULL,
  entry_id INT(11) NOT NULL,
  text TEXT NOT NULL,
  created_at DATETIME NOT NULL,
  foreign key(status_id) references question_statuses(id),
  foreign key(entry_id) references entries(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE questions;


# --- !Ups
CREATE TABLE IF NOT EXISTS answers (
  id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  admin_user_id INT(11) NOT NULL,
  question_id INT(11) NOT NULL,
  text TEXT NOT NULL,
  created_at DATETIME NOT NULL,
  foreign key(admin_user_id) references admin_users(id),
  foreign key(question_id) references questions(id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
# --- !Downs
DROP TABLE answers;

