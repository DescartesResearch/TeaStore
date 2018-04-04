<!-- Static navbar -->
<nav id="footnav"
	class="navbar navbar-default navbar-fixed-bottom container">
	<div class="container-fluid">

		<div class="navbar-header"></div>
		<div id="navbarbottom" class="navbar-collapse collapse in">
			<ul class="nav navbar-nav navbar-left">
				<li><a href="http://www.descartes.tools" target="_blank">www.descartes.tools</a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="<c:url value="/database"/>">Database</a></li>
				<li><a href="<c:url value="/status"/>">Status</a></li>
				<li><a href="<c:url value="/about"/>">About us</a></li>
				<li><a
					href="https://github.com/DescartesResearch/TeaStore/wiki"><span
						class="glyphicon glyphicon-question-sign" aria-hidden="true"></span></a></li>
			</ul>
		</div>
	</div><%--/.container-fluid --%>
</nav><%-- Bootstrap core JavaScript
    ==================================================
Placed at the end of the document so the pages load faster --%>
<script src="<c:url value="bootstrap/js/jquery.min.js"/>"></script>
<script src="<c:url value="bootstrap/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resizingscript.js"/>"></script>
</body>
</html>
