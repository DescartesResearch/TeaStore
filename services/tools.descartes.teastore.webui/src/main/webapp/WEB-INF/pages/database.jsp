<%@include file="head.jsp"%><%@include file="header.jsp"%>
<div class="container" id="main">
	<form action="dataBaseAction" method="POST">
		<div class="row">
			<div class="col-sm-9">
				<h4 class="minipage-title">Setup the Database</h4>
				<div class="form-group row">
					<label for="categories"
						class="col-sm-6 col-form-label col-form-label-lg"> Number of new categories</label>
					<div class="col-sm-3">
						<input type="number" class="form-control form-control-lg"
							name="categories" id="categories" placeholder="5" value="5" min="1" required >
					</div>
				</div>
				<div class="form-group row">
					<label for="products"
						class="col-sm-6 col-form-label col-form-label-lg">Number of new products per category</label>
					<div class="col-sm-3">
						<input type="number" class="form-control form-control-lg"
							name="products" id="products" placeholder="100" value="100" min="1" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="users"
						class="col-sm-6 col-form-label col-form-label-lg">Number of new users</label>
					<div class="col-sm-3">
						<input type="number" class="form-control form-control-lg"
							name="users" id="users" placeholder="100"
							value="100" min="1" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="orders"
						class="col-sm-6 col-form-label col-form-label-lg">Number of max orders per user</label>
					<div class="col-sm-3">
						<input type="number" class="form-control form-control-lg"
							name="orders" id="orders" placeholder="5"
							value="5" min="0" required>
					</div>
				</div>
			</div>
		</div>
		<input class="btn" name="confirm" value="Confirm" type="submit">
	</form>
</div>
<%@include file="footer.jsp"%>
