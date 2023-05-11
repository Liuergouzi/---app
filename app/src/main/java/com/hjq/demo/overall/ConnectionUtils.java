package com.hjq.demo.overall;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 配置RabbitMQ连接
 */
public class ConnectionUtils
{
    public static Connection getConnection() throws IOException, TimeoutException
    {
        ConnectionFactory conn = new ConnectionFactory();
        conn.setHost("ctrlc.cc");
        conn.setPort(5672);
        conn.setUsername("rabbitmq 密码");
        conn.setPassword("rabbitmq 账号");
        return conn.newConnection();
    }
}