
<div class="col-sm-3">
	
	<div class="row">
	<h4 class="advertismenttitle">Are you interested in?</h4>
		<c:forEach items="${Advertisment}" var="product">
			<div class="col-sm-12 placeholder">
				<%@include file="product_item.jsp"%>
			</div>
		</c:forEach>
	</div>

</div>