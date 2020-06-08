# master slave 示例

示例代码来自 Sharding Sphere 官方 `https://github.com/apache/shardingsphere/tree/master/examples`    

## 环境信息

- spring boot 2.2.7
- mysql 5.7.30
- sharding sphere 4.1.0

## 主要配置

1. pom.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
   	<modelVersion>4.0.0</modelVersion>
   	<parent>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-starter-parent</artifactId>
   		<version>2.2.7.RELEASE</version>
   		<relativePath/> <!-- lookup parent from repository -->
   	</parent>
   	<groupId>com.xin.sharding</groupId>
   	<artifactId>read-write-demo</artifactId>
   	<version>0.0.1-SNAPSHOT</version>
   	<name>read-write-demo</name>
   	<description>Demo project for Spring Boot</description>
   
   	<properties>
   		<java.version>1.8</java.version>
   		<shardingsphere.version>4.1.0</shardingsphere.version>
   	</properties>
   
   	<dependencies>
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter</artifactId>
   		</dependency>
   		<dependency>
   			<groupId>org.mybatis.spring.boot</groupId>
   			<artifactId>mybatis-spring-boot-starter</artifactId>
   			<version>2.1.2</version>
   		</dependency>
   
   		<dependency>
   			<groupId>mysql</groupId>
   			<artifactId>mysql-connector-java</artifactId>
   			<scope>runtime</scope>
   			<version>5.1.48</version><!--$NO-MVN-MAN-VER$-->
   		</dependency>
   		<dependency>
   			<groupId>org.projectlombok</groupId>
   			<artifactId>lombok</artifactId>
   			<optional>true</optional>
   		</dependency>
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-test</artifactId>
   			<scope>test</scope>
   			<exclusions>
   				<exclusion>
   					<groupId>org.junit.vintage</groupId>
   					<artifactId>junit-vintage-engine</artifactId>
   				</exclusion>
   			</exclusions>
   		</dependency>
   		
   		<dependency>
               <groupId>org.apache.shardingsphere</groupId>
               <artifactId>sharding-core-api</artifactId>
               <version>${shardingsphere.version}</version>
           </dependency>
           <dependency>
               <groupId>org.apache.shardingsphere</groupId>
               <artifactId>encrypt-core-common</artifactId>
               <version>${shardingsphere.version}</version>
           </dependency>
           <dependency>
               <groupId>org.apache.shardingsphere</groupId>
               <artifactId>sharding-jdbc-core</artifactId>
               <version>${shardingsphere.version}</version>
           </dependency>
           <dependency>
               <groupId>org.apache.shardingsphere</groupId>
               <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
               <version>${shardingsphere.version}</version>
           </dependency>
   	</dependencies>
   
   	<build>
   		<plugins>
   			<plugin>
   				<groupId>org.apache.maven.plugins</groupId>
   				<artifactId>maven-compiler-plugin</artifactId>
   				<configuration>
   					<source>1.8</source>
   					<target>1.8</target>
   					<encoding>UTF-8</encoding>
   				</configuration>
   			</plugin>
   			<plugin>
   				<groupId>org.springframework.boot</groupId>
   				<artifactId>spring-boot-maven-plugin</artifactId>
   			</plugin>
   		</plugins>
   	</build>
   
   </project>
   ```

2. application.properties

   ```properties
   spring.application.name=read-write-demo
   
   mybatis.mapper-locations=classpath:mapper/*.xml
   
   spring.shardingsphere.datasource.names=ds-master,ds-slave-0,ds-slave-1
   
   spring.shardingsphere.datasource.ds-master.type=com.zaxxer.hikari.HikariDataSource
   spring.shardingsphere.datasource.ds-master.driver-class-name=com.mysql.jdbc.Driver
   spring.shardingsphere.datasource.ds-master.jdbc-url=jdbc:mysql://192.168.202.21:3306/demo_ds_master?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8
   spring.shardingsphere.datasource.ds-master.username=demo
   spring.shardingsphere.datasource.ds-master.password=pvcloud@2020
   
   spring.shardingsphere.datasource.ds-slave-0.type=com.zaxxer.hikari.HikariDataSource
   spring.shardingsphere.datasource.ds-slave-0.driver-class-name=com.mysql.jdbc.Driver
   spring.shardingsphere.datasource.ds-slave-0.jdbc-url=jdbc:mysql://192.168.202.22:3306/demo_ds_master?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8
   spring.shardingsphere.datasource.ds-slave-0.username=demo
   spring.shardingsphere.datasource.ds-slave-0.password=pvcloud@2020
   
   spring.shardingsphere.datasource.ds-slave-1.type=com.zaxxer.hikari.HikariDataSource
   spring.shardingsphere.datasource.ds-slave-1.driver-class-name=com.mysql.jdbc.Driver
   spring.shardingsphere.datasource.ds-slave-1.jdbc-url=jdbc:mysql://192.168.202.23:3306/demo_ds_master?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8
   spring.shardingsphere.datasource.ds-slave-1.username=demo
   spring.shardingsphere.datasource.ds-slave-1.password=pvcloud@2020
   
   spring.shardingsphere.masterslave.load-balance-algorithm-type=round_robin
   spring.shardingsphere.masterslave.name=ds-ms
   spring.shardingsphere.masterslave.master-data-source-name=ds-master
   spring.shardingsphere.masterslave.slave-data-source-names=ds-slave-0,ds-slave-1
   
   # 开启SQL显示，默认值: false
   spring.shardingsphere.props.sql.show=true
   ```

3. 正常使用 mybatis 进行增删改查。

4. 运行结果中 `Actual SQL` 会显示使用了哪个数据源。查询会使用不同的从库，增删改会使用主库。

   ```
   [ShardingSphere-SQL] Actual SQL: ds-master ::: CREATE TABLE IF NOT EXISTS t_user (user_id INT NOT NULL AUTO_INCREMENT, user_name VARCHAR(200), user_name_plain VARCHAR(200), pwd VARCHAR(200), assisted_query_pwd VARCHAR(200), PRIMARY KEY (user_id)); 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: TRUNCATE TABLE t_user; 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: INSERT INTO t_user (user_id, user_name, pwd) VALUES (?, ?, ?) 
   [ShardingSphere-SQL] Actual SQL: ds-slave-0 ::: SELECT * FROM t_user; 
   [ShardingSphere-SQL] Actual SQL: ds-slave-1 ::: SELECT * FROM t_user; 
   [ShardingSphere-SQL] Actual SQL: ds-master ::: DROP TABLE IF EXISTS t_user; 
   ```

   