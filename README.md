# 分布式 websocket 解决方案

## netty-websocket-cluster

解决 websocket 集群部署的问题，所用技术栈如下
netty
websocket
rabbitMq
redis


## 架构对比

### 传统 websocket

![image-20221031172750222](https://charge-up.oss-cn-beijing.aliyuncs.com/mdImg/2022/10/31/e17517633d5c5d03ed920983d085708c-image-20221031172750222-5d4d1e.png)

### 负载均衡 websocket

![image-20221031180011642](https://charge-up.oss-cn-beijing.aliyuncs.com/mdImg/2022/10/31/fcb2535f6de47b90b2b10429f0177df3-image-20221031180011642-c53d92.png)

### `netty-websocket-cluster` 分布式集群 websocket

![image-20221031180111174](https://charge-up.oss-cn-beijing.aliyuncs.com/mdImg/2022/10/31/3e23ecc494e0383988cba8d76c142529-image-20221031180111174-c2d50c.png)

#### 服务注册与启动

```sequence
participant server_node
participant nacos
participant redis
participant rabbitMq
server_node -> server_node: 服务启动
server_node -> nacos: 获取配置
nacos -> server_node: yml
server_node -> server_node: cron 定时维护节点数据
Note right of server_node: 循环间隔时间小于缓存失效时间
server_node -> redis: 缓存 node 节点数据
server_node -> rabbitMq: 声明交换机
server_node -> rabbitMq: 声明绑定交换机的队列(属性：自动删除)
```

#### 客户端建立连接


```sequence
participant client
participant gateway
participant server
participant redis
client -> gateway:连接
gateway -> gateway: nacos 注册中心找寻在线 server 
gateway -> server: 建立 websocket 连接
server -> redis: 缓存用户数据、用户所在节点
server -> client: pong
```

#### 推送消息给某个用户（1 对 1）

```sequence
participant client1
participant gateway
participant server_node_1
participant server_node_...
participant rabbitMq
participant client2
client1 -> gateway: 发送消息给某人
gateway -> server_node_1: 负载均衡
Note over server_node_1, server_node_...: 任意 server node 都可以提供 api 服务
server_node_1 -> server_node_1: 构建消息 MsgDTO
server_node_1 -> rabbitMq: 传输消息到队列
rabbitMq -> rabbitMq: fanout 到所有绑定交换机的队列
rabbitMq -> server_node_1: 消费消息
rabbitMq -> server_node_...: 消费消息
Note over server_node_1, server_node_...: 判断目标用户是否在此节点建立了 socket 连接
server_node_1 -> server_node_1: 否
server_node_... -> server_node_...: 是
server_node_... -> client2: 送达消息
```

**此处消息 DTO 用于在服务间传递**

```java
public class MsgDTO {
    private Object content;
    private Boolean allUser;
    private List<User> targetUserList;
}
```

#### 推送消息给所有用户（或者一批用户）

```sequence
participant client1
participant gateway
participant server_node_1
participant server_node_...
participant rabbitMq
participant client2
participant client3
participant client4
client1 -> gateway: 发送消息给全部人
gateway -> server_node_1: 负载均衡
Note over server_node_1, server_node_...: 任意 server node 都可以提供 api 服务
server_node_1 -> server_node_1: 构建消息 MsgDTO
server_node_1 -> rabbitMq: 传输消息到队列
rabbitMq -> rabbitMq: fanout 到所有绑定交换机的队列
rabbitMq -> server_node_1: 消费消息
rabbitMq -> server_node_...: 消费消息
Note over server_node_1, server_node_...: 判断目标用户是否在此节点建立了 socket 连接
server_node_1 -> server_node_1: 是
server_node_1 -> client2: 送达消息
server_node_... -> server_node_...: 是
server_node_... -> client3: 送达消息
server_node_... -> client4: 送达消息
```

#### 多节点追求高性能调整

上述模式都使用了 `rabbitMq` 的 `fanout` 来广播消息到每个 `server_node` ，消费者来自行处理有则发送无则抛弃。在节点较多的情况下会产生大量冗余消息，造成性能问题。以下方案可以解决，那就是依据 [#客户端建立连接](#客户端建立连接) 中存储的用户与节点的关系，来判断真正的消费者是哪个 `server_node`

```sequence
participant client1
participant gateway
participant server_node_1
participant server_node_...
participant redis
participant rabbitMq
participant client2
client1 -> gateway: 发送消息给某人
gateway -> server_node_1: 负载均衡
Note over server_node_1, server_node_...: 任意 server node 都可以提供 api 服务
server_node_1 -> server_node_1: 构建消息 MsgDTO
server_node_1 -> redis: 获取目标用户所在机器信息
redis -> server_node_1: 获取目标用户所在机器信息
server_node_1 -> rabbitMq: 发送消息到指定交换机的指定路由
rabbitMq -> server_node_...: 目标用户所在服务器进行消费
server_node_... -> client2: 送达消息
```
