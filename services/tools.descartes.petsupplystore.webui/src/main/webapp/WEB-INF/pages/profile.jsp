<%@include file="head.jsp"%><%@include file="header.jsp"%>
<div class="container" id="main">
	<div class="row">
		<%@include file="categorylist.jsp"%>
		<div class="col-sm-9 col-md-9 col-lg-10">
			<div class="row">
				<div class="col-sm-6">
					<h4>User Information</h4>
					<table class="table table-bordered">
						<tbody>
							<tr>
								<th>Username</th>
								<td>${User.userName}</td>
							</tr>
							<tr>
								<th>Real Name</th>
								<td>${User.realName}</td>
							</tr>
							<tr>
								<th>Email</th>
								<td>${User.email}</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>

			<h4>Orders</h4>
			<table class="table table-bordered">
				<thead>
					<tr>
						<th>ID</th>
						<th>Time</th>
						<th>Price</th>
						<th>Address Name</th>
						<th>Address</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${Orders}" var="order">
						<tr>
							<td>${order.id}</td>
							<td>${helper.formatToPrettyDate(order.time)}</td>
							<td>${helper.formatPriceInCents(order.totalPriceInCents)}</td>
							<td>${order.addressName}</td>
							<td>${order.address1}, ${order.address2}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
<%@include file="footer.jsp"%>