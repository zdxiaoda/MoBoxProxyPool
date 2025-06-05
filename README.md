# MoBoxProxyPool 
## ——墨盒代理池管理系统 

### 兼容性
| key    | value |
|--------|-------|
| Java   | 8+    |
| Centos | 7.9   |

### 优势
- 绝绝子降低代理池成本
- 参考价格：
- iproyal家宽http代理：35RMB/G流量，IP池不确定深浅
- 基于MBPP的阿里云http代理：0.8RMB/G流量+0.03/IP/小时
- 可设置IP存活时间，超时自动换IP
- 全自动化服务器部署

### 代码流程
- 括号中对应实例状态标识和是否可用标识
- 1.创建实例（状态：CREATED/False）
- 2.等待实例启动（状态：CREATED/False）
- 3.自动化部署终端（状态：INSTALLING/False）
- 4.部署完成（状态：RUNNING/True）
- 5.轮询实例状态（状态：RUNNING/True）
- 6.到期进入释放态（状态：CYCLING/False）
- 7.原实例释放，新实例开始创建（状态：CYCLING/False）

### 客户端请求主控参数
- 请求方法：POST
- 请求格式：JSON
- 请求数据示例：
```
{
    "type": "getProxy",
    "token": "123456",
    "sessionID": "abcd"
    
}
```
其中，sessionID为自行生成的一串字符串<br>
在IP池未耗尽的情况下，主控会优先分配该sessionID未使用过的IP
- 响应数据示例：
```
{
    "status": True,
    "proxyHost": "123.45.67.89:22339",
    "proxyUser": "mosscg",
    "proxyPassword": "123456",
    "remain": "123421"
}
```
其中，remain代表该IP剩余有效时间，单位为毫秒

### 如何部署
1. 毫无云服务器经验的用户左转出门哦 
2. 阿里云创建权限用户（https://ram.console.aliyun.com/users）
3. 保存创建用户的AccessKeyID和AccessKeySecret备用，给该用户ecs全部权限
4. 去阿里云ecs选择你心仪的ecs规格和地区，保存备用
5. 在阿里云配置好安全组策略，无脑开放全部端口即可
6. 准备一台服务器，安装java8，运行本jar
7. 填写位于MBProxyPool/config.yml的配置文件
8. 保证对应httpAPI端口开放，再次启动本软件
9. 正常情况下会自动启动并调用ecs接口
10. 客户端通过API获取代理信息并配置

- 客户端代码参考（Python）
```
session = requests.Session()
def getProxy():
    api = "http://127.0.0.1:7891/API"
    req = {
        "token": "123456789abc",
        "type": "getProxy",
        "sessionID": "test"
    }
    rs = session.post(api, json=req, headers=headers).json()
    print(rs)
    username = rs["proxyUser"]
    password = rs["proxyPassword"]
    host = rs["proxyHost"]
    proxyURL = 'http://' + username + ':' + password + '@' + host
    proxies = {
        'http': proxyURL,
        'https': proxyURL,
    }
    session.proxies = proxies
```
- 建议补充连通性测试，请自行编写