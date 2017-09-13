<%@include file="head.jsp"%><%@include file="header.jsp"%>
<div class="container" id="main">
	<div class="row">
		<%@include file="categorylist.jsp"%>
		<div class="col-sm-6 col-md-6 col-lg-8"><%--
			<h2 class="minipage-title">${product.name}</h2> --%>
			<form action="cartAction" method="POST">
				<div class="row">
					<input type='hidden' name="productid" value="${product.id}">
					<div class="col-sm-12 showcase"><div>
					<h2 class="minipage-title">${product.name}</h2>
						<img class="productpicture"
							src="${productImage}"
							alt="${product.name}">
						<blockquote>${product.description}</blockquote>
						<span>Price: ${helper.formatPriceInCents(product.listPriceInCents)}</span><br/>
						<input name="addToCart" class="btn" value="Add to Cart" type="submit">
					</div></div>
				</div>
				
			</form>
		</div>
		<%@include file="recommender.jsp"%>
	</div>
</div>
<%@include file="footer.jsp"%>
