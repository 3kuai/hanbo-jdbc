# hanbo-starter
hanbo-starter是一个springbootStarter，适用于mysql5.7+的群组复制（单主模式）

基于connector/J的mysqlx、replication等协议实现的客户端自动选主以及负载均衡

## 打包
因为没有发布到中央库，需要手动打包上传到自己的私服

    cd proDir && mvn clean install

## 添加maven依赖：
        <dependency>
            <groupId>com.lmx</groupId>
            <artifactId>hanbo-starter</artifactId>
            <version>0.0.2-snapshot</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.11</version>
        </dependency>

## springboot配置： 
application.properties

    #单主模式配置
    cj.replicationUrl=jdbc:mysql:replication://address=(type=master)(host=192.168.32.128)(port=24802),address=(type=slave)(host=192.168.32.128)(port=24801),address=(type=slave)(host=192.168.32.128)(port=24803)/test?useSSL=false&autoReconnect=true
    cj.userName=root
    cj.passWord=root
    cj.driverClass=com.mysql.cj.jdbc.Driver
    cj.maxWait=10000
    cj.maxConn=64
    #故障转移配置,优先级按照出现在list的顺序
    cj.failoverUrl=jdbc:mysql://192.168.32.128:24801,192.168.32.128:24802,192.168.32.128:24803/test?characterEncoding=UTF-8&useSSL=false

## 使用
结合事务注解`@Transactional`来享受上述特性


    /**
     * readonly=true，走从库连接 ，如果强行执行写操作会抛出异常
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "redisCacheManager", cacheNames = "app:user", key = "#id")
    public User getUser(long id) {
        return userRep.findOne(id);
    }

    /**
     * readonly=false，走主库连接 
     * @param id
     * @return
     */
    @Transactional
    @CacheEvict(cacheManager = "redisCacheManager", cacheNames = "app:user", key = "#id")
    public User getTXUser(long id) {
        User user = userRep.findOne(id);
        user.setC2(UUID.randomUUID().toString().substring(0, 10));
        userRep.save(user);
        User user1 = new User();
        user1.setC2(UUID.randomUUID().toString().substring(0, 10));
        user1.setC3(Math.random() * 10000 + "");
        user1.setC4(new Date());
        userRep.save(user1);
        return userRep.findOne(id);
    });
    }
