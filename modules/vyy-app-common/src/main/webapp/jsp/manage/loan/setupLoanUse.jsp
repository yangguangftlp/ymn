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
</head>
<body class="frame-body">
	<!-- BEGIN PAGE HEADER-->
	<div class="row-fluid">
		<div class="span12">
			<!-- BEGIN PAGE TITLE & BREADCRUMB-->
			<h3 class="page-title">
				我的应用 <small>借款用途</small>
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
				<li><a href="manage/loan/setupLoanRule.action">借款设置</a></li>
				<li  class="active"><a href="manage/loan/setupLoanUse.action">借款用途设置</a></li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane active">
					<div class="row-fluid">
						<div id="tab_3_3" class="tab-pane">
                            	<h5 class="set-hd-bar">
		                    		<strong>借款用途设置</strong>
		                    		<a href="#" class="pull-right" title='添加' data-target="#addLoanUse" data-toggle="modal"><i class=" icon-plus"></i></a>
		                    		<a href="javascript:location.reload();" class="pull-right" title="刷新"><i class="icon-refresh"></i></a>
								</h5>
								<hr>
								<div class="order-table v-table">
									<table class="table  table-hover" id="ck_table" width="100%">
										<thead>
											<tr>
												<th>名称</th>
												<th>排序</th>
												<th>状态</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<c:if test="${loanUseList == null}">
												<tr class="odd"><td valign="top" colspan="4" class="dataTables_empty">对不起，没有匹配的数据</td></tr>
											</c:if>
											<c:if test="${loanUseList != null}">
											<c:forEach items="${loanUseList}" var="type">
											<tr id="${type.id }">
				                                 <td class="type-name">${type.name }</td> 
				                                 <td>${type.px }</td> 
				                                 <td>
			                                 		<c:if test="${type.status eq '0'}">
			                                 			<label data-id="${type.id }"  class="disabled"><small>未开启</small></label> 
			                                 		</c:if>
			                                 		<c:if test="${type.status != '0'}">
			                                 			<label data-id="${type.id }"  class="enabled"><small>已开启</small></label>
			                                 		</c:if>
				                                 </td>
				                                 <td><a data-id="${type.id }" href="javascript:void(0)" class="icon-trash active-trash" title="删除"></a></td>   	
				                            </tr>
				                            </c:forEach>
				                            </c:if>
										</tbody>
									</table>
									
								</div>
                            </div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- BEGIN MODAL-->
	<div id="addLoanUse" class="modal fade modal-absent-type"
		aria-hidden="true" style="display: none">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true"></button>
					<h4 class="modal-title" id="myModalLabel">添加借款用途</h4>
				</div>
				<div class="modal-body row-fluid">
					<form action="" class="form-horizontal">
						<div class="control-group">
							<label class="control-label must">名称</label>
							<div class="controls">
								<input type="text" maxlength="50" id="useName" class="span10 m-wrap">
							</div>
						</div>
						<!-- END　ROW -->
						<div class="control-group">
							<label class="control-label must">排序</label>
							<div class="controls">
								<input type="text" id="usePx" class="span10 m-wrap">
							</div>
						</div>
						<!-- END　ROW -->
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" id="save" class="btn btn-default orange">保存</button>
					<button type="button" id="cancel"  class="btn btn-primary " data-dismiss="modal">取消</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
	</div>
	<!-- END MODAL -->
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
			loanUse.init();
		});
	</script>
</body>
</html>