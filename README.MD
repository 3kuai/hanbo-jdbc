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
###### 故障转移配置,仅配置组内的从节点，优先级数值越大 越优先选择
`cj.failoverUrl=mysqlx://root:root@[(address=192.168.32.128:24802,priority=2),(address=192.168.32.128:24803,priority=3)]/test?characterEncoding=UTF-8&useSSL=false`
