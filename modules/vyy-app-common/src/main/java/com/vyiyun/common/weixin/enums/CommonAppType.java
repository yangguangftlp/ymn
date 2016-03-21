/**
 * 
 */
package com.vyiyun.common.weixin.enums;

/**
 * @author tf
 * 
 * @date 下午5:02:18
 */
public interface CommonAppType {
	public int index();

	public String value();

	enum CommandType implements CommonAppType {
		同意(1), 退回(2), 拒绝(3), 确认(4);
		// 成员变量
		private int index;

		// 构造方法
		private CommandType(int index) {
			this.index = index;
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum AccountType implements CommonAppType {
		// D为部门，T为标签，U为用户
		D(0), T(1), U(2);
		private int index = 0;

		private AccountType(int index) {
			this.index = index;
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum EntityType implements CommonAppType {
		// QD为签到，QJ为请假，BX为报销，SP为审批，HY为会议室预定，XM为项目管理,JK借款,PJ评价,JB
		QD(0), QJ(1), BX(2), SP(3), HY(4), XM(5), JK(6), PJ(7), JB(8);
		private int index;

		private EntityType(int index) {
			this.index = index;
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum PersonType implements CommonAppType {
		// SH为审核，CS为抄送，CW为财务,JQ加签,BPJ被评价,PJ评价,JB加班,CK查看
		SH(0), CS(1), CW(2), JQ(3), BPJ(4), PJ(5), JB(6), CK(7);

		private int index;

		private PersonType(int index) {
			this.index = index;
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum Status implements CommonAppType {
		// 0-草稿，1-待审核，2-审核中，3-已审核，4-待报销，5-已报销，6-实施中，7-结束确认中，8-已结束
		草稿(0), 待审核(1), 审核中(2), 已审核(3), 待报销(4), 已报销(5), 实施中(6), 结束确认中(7), 已结束(8), 确认中(9), 审核退回(-1), 报销退回(-2), 借款退回(-3), 已通过(10), 未评价(
				11), 评价中(12), 已评价(13);

		private int index = 0;

		private Status(int index) {
			this.index = index;
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum SignType implements CommonAppType {
		// KQ为考勤签到，QT为其他签到
		KQ(0), QD(1);
		private int index;

		private SignType(int index) {
			this.index = index;
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum AbsentType implements CommonAppType {
		// 0-事假, 1-病假, 2-丧假, 3-产假, 4-特殊休假, 5-其他;
		事假(0), 病假(1), 丧假(2), 产假(3), 特殊休假(4), 其他(5);
		private int index = 0;

		private AbsentType(int index) {
			this.index = index;
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum WorkdayFlag implements CommonAppType {
		// 0-不上班, 1-上班;
		NO(0), YES(1);

		private int index = 0;

		private WorkdayFlag(int index) {
			this.index = index;
		}

		public static WorkdayFlag valueOf(int index) {
			switch (index) {
			case 0:
				return NO;
			case 1:
				return YES;
			default:
				return null;
			}
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum CostCategory implements CommonAppType {
		交通费(1), 出差补贴(2), 住宿补贴(3), 住宿费(4), 房租(5), 中介费(6), 押金(7), 招待费(8), 会务费(9), 通讯费(10),
		标书费(11), 固定资产(12), 低值易耗品(13), 加油费(14), 车辆使用费(15), 水电燃气费(16), 网络费(17), 快递费(18), 福利费(19), 其他(20);

		private int index = 1;

		private CostCategory(int index) {
			this.index = index;
		}

		public static CostCategory valueOf(int index) {
			switch (index) {
			case 1:
				return 交通费;
			case 2:
				return 出差补贴;
			case 3:
				return 住宿补贴;
			case 4:
				return 住宿费;
			case 5:
				return 房租;
			case 6:
				return 中介费;
			case 7:
				return 押金;
			case 8:
				return 招待费;
			case 9:
				return 会务费;
			case 10:
				return 通讯费;
			case 11:
				return 标书费;
			case 12:
				return 固定资产;
			case 13:
				return 低值易耗品;
			case 14:
				return 加油费;
			case 15:
				return 车辆使用费;
			case 16:
				return 水电燃气费;
			case 17:
				return 网络费;
			case 18:
				return 快递费;
			case 19:
				return 福利费;
			case 20:
				return 其他;
			default:
				return null;
			}
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum LoanType implements CommonAppType {
		// 0-对私, 1-对公;
		对私(0), 对公(1);

		private int index = 0;

		private LoanType(int index) {
			this.index = index;
		}

		public static LoanType valueOf(int index) {
			switch (index) {
			case 0:
				return 对私;
			case 1:
				return 对公;
			default:
				return null;
			}
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum FeedbackStatus implements CommonAppType {
		// 0-待解决, 1-已解决;
		待解决(0), 已解决(1);

		private int index = 0;

		private FeedbackStatus(int index) {
			this.index = index;
		}

		public static FeedbackStatus valueOf(int index) {
			switch (index) {
			case 0:
				return 待解决;
			case 1:
				return 已解决;
			default:
				return null;
			}
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}

	enum AppAccessType implements CommonAppType {
		// 0-人员, 1-应用;
		USER(0), APP(1);

		private int index = 0;

		private AppAccessType(int index) {
			this.index = index;
		}

		public static AppAccessType valueOf(int index) {
			switch (index) {
			case 0:
				return USER;
			case 1:
				return APP;
			default:
				return null;
			}
		}

		public int index() {
			return this.index;
		}

		public String value() {
			return String.valueOf(index);
		}
	}
}