<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div class="thumbnail">
	<form action="cartAction" method="POST">
		<input type='hidden' name="productid" value="${product.id}"> <a
			href="<c:url value="/product?id=${product.id}" />" ><img
			src="${productImages[product.id]}"
			alt="${product.name}"></a>
		<div class="divider"></div>
		<b>${product.name}</b> <br> <span> Price: <fmt:formatNumber
				value="${product.listPriceInCents/100}" type="currency"
				currencySymbol="$" />
		</span><br> <span>${product.description}</span> <br> <input
			name="addToCart" class="btn" value="Add to Cart" type="submit">
	</form>
</div>