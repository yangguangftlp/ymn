#部署脚本 
#1 解压部署zip包 2 停止tomcat运行 3 开始备份energy-web应用 、备份数据库
#4 将安装包mv 至webapps目录 5启动tomcat 6查看安装日志以及启动日志
#! /bin/sh
#解压部署zip包

#当前目录
CURRENT_PATH=`pwd`
#tomcat 目录
TOMCAT_PATH='apache-tomcat-7.0.40/'
#备份目录
BACKUP_DIR='backup_`date + "%Y-%m-%d"`'
#发布包 energy-web-1.0-SNAPSHOT.war
DEPLOY_PAKAGE=$1 


#进度值
progressValue=''

#开始
function start (){
     #停止tomcat
    cd $CURRENT_PATH/$TOMCAT_PATH/bin
	./shutdown.sh
	 #初始化备份目录 如果不存在将创建
	if [ -d $CURRENT_PATH/$BACKUP_DIR];then
	   echo  "目录存在"
	else
	    mkdir $CURRENT_PATH/$BACKUP_DIR
	fi
}
#执行
function execute(){
	cd  cd $CURRENT_PATH/$TOMCAT_PATH/webapps/
	#创建备份文件
	tar -cvf energy-web.tar energy-web
	mv  energy-web.tar $CURRENT_PATH/$BACKUP_DIR/energy-web_`date + '%Y-%m-%d %H:%M:%S'`.tar
	rm -rf energy-web/*
	cp $CURRENT_PATH/$DEPLOY_PAKAGE energy-web/
	cd energy-web;jar -xvf $DEPLOY_PAKAGE
	cd $CURRENT_PATH/$TOMCAT_PATH/bin
	./startup.sh 
}
#完成
function complete(){
}


#执行
progressValue=''
echo 'get the deploy and prepare install...'
start()> deploy.log



echo 'complete install...'

