<div class="col-sm-3 sidebar">
	<h3>Categories</h3>
	<ul class="nav-sidebar">
		<c:forEach items="${CategoryList}" var="category">
			<li class="category-item" role="presentation"><a
				href="${pageContext.request.contextPath}/category?category=${category.id}&page=1"
				class="menulink" id="link_${category.name}">${category.name}<br>
					<span>${category.description}</span></a></li>
		</c:forEach>
	</ul>

</div>