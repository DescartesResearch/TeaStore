<%@include file="head.jsp"%>


<%@include file="header.jsp"%>

<div class="container" id="main">


	<div class="row">


		<div class="col-sm-3 col-md-3 col-lg-2 sidebar"></div>

		<div id="MainImage" class="col-sm-6 col-lg-8">

			<h2>
				<c:choose>
					<c:when test="${not empty messagetitle}">${messagetitle}</c:when>
					<c:otherwise>Oops, something went wrong!</c:otherwise>
				</c:choose>
			</h2>
			
			<c:if test="${not empty messageparagraph}"><p>${messageparagraph}</p></c:if>
			
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


