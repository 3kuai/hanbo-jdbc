druid+connector/J
--
功能： 集成mysqlx,replication等协议实现客户端自动选主功能，结合服务端群组复制（单主模式）

配置
--

###### connect/J 单主模式设置:replicationEnableJMX=true&replicationConnectionGroup=default （动态切换主备的开关）
`cj.replicationUrl=jdbc:mysql:replication://address=(type=master)(host=192.168.32.128)(port=24802),address=(type=slave)(host=192.168.32.128)(port=24801),address=(type=slave)(host=192.168.32.128)(port=24803)/test?useSSL=false&autoReconnect=true&roundRobinLoadBalance=true&replicationEnableJMX=true&replicationConnectionGroup=default
cj.userName=root
cj.passWord=root
cj.driverClass=com.mysql.cj.jdbc.Driver`
###### 故障转移配置,优先级按照出现在list的顺序
`cj.failoverUrl=jdbc:mysql://192.168.32.128:24801,192.168.32.128:24802,192.168.32.128:24803/test?characterEncoding=UTF-8&useSSL=false`