package com.lmx.hanbo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cj")
public class HanboProperties {
    private String replicationUrl;
    private String userName;
    private String passWord;
    private String failoverUrl;
    private String driverClass;

    public String getReplicationUrl() {
        return replicationUrl;
    }

    public void setReplicationUrl(String replicationUrl) {
        this.replicationUrl = replicationUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getFailoverUrl() {
        return failoverUrl;
    }

    public void setFailoverUrl(String failoverUrl) {
        this.failoverUrl = failoverUrl;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }
}
