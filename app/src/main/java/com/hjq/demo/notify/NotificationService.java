package com.hjq.demo.notify;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.hjq.demo.R;
import com.hjq.demo.login.LoginActivity;
import com.hjq.demo.main.MainActivity;
import com.hjq.demo.overall.ConnectionUtils;
import com.hjq.demo.overall.DataMessage;
import com.hjq.demo.overall.SPUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 后台监听消息推送的服务
 */
public class NotificationService extends Service
{
    private Context context;

    private static final String EXCHANGE_NAME = "app_msg_push_exchange";
    private static String QUEUE_NAME = "";

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {  // 当服务第一次被开启的时候调用
        super.onCreate();

        context = this;

        //设置每个用户对应一个队列
        if (TextUtils.isEmpty((String) SPUtils.getParam(context, "QUEUE_NAME", "")))
        {
            SPUtils.setParam(context, "QUEUE_NAME", "app_msg_push_queue_" + UUID.randomUUID());
        }

        QUEUE_NAME = (String) SPUtils.getParam(context, "QUEUE_NAME", "");

        getDataFromMQ();
    }

    /**
     * 从消息队列获取数据
     */
    private void getDataFromMQ()
    {
        new Thread(() -> {
            try
            {
                // 获取到连接
                Connection connection = ConnectionUtils.getConnection();
                // 获取通道
                final Channel channel = connection.createChannel();
                // 声明队列
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                // 绑定队列到交换机，同时指定需要订阅的routing key。
//                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "app.s666666s");
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "app.s"+ DataMessage.temp_read("account")+"s");
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "app.msg.#");
                // 定义队列的消费者
                DefaultConsumer consumer = new DefaultConsumer(channel)
                {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
                    {
                        // body 即消息体
                        final String msg = new String(body);
                        showNotifictionIcon(context, "轮子哥", msg);
                        //手动设置ACK
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                };
                // 监听队列，手动ACK
                channel.basicConsume(QUEUE_NAME, false, consumer);
            } catch (IOException | TimeoutException e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    public void showNotifictionIcon(Context context, String title, String content)
    {
        //Android 8.0+
        NotificationChannel channel;//重要性
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("lunzige"//唯一id
                    , "通知"//渠道名
                    , NotificationManager.IMPORTANCE_DEFAULT);

            //设置渠道属性
            channel.enableLights(true);//呼吸灯
            channel.setShowBadge(true);//是否要显示到桌面应用图标上

            //getSystemService
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            //创建
            Notification.Builder builder = new Notification.Builder(context);
            Intent intent = new Intent(context, LoginActivity.class);//跳转的界面
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            Notification notification = builder.setSmallIcon(R.mipmap.logo_app)//应用图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo_app))//消息配套图片
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)//点击后是否删除
//                            .setProgress(100, 30, true)//进度条最大现在是否变化
                    .setChannelId(channel.getId())
                    //Intent 意向意图 启动Activity,Service
                    //PendingIntent 对Intent的封装，将来要干什么，延迟的意向
                    .setContentIntent(pendingIntent)//点击通知将要做什么
                    .build();//创建一个通知

            notificationManager.notify(1, notification);
        }
    }

    /**
     * 创建通知栏消息点击后跳转的intent。
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    public PendingIntent createIntent(Context context, String data)
    {
        Intent intent = new Intent(context, MainActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("data", data);
        intent.putExtras(mBundle);
        intent.setAction("com.hjq.demo");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
