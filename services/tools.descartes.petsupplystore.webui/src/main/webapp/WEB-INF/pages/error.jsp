<%@include file="head.jsp"%>


<%@include file="header.jsp"%>

<div class="container" id="main">


	<div id="MainImage" class="row">





		<h3>Oops, something went wrong!</h3>

		<img height="100%"
			src="${errorImage}"
			align="middle" />



	</div>

	<input type="button" class="btn" value="Back to Shop" onclick="location.href = '<c:url value='/' />';">
</div>


<%@include file="footer.jsp"%>


