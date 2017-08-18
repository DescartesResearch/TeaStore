<%@include file="head.jsp"%>

<%@include file="header.jsp"%>

<div class="container" id="main">
	<form action="cartAction" method="POST">
		<div class="row">
			<div class="col-sm-6">
				<h4 class="category-title">Billing Address</h4>
				<div class="form-group row">
					<label for="firstname"
						class="col-sm-3 col-form-label col-form-label-lg">First
						Name</label>
					<div class="col-sm-9">
						<input type="text" class="form-control form-control-lg"
							name="firstname" id="firstname" placeholder="Joe" value="Jon" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="lastname"
						class="col-sm-3 col-form-label col-form-label-lg">Last
						Name</label>
					<div class="col-sm-9">
						<input type="text" class="form-control form-control-lg"
							name="lastname" id="lastname" placeholder="Doe" value="Snow" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="address1"
						class="col-sm-3 col-form-label col-form-label-lg">Address
						1</label>
					<div class="col-sm-9">
						<input type="text" class="form-control form-control-lg"
							name="address1" id="address1" placeholder="901 San Antonio Road" value="Throne" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="adress2"
						class="col-sm-3 col-form-label col-form-label-lg">Address
						2</label>
					<div class="col-sm-9">
						<input type="text" class="form-control form-control-lg"
							name="address2" id="address2" placeholder="MS UCUP02-206" value="Big Tower" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="city" class="col-sm-3 col-form-label col-form-label-lg">City</label>
					<div class="col-sm-9">
						<input type="text" class="form-control form-control-lg"
							name="city" id="city" placeholder="Palo Alto" value="Winterfell" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="state"
						class="col-sm-3 col-form-label col-form-label-lg">State</label>
					<div class="col-sm-9">
						<input type="text" class="form-control form-control-lg"
							name="state" id="state" placeholder="CA" value="The North" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="zip" class="col-sm-3 col-form-label col-form-label-lg">Zip</label>
					<div class="col-sm-9">
						<input type="number" min=0 class="form-control form-control-lg"
							name="zip" id="zip" placeholder="94303" value="11111" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="country"
						class="col-sm-3 col-form-label col-form-label-lg">Country</label>
					<div class="col-sm-9">
						<input type="text" class="form-control form-control-lg"
							name="country" id="country" placeholder="USA" value="Westeros" required>
					</div>
				</div>
			</div>
			<div class="col-sm-6">
				<h4 class="category-title">Payment Details</h4>
				<div class="form-group row">
					<label for="cardtype"
						class="col-sm-3 col-form-label col-form-label-lg">Card
						Type</label>
					<div class="col-sm-9">
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
						class="col-sm-3 col-form-label col-form-label-lg">Card
						Number</label>
					<div class="col-sm-9">
						<input type="number" min="0" class="form-control form-control-lg"
							name="cardnumber" id="cardnumber" placeholder="314159265359" value="314159265359" required>
					</div>
				</div>
				<div class="form-group row">
					<label for="expirydate"
						class="col-sm-3 col-form-label col-form-label-lg">Expiry
						Date (MM/YYYY)</label>
					<div class="col-sm-9">
						<input type="text" class="form-control form-control-lg"
							name="expirydate" id="expirydate" placeholder="12/2025" value="12/2025" pattern="(0[1-9]|1[012])[/](19|20)\d\d" required>
					</div>
				</div>
			</div>
		</div>
		<input class="btn" name="confirm" value="Confirm"
					type="submit">
	</form>
</div>



<%@include file="footer.jsp"%>

