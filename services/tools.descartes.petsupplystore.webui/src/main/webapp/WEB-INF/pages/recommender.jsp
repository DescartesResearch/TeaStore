<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div class="col-sm-3 col-md-3 col-lg-2">
	<c:if test="${!empty Advertisment}">
		<div class="row">
			<h4 class="advertismenttitle">Are you interested in?</h4>
			<c:forEach items="${Advertisment}" var="product">
				<div class="col-sm-12 placeholder">
					<%@include file="product_item.jsp"%>
				</div>
			</c:forEach>
		</div>
	</c:if>
</div>