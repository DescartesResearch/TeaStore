<%@include file="head.jsp"%><%@include file="header.jsp"%>
<div class="container" id="main">
	<div class="row">
		<%@include file="categorylist.jsp"%>
		<div class="col-sm-6 col-lg-8">
			<h2 class="minipage-title">Login</h2>
			<form action="loginAction" method="POST">
				<div class="row">
					<h4 class="advertismenttitle">Please enter your username and
						password.</h4>
					<input type="hidden" name="referer" value="${referer}">
					<div class="col-sm-8 col-md-8 col-lg-4">

						<div class="form-group row">
							<label for="username"
								class="col-sm-4 col-form-label col-form-label-lg">Username</label>
							<div class="col-sm-8">
								<input type="text" class="form-control form-control-lg"
									name="username" id="username" value="user2" placeholder="user"
									required>
							</div>
						</div>
						<div class="form-group row">
							<label for="password"
								class="col-sm-4 col-form-label col-form-label-lg">Password</label>
							<div class="col-sm-8">
								<input type="password" class="form-control form-control-lg"
									name="password" id="password" value="password"
									placeholder="password" required>
							</div>
						</div>
						<input class="btn" name="signin" value="Sign in" type="submit">
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<%@include file="footer.jsp"%>
