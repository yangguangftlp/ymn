/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50621
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2015-12-13 17:02:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tb_sys_config`
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_config`;
CREATE TABLE `tb_sys_config` (
  `ID` varchar(32) NOT NULL COMMENT '唯一ID',
  `CorpId` varchar(64) DEFAULT NULL COMMENT '企业corpId',
  `Name` varchar(100) DEFAULT NULL COMMENT '名称',
  `Value` varchar(300) DEFAULT NULL COMMENT '配置值',
  `Type` varchar(10) DEFAULT NULL COMMENT '配置类型',
  `Desc` varchar(300) DEFAULT NULL COMMENT '描述',
  `Content` varchar(200) DEFAULT NULL COMMENT '内容',
  `Px` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统配置表';

-- ----------------------------
-- Records of tb_sys_config
-- ----------------------------

-- ----------------------------
-- Table structure for `tb_sys_status`
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_status`;
CREATE TABLE `tb_sys_status` (
  `ID` char(32) NOT NULL COMMENT '唯一ID',
  `CorpId` varchar(64) DEFAULT NULL,
  `Type` varchar(32) DEFAULT NULL COMMENT '类型',
  `Name` varchar(100) DEFAULT NULL COMMENT '状态名称',
  `Status` varchar(10) DEFAULT NULL COMMENT '状态',
  `Value` varchar(128) DEFAULT NULL COMMENT '状态值',
  `Px` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统状态配置';

-- ----------------------------
-- Records of tb_sys_status
-- ----------------------------

-- ----------------------------
-- Table structure for `tb_wx_dept`
-- ----------------------------
DROP TABLE IF EXISTS `tb_wx_dept`;
CREATE TABLE `tb_wx_dept` (
  `ID` varchar(32) NOT NULL COMMENT '部门id',
  `CorpId` varchar(64) NOT NULL COMMENT '企业corpId',
  `Name` varchar(32) NOT NULL COMMENT '部门名称',
  `Parentid` varchar(5) DEFAULT NULL COMMENT '父亲部门id。根部门为1',
  `Order` int(11) DEFAULT NULL COMMENT '在父部门中的次序值。order值小的排序靠',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='微信部门';

-- ----------------------------
-- Records of tb_wx_dept
-- ----------------------------

-- ----------------------------
-- Table structure for `tb_wx_tag`
-- ----------------------------
DROP TABLE IF EXISTS `tb_wx_tag`;
CREATE TABLE `tb_wx_tag` (
  `Tagid` varchar(32) NOT NULL COMMENT '标签ID',
  `CorpId` varchar(64) NOT NULL COMMENT '企业corpId',
  `Tagname` varchar(32) NOT NULL COMMENT '标签名称',
  PRIMARY KEY (`Tagid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='微信标签';

-- ----------------------------
-- Records of tb_wx_tag
-- ----------------------------

-- ----------------------------
-- Table structure for `tb_wx_user`
-- ----------------------------
DROP TABLE IF EXISTS `tb_wx_user`;
CREATE TABLE `tb_wx_user` (
  `Userid` varchar(32) NOT NULL COMMENT '用户ID',
  `CorpId` varchar(64) NOT NULL COMMENT '企业corpId',
  `Name` varchar(64) NOT NULL COMMENT '名称',
  `Department` varchar(248) DEFAULT NULL COMMENT '部门',
  `Position` varchar(128) DEFAULT NULL,
  `Mobile` varchar(20) DEFAULT NULL,
  `Gender` char(1) DEFAULT NULL COMMENT '性别。0表示未定义，1表示男性，2表示女性',
  `Email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `Weixinid` varchar(128) DEFAULT NULL COMMENT '微信号',
  `Avatar` varchar(300) DEFAULT NULL COMMENT '头像url。注：如果要获取小图将url最后的"/0"改成"/64"即可',
  `Status` varchar(2) DEFAULT NULL COMMENT '关注状态: 1=已关注，2=已冻结，4=未关注',
  `Extattr` longtext COMMENT '扩展属性',
  PRIMARY KEY (`Userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='微信用户';

-- ----------------------------
-- Records of tb_wx_user
-- ----------------------------
