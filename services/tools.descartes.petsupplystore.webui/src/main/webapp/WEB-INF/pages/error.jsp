<%@include file="head.jsp"%>


<%@include file="header.jsp"%>

<div class="container" id="main">


	<div class="row">


		<%@include file="categorylist.jsp"%>

		<div id="MainImage" class="col-sm-6 col-lg-8">

			<h2>Oops, something went wrong!</h2>

			<img class="titleimage" src="${errorImage}" align="middle" />
			<div class="row">
				<input type="button" class="btn errorbtn" value="Back to Shop"
					onclick="location.href = '<c:url value='/' />';">
				<input type="button" class="btn errorbtn" value="Check Status"
					onclick="location.href = '<c:url value='/status' />';">	
			</div>
		</div>


	</div>


</div>


<%@include file="footer.jsp"%>


