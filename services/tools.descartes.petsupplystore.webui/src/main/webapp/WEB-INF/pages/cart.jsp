<%@include file="head.jsp"%><%@include file="header.jsp"%>
<div class="container" id="main">
	<div class="row">
		<%@include file="categorylist.jsp"%>
		<div class="col-sm-9 col-md-6  col-lg-8">
			<h2 class="minipage-title">Shopping Cart</h2>
			<form action="cartAction" method="POST">
				<table class="table table-bordered">
					<thead>
						<tr>
							<th><b>Item ID</b></th>
							<th><b>Product Name</b></th>
							<th><b>Description</b></th>

							<th><b>Quantity</b></th>
							<th><b>List Price</b></th>
							<th><b>Total Cost</b></th>
							<th>Remove</th>
						</tr>
					</thead>
					<tbody>
						<c:set var="count" value="0" scope="page" />
						<c:forEach items="${OrderItems}" var="orderItem">
							<tr>
								<td>${orderItem.productId}<input type='hidden'
									name="productid" value="${orderItem.productId}"></td>
								<td>${Products[orderItem.productId].name}</td>
								<td>${Products[orderItem.productId].description}</td>
								<td><input required min="1" name="orderitem_${orderItem.productId}"
									type="number" class="quantity" value="${orderItem.quantity}"></td>
								<td><fmt:formatNumber
										value="${orderItem.unitPriceInCents/100}" type="currency"
										currencySymbol="$" /></td>
								<td><fmt:formatNumber
										value="${orderItem.unitPriceInCents*orderItem.quantity/100}"
										type="currency" currencySymbol="$" /></td>
								<c:set var="count"
									value="${count+(orderItem.unitPriceInCents*orderItem.quantity/100)}"
									scope="page" />
								<td><button type="submit" class="submit-with-icon"
										name="removeProduct_${orderItem.productId}">
										<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
									</button></td>
							</tr>
						</c:forEach>
						<c:if test="${empty OrderItems}">
							<tr>
								<td colspan="7"><b>Your cart is empty.</b></td>
							</tr>
						</c:if>
						<tr>
							<td colspan="7">Total: <fmt:formatNumber value="${count}"
									type="currency" currencySymbol="$" /> <input
								name="updateCartQuantities" class="btn" value="Update Cart" type="submit"></td>
						</tr>
					</tbody>
				</table>
				<c:if test="${!empty OrderItems}">
					<input name="proceedtoCheckout" class="btn" value="Proceed to Checkout"
						type="submit">
				</c:if>
			</form>
		</div>
		<%@include file="recommender.jsp"%>
	</div>
</div>
<%@include file="footer.jsp"%>