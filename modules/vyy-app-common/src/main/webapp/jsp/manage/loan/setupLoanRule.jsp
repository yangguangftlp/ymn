<!-- 设置对公，对私借款财务负责人页面 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>微移云管理系统</title>
<%@ include file="../../include/layout.jsp"%>
<link href="css/manage/contact.css" rel="stylesheet" type="text/css" />
</head>
<body class="frame-body">
	<!-- BEGIN PAGE HEADER-->
	<div class="row-fluid">
		<div class="span12">
			<!-- BEGIN PAGE TITLE & BREADCRUMB-->
			<h3 class="page-title">
				我的应用 <small>借款</small>
			</h3>
			<!-- END PAGE TITLE & BREADCRUMB-->
		</div>
	</div>
	<!-- END PAGE HEADER-->
	<!-- BEGIN MY APP-->
	<div class="app-set-fluid">
		<div class="tabbable tabbable-custom tabs-left">
			<!-- Only required for left/right tabs -->
			<ul class="nav nav-tabs tabs-left">
				<li><a href="manage/loan/privateLoanList.action">对私借款</a></li>
				<li><a href="manage/loan/publicLoanList.action">对公借款</a></li>
				<li class="active"><a href="manage/loan/setupLoanRule.action">借款设置</a></li>
				<li><a href="manage/loan/setupLoanUse.action">借款用途设置</a></li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane active">
					<div class="row-fluid">
						<div id="tab_3_3" class="tab-pane">
                            	<h5 class="set-hd-bar">
		                    		<strong>借款设置</strong>
									<a href="javascript:location.reload();" class="pull-right" title="刷新"><i class="icon-refresh"></i></a>
								</h5>
								<hr />
								<!--  -->
								<form action="" class="form-horizontal">
									<div id="addPublishAuditor" class="loan-director selectPeople">
										<input type="hidden" id="privateId" value = "${ privateId}" />
						                <div class="selectPeopleTit">对私借款财务负责人：</div>
						                <div class="selectPeopleList">
						                    <div id="addFlag0" class="addedPeopleList">
						                    	<c:if test="${privateCharger != null}">
													<div class="selpeo" data-uid="${privateCharger.userid }" data-name="${privateCharger.name }" data-uavatar="${privateCharger.avatar }">
														<img src="${privateCharger.avatar }">
														<span>${privateCharger.name }</span><i></i>
													</div>
												</c:if>
						                    </div>
						                      <div id="addPriPeo" class="addPeople">
					                        	   <img src="images/addPeople.gif">
					                          </div>
						                </div>
						            </div>
						            <!--  -->
						            <div id="addPrivateAuditor" class="selectPeople">
						            	<input type="hidden" id="publicId" value = "${publicId}" />
						                <div class="selectPeopleTit">对公借款财务负责人：</div>
						                <div class="selectPeopleList">
						                    <div id="addFlag1" class="addedPeopleList">
						                       <c:if test="${publicCharger != null}">
													<div class="selpeo" data-uid="${publicCharger.userid }" data-name="${publicCharger.name }" data-uavatar="${publicCharger.avatar }">
														<img src="${publicCharger.avatar }">
														<span>${publicCharger.name }</span><i></i>
													</div>
												</c:if>
						                    </div>
						                    <div id="addPubPeo" class="addPeople">
					                        	<img src="images/addPeople.gif">
					                       </div>
						                </div>
						
						            </div>
						            <!--  -->
						            <div class="form-actions">
	                                    <a href="javascript:void(0)" class="btn orange" id="save">保存设置</a>
	                                </div>
					            </form>
                            </div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="selectBox" tabindex="-1" role="dialog" 
	   aria-labelledby="selectBoxLabel" aria-hidden="true">
	   <div class="modal-dialog">
	      <div class="modal-content">
	         <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" 
	               aria-hidden="true">
	            </button>
	            <h4 class="modal-title" id="selectBoxLabel">
	              	选择人员
	            </h4>
	         </div>
	         <div class="modal-body">
	         	 <div class="selpeo-wrap">
	           	 	<div class="membersListWarp"></div>
	           	 </div>
	         </div> 
	      </div><!-- /.modal-content -->
	   </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	<!-- END MY APP-->
	<!-- BEGIN CORE PLUGINS -->
	<script src="js/manage/jquery-1.10.1.min.js" type="text/javascript"></script>
	<script src="js/manage/bootstrap.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/manage/mcommon.js"></script>
	<script type="text/javascript" src="js/manage/eSelectPeople.js"></script>
	<script type="text/javascript" src="js/manage/loan/loan-manage.js"></script>
	<!-- END CORE PLUGINS -->
	<script>
		$(function() {
			loanSetting.init();
		});
	</script>
</body>
</html>