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
	
	if(viewportwidth < 768){
		hiding();
	} else {
		showing();
	}

}

function hiding(){
	$('.sidebar').hide();
	$('.category-item').each(function() {
		$(this).appendTo('ul.headnavbarlist');
	});
}


function showing(){
	$('.sidebar').show();
	$('.category-item').each(function() {
		$(this).appendTo('ul.nav-sidebar');
	});
}

