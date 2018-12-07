package com.lmx.hanbo;

import com.mysql.cj.jdbc.ha.ReplicationConnectionGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * <p>
 * 用于适配服务端（单主模式）重新选主后的故障转移
 * <p>
 * 假设集群中有一个存活，则拿这个server进行通信，若集群都全部不可以，可以发出系统不可用告警，人工介入
 * 可以利用此特性进行自动选主，查询当前master信息和初始化的master比较是否一致，不一致则进行master转移
 * <p>
 * Notice that this feature only allows for a failover when Connector/J is trying to establish a connection,
 * but not during operations after a connection has already been made.
 *
 * @see \\https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-client-side-failover.html
 */
public class AutoSelectivePrimaryService implements Runnable {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private HanboProperties hanboProperties;
    private Thread thread = new Thread(this);
    private final static String DEFAULT_GROUP = "default";
    private final String Q_SQL = "SELECT MEMBER_HOST,MEMBER_PORT " +
            "FROM performance_schema.replication_group_members " +
            "WHERE MEMBER_ID = (" +
            "  SELECT VARIABLE_VALUE" +
            "  FROM performance_schema.global_status" +
            "  WHERE VARIABLE_NAME= 'group_replication_primary_member'" +
            ");";
    private Properties properties = new Properties();

    {
        properties.put("roundRobinLoadBalance", "true");
        properties.put("replicationEnableJMX", "true");
        properties.put("replicationConnectionGroup", "default");
    }

    public AutoSelectivePrimaryService(HanboProperties hanboProperties) {
        this.hanboProperties = hanboProperties;
        this.init();
    }

    public void init() {
        properties.put("username", hanboProperties.getUserName());
        properties.put("password", hanboProperties.getPassWord());
        thread.setName("AutoSelectivePrimary-Thread");
        if (hanboProperties.getFailoverUrl() != null)
            thread.start();
    }

    @Override
    public void run() {
        logger.info("AutoSelectivePrimary-Thread is started");
        while (!Thread.currentThread().isInterrupted()) {
            this.selectivePrimary();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
            }
        }
    }

    private void selectivePrimary() {
        try (Connection connection = DriverManager.getConnection(hanboProperties.getFailoverUrl(), properties);
             PreparedStatement preparedStatement = connection.prepareStatement(Q_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            StringBuilder stringBuilder = new StringBuilder();
            while (resultSet.next()) {
                stringBuilder
                        .append(resultSet.getString(1))
                        .append(":")
                        .append(resultSet.getString(2));
            }
            String masterHost = stringBuilder.toString();
            Collection<String> hosts = ReplicationConnectionGroupManager.getMasterHosts(DEFAULT_GROUP);
            if (hosts.contains("") || hosts.contains("null") || hosts.contains(null)) {
                logger.warn("group has no master");
                //TODO 告警
                return;
            }            //不一致则切换
            if (!hosts.contains(masterHost)) {
                logger.info("start failover...");
                //故障转移：把原始的主删除，再把一个被选举为主的从切为主
                ReplicationConnectionGroupManager.removeMasterHost(DEFAULT_GROUP, (String) ((List) hosts).remove(0));
                ReplicationConnectionGroupManager.promoteSlaveToMaster(DEFAULT_GROUP, masterHost);
                logger.info("failover is ok now,current master node={}", masterHost);
            } else {
                logger.info("master {} is health now", masterHost);
            }
        } catch (Exception e) {
            //TODO 告警
            logger.error("", e);
        }
    }


}
