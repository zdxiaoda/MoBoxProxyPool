#!/bin/bash

# MoBoxProxyPool 启动脚本
# 基于腾讯云CVM的代理池管理系统

echo "启动MoBoxProxyPool..."

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java。请确保已安装Java 8或更高版本。"
    exit 1
fi

# 检查jar文件是否存在
if [ ! -f "target/MoBoxProxyPool-1.0-SNAPSHOT.jar" ]; then
    echo "错误: 未找到jar文件。请先运行 'mvn clean package -DskipTests' 进行打包。"
    exit 1
fi

# 检查系统依赖是否存在
if [ ! -f "target/lib/mosslib-1.0.0.jar" ]; then
    echo "错误: 未找到系统依赖。请重新运行 'mvn clean package -DskipTests'。"
    exit 1
fi

# 启动应用程序
java -cp "target/MoBoxProxyPool-1.0-SNAPSHOT.jar:target/lib/*" org.moboxlab.MoBoxProxyPool.Main 