# zhihu-manage
知乎订单邮件微信通知

# 配置文件内容如下
```
server:
  port: 8888


# 配置京东参数模板
app:
  key: # 填自己得京东参数
  secret: # 填自己得京东参数
  
  

# 是否开启
open:
  mail: true # 开启邮件通知
  wx: false # 开启微信通知
  filterPaid: true # 是否开启过滤未付款订单



# 配置邮件发送着参数
spring:
  mail:
    port: 465
    default-encoding: UTF-8
    host: smtp.qq.com
    username: # 填自己配置得邮箱
    password: # 填邮箱生成得密码
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            require: true
          socketFactory:
            port: 465
            class: javax.net.ssl.SSLSocketFactory
            fallback: false



# 配置邮件接收者邮箱
mail:
  receive: #你自己得接收邮件邮箱

# 配置微信发送
wx:
  send:
    url: # 填写微信发送通知ULR




# 配置定时任务执行时间间隔
lyj:
  quartz:
    task: 0 */1 * * * ?  #每 5 分钟
```

# 注意事项
你只需要配置下你自己邮件发送所需要的参数就行
可以先参考下这篇文章：https://www.cnblogs.com/joker-dj/p/12693557.html
后续我自己再更新一篇操作文档

# 关于我

一个在国企的打工人，欢迎关注公众号： “技术从心” 一起学习交流，不定期更新一些技术和资源。