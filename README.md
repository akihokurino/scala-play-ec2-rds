## Used, Dependency
- scala
- play framework（ https://www.playframework.com/documentation/ja/2.4.x/Home ）
- scalikejdbc（ http://scalikejdbc.org/ ）
- aws-java-sdk
- mongodb
- rds
- ec2

## Deploy Flow Memo
1. lsof -i:9000
2. activator clean stage -mem 512
3. activator "start -Dapplication.secret='abcdefghijk' -Dconfig.resource=staging.conf"
4. activator "start -Dapplication.secret='abcdefghijk' -Dconfig.resource=production.conf"
