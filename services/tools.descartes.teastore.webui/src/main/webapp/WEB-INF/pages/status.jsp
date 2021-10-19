<%@include file="head.jsp"%>



<div class="container" id="main">
	<div class="row">
			<div class="col-sm-12 col-lg-8 col-lg-offset-2">
				<h2 class="minipage-title">TeaStore Service Status</h2>
				<br/>
				<c:if test = "${noregistry == true}"> <h2>Load Balancer does not work. Is Registry offline?</h2> <br/></c:if>
				<p><b>This page does not auto refresh!</b> Refresh manually or start an auto refresh for checking the current status (e.g. to see if database generation has finished).</p>
				<p>
					<b>Note:</b> Database and image generation may take a while.
					Leave the TeaStore in a stable and unused state while the database is generating.
					You may use the TeaStore once the database has finished generating.
					Please wait for the image provider to finish as well before running any performance tests.
				</p>
				<br/>
				<form id="refreshForm">
					<label for="refreshDurationField">Refresh Duration(s)</label>
					<input style="border: 1px solid black;" id="refreshDurationField" type="number" min="0" max="120" class="btn" name="duration">
					<input type="submit" class="btn" value="Start Auto Refresh">
				</form>
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
						<td>Auth</td>
						<td <c:if test = "${authenticationservers.size() < 1}"> class="danger" </c:if>>${authenticationservers.size()}</td>
						<td>
							<c:forEach items="${authenticationservers}" var="server" varStatus="loop">
								${server.host}:${server.port}<br/>
							</c:forEach>
						</td>
						<c:choose>
							<c:when test = "${authenticationservers.size() > 0}">
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
				         	<c:when test = "${dbfinished == false}">
				            	<td class="warning">Waiting for Persistence <span class="statusloader"></span></td>
				        	</c:when>
				        	<c:when test = "${recommenderfinished == false}">
				            	<td class="warning">Training <span class="statusloader"></span></td>
				        	</c:when>
				         	<c:otherwise>
			            		<td class="success">OK and trained</td>
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
				<input type="button" class="btn errorbtn" value="Back to Shop" onclick="location.href = '<c:url value='/' />';">
			</div>
		</div>
</div>


<!-- Bootstrap core JavaScript
    ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="<c:url value="bootstrap/js/jquery.min.js"/>"></script>
<script src="<c:url value="bootstrap/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resizingscript.js"/>"></script>
<script src="<c:url value="/autoRefreshScript.js"/>"></script>

</body>
</html>


