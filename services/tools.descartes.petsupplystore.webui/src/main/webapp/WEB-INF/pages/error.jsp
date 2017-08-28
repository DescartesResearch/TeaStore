<%@include file="head.jsp"%>


<%@include file="header.jsp"%>

<div class="container" id="main">


	<div class="row">


		<%@include file="categorylist.jsp"%>

		<div id="MainImage" class="col-sm-6 col-lg-8">

			<h3>Oops, something went wrong!</h3>

			<img height="100%" src="${errorImage}" align="middle" />

		</div>


	</div>

	<input type="button" class="btn" value="Back to Shop"
		onclick="location.href = '<c:url value='/' />';">
</div>


<%@include file="footer.jsp"%>


