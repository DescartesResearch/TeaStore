<%@include file="head.jsp"%>


<%@include file="header.jsp"%>

<div class="container" id="main">
	<div class="row">
			<div class="col-sm-12 col-lg-8 col-lg-offset-2">
				<h2 class="category-title">Database Status</h2>
				<br/>
				<p><b>This page does not auto refresh!</b> Refresh manually.</p>
				<p>
					Database and image generation may take a while.
					Leave the Pet Supply Store in a stable and unused state while the database is generating.
					You may use the site once the database has finished generating.
					Please wait for the image provider to finish as well, before running any performance tests.
				</p>
				<p/>
				<table class="table">
					<tr>
						<td><b>Service</b></td><td><b>Status</b></td>
					</tr>
					<tr>
						<td>WebUI</td><td class="success">OK</td>
					</tr>
					<tr>
						<td>Store</td><td class="info">Probably OK</td>
					</tr>
					<tr>
						<td>Persistence</td>	<c:choose>
											         <c:when test = "${dbfinished == true}">
											            <td class="success">OK and populated</td>
											         </c:when>
											         <c:otherwise>
											            <td class="danger">Generating data ...</td>
											         </c:otherwise>
      											</c:choose>
					</tr>
					<tr>
						<td>Recommender</td>	<c:choose>
											         <c:when test = "${dbfinished == true}">
											            <td class="info">Probably OK and trained</td>
											         </c:when>
											         <c:otherwise>
											            <td class="danger">Waiting for Persistence ...</td>
											         </c:otherwise>
      											</c:choose>
					</tr>
					<tr>
						<td>Image</td>			<c:choose>
											         <c:when test = "${dbfinished == true}">
											            <td class="warning">Unknown</td>
											         </c:when>
											         <c:otherwise>
											            <td class="danger">Waiting for Persistence ...</td>
											         </c:otherwise>
      											</c:choose>
					</tr>
				</table>
				<c:if test = "${dbfinished}">
         			
      			</c:if>
			</div>
		</div>
</div>


<%@include file="footer.jsp"%>


