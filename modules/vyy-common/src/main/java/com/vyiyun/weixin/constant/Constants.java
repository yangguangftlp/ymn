/**
 * 
 */
package com.vyiyun.weixin.constant;

/**
 * 常量类
 * 
 * @author tf
 * 
 *         2015年6月25日
 */
public interface Constants {

	/**
	 * 微信相关配置
	 */
	String WEIXIN_APP_PATH = "weixin_app";
	/**
	 * 系统相关配置
	 */
	String VYIYUN_CONFIG_PATH = "vyiyun_config";

	/**
	 * 附件存储路径
	 */
	String ACCESSORY_STORAGE_PATH = "accessory_storage_path";

	/**
	 * 文件目录名称
	 */
	String FILE_DIR = "resource_dir";

	/**
	 * 图片缩放目录名称
	 */
	String ZOOM_DIR = "zoom_dir";

	/****************************** 系统类型 **************************************/

	/**
	 * 请假
	 */
	String SYSTEM_ABSENT_TYPE = "AbsentType";
	/**
	 * 账户类型
	 */
	String SYSTEM_ACCOUNT_TYPE = "AccountType";
	/**
	 * 出勤类型
	 */
	String SYSTEM_ATTEND_TYPE = "AttendType";
	/**
	 * 负责人类型
	 */
	String SYSTEM_CHIEF_TYPE = "ChiefType";
	/**
	 * 建设类型
	 */
	String SYSTEM_CONSTRUCTION_TYPE = "ConstructionType";
	/**
	 * 实体类型
	 */
	String SYSTEM_ENTITY_TYPE = "EntityType";
	/**
	 * 人员类型
	 */
	String SYSTEM_PERSON_TYPE = "PersonType";
	/**
	 * 职务/岗位
	 */
	String SYSTEM_POSITION = "Position";
	/**
	 * 职务/岗位类型
	 */
	String SYSTEM_POSITION_TYPE = "PositionType";
	/**
	 * 项目性质
	 */
	String SYSTEM_PROGRAM_NATURE = "ProgramNature";
	/**
	 * 项目所属阶段
	 */
	String SYSTEM_PROGRAM_STAGE = "ProgramStage";
	/**
	 * 项目类型
	 */
	String SYSTEM_PROGRAM_TYPE = "ProgramType";
	/**
	 * 签到类型
	 */
	String SYSTEM_SIGN_TYPE = "SignType";
	/**
	 * 状态枚举类
	 */
	String SYSTEM_STATUS = "Status";
	/**
	 * 报销费用类型
	 */
	String COST_CATEGORY = "CostCategory";
	/**
	 * 会议室总时间配置类型
	 */
	String SYSTEM_MEETING_ROOM_TIME = "MeetingRoomTime";
	/**
	 * 借款用途
	 */
	String SYSTEM_LOAN_USE = "LoanUse";
	/**
	 * 借款类型
	 */
	String SYSTEM_LOAN_TYPE = "LoanType";
	
	/************************************* 命令 *******************************************/
	/**
	 * 通用
	 */
	String CMD_GENERAL = "general";

	/**
	 * 拒绝
	 */
	String CMD_REFUSE = "refuse";

	/**
	 * 退回
	 */
	String CMD_ROLLBACK = "rollBack";
	/**
	 * 草稿
	 */
	String CMD_DRAFT = "draft";

	/**
	 * 催办/提醒
	 */
	String CMD_REMIND = "remind";

	/**
	 * 重新发起
	 */
	String CMD_AGAINLAUNCH = "againLaunch";

	/**
	 * 撤销申请
	 */
	String CMD_UNDO = "undo";

	/************************************* 常用常量 *******************************************/
	String EMPTY = "";
	String STRING_0 = "0";
	String STRING_1 = "1";
	String STRING_2 = "2";

	int ZERO = 0;
	int ONE = 1;
	int TWO = 2;
	int ERROR_CODE = -1;

}
