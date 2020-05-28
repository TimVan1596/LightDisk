# LightDisk
The Times 03/Jan/2009 Chancellor on brink of second bailout for banks.

#### 介绍
一个基于区块链的存储系统，可以在多个节点（设备）中，为用户生成非对称加密的公私钥对，并使用此密钥对，对数据进行加密上传，和解密下载。

#### Bug列表
1、局域网内外互联，未实现NAT穿透连接  
2、局域网内有一定几率存活节点被发现死亡  
3、仅支持Coinbase交易（普通交易开发中）

#### 软件架构
JDK 1.8+  
Spring Boot 2.0.1  
[Apache Gossip](https://gitee.com/TimVanX/incubator-retired-gossip)

#### 安装教程
1、命令行界面：配置JDK或JRE本地环境变量（版本号1.8+），解压并打开运行“open.bat”。  
2、Web本地操作台：暂无war包，需要使用Git导入此项目到 IntelliJ IDEA ，运行com.timvanx.Application入口，启动Spring Boot项目，默认入口地址为http://localhost:8080/。

#### 使用说明
##### 命令行界面
1.  若无种子节点，可以运行“作为种子节点登入”，将本机作为种子节点。
2.  若存在种子节点，可以选择“作为普通节点登入”，并输入种子节点的IP地址与端口号。
![命令行界面](https://images.gitee.com/uploads/images/2020/0528/114044_075ad114_1464254.jpeg "控制台.jpg")
##### Web本地操作台
基本功能同上  
![登录](https://images.gitee.com/uploads/images/2020/0528/114151_cbbfbde0_1464254.jpeg "登录.jpg")
![主页](https://images.gitee.com/uploads/images/2020/0528/114205_bcc49299_1464254.jpeg "主页.jpg")
![挖矿（上传文件）](https://images.gitee.com/uploads/images/2020/0528/114220_54149cad_1464254.jpeg "挖矿.jpg")
![新区块信息](https://images.gitee.com/uploads/images/2020/0528/114240_7357a944_1464254.jpeg "新区块界面.jpg")

#### 结构与框架
1、流程图  
![输入图片说明](https://images.gitee.com/uploads/images/2020/0528/121839_565cfe2a_1464254.jpeg "流程图‘.jpg")
2、区块链结构图，类似于比特币的数据结构，文件作为每组Merkle树的叶子节点数据的来源  
![输入图片说明](https://images.gitee.com/uploads/images/2020/0528/121855_5fd1e006_1464254.jpeg "区块链结构图.jpg")
3、技术框架
![输入图片说明](https://images.gitee.com/uploads/images/2020/0528/121904_40af6539_1464254.jpeg "技术框架.jpg")

#### 参与贡献
1、TimVan

