<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div class="thumbnail">
	<form action="cartAction" method="POST">
	<table><tr><td class ="productthumb">
		<input type='hidden' name="productid" value="${product.id}">
		<a href="<c:url value="/product?id=${product.id}" />" ><img
			src="${productImages[product.id]}"
			alt="${product.name}"></a>
		</td> <%-- <div class="divider col-sm-1"></div> --%>
		<td class="divider"></td>
		<td class="description">
			<b>${product.name}</b> <br> <span> Price: <fmt:formatNumber
					value="${product.listPriceInCents/100}" type="currency"
					currencySymbol="$" />
			</span><br> <span>${product.description}</span>
		</td></tr></table><input name="addToCart" class="btn" value="Add to Cart" type="submit">
	</form>
</div>