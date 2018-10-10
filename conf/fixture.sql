# TRUNCATE table admin_users;
INSERT INTO admin_users (role_id, username, email, password) VALUES
  (1, 'master', 'master@gmail.com', sha1('test')),
  (2, 'agency1', 'agency1@gmail.com', sha1('test')),
  (2, 'agency2', 'agency2@gmail.com', sha1('test')),
  (3, 'store1', 'store1@gmail.com', sha1('test')),
  (3, 'store2', 'store2@gmail.com', sha1('test')),
  (3, 'store3', 'store3@gmail.com', sha1('test'));

# TRUNCATE table users;
INSERT INTO users (name, birth_date, gender_id, phone_number, email, apply_mail_magazine) VALUES
  ('testユーザー1', '1991/04/02', 1, '08012345678', 'user1@gmail.com', 1),
  ('testユーザー2', '1991/04/02', 2, '08012345678', 'user2@gmail.com', 2);

# TRUNCATE table agencies;
INSERT INTO agencies (name) VALUES
  ('テスト代理店１'),
  ('テスト代理店2');

# TRUNCATE table agency_admin_user;
INSERT INTO agency_admin_user (agency_id, admin_user_id) VALUES
  (1, 2),
  (2, 3);

# TRUNCATE table stores;
INSERT INTO stores (
  agency_id,
  admin_user_id,
  business_condition_id,
  status_id,
  prefecture_id,
  area_id,
  requested_date,
  name,
  name_kana,
  postal_code,
  address,
  building_name,
  phone_number,
  restaurant_permission_number,
  customs_permission_number,
  manager_name,
  manager_name_kana,
  manager_email,
  manager_sub_email,
  created_at,
  updated_at
) VALUES
  (1,2,1,1,13,1,NOW(),'テスト店舗1','てすとてんぽ1','106-0032','東京都港区六本木4-11-4','六本木ビル5','03-6434-5846','1111','1111','丸山','まるやま','maruyama@gmail.com','',NOW(),NOW()),
  (1,2,2,2,13,1,NOW(),'テスト店舗2','てすとてんぽ2','106-0032','東京都港区六本木4-11-4','六本木ビル5','03-6434-5846','1111','1111','丸山','まるやま','maruyama@gmail.com','',NOW(),NOW()),
  (1,2,3,3,13,1,NOW(),'テスト店舗3','てすとてんぽ3','106-0032','東京都港区六本木4-11-4','六本木ビル5','03-6434-5846','1111','1111','丸山','まるやま','maruyama@gmail.com','',NOW(),NOW());

# TRUNCATE table store_admin_user;
INSERT INTO store_admin_user (store_id, admin_user_id) VALUES
  (1, 4),
  (2, 5),
  (3, 6);

# TRUNCATE table published_contracts;
INSERT INTO published_contracts (store_id, type_id, status_id, start_date, end_date, billing_amount, created_at, updated_at) VALUES
  (1, 1, 3, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), 50000, NOW(), NOW()),
  (2, 1, 3, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), 50000, NOW(), NOW()),
  (3, 1, 3, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), 50000, NOW(), NOW()),
  (1, 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), 50000, NOW(), NOW()),
  (2, 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), 50000, NOW(), NOW()),
  (3, 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), 50000, NOW(), NOW());

# TRUNCATE table published_plan_contracts;
INSERT INTO published_plan_contracts (id, plan_id) VALUES
  (1, 1),
  (2, 1),
  (3, 1);

# TRUNCATE table published_option_contracts;
INSERT INTO published_option_contracts (id, option_id) VALUES
  (4, 1),
  (5, 2),
  (6, 3);

# TRUNCATE table recruitments;
INSERT INTO recruitments (
  store_id,
  status_id,
  display_occupation_id,
  display_payment_type_id,
  display_payment_from,
  display_payment_to,
  title,
  pr,
  work_info,
  payment_info,
  working_hours_info,
  holiday_info,
  requirement_info,
  treatment_info,
  entry_method_info,
  line_url,
  created_at,
  updated_at
) VALUES
  (1,4,1,1,1000,2000,'原稿1','PR文','お仕事情報','給与','勤務時間','休日','応募資格','手当','応募方法','',NOW(),NOW()),
  (2,4,3,1,1000,2000,'原稿2','PR文','お仕事情報','給与','勤務時間','休日','応募資格','手当','応募方法','',NOW(),NOW()),
  (3,4,5,1,1000,2000,'原稿3','PR文','お仕事情報','給与','勤務時間','休日','応募資格','手当','応募方法','',NOW(),NOW());

# TRUNCATE table recruitment_occupation;
INSERT INTO recruitment_occupation (recruitment_id, occupation_id) VALUES
  (1, 1),
  (1, 2),
  (2, 3),
  (2, 4),
  (3, 5),
  (3, 6);

# TRUNCATE table recruitment_specific_tag;
INSERT INTO recruitment_specific_tag (recruitment_id, specific_tag_id) VALUES
  (1, 1),
  (1, 2),
  (2, 3),
  (2, 4),
  (3, 5),
  (3, 6);

# TRUNCATE table recruitment_photos;
INSERT INTO recruitment_photos (recruitment_id, resource_name) VALUES
  (1, 'sample.jpg'),
  (1, 'sample.jpg'),
  (2, 'sample.jpg'),
  (2, 'sample.jpg'),
  (3, 'sample.jpg'),
  (3, 'sample.jpg');

# TRUNCATE table option_ads;
INSERT INTO option_ads (store_id, option_id, occupation_id, resource_name, start_date, end_date, created_at) VALUES
  (1, 1, 1, '', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW()),
  (2, 2, 3, '', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW()),
  (3, 3, 5, '', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW());

 # TRUNCATE table entries;
INSERT INTO entries (user_id, recruitment_id, created_at) VALUES
  (1, 1, NOW()),
  (2, 1, NOW()),
  (1, 2, NOW()),
  (2, 2, NOW()),
  (1, 3, NOW()),
  (2, 3, NOW());

# TRUNCATE table entry_occupation;
INSERT INTO entry_occupation (entry_id, occupation_id) VALUES
  (1, 1),
  (2, 2),
  (3, 3),
  (4, 4),
  (5, 5),
  (6, 6);

# TRUNCATE table questions;
INSERT INTO questions (status_id, entry_id, text, created_at) VALUES
  (1, 1, '質問1', NOW()),
  (1, 2, '質問2', NOW()),
  (2, 3, '質問3', NOW()),
  (1, 4, '質問4', NOW()),
  (1, 5, '質問5', NOW()),
  (2, 6, '質問6', NOW());

# TRUNCATE table answers;
INSERT INTO answers (admin_user_id, question_id, text, created_at) VALUES
  (6, 3, '回答1', NOW()),
  (6, 6, '回答2', NOW());

# TRUNCATE table billings;
INSERT INTO billings (published_contract_id, created_at, updated_at) VALUES
  (1, NOW(), NOW()),
  (2, NOW(), NOW()),
  (3, NOW(), NOW()),
  (4, NOW(), NOW()),
  (5, NOW(), NOW()),
  (6, NOW(), NOW());