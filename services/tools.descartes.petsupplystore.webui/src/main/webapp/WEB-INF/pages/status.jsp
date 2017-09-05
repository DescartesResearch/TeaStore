<%@include file="head.jsp"%>



<div class="container" id="main">
	<div class="row">
			<div class="col-sm-12 col-lg-8 col-lg-offset-2">
				<h2 class="minipage-title">Pet Supply Store Service Status</h2>
				<br/>
				<c:if test = "${noregistry == true}"> <h2>Load Balancer does not work. Is Registry offline?</h2> <br/></c:if>
				<p><b>This page does not auto refresh!</b> Refresh manually when checking the current status (e.g. to see if database generation has finished).</p>
				<p>
					<b>Note:</b> Database and image generation may take a while.
					Leave the Pet Supply Store in a stable and unused state while the database is generating.
					You may use the Pet Supply Store once the database has finished generating.
					Please wait for the image provider to finish as well before running any performance tests.
				</p>
				<br/>
				<table class="table">
					<tr>
						<td><b>Service</b></td>
						<td><b>#</b></td>
						<td><b>Host(s)</b></td>
						<td><b>Status</b></td>
					</tr>
					<tr>
						<td>WebUI</td>
						<td>${webuiservers.size()}</td>
						<td>
							<c:forEach items="${webuiservers}" var="server" varStatus="loop">
								${server.host}:${server.port}<br/>
							</c:forEach>
						</td>
						<td class="success">OK</td>
					</tr>
					<tr>
						<td>Store</td>
						<td <c:if test = "${storeservers.size() < 1}"> class="danger" </c:if>>${storeservers.size()}</td>
						<td>
							<c:forEach items="${storeservers}" var="server" varStatus="loop">
								${server.host}:${server.port}<br/>
							</c:forEach>
						</td>
						<c:choose>
							<c:when test = "${storeservers.size() > 0}">
							   <td class="success">OK</td>
							</c:when>
							<c:otherwise>
							   <td class="danger">Offline</td>
							</c:otherwise>
 						</c:choose>
					</tr>
					<tr>
						<td>Persistence</td>
						<td <c:if test = "${persistenceservers.size() < 1}"> class="danger" </c:if>>${persistenceservers.size()}</td>
						<td>
							<c:forEach items="${persistenceservers}" var="server" varStatus="loop">
								${server.host}:${server.port}<br/>
							</c:forEach>
						</td>
						<c:choose>
							<c:when test = "${persistenceservers.size() < 1}">
							   <td class="danger">Offline</td>
							</c:when>
							<c:when test = "${dbfinished == true}">
							   <td class="success">OK and populated</td>
							</c:when>
							<c:otherwise>
							   <td class="warning">Generating data <span class="statusloader"></span></td>
							</c:otherwise>
 						</c:choose>
					</tr>
					<tr>
						<td>Recommender</td>	
   						<td <c:if test = "${recommenderservers.size() < 1}"> class="danger" </c:if>>${recommenderservers.size()}</td>
   						<td>
							<c:forEach items="${recommenderservers}" var="server" varStatus="loop">
								${server.host}:${server.port}<br/>
							</c:forEach>
						</td>
   						<c:choose>
   							<c:when test = "${recommenderservers.size() < 1}">
							   <td class="danger">Offline</td>
							</c:when>
							<c:when test = "${dbfinished == true}">
				            	<td class="info">Probably OK and trained</td>
				         	</c:when>
				         	<c:otherwise>
				            	<td class="warning">Waiting for Persistence <span class="statusloader"></span></td>
				         	</c:otherwise>
						</c:choose>
					</tr>
					<tr>
						<td>Image</td>
      					<td <c:if test = "${imageservers.size() < 1}"> class="danger" </c:if>>${imageservers.size()}</td>
   						<td>
							<c:forEach items="${imageservers}" var="server" varStatus="loop">
								${server.host}:${server.port}<br/>
							</c:forEach>
						</td>
      					<c:choose>
      						<c:when test = "${imageservers.size() < 1}">
							   <td class="danger">Offline</td>
							</c:when>
				        	<c:when test = "${dbfinished == false}">
				            	<td class="warning">Waiting for Persistence <span class="statusloader"></span></td>
				        	</c:when>
				        	<c:when test = "${imagefinished == false}">
				            	<td class="warning">Generating images <span class="statusloader"></span></td>
				        	</c:when>
				         	<c:otherwise>
			            		<td class="success">OK and populated</td>
				        	</c:otherwise>
						</c:choose>
					</tr>
				</table>
				<c:if test = "${dbfinished}">
         			
      			</c:if>
			</div>
		</div>
</div>


<!-- Bootstrap core JavaScript
    ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="<c:url value="/resizingscript.js"/>"></script>

</body>
</html>


