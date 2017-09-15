<%@include file="head.jsp"%><%@include file="header.jsp"%>
<div class="container" id="main">
	<div class="row">
		<%@include file="categorylist.jsp"%>
		<div class="col-sm-6 col-lg-8">
			<h2 class="minipage-title">Order</h2>
			<form action="cartAction" method="POST">
				<div class="row">
					<div class="col-sm-12 col-md-12 col-lg-4">
						<h4 class="minipage-title">Billing Address</h4>
						<div class="form-group row">
							<label for="firstname"
								class="col-sm-6 col-md-5 col-form-label col-form-label-lg">First
								Name</label>
							<div class="col-sm-6 col-md-7">
								<input type="text" class="form-control form-control-lg"
									name="firstname" id="firstname" placeholder="Joe" value="Jon"
									required>
							</div>
						</div>
						<div class="form-group row">
							<label for="lastname"
								class="col-sm-6 col-md-5 col-form-label col-form-label-lg">Last
								Name</label>
							<div class="col-sm-6 col-md-7">
								<input type="text" class="form-control form-control-lg"
									name="lastname" id="lastname" placeholder="Doe" value="Snow"
									required>
							</div>
						</div>
						<div class="form-group row">
							<label for="address1"
								class="col-sm-6 col-md-5 col-form-label col-form-label-lg">Address
								1</label>
							<div class="col-sm-6 col-md-7">
								<input type="text" class="form-control form-control-lg"
									name="address1" id="address1"
									placeholder="901 San Antonio Road" value="Winterfell" required>
							</div>
						</div>
						<div class="form-group row">
							<label for="adress2"
								class="col-sm-6 col-md-5 col-form-label col-form-label-lg">Address
								2</label>
							<div class="col-sm-6 col-md-7">
								<input type="text" class="form-control form-control-lg"
									name="address2" id="address2" placeholder="MS UCUP02-206"
									value="11111 The North, Westeros" required>
							</div>
						</div>
					</div>
					<div class="col-sm-12 col-md-12 col-lg-4">
						<h4 class="minipage-title">Payment Details</h4>
						<div class="form-group row">
							<label for="cardtype"
								class="col-sm-6 col-md-5 col-form-label col-form-label-lg">Card
								Type</label>
							<div class="col-sm-6 col-md-7">
								<select class="form-control form-control-lg" name="cardtype"
									id="cardtype">
									<option value="volvo">Visa</option>
									<option value="saab">MasterCard</option>
									<option value="fiat">American Express</option>
								</select>
							</div>
						</div>
						<div class="form-group row">
							<label for="cardnumber"
								class="col-sm-6 col-md-5 col-form-label col-form-label-lg">Card
								Number</label>
							<div class="col-sm-6 col-md-7">
								<input type="number" min="0"
									class="form-control form-control-lg" name="cardnumber"
									id="cardnumber" placeholder="314159265359" value="314159265359"
									required>
							</div>
						</div>
						<div class="form-group row">
							<label for="expirydate"
								class="col-sm-6 col-md-5 col-form-label col-form-label-lg">Expiry
								Date (MM/YYYY)</label>
							<div class="col-sm-6 col-md-7">
								<input type="text" class="form-control form-control-lg"
									name="expirydate" id="expirydate" placeholder="12/2025"
									value="12/2025" pattern="(0[1-9]|1[012])[/](19|20)\d\d"
									required>
							</div>
						</div>
					</div>
				</div>
				<input class="btn" name="confirm" value="Confirm" type="submit">
			</form>
		</div>
	</div>
</div>
<%@include file="footer.jsp"%>
