<%@include file="head.jsp"%>

<%@include file="header.jsp"%>



<div class="container" id="main">


	<div class="row">

		<%@include file="categorylist.jsp"%>

		<div class="col-md-9 col-sm-12">
			<h2 class="category-title">${category}</h2>
			<div class="row">

				<c:forEach items="${Productslist}" var="product" varStatus="loop">
					<div class="col-sm-6 col-md-4 col-lg-3 placeholder">
						<%@include file="product_item.jsp"%>
					</div>
				</c:forEach>

			</div>

			<div class="row">
				<div class="col-sm-6">
					<ul class="pagination">
						<c:forEach items="${pagination}" var="paginationitem">

							<c:choose>
								<c:when test="${paginationitem == 'previous'}">
									<li><a
										href="${pageContext.request.contextPath}/category?category=${categoryID}&page=${pagenumber-1}">${paginationitem}</a></li>
								</c:when>
								<c:when test="${paginationitem == 'next'}">
									<li><a
										href="${pageContext.request.contextPath}/category?category=${categoryID}&page=${pagenumber+1}">${paginationitem}</a></li>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${paginationitem == pagenumber}">
											<li class="active"><a
												href="${pageContext.request.contextPath}/category?category=${categoryID}&page=${paginationitem}">${paginationitem}</a></li>
										</c:when>
										<c:otherwise>
											<li><a
												href="${pageContext.request.contextPath}/category?category=${categoryID}&page=${paginationitem}">${paginationitem}</a></li>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>


						</c:forEach>
					</ul>
				</div>
				<div class="col-sm-6">
					<c:set var="numberlist">3,5,10,15,18</c:set>

					<form id="formpages" method="post" action="">
						<select name="number" onChange="this.form.submit()">
							<c:forEach items="${numberlist}" var="number">
								<c:choose>
									<c:when test="${number == currentnumber}">
										<option value="${number}" selected="selected">${number}</option>
									</c:when>
									<c:otherwise>
										<option value="${number}">${number}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select> <span> products per page</span>
					</form>
				</div>
			</div>

		</div>


	</div>



</div>


<%@include file="footer.jsp"%>

