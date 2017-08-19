/**
 * 
 */
$(document).ready(resizing());
$(window).resize(function() {
	resizing();
});
function resizing() {

	var viewportwidth;
	var viewportheight;

	// the more standards compliant browsers (mozilla/netscape/opera/IE7) use
	// window.innerWidth and window.innerHeight

	if (typeof window.innerWidth != 'undefined') {
		viewportwidth = window.innerWidth;
		viewportheight = window.innerHeight;
	}
	// IE6 in standards compliant mode (i.e. with a valid doctype as the first
	// line in the document)

	else if (typeof document.documentElement != 'undefined'
			&& typeof document.documentElement.clientWidth != 'undefined'
			&& document.documentElement.clientWidth != 0) {
		viewportwidth = document.documentElement.clientWidth,
				viewportheight = document.documentElement.clientHeight
	}

	// older versions of IE

	else {
				viewportwidth = document.getElementsByTagName('body')[0].clientWidth,
				viewportheight = document.getElementsByTagName('body')[0].clientHeight
	}

	$("#main").css(
			"min-height",
			(viewportheight - $("#headnav").outerHeight(true) - $("#footnav")
					.outerHeight(true))
					+ "px");
	// alert(viewportheight+","+$("#headnav").outerHeight(true)+","+$("#footnav").outerHeight(true))

//	if ($('.category').length) {
//
//		if(100 * parseFloat($('.category').css('width'))/ parseFloat($('.category').parent().css('width'))>= 50){
//			$(".navbar-header").css("float", "none");
//			$(".navbar-left").css("float", "none !important");
//			$(".navbar-right").css("float", "none !important");
//			$(".navbar-toggle").css("display", "block");
//			$(".navbar-collapse").css("border-top", "1px solid transparent");
//			$(".navbar-collapse").css("box-shadow", "inset 0 1px 0 rgba(255,255,255,0.1)");
//			$(".navbar-collapse.collapse").css("display", "none!important");
//			$(".navbar-nav").css("float", "none!important");
//			$(".navbar-nav").css("margin-top", "7.5px");
//			$(".navbar-nav>li").css("float", "none");
//			$(".navbar-nav>li>a").css("padding-top", "10px");
//			$(".navbar-nav>li>a").css("padding-bottom", "10px");
//			$("collapse.in").css("display", "block !important");
//		}
//	}

	if ($('#navbarbutton').is(':visible')) {
		hiding();
	} else {
		showing();
	}

}

function hiding() {
	$('.sidebar').hide();
	$('.category-item').each(function() {
		$(this).appendTo('ul.headnavbarlist');
	});
}

function showing() {
	$('.sidebar').show();
	$('.category-item').each(function() {
		$(this).appendTo('ul.nav-sidebar');
	});
}
