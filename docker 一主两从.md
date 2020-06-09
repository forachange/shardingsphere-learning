# docker 一主两从

# 0 环境
- CentOS Linux release 7.3.1611 (Core)
- Docker version 19.03.9, build 9d988398e7
- MySQL 5.7.30

# 1 pull mysql
拉取 mysql 5.7 最新版本
```
docker pull mysql:5.7
```

# 2 服务器上创建文件夹，用于存储配置文件与数据文件
```
mkdir /opt/data/{mysql-a,mysql-b,mysql-c}/{conf,data,logs}
```

主服务器的配置
``` properties
# 如果是移动互联网，使用utf8mb4，可以保存表情（需要4个字节，utf8只有3个字节）
# master 配置
[client]
default-character-set=utf8mb4

[mysql]
default-character-set=utf8mb4

[mysql.server]
default-character-set=utf8mb4

[mysqld]

character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci

default-time_zone = '+8:00'

init_connect='SET collation_connection=utf8mb4_unicode_ci'
init_connect='SET NAMES utf8mb4'
character-set-client-handshake = FALSE

sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
max_connections=1000

lower_case_table_names=1


# gtid 同一集群内要唯一，最好根据 IP 最后一段
server_id = 1
gtid_mode = on
enforce_gtid_consistency = on

# binlog 开启二进制日志功能，可以随便取（关键）
log_bin = mysql-bin
log-slave-updates = 1
binlog_format = row
sync-master-info = 1
sync_binlog = 1
# 日志过期时间，天
expire_logs_days = 10

# relay log
skip_slave_start = 1
```

slave 配置，基本与 master 一样
```properties

# gtid 同一集群内要唯一，最好根据 IP 最后一段
server_id = 2  

# relay log
read_only = on
```

# 3 运行 docker 里的 mysql
```
docker run -p 23306:3306 --name mysql-a \
-v /opt/data/mysql-a/conf/my.cnf:/etc/my.cnf \
-v /opt/data/mysql-a/logs:/var/log/mysql \
-v /opt/data/mysql-a/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
-e TZ=Asia/Shanghai \
-d mysql:5.7 


docker run -p 33306:3306 --name mysql-b \
-v /opt/data/mysql-b/conf/my.cnf:/etc/my.cnf \
-v /opt/data/mysql-b/logs:/var/log/mysql \
-v /opt/data/mysql-b/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
-e TZ=Asia/Shanghai \
-d mysql:5.7 

docker run -p 43306:3306 --name mysql-c \
-v /opt/data/mysql-c/conf/my.cnf:/etc/my.cnf \
-v /opt/data/mysql-c/logs:/var/log/mysql \
-v /opt/data/mysql-c/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
-e TZ=Asia/Shanghai \
-d mysql:5.7 
```

# 4 配置主服务
```
docker exec -it mysql-a /bin/bash

# 登录
mysql -u root -p

# 创建同步账号

CREATE USER 'repl'@'%' IDENTIFIED BY '123456';

GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'repl'@'%';
```

# 5 配置从服务
```
docker exec -it mysql-b /bin/bash

# 登录
mysql -u root -p

# 配置主服务器信息

stop slave;

change master to
    master_host = '192.168.91.38',
    master_port = 23306,
    master_user = 'repl',
    master_password = '123456';

start slave;

show slave status \G;
```