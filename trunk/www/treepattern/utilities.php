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



</script>