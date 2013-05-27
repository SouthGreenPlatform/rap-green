<script type="text/javascript">

function changeVisibilite(thingId,textId){
	targetElement = document.getElementById(thingId) ;
	targetText = document.getElementById(textId) ;

	if(targetElement.style.display == "none")
	{
		targetElement.style.display = "" ;
		targetText.value="Hide advanced menu";
	} else {
		targetElement.style.display = "none" ;
		targetText.value="Show advanced menu";
	}
}

function changeVisibilite2(thingId,order){
	targetElement = document.getElementById(thingId) ;

	if (order==1)
	{
		targetElement.style.display = "" ;
		targetElement.style.zIndex = 100;
	} else {
		targetElement.style.display = "none" ;
	}
}

function check(thingName){
	targetElement = document.getElementsByName(thingName)[0] ;

	if (targetElement.checked==true)
	{
		targetElement.checked=false ;
	} else {
		targetElement.checked=true ;
	}
}

function changeVisibiliteOnName(thingName,order){
	targetElement = document.getElementsByName(thingName)[0] ;
	if (targetElement==null) {
		alert(thingName);	
	}

	if (order==1)
	{
		targetElement.style.display = "" ;
		targetElement.style.zIndex = 100;
	} else {
		targetElement.style.display = "none" ;
	}
}

function localWidth() {
	var myWidth = 0, myHeight = 0;
	if( typeof( window.innerWidth ) == 'number' ) {
		//Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
		//IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	return myWidth;
}



function force() {
	var myWidth = 0, myHeight = 0;
	if( typeof( window.innerWidth ) == 'number' ) {
		//Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
		//IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	//alert(myHeight);
	var target = document.getElementById('treeDivId');
	var toset=  ( myHeight - 60) + "px";
	if (document.getElementById("legend").style.display != "none" ) {
		toset=  ( myHeight - 100) + "px";
	}
	target.style.height = toset;
}

window.onload = function(event) {
	var myWidth = 0, myHeight = 0;
	if( typeof( window.innerWidth ) == 'number' ) {
		//Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
		//IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	//alert(myHeight);
	var target = document.getElementById('treeDivId');
	var toset=  ( myHeight - 60) + "px";
	if (document.getElementById("legend").style.display != "none" ) {
		toset=  ( myHeight - 100) + "px";
	}
	target.style.height = toset;
}

window.onresize = function(event) {
	var myWidth = 0, myHeight = 0;
	if( typeof( window.innerWidth ) == 'number' ) {
		//Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
		//IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	//alert(myHeight);
	var target = document.getElementById('treeDivId');
	var toset=  ( myHeight - 60) + "px";
	if (document.getElementById("legend").style.display != "none" ) {
		toset=  ( myHeight - 100) + "px";
	}
	target.style.height = toset;
}

</script>