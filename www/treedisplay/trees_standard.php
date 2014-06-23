<script type="text/javascript">

var alertDebug=0;

// Define primal dimensions and margins
var width=1800;
var height=600;
var margin=30;
var annotMargin=400;
var annotX=0;
var legendHeight=80;
var legendX=0;
// Computed regarding data
var taxaMargin=0;

// Define the esthetic parameters of the tree displaying
var fontFamily="Candara";
var fontSize="14";
var legendFontSize="14";
var supportSize=11;
var fontColor="black";
var backColor="white";
var lineWidth= 2;
var lineColor= "#05357E";
var collapseColor="#EEEEEE";
var tagColor="#FF0000";
var roundray=20;
var collapseWidth=3.0;
var tagWidth=15;
var opacitydegree=0.3;

//Static annotation graphic elements
var annotationFrame;
var annotationTexts= new Array();
var colorArray= new Array();
var annotationPoly= new Array();
var annotationLegend= new Array();

var hideShowAnnot= new Array();
var typeAnnot= new Array();
var annotXArray= new Array();
var annotArray= new Array();

//State saving facilities
var operatingSteps;
//colorword;#XXXXXX;keyword
//color;

var selection="";

function addAnnot(tag,defaultValue,defaultType) {
	annotX=10;
	if (typeAnnot[tag]==null) {
		typeAnnot[tag]=defaultType;
	}
	if (hideShowAnnot[tag]==null) {
		hideShowAnnot[tag]=defaultValue;
	}
	var xa=annotArray.length;
	annotArray[xa]=tag;
	var i = 0;
	for (i = 0; i < annotArray.length; i++) {
		if (hideShowAnnot[annotArray[i]]==1) {
			annotXArray[annotArray[i]]=annotX;
			annotX=annotX + annotArray[i].length * parseInt(fontSize);
			//alert("a" + i + " " + annotArray[i]);
		}
	}
	//alert("a " + annotX);
}

function hideShow(tag) {
	annotX=10;
	annotXArray= new Array();
	if (hideShowAnnot[tag]==0) {
		hideShowAnnot[tag]=1;
	} else {
		hideShowAnnot[tag]=0;
	}
	var i = 0;
	for (i = 0; i < annotArray.length; i++) {
		if (hideShowAnnot[annotArray[i]]==1) {
			annotXArray[annotArray[i]]=annotX;
			annotX=annotX + annotArray[i].length * parseInt(fontSize);
			//alert("hs" + i + " " + annotArray[i]);
		}
	}
	//alert("hs " + annotX);
	//alert(tag + " " + hideShowAnnot[tag]);
	reinitCoordinateSVG();
	resizeSVG();

}

// display type
var displaytype="ultra";

// tool switcher
var annotebranchestool="collapse";
var colorbranchannote="Red";

// display or not branch support
var displaySupport=0;

// Reference clickable objects
var clickedTreeNodes;

// Legend objects
var legendTexts;
var legendLabels=new Array();

// the SVG objects
var svg;
var legendSvg;
var back;
var legendBack;

function setAttributes(widthparam,displaytypeparam,lineParam,roundparam,fontSizeParam,supportSizeParam,fontFamilyParam,collapseWidthParam,backColorParam,lineColorParam,collapseColorParam,fontColorParam, tagColorParam, opacitydeg) {
	if (widthparam!="") {
		width=parseInt(widthparam);
	}
	if (displaytypeparam!="") {
		displaytype=displaytypeparam;
	}
	if (lineParam!="") {
		lineWidth=parseInt(lineParam);
	}
	if (roundparam!="") {
		roundray=parseInt(roundparam);
	}
	if (fontSizeParam!="") {
		fontSize=fontSizeParam;
	}
	if (supportSizeParam!="") {
		supportSize=supportSizeParam;
	}
	if (fontFamilyParam!="") {
		fontFamily=fontFamilyParam;
	}
	if (collapseWidthParam!="") {
		collapseWidth=parseInt(collapseWidthParam);
	}
	if (backColorParam!="") {
		backColor=backColorParam;
	}
	if (lineColorParam!="") {
		lineColor=lineColorParam;
	}
	if (collapseColorParam!="") {
		collapseColor=collapseColorParam;
	}
	if (fontColorParam!="") {
		fontColor=fontColorParam;
	}
	if (tagColorParam!="") {
		tagColor=tagColorParam;
	}
	if (opacitydeg!="") {
		opacitydegree=parseFloat(opacitydeg);
	}
	//alert("deg:" + opacitydegree + " " + opacitydeg);
	reinitCoordinateSVG();
	resizeSVG();
}
function resetColors() {
	tree.resetAnnotationColors();
	colorArray= new Array();
	resizeSVG();

	// hide legend

	changeVisibilite2("legend",0);
	document.getElementsByName("legendPanel")[0].removeChild(legendSvg);
	legendSvg=null;
	legendX=0;

	force();
}

function colorize(word,color) {
	var resreqarray=new Array();
	// send request to the database
	if (infos!=null) {
		if (window.XMLHttpRequest)
			xhr_object = new XMLHttpRequest();
		else if (window.ActiveXObject)
			xhr_object = new ActiveXObject("Microsoft.XMLHTTP");

		xhr_object.open("GET", "colorizedatabase.php?word=" + word, false);
		xhr_object.onreadystatechange = function(){
			if ( xhr_object.readyState == 4 ) {
				var resreq=xhr_object.responseText.split(" ");
				var i=0;
				for (i = 0; i < resreq.length; i++) {
					resreqarray[resreq[i]]="1";
				}
			}
		}

		// dans le cas du get
		xhr_object.send(null);
	}

	/*if (resreqarray["1"]!=null) {
		alert("Existe dans le 1");
	} else {
		alert("Non dans le 1");
	}*/

	colorArray[colorArray.length]=color;
	if (tree.collapsed!="") {
		tree.colorizeByAnnotation(word,color,1,resreqarray);
	} else {
		tree.colorizeByAnnotation(word,color,0,resreqarray);
	}
	resizeSVG();
	//window.fireEvent('resize');
	//display legend
	if (legendSvg==null) {
		legendTexts= new Array();
		changeVisibilite2("legend",1);
		// Build the main SVG object

		//var canvas = Raphael('legendPanel', 20, 20);
		legendSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		legendSvg.setAttribute('width', localWidth());
		legendSvg.setAttribute('height', legendHeight);
		legendBack = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
		legendBack.setAttribute('x', 0);
		legendBack.setAttribute('y', 0);
		legendBack.setAttribute('width', localWidth());
		legendBack.setAttribute('height', legendHeight);
		legendBack.setAttribute('stroke', "none");
		legendBack.setAttribute('fill', backColor);
		legendSvg.appendChild(legendBack);
		document.getElementsByName("legendPanel")[0].appendChild(legendSvg);
	}
	//add new element to legend

	var inner = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');
	inner.setAttribute('points', (legendX + 20) + "," + 10 + " " + (legendX + 40) + "," + 10 + " " + (legendX + 40) + "," + (10 + parseInt(legendFontSize)) + " " + (legendX + 20) + "," + (10 + parseInt(legendFontSize)));
	inner.setAttribute("stroke-width", lineWidth);
	inner.setAttribute("stroke", "none");
	inner.setAttribute('fill', color);
	legendSvg.appendChild(inner);
	legendX+=45;

	var text1 = document.createElementNS("http://www.w3.org/2000/svg", "text");
	text1.setAttribute("x", legendX);
	text1.setAttribute("y", 10+parseInt(legendFontSize)-2);
	text1.setAttribute("font-family", fontFamily);
	text1.setAttribute("font-size", legendFontSize);
	text1.setAttribute("fill", fontColor);
	text1.appendChild(document.createTextNode(word));
	legendSvg.appendChild(text1);
	legendX+=parseInt(legendFontSize)*word.length/2;
	legendTexts[legendTexts.length]=text1;
	//alert("a");
	force();

}

function reinitCoordinateSVG() {
	// Compute the private parameters, from the primal dimensions and the tree
	var maxDepth=0.0;
	if (displaytype=="ultra") {
		maxDepth=tree.maxUltraDepth();
	} else {
		maxDepth=tree.maxDepth();
	}
	var nbLeaves= tree.nbLeaves();
	height=nbLeaves*(parseInt(fontSize)+1)+2*margin;
	svg.setAttribute('height', height);
	svg.setAttribute('width', width);
	var maxTaxaString= tree.maxTaxaString();
	taxaMargin= maxTaxaString * parseInt(fontSize) * 0.6 + annotMargin;
	//alert("re " + taxaMargin);

	// Initialize the coordinates of each node and leaf of the tree
	if (displaytype=="ultra") {
		tree.initUltraCoordinates(0.0,taxaMargin,maxDepth,nbLeaves,0);
	} else {
		tree.initCoordinates(0.0,taxaMargin,maxDepth,nbLeaves,0);
	}
}


function resizeSVG() {
	if (tree!="undef") {
		back.setAttribute("fill", backColor);
		if (legendBack!=null) {
			legendBack.setAttribute("fill", backColor);
		}
		if (legendTexts!=null) {
			var i = 0;
			for (i = 0; i < legendTexts.length; i++) {
				legendTexts[i].setAttribute("fill", fontColor);
			}
		}
	   	tree.resizeTree(1);
		
		if (splitsValues.length>0) {
			drawSplits();
		}

		document.getElementById('hiddenfield').value = tree.getNewick() + ";";
	   	refreshSVG();
	}

}



function hZoomIn() {
	annotX=10;
	annotXArray= new Array();
	var i = 0;
	for (i = 0; i < annotArray.length; i++) {
		if (hideShowAnnot[annotArray[i]]==1) {
			annotXArray[annotArray[i]]=annotX;
			annotX=annotX + annotArray[i].length * parseInt(fontSize);
			//alert("hs" + i + " " + annotArray[i]);
		}
	}
	fontSize=parseInt(fontSize)+1;
		reinitCoordinateSVG();
	resizeSVG();
}

function hZoomOut() {
	annotX=10;
	annotXArray= new Array();
	var i = 0;
	for (i = 0; i < annotArray.length; i++) {
		if (hideShowAnnot[annotArray[i]]==1) {
			annotXArray[annotArray[i]]=annotX;
			annotX=annotX + annotArray[i].length * parseInt(fontSize);
			//alert("hs" + i + " " + annotArray[i]);
		}
	}
	fontSize=parseInt(fontSize)-1;
		reinitCoordinateSVG();
	resizeSVG();
}
function wZoomIn() {
	annotX=10;
	annotXArray= new Array();
	var i = 0;
	for (i = 0; i < annotArray.length; i++) {
		if (hideShowAnnot[annotArray[i]]==1) {
			annotXArray[annotArray[i]]=annotX;
			annotX=annotX + annotArray[i].length * parseInt(fontSize);
			//alert("hs" + i + " " + annotArray[i]);
		}
	}
	width+=100;
	reinitCoordinateSVG();
	resizeSVG();
}

function wZoomOut() {
	annotX=10;
	annotXArray= new Array();
	var i = 0;
	for (i = 0; i < annotArray.length; i++) {
		if (hideShowAnnot[annotArray[i]]==1) {
			annotXArray[annotArray[i]]=annotX;
			annotX=annotX + annotArray[i].length * parseInt(fontSize);
			//alert("hs" + i + " " + annotArray[i]);
		}
	}
	width-=100;
	reinitCoordinateSVG();
	resizeSVG();
}
function drawAll() {
	if (tree!="undef") {
		// Compute the private parameters, from the primal dimensions and the tree
		var maxDepth=0.0;
		if (displaytype=="ultra") {
			maxDepth=tree.maxUltraDepth();
		} else {
			maxDepth=tree.maxDepth();
		}
		var nbLeaves= tree.nbLeaves();
		height=nbLeaves*(parseInt(fontSize)+1)+2*margin;
		var maxTaxaString= tree.maxTaxaString();
		taxaMargin= maxTaxaString * parseInt(fontSize) * 0.6 + annotMargin;

		//alert("init " + taxaMargin);

		// Initialize the coordinates of each node and leaf of the tree
		if (displaytype=="ultra") {
			tree.initUltraCoordinates(0.0,taxaMargin,maxDepth,nbLeaves,0);
		} else {
			tree.initCoordinates(0.0,taxaMargin,maxDepth,nbLeaves,0);
		}

		// Build the main SVG object
		svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		svg.setAttribute('width', width);
		svg.setAttribute('height', height+legendHeight);

		// Build the image border
		back = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
		back.setAttribute('x', 0);
		back.setAttribute('y', 0);
		back.setAttribute('width', width);
		back.setAttribute('height', height+legendHeight);
		back.setAttribute('stroke', "none");
		back.setAttribute('fill', backColor);
		svg.appendChild(back);

	   	clickedTreeNodes= new Array();
	   	tree.drawTree(taxaMargin,1,1);
		document.getElementsByName("treePanel")[0].appendChild(svg);
    
		document.getElementById('hiddenfield').value = tree.getNewick() + ";";
        /*var selection = window.getSelection();            
        var range = document.createRange();
        range.selectNodeContents(selection);
        selection.removeAllRanges();
        selection.addRange(range);*/
        
		//selection.focus();
	}
}

function removeAll() {
	document.getElementsByName("treePanel")[0].removeChild(svg);
}

function refreshSVG() {
	document.getElementsByName("treePanel")[0].removeChild(svg);
	document.getElementsByName("treePanel")[0].appendChild(svg);
}

// **********************************************
// * Recursive definition of a result gene tree *
// **********************************************

var index=0;

// ************************
// Constructor, from a newick string, and an index in this string.
// Invariant: the index point the opening "(", the opening ",", or the taxon first letter
function Node(newick) {
	// Initialization to 0.0 of coordinates
	this.x=0.0;
	this.y=0.0;

	// Specific coordinate to manage phylogram collapsing
	this.upx=0.0;
	
	this.father=null;


	// Sons are an array of SpeciesNode
	this.sons= new Array();

	// Initialize the taxon string
	this.taxon="";


	// the branch length
	this.length="0.0";

	// the branch support
	this.support="";

	this.depth=0.0;

	this.line=null;
	this.addLine=0;
	this.round=null;
	this.addRound=0;
	this.poly=null;
	this.addPoly=0;
	this.col=null;
	this.addCol=0;
	this.sup=null;
	this.addSup=0;
	this.text=null;
	this.addText=0;
	this.poimg=null;
	this.addPoimg=0;
	this.goimg=null;
	this.addGoimg=0;
	this.left1=null;
	this.left2=null;
	this.addLeft=0;
	this.splitCounter=0;
	this.nodeType=null;
	this.addNodeType=0;
	this.tags= new Array();
	this.tagsOut= new Array();
	this.tagsIn= new Array();

	this.xtext= new Array();
	this.addXtext= new Array();
	
	// Mark of coloring point for branch annotation
	this.isColoringRoot=0;


	// Coloration of pattern matching
	this.colored=0;
	
	// general color
	this.color="";
	
	// intialisation of opacity
	this.opacity="1.0";

	// Collapse of the tree
	this.collapsed="";

	// Node of leaf conditionnal
	if (newick.charAt(index)=="(") {
		// The node case
		// Initialize the son index
		var sonIndex=0;
		// Parse the first node
		index++;
		var son= new Node(newick);
		this.sons[sonIndex]= son;
		son.father=this;
		// While there is still a son to parse
		while (newick.charAt(index)==",") {
			// Parse the current son
			sonIndex++;
			index++;
			son= new Node(newick);
			this.sons[sonIndex]= son;
			son.father=this;
		}
		index++;

		while (newick.charAt(index)!="[" && newick.charAt(index)!=":" && newick.charAt(index)!="," && newick.charAt(index)!=")" && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
			this.support = this.support + newick.charAt(index);
			index++;
		}
		if (this.support.indexOf("COLORED",0)!=-1) {
			this.support=this.support.substring(8,this.support.length);
			this.colored=1;
		}
		if (newick.charAt(index)==":") {
			index++;
			this.length="";
			while (newick.charAt(index)!="[" && newick.charAt(index)!="," && newick.charAt(index)!=")"  && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
				this.length = this.length + newick.charAt(index);
				index++;
			}
			//echo this.length;
		}
		if (newick.charAt(index)=="[") {
			var ends= newick.substring(index,newick.length);
			var ext= ends.substring(0,ends.indexOf("]"));
			
			if (ext.indexOf(":C=")!=-1) {
				var colorLocalPrev=ext.substring(ext.indexOf(":C=")+3,ext.length);
				this.color=colorLocalPrev.substring(0,colorLocalPrev.indexOf(":"));
				//alert(this.color);
			}
			if (ext.indexOf(":L=")!=-1) {
				var collapseLocalPrev=ext.substring(ext.indexOf(":L=")+3,ext.length);
				this.collapsed=collapseLocalPrev.substring(0,collapseLocalPrev.indexOf(":"));
				//alert(this.color);
			}
			if (ext.indexOf(":O=")!=-1) {
				var collapseLocalPrev=ext.substring(ext.indexOf(":O=")+3,ext.length);
				this.opacity=collapseLocalPrev.substring(0,collapseLocalPrev.indexOf(":"));
				//alert(this.color);
			}
			
			while (newick.charAt(index)!="]") {
				index++;
			}
			index++;	
		
		}	
	} else {

		// The leaf case
		while (newick.charAt(index)!="[" && newick.charAt(index)!=":" && newick.charAt(index)!="," && newick.charAt(index)!=")" && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
			this.taxon = this.taxon + newick.charAt(index);
			index++;
		}
		if (this.taxon.indexOf("COLORED",0)!=-1) {
			this.taxon=this.taxon.substring(8,this.taxon.length);
			this.colored=1;

		}

		if (newick.charAt(index)==":") {
			index++;
			this.length="";
			while (newick.charAt(index)!="[" && newick.charAt(index)!="," && newick.charAt(index)!=")"  && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
				this.length = this.length + newick.charAt(index);
				index++;
			}
			//echo this.length;
		}
		if (newick.charAt(index)=="$") {
			this.colored=1;
			index++;
		}
		while (newick.charAt(index)!="[" && newick.charAt(index)!=":" && newick.charAt(index)!="," && newick.charAt(index)!=")"  && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
			index++;
		}
		if (newick.charAt(index)=="[") {
			var ends= newick.substring(index,newick.length);
			var ext= ends.substring(0,ends.indexOf("]"));
			if (ext.indexOf(":C=")!=-1) {
				var colorLocalPrev=ext.substring(ext.indexOf(":C=")+3,ext.length);
				this.color=colorLocalPrev.substring(0,colorLocalPrev.indexOf(":"));
				//alert(this.color);
			}
			if (ext.indexOf(":O=")!=-1) {
				var collapseLocalPrev=ext.substring(ext.indexOf(":O=")+3,ext.length);
				this.opacity=collapseLocalPrev.substring(0,collapseLocalPrev.indexOf(":"));
				//alert(this.color);
			}
			while (newick.charAt(index)!="]") {
				index++;
			}
			index++;	
		
		}			
	}


}
// ************************
// Print the tree in text
function fprintTree(quote) {
	var i = 0;
	for (i = 0; i < quote; i++) {
		document.writeln('&nbsp;&nbsp;-&nbsp;&nbsp;');
	}
	var count = this.sons.length;
	if (count>0) {
		document.writeln(this.colored + ";" + this.type);
		document.writeln("&nbsp;" + this.x);
		document.writeln("&nbsp;" + this.y);
		document.writeln("<br>");
		i=0;
		for (i = 0; i < count; i++) {
			this.sons[i].printTree(quote+1);
		}
	} else {
		document.writeln(this.colored + ";" + this.type + " " + this.taxon);
		document.writeln("&nbsp;" + this.x);
		document.writeln("&nbsp;" + this.y);
		document.writeln("<br>");
	}
}
// ************************
// Return the maximum size of taxa string
function fmaxTaxaString() {
	// Counting the number of sons
	var count = this.sons.length;
	if (count>0) {
		var max=0.0;
		// It's a node
		var i = 0;
		for (i = 0; i < count; i++) {
			// Compute the max depth of the sons
			var local= this.sons[i].maxTaxaString();
			if (local>max) {
				max=local;
			}
		}
		return max;
	} else {
		// It's a leaf
		return this.taxon.length;
	}
}
// ************************
// Colorize the leaf regarding annotations and colors
function fannoteValues(size,colorLocal,labelExpress,maxExpress,express,modif) {
	//alert("echo1");
	if (legendSvg==null) {
		legendTexts= new Array();
		changeVisibilite2("legend",1);
		// Build the main SVG object

		//var canvas = Raphael('legendPanel', 20, 20);
		legendSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		legendSvg.setAttribute('width', localWidth());
		legendSvg.setAttribute('height', legendHeight);
		legendBack = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
		legendBack.setAttribute('x', 0);
		legendBack.setAttribute('y', 0);
		legendBack.setAttribute('width', localWidth());
		legendBack.setAttribute('height', legendHeight);
		legendBack.setAttribute('stroke', "none");
		legendBack.setAttribute('fill', backColor);
		legendSvg.appendChild(legendBack);
		document.getElementsByName("legendPanel")[0].appendChild(legendSvg);
	}

	//alert("echo2");
	// Counting the number of sons
	var count = this.sons.length;
	var loc=0;
	if (count>0) {
	
	//alert("echo3");
		// It's a node
		var i = 0;
		for (i = 0; i < count; i++) {
			if (this.collapsed=="") {
				loc+= this.sons[i].annoteValues(size,colorLocal,labelExpress,maxExpress,express,modif);
			} else {
				loc+= this.sons[i].annoteValues(size,colorLocal,labelExpress,maxExpress,express,modif);
				
			}
		}
	} else {	
	
	//alert("echo4");
		// It's a leaf

		//this.text.setAttributeNS(null, "fill",colorparam);
		
		var i = 0;
		for (i = 0; i < labelExpress.length; i++) {
			var nbtags= this.tags.length;
			if (legendLabels.length<size) {	
				var text1 = document.createElementNS("http://www.w3.org/2000/svg", "text");
				text1.setAttribute("x", (12+width + annotX - margin - annotMargin + (nbtags*tagWidth)));
				text1.setAttribute("y", (legendHeight));
				text1.setAttribute("font-family", fontFamily);
				text1.setAttribute("font-size", legendFontSize);
				text1.setAttribute("fill", fontColor);
				text1.appendChild(document.createTextNode(labelExpress[i]));
				legendSvg.appendChild(text1);
				text1.setAttribute("transform", "rotate(-90 " + (12+width + annotX - margin - annotMargin + (nbtags*tagWidth)) + " " + (legendHeight) + ")");
				//legendX+=parseInt(legendFontSize)*labelExpress[i].length/2;
				legendLabels[legendLabels.length]=text1;
			}	
			this.tags[nbtags]= document.createElementNS('http://www.w3.org/2000/svg', 'polygon');
			var localValue=0.0;
			if (express[this.taxon]!=null) {
				localValue=express[this.taxon][i];
				localValue=localValue+modif;
			}

			this.tags[nbtags].setAttribute('points', (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y + (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y + (parseInt(fontSize))/2));
			//this.tags[nbtags].setAttribute('stroke', lineColor);

			this.tags[nbtags].setAttribute("stroke-width", 0);
			
			var colorValue="white";
			var colorInt= "0";
			if (localValue<maxExpress) {
				colorInt=(parseInt(255.0-(255/maxExpress*localValue))).toString(16);
			}
			if (colorInt.length==1) {
				colorInt="0"+colorInt;
			}
			//alert(colorInt);
			if (colorLocal=="red") {
				colorValue="#ff"+colorInt+colorInt;
			} else {
				colorValue="#"+colorInt+colorInt+"ff";
			
			}
			//alert(localValue + " "  + colorValue);
			this.tags[nbtags].setAttribute('fill', colorValue);

			//if (this.addText==1 && hide==0) {
				svg.appendChild(this.tags[nbtags]);
			//}
			loc++;
		}
	}



	
	return loc;
}
// ************************
// Colorize the leaf regarding annotations and colors
function fcolorizeArbitrarly(colorparam,hide) {
	// Counting the number of sons
	var count = this.sons.length;
	var loc=0;
	if (count>0) {
		// It's a node
		var i = 0;
		for (i = 0; i < count; i++) {
			if (this.collapsed=="") {
				loc+= this.sons[i].colorizeArbitrarly(colorparam,0);
			} else {
				loc+= this.sons[i].colorizeArbitrarly(colorparam,1);
				
			}
		}
	} else {

		// It's a leaf

		//alert(this.color);
		//this.text.setAttributeNS(null, "fill",colorparam);
		
		var nbtags= this.tags.length;
		this.tags[nbtags]= document.createElementNS('http://www.w3.org/2000/svg', 'polygon');


		this.tags[nbtags].setAttribute('points', (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y + (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y + (parseInt(fontSize))/2));
		//this.tags[nbtags].setAttribute('stroke', lineColor);

		this.tags[nbtags].setAttribute("stroke-width", 0);
		this.tags[nbtags].setAttribute('fill', colorparam);

		if (this.addText==1 && hide==0) {
			svg.appendChild(this.tags[nbtags]);
		}
		loc++;
	}



	
	return loc;
}


// ************************
// Colorize the leaf regarding annotations and colors
function fcolorizeByAnnotation(wordparam,colorparam,hide,resreqarray) {
	// Counting the number of sons
	var count = this.sons.length;
	var loc=0;
	if (count>0) {
		// It's a node
		var i = 0;
		for (i = 0; i < count; i++) {
			if (this.collapsed!="") {
				loc+= this.sons[i].colorizeByAnnotation(wordparam,colorparam,1,resreqarray);
			} else {
				loc+= this.sons[i].colorizeByAnnotation(wordparam,colorparam,0,resreqarray);

			}
		}
		if (this.collapsed!="") {
			this.tags[this.tags.length]=loc;
			//alert(loc);
			var full= this.fullNbLeaves();
			var up=((this.y - collapseWidth / 2.0 * (parseInt(fontSize))));
			var down=((this.y + collapseWidth / 2.0 * (parseInt(fontSize))));
			var limit= down  - (down-up)/full*loc;
			//alert(loc + " / " + full + " ; " + up + " " + down + " " + limit);
			//outer polygon
			var outer = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');

			outer.setAttribute('points', (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + up + " " + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth) + tagWidth -1) + "," + up + " " + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth) + tagWidth -1) + "," + down + " " + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + down);

			outer.setAttribute('stroke', "none");
			outer.setAttribute("stroke-width", 0);
			outer.setAttribute('fill', collapseColor);

			outer.addEventListener("mouseover", lineMouseOver, false);
			outer.addEventListener("mouseout", lineMouseOut, false);
			this.tagsOut[this.tagsOut.length]=outer;
			svg.appendChild(outer);

			//inner polygon
			var inner = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');

			inner.setAttribute('points', (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + limit + " " + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth) + tagWidth -1) + "," + limit + " " + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth) + tagWidth -1) + "," + down + " " + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + down);

			inner.setAttribute('stroke', "none");
			inner.setAttribute("stroke-width", 0);
			inner.setAttribute('fill', colorArray[colorArray.length-1]);

			inner.addEventListener("mouseover", lineMouseOver, false);
			inner.addEventListener("mouseout", lineMouseOut, false);
			this.tagsIn[this.tagsIn.length]=inner;
			svg.appendChild(inner);

		}
	} else {
		// It's a leaf
		var already=0;
		var inInfos=0;
		var z=0;
		for (z=0;z<annotArray.length;z++) {
			var localTag= annotArray[z];
			if (infos[this.taxon]!=null) {
				if (infos[this.taxon][localTag]!=null) {
					if (infos[this.taxon][localTag].toLowerCase().indexOf(wordparam.toLowerCase(),0)!=-1) {
						inInfos=1;
					}

				}
			}
		}
		if (resreqarray[this.taxon]!=null) {
			inInfos=1;
		}


		if (inInfos==1 || this.taxon.toLowerCase().indexOf(wordparam.toLowerCase(),0)!=-1 || infos[this.taxon]!=null) {
			if (inInfos==1 || this.taxon.toLowerCase().indexOf(wordparam.toLowerCase(),0)!=-1 || (infos[this.taxon]["GO"]!=null && (infos[this.taxon]["GO"]).toLowerCase().indexOf(wordparam.toLowerCase(),0)!=-1) || (infos[this.taxon]["PO"]!=null && (infos[this.taxon]["PO"]).toLowerCase().indexOf(wordparam.toLowerCase(),0)!=-1)) {
				//alert(this.color);
				//this.text.setAttributeNS(null, "fill",colorparam);
				var nbtags= this.tags.length;
				this.tags[nbtags]= document.createElementNS('http://www.w3.org/2000/svg', 'polygon');


				this.tags[nbtags].setAttribute('points', (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y + (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y + (parseInt(fontSize))/2));
				//this.tags[nbtags].setAttribute('stroke', lineColor);

				this.tags[nbtags].setAttribute("stroke-width", 0);
				this.tags[nbtags].setAttribute('fill', colorparam);

				if (this.addText==1 && hide==0) {
					svg.appendChild(this.tags[nbtags]);
				}
				already=1;
				loc++;

			//alert(this.color);
			}
		}
		if (already==0) {

				var nbtags= this.tags.length;
				this.tags[nbtags]= document.createElementNS('http://www.w3.org/2000/svg', 'polygon');


				this.tags[nbtags].setAttribute('points', (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y + (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y + (parseInt(fontSize))/2));
				//this.tags[nbtags].setAttribute('stroke', lineColor);

				this.tags[nbtags].setAttribute("stroke-width", 0);
				this.tags[nbtags].setAttribute('fill', collapseColor);

				if (this.addText==1 && hide==0) {
					svg.appendChild(this.tags[nbtags]);
				}
				already=1;
		}
	}
	return loc;
}

// ************************
// Colorize the leaf regarding annotations and colors
function fresetAnnotationColors() {
	// Counting the number of sons
	var count = this.sons.length;
	if (count>0) {
		// It's a node
		var i = 0;
		for (i = 0; i < count; i++) {
			// Compute the max depth of the sons
			var local= this.sons[i].resetAnnotationColors();
		}
		if (this.collapsed!="") {
			var w=0;
			for (w=0;w<this.tags.length;w++) {
				svg.removeChild(this.tagsIn[w]);
				svg.removeChild(this.tagsOut[w]);
			}
			this.tags= new Array();
			this.tagsOut= new Array();
			this.tagsIn= new Array();

		}
	} else {
		// It's a leaf

		var nbtags= this.tags.length;
		var i = 0;
		for (i = 0; i < nbtags; i++) {
			if (this.addText==1) {
				svg.removeChild(this.tags[i]);
			}
		}
		this.tags= new Array();

		//this.text.setAttributeNS(null, "fill",fontColor);
	}
}

// ************************
// Fill the parameter tab with color representation under this node
function ffillInternalTags(locnode) {
	// Counting the number of sons
	var count = this.sons.length;
	if (count>0) {
		// It's a node
		var i = 0;
		for (i = 0; i < count; i++) {
			// Compute the max depth of the sons
			this.sons[i].fillInternalTags(locnode);
		}
	} else {
		// It's a leaf

		var nbtags= this.tags.length;
		var i = 0;
		for (i = 0; i < nbtags; i++) {
			var c= this.tags[i].getAttribute("fill");
			//alert(c);
			if (c==colorArray[i]) {
				if (locnode.tags[i]==null) {
					locnode.tags[i]=1;
				} else {
					locnode.tags[i]=locnode.tags[i]+1;
				}

			}
		}

	}

}
// ************************
// reset to 0 all the split counters
function fresetSplitCounters() {
	// Counting the number of sons
	var count = this.sons.length;
	if (count>0) {
		this.splitCounter=0;
		// It's a node
		var i = 0;
		for (i = 0; i < count; i++) {
			// Compute the max depth of the sons
			this.sons[i].resetSplitCounters();
		}
	} else {
		// It's a leaf
		this.splitCounter=0;

	}

}
// ************************
// Return the maximum depth
function fmaxDepth() {
	// Counting the number of sons
	var count = this.sons.length;
	if (count>0) {
		var max=0.0;
		// It's a node
		var i=0;
		for (i = 0; i < count; i++) {
			// Compute the max depth of the sons
			local=this.sons[i].maxDepth();
			if (local>max) {
				max=local;
			}
		}
		//alert(max+ parseFloat(this.length));
		this.depth=max+ parseFloat(this.length);
		return max+ parseFloat(this.length);
		//return max+ 1.0;
	} else {
		// It's a leaf
		this.depth=parseFloat(this.length);
		return parseFloat(this.length);
		//return 1.0;
	}
}
// ************************
// Return the maximum depth
function fmaxUltraDepth() {
	// Counting the number of sons
	var count = this.sons.length;
	if (count>0) {
		var max=0.0;
		// It's a node
		var i=0;
		for (i = 0; i < count; i++) {
			// Compute the max depth of the sons
			local=this.sons[i].maxUltraDepth();
			if (local>max) {
				max=local;
			}
		}
		//alert(max+ parseFloat(this.length));
		//return max+ parseFloat(this.length);
		this.depth=max+ 1.0;
		return max+ 1.0;
	} else {
		// It's a leaf
		//return parseFloat(this.length);
		this.depth=1.0;
		return 1.0;
	}
}
// ************************
// Return the number of leaves
function fnbLeaves() {
	// Counting the number of sons
	if (this.collapsed!="") {
		return collapseWidth;
	} else {
		var count = this.sons.length;
		if (count>0) {
			var sum=0.0;
			// It's a node
			var i=0;
			for (i = 0; i < count; i++) {
				// Compute the number of leaves of the sons
				sum+=this.sons[i].nbLeaves();
			}
			return sum;
		} else {
			// It's a leaf
			return 1.0;
		}
	}
}

// ************************
// Return the number of leaves, including leaves under a collapse
function ffullNbLeaves() {
	// Counting the number of sons
	var count = this.sons.length;
	if (count>0) {
		var sum=0.0;
		// It's a node
		var i=0;
		for (i = 0; i < count; i++) {
			// Compute the number of leaves of the sons
			sum+=this.sons[i].fullNbLeaves();
		}
		return sum;
	} else {
		// It's a leaf
		return 1.0;
	}

}

// ************************
// Return the number of leaves
function ffindAncestor(labelParam) {
	// Counting the number of sons
	var count = this.sons.length;
	//alert(count);
	if (count>0) {
		var nb=0;
		var res=null;
		// It's a node
		var i=0;
		for (i = 0; i < count; i++) {
			var localres=this.sons[i].findAncestor(labelParam);
			if (localres!=null) {
				res=localres;
				nb++;
			}
		}
		if (nb==0) {
			return null;
		} else if (nb==1) {
			return res;
		} else {
			return this;
		}
	} else {
		// It's a leaf
		//alert(this.label + " " + labelParam);

		var labelMod= labelParam/*+"_"*/;
		var inInfos=0;
		var z=0;
		for (z=0;z<annotArray.length;z++) {
			var localTag= annotArray[z];
			if (infos[this.taxon]!=null) {
				if (infos[this.taxon][localTag]!=null) {
					if (infos[this.taxon][localTag].toLowerCase().indexOf(labelMod.toLowerCase(),0)!=-1) {
						inInfos=1;
					}

				}
			}
		}
		if (inInfos==0 && this.taxon.indexOf(labelMod,0)==-1) {
			return null;
		} else {
			return this;
		}
	}

}

// ************************
// Initialize the (x,y) coordinates
function finitCoordinates(depth,taxaMargin,maxDepth,nbLeaves,level) {
	if (this.collapsed!="") {
		this.x= margin + (depth + parseFloat(this.length))/maxDepth*(width-2*margin-taxaMargin);
		this.upx= margin + (depth + this.depth)/maxDepth*(width-2*margin-taxaMargin);
		this.y= margin + ((level * (height-2*margin)/(nbLeaves-1))+((level+ collapseWidth - 1.0) * (height-2*margin)/(nbLeaves-1)))/2.0;
		return collapseWidth;
	} else {
		// Counting the number of sons
		var count = this.sons.length;
		//alert(this.length + " ; " + parseFloat(this.length));
		if (count>0) {
			// It's a node
			var minX=width;
			var sumY=0.0;
			var newLevel=0;
			var i=0;
			for (i = 0; i < count; i++) {
				// Compute the minimum Y and the sum of X, coordinates of the sons
				var localLevel= this.sons[i].initCoordinates(depth + parseFloat(this.length),taxaMargin,maxDepth,nbLeaves,newLevel+level);
				newLevel= newLevel+localLevel;
				sumY+=this.sons[i].y;
				if (minX>this.sons[i].x) {
					minX = this.sons[i].x;
				}

			}
			// Compute the (x,y) coordinates of the node from its sons
			//this.x= minX-(width-2*margin-taxaMargin)/(maxDepth-1);
			this.x= margin + (depth + parseFloat(this.length))/maxDepth*(width-2*margin-taxaMargin);
			this.y= sumY/count;

			return newLevel;
		} else {
			// It's a leaf
			//this.x=width-margin-taxaMargin;
			this.x= margin + (depth + parseFloat(this.length))/maxDepth*(width-2*margin-taxaMargin);
			this.y=margin + level * (height-2*margin)/(nbLeaves-1);
			return 1;
		}
	}
}

// ************************
// Initialize the (x,y) coordinates
function finitUltraCoordinates(depth,taxaMargin,maxDepth,nbLeaves,level) {

	// Counting the number of sons
	var count = this.sons.length;
	//alert(this.length + " ; " + parseFloat(this.length));
	if (count>0) {
		// It's a node
		var minX=width;
		var sumY=0.0;
		var newLevel=0;
		var i=0;
		for (i = 0; i < count; i++) {
			// Compute the minimum Y and the sum of X, coordinates of the sons
			var localLevel= this.sons[i].initUltraCoordinates(depth + parseFloat(this.length),taxaMargin,maxDepth,nbLeaves,newLevel+level);
			newLevel= newLevel+localLevel;
			sumY+=this.sons[i].y;
			if (minX>this.sons[i].x) {
				minX = this.sons[i].x;
			}

		}
		if (this.collapsed!="") {
			this.x= minX-(width-2*margin-taxaMargin)/(maxDepth-1);
			this.y= margin + ((level * (height-2*margin)/(nbLeaves-1))+((level + collapseWidth - 1.0) * (height-2*margin)/(nbLeaves-1)))/2.0;
			return collapseWidth;
		} else {
			// Compute the (x,y) coordinates of the node from its sons
			this.x= minX-(width-2*margin-taxaMargin)/(maxDepth-1);
			//this.x= margin + (depth + parseFloat(this.length))/maxDepth*(width-2*margin-taxaMargin)
			this.y= sumY/count;

			return newLevel;
		}
	} else {
		// It's a leaf
		this.x=width-margin-taxaMargin;
		//this.x= margin + (depth + parseFloat(this.length))/maxDepth*(width-2*margin-taxaMargin)
		if (this.collapsed!="") {
			this.y= margin + ((level * (height-2*margin)/(nbLeaves-1))+((level + collapseWidth - 1.0) * (height-2*margin)/(nbLeaves-1)))/2.0;
			return collapseWidth;
		} else {
			this.y=margin + level * (height-2*margin)/(nbLeaves-1);
			return 1;
		}
	}

}



// ************************
// Print the tree in an SVG frame, inside the species tree
function fresizeTree(normalMod) {
	if (normalMod==0) {
		var count = this.sons.length;
		if (count>0) {
			var i=0;
			for (i=0;i<count;i++) {
				this.sons[i].resizeTree(0);

			}

		}
		var z=0;
		for (z=0;z<annotArray.length;z++) {
			var localTag= annotArray[z];


			if (this.addXtext[localTag]!=null) {
				if (this.addXtext[localTag]==1) {
					svg.removeChild(this.xtext[localTag]);
					this.addXtext[localTag]=0;

				}
			}


		}
		if (this.addLine==1) {
			svg.removeChild(this.line);
			this.addLine=0;
		}
		if (this.addLeft==1) {
			svg.removeChild(this.left1);
			svg.removeChild(this.left2);
			this.addLeft=0;
		}
		if (this.addRound==1) {
			svg.removeChild(this.round);
			this.addRound=0;
		}
		if (this.addText==1) {
			var w=0;
			for (w = 0; w < this.tags.length; w++) {
				svg.removeChild(this.tags[w]);
			}
			svg.removeChild(this.text);
			this.addText=0;
		}
		if (this.addCol==1) {
			svg.removeChild(this.col);
			this.addCol=0;
		}
		if (this.addPoly==1) {
			var w=0;
			for (w = 0; w < this.tagsIn.length; w++) {
				svg.removeChild(this.tagsIn[w]);
				svg.removeChild(this.tagsOut[w]);
			}
			svg.removeChild(this.poly);
			this.addPoly=0;
		}
		if (this.addGoimg==1) {
			svg.removeChild(this.goimg);
			this.addGoimg=0;
		}
		if (this.addPoimg==1) {
			svg.removeChild(this.poimg);
			this.addPoimg=0;
		}
		if (this.addSup==1) {
			svg.removeChild(this.sup);
			this.addSup=0;
		}

		if (this.addNodeType==1) {
			this.addNodeType=0;
			svg.removeChild(this.nodeType);
		}


	} else {

		if (this.collapsed!="") {
			if (this.addCol==0) {
				// collapse text
				var text1 = document.createElementNS("http://www.w3.org/2000/svg", "text");
				if (displaytype=="ultra") {
					text1.setAttribute("x", width-margin-taxaMargin + 5);
				} else {
					text1.setAttribute("x", this.upx + 5);
				}
				text1.setAttribute("y", this.y+ (fontSize/3));
				text1.setAttribute("font-family", fontFamily);
				text1.setAttribute("font-size", fontSize);
				text1.setAttribute("fill", fontColor);
				text1.appendChild(document.createTextNode(this.collapsed));
				text1.addEventListener("mouseover", textMouseOver, false);
				text1.addEventListener("mouseout", textMouseOut, false);
				this.col=text1;
				svg.appendChild(text1);
				this.addCol=1;


				// clean this subtree from old topology
				if (this.addLeft==1) {
					this.addLeft=0;
					svg.removeChild(this.left1);
					svg.removeChild(this.left2);
				}
				if (this.addGoimg==1) {
					svg.removeChild(this.goimg);
					this.addGoimg=0;
				}
				if (this.addNodeType==1) {
					svg.removeChild(this.nodeType);
					this.addNodeType=0;
				}
				if (this.addPoimg==1) {
					svg.removeChild(this.poimg);
					this.addPoimg=0;
				}
				if (this.addText==1) {
					svg.removeChild(this.text);
					this.addText=0;
				}
				var i=0;
				for (i=0;i<this.sons.length;i++) {
					this.sons[i].resizeTree(0);
				}
			} else {
				// The collapse case
				if (displaytype=="ultra") {
					this.col.setAttribute("x", width-margin-taxaMargin + 5);
				} else {
					this.col.setAttribute("x", this.upx + 5);
				}
				this.col.setAttribute("y", this.y+ (fontSize/3));
				this.col.setAttribute("font-size", fontSize);
				this.col.setAttribute("font-family", fontFamily);
				this.col.setAttribute("fill", fontColor);
			}


			if (displaySupport!=0 && this.sons.length>0) {
				// display support case
				var sup= this.support;
				if (sup.indexOf("_",0)!=-1) {
					sup=sup.substring(sup.lastIndexOf("_")+1,sup.length);
				}
				if (sup.length>5) {
					sup=sup.substring(0,5);
				}
				this.sup.setAttribute("x", this.x - parseInt(supportSize)*sup.length/2.0 - 2);
				this.sup.setAttribute("y", this.y - parseInt(supportSize)/3 - 4);
				if (this.addSup==0) {
					this.addSup=1;
					svg.appendChild(this.sup);
				}
			} else {
				if (this.addSup==1) {
					this.addSup=0;
					svg.removeChild(this.sup);

				}

			}

		} else {
			if (this.addCol==1) {
				this.addCol=0;
				svg.removeChild(this.col);

			}
			// Counting the number of sons
			var count = this.sons.length;
			if (count>0) {


				if ((this.sons[0].y+roundray)<this.y) {
					if (this.addLeft==0) {
						this.addLeft=1;
						svg.appendChild(this.left1);
						svg.appendChild(this.left2);

					}
					this.left1.setAttribute("x1", this.x);
					this.left1.setAttribute("y1", this.sons[0].y + roundray);
					this.left1.setAttribute("x2", this.x);
					this.left1.setAttribute("y2", this.y);
					this.left1.setAttribute("stroke-width", lineWidth);
					this.left2.setAttribute("x1", this.x);
					this.left2.setAttribute("y1", this.y);
					this.left2.setAttribute("x2", this.x);
					this.left2.setAttribute("y2", this.sons[count-1].y - roundray);
					this.left2.setAttribute("stroke-width", lineWidth);
					if (this.sons[0].colored==1 && this.colored==1) {
						this.left1.setAttribute("stroke", tagColor);
					} else {
						//this.left1.setAttribute("stroke", lineColor);

					}
					if (this.sons[count-1].colored==1 && this.colored==1) {
						this.left2.setAttribute("stroke", tagColor);
					} else {
						//this.left2.setAttribute("stroke", lineColor);

					}
				} else {

					if (this.addLeft==1) {
						this.addLeft=0;
						svg.removeChild(this.left1);
						svg.removeChild(this.left2);

					}
				}

				// round parts
				var d2= "";
				if ((this.sons[0].y+roundray)<this.y) {
					if (this.sons[0].x< (this.x + roundray)) {
						d2="M " + (this.x) + " " + (this.sons[0].y + roundray) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.sons[0].x) + " " + (this.sons[0].y);
					} else {
						d2="M " + (this.x) + " " + (this.sons[0].y + roundray) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.x + roundray) + " " + (this.sons[0].y);
					}
				} else {
					if (this.sons[0].x< (this.x + roundray)) {
						d2="M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.sons[0].x) + " " + (this.sons[0].y);
					} else {
						d2="M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.x + roundray) + " " + (this.sons[0].y);
					}
				}
				this.sons[0].round.setAttribute("d", d2);
				this.sons[0].round.setAttribute("stroke-width", lineWidth);

				if (this.sons[0].colored==1 && this.colored==1) {
					this.sons[0].round.setAttribute("stroke", tagColor);
				} else {
					//this.sons[0].round.setAttribute("stroke", lineColor);

				}

				if (this.sons[0].addRound==0) {

					this.sons[0].addRound=1;
					svg.appendChild(this.sons[0].round);

				}

				var d3 = "";
				if ((this.sons[count-1].y-roundray)>this.y) {
					if (this.sons[count-1].x< (this.x + roundray)) {
						d3= "M " + (this.x) + " " + (this.sons[count-1].y - roundray) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.sons[count-1].x) + " " + (this.sons[count-1].y);
					} else {
						d3= "M " + (this.x) + " " + (this.sons[count-1].y - roundray) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.x + roundray) + " " + (this.sons[count-1].y);
					}
				} else {
					if (this.sons[count-1].x< (this.x + roundray)) {
						d3= "M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.sons[count-1].x) + " " + (this.sons[count-1].y);
					} else {
						d3= "M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.x + roundray) + " " + (this.sons[count-1].y);
					}
				}
				this.sons[count-1].round.setAttribute("d", d3);
				this.sons[count-1].round.setAttribute("stroke-width", lineWidth);
				if (this.sons[count-1].colored==1 && this.colored==1) {
					this.sons[count-1].round.setAttribute("stroke", tagColor);
				} else {
					//this.sons[count-1].round.setAttribute("stroke", lineColor);

				}
				if (this.sons[count-1].addRound==0) {

					this.sons[count-1].addRound=1;
					svg.appendChild(this.sons[count-1].round);

				}

				if (this.support.indexOf("D_",0)!=-1) {
					this.nodeType.setAttribute('points', (this.x -2*lineWidth-lineWidth) + "," + (this.y -lineWidth) + " " + (this.x-2*lineWidth+lineWidth) + "," + (this.y - lineWidth) + " " + (this.x-2*lineWidth+lineWidth) + "," + (this.y + lineWidth) + " " + (this.x-2*lineWidth  - lineWidth) + "," + (this.y + lineWidth));
					//this.nodeType.setAttribute("stroke", lineColor);
					//this.nodeType.setAttribute("fill", lineColor);
					if (this.addNodeType==0) {
						this.addNodeType=1;
						svg.appendChild(this.nodeType);
					}
				}
				
				if (this.support.indexOf("T_",0)!=-1) {
					this.nodeType.setAttribute('points', (this.x -2*lineWidth-2*lineWidth) + "," + (this.y -2*lineWidth) + " " + (this.x-2*lineWidth+lineWidth) + "," + (this.y) + " " + (this.x-2*lineWidth-2*lineWidth) + "," + (this.y + 2*lineWidth));
					//this.nodeType.setAttribute("stroke", lineColor);
					//this.nodeType.setAttribute("fill", lineColor);
					if (this.addNodeType==0) {
						this.addNodeType=1;
						svg.appendChild(this.nodeType);
					}
				}
				if (displaySupport!=0) {
					// display support case
					var sup= this.support;
					if (sup.indexOf("_",0)!=-1) {
						sup=sup.substring(sup.lastIndexOf("_")+1,sup.length);
					}
					if (sup.length>5) {
						sup=sup.substring(0,5);
					}
					this.sup.setAttribute("x", this.x - parseInt(supportSize)*sup.length/2.0 - 2);
					this.sup.setAttribute("y", this.y- parseInt(supportSize)/3 - 4);
					this.sup.setAttribute("font-size", supportSize);
					this.sup.setAttribute("fill", fontColor);
					this.sup.setAttribute("font-family", fontFamily);
					if (this.addSup==0) {
						this.addSup=1;
						svg.appendChild(this.sup);
					}
				} else {
					if (this.addSup==1) {
						this.addSup=0;
						svg.removeChild(this.sup);

					}

				}
				// It's a node
				var i=0;
				for (i = 0; i < count; i++) {


					if (this.sons[i].x> (this.x + roundray)) {
						if (this.sons[i].addLine==0) {
							this.sons[i].addLine=1;
							svg.appendChild(this.sons[i].line);
							//alert("toto");

						}
						this.sons[i].line.setAttribute("x1", this.sons[i].x);
						this.sons[i].line.setAttribute("y1", this.sons[i].y);
						if (i==0 || i==(count-1)) {
							this.sons[i].line.setAttribute("x2", this.x + roundray);
						} else {
							this.sons[i].line.setAttribute("x2", this.x);
						}
						this.sons[i].line.setAttribute("y2", this.sons[i].y);
						this.sons[i].line.setAttribute("stroke-width", lineWidth);
						if (this.sons[i].colored==1 && this.colored==1) {
							this.sons[i].line.setAttribute("stroke", tagColor);
						} else {
							//this.sons[i].line.setAttribute("stroke", lineColor);

						}
					} else {
						if (this.sons[i].addLine==1) {
							this.sons[i].addLine=0;
							svg.removeChild(this.sons[i].line);

						}

					}

					if (this.sons[i].collapsed!="") {
						//collapse polygon

						if (this.sons[i].addPoly==0) {
							// collapse polygon
							var polyCol = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');

							if (displaytype=="ultra") {
								polyCol.setAttribute('points', (this.sons[i].x) + "," + (this.sons[i].y) + " " + (width-margin-taxaMargin) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + " " + (width-margin-taxaMargin) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))));
							} else {
								polyCol.setAttribute('points', (this.sons[i].x) + "," + (this.sons[i].y) + " " + (this.sons[i].upx) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + " " + (this.sons[i].upx) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))));
							}
							if (this.sons[i].line!=null && this.sons[i].line!="") {
								polyCol.setAttribute('stroke', this.sons[i].line.getAttribute("stroke"));
							} else {
								polyCol.setAttribute('stroke', lineColor);
							
							}
							polyCol.setAttribute("stroke-width", lineWidth);
							polyCol.setAttribute('fill', collapseColor);

							polyCol.addEventListener("mouseover", lineMouseOver, false);
							polyCol.addEventListener("mouseout", lineMouseOut, false);

							var clickedIndex=clickedTreeNodes.length;
							clickedTreeNodes[clickedIndex]=this.sons[i];
							this.sons[i].poly=polyCol;
							polyCol.setAttribute("indexNode", clickedIndex);
							polyCol.addEventListener("click",lineMouseClick, false);
							this.sons[i].addPoly=1;

							svg.appendChild(polyCol);

							//add colors
							var w=0;
							var full= this.sons[i].fullNbLeaves();
							for (w=0;w<this.sons[i].tags.length;w++) {
								var loc= this.sons[i].tags[w];
								if (loc==null) {
									loc=0;
								}
								var up=((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize))));
								var down=((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize))));
								var limit= down  - (down-up)/full*loc;

								//outer polygon
								var outer = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');

								outer.setAttribute('points', (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + up + " " + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + up + " " + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + down + " " + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + down);

								outer.setAttribute('stroke', "none");
								outer.setAttribute("stroke-width", 0);
								outer.setAttribute('fill', collapseColor);

								this.sons[i].tagsOut[w]=outer;
								svg.appendChild(outer);

								//inner polygon
								var inner = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');

								inner.setAttribute('points', (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + limit + " " + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + limit + " " + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + down + " " + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + down);

								inner.setAttribute('stroke', "none");
								inner.setAttribute("stroke-width", 0);
								inner.setAttribute('fill', colorArray[w]);

								this.sons[i].tagsIn[w]=inner;
								svg.appendChild(inner);


							}



						} else {

							if (displaytype=="ultra") {
								this.sons[i].poly.setAttribute('points', (this.sons[i].x) + "," + (this.sons[i].y) + " " + (width-margin-taxaMargin) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + " " + (width-margin-taxaMargin) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))));
							} else {
								this.sons[i].poly.setAttribute('points', (this.sons[i].x) + "," + (this.sons[i].y) + " " + (this.sons[i].upx) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + " " + (this.sons[i].upx) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))));
							}
							this.sons[i].poly.setAttribute('stroke-width', lineWidth);
							//this.sons[i].poly.setAttribute('stroke', lineColor);
							this.sons[i].poly.setAttribute('fill', collapseColor);
							var w=0;
							var full= this.sons[i].fullNbLeaves();
							for (w=0;w<this.sons[i].tags.length;w++) {
								var loc= this.sons[i].tags[w];
								if (loc==null) {
									loc=0;
								}
								var up=((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize))));
								var down=((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize))));
								var limit= down  - (down-up)/full*loc;
								this.sons[i].tagsIn[w].setAttribute('points', (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + limit + " " + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + limit + " " + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + down + " " + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + down);
								this.sons[i].tagsOut[w].setAttribute('points', (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + up + " " + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + up + " " + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + down + " " + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + down);

								this.sons[i].tagsOut[w].setAttribute('fill',collapseColor);
							}




						}
					} else if (this.sons[i].addPoly==1) {
							this.sons[i].addPoly=0;
							svg.removeChild(this.sons[i].poly);
							var w=0;
							for (w=0;w<this.sons[i].tags.length;w++) {
								svg.removeChild(this.sons[i].tagsIn[w]);
								svg.removeChild(this.sons[i].tagsOut[w]);

							}

					}


					this.sons[i].resizeTree(1);
				}
			} else {
				// The leaf case

				this.text.setAttribute("x", this.x + 5);
				this.text.setAttribute("y", this.y+ (fontSize/3));
				this.text.setAttribute("font-size", fontSize);
				this.text.setAttribute("fill", fontColor);
				this.text.setAttribute("font-family", fontFamily);
				if (this.addText==0) {
					svg.appendChild(this.text);
					this.addText=1;
					var w=0;
					for (w = 0; w < this.tags.length; w++) {
						svg.appendChild(this.tags[w]);
					}

				}
				//tags
				var nbtags= this.tags.length;
				var i = 0;
				for (i = 0; i < nbtags; i++) {
					this.tags[i].setAttribute('points', (width + annotX - margin - annotMargin + (i*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (i*tagWidth) + tagWidth - 1) + "," + (this.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (i*tagWidth) + tagWidth - 1) + "," + (this.y + (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (i*tagWidth)) + "," + (this.y + (parseInt(fontSize))/2));
					/*if (this.tags[i].getAttribute("fill")!=colorArray[i]) {
						this.tags[i].setAttribute("fill", collapseColor)
					}*/
				}


				var z=0;
				for (z=0;z<annotArray.length;z++) {
					var localTag= annotArray[z];


					if (infos[this.taxon]!=null) {
						/*if (alertDebug==0) {
							alertDebug=1;
							alert("echo");

						}*/
						if (typeAnnot[localTag]=="plain" && infos[this.taxon][localTag]!=null && hideShowAnnot[localTag]!=null && hideShowAnnot[localTag]!=0) {

							this.xtext[localTag].setAttribute ("x", width - margin - annotMargin + annotXArray[localTag]);
							this.xtext[localTag].setAttribute ("y", this.y + (fontSize/3));
							this.xtext[localTag].setAttribute("font-size", fontSize);
							this.xtext[localTag].setAttribute("fill", fontColor);
							this.xtext[localTag].setAttribute("font-family", fontFamily);

							if (this.addXtext[localTag]==0) {
								svg.appendChild(this.xtext[localTag]);
								this.addXtext[localTag]=1;

							}

						} else {
							if (this.addXtext[localTag]==1) {
								svg.removeChild(this.xtext[localTag]);
								this.addXtext[localTag]=0;

							}

						}

					}

				}
				//annotations
				if (infos[this.taxon]!=null) {
					if (infos[this.taxon]["PO"]!=null && hideShowAnnot["PO"]!=null && hideShowAnnot["PO"]!=0) {
						this.poimg.setAttribute ("x", width - margin - annotMargin + annotXArray["PO"]);
						this.poimg.setAttribute ("y", this.y + (fontSize/3));
						this.poimg.setAttribute("font-size", fontSize);
						this.poimg.setAttribute("fill", fontColor);
						this.poimg.setAttribute("font-family", fontFamily);
						if (this.addPoimg==0) {
							svg.appendChild(this.poimg);
							this.addPoimg=1;

						}

					} else {
						if (this.addPoimg==1) {
							svg.removeChild(this.poimg);
							this.addPoimg=0;

						}

					}

					if (infos[this.taxon]["GO"]!=null && hideShowAnnot["GO"]!=null && hideShowAnnot["GO"]!=0) {
						this.goimg.setAttribute ("x", width - margin - annotMargin + annotXArray["GO"]);
						this.goimg.setAttribute ("y", this.y + (fontSize/3));
						this.goimg.setAttribute("font-size", fontSize);
						this.goimg.setAttribute("fill", fontColor);
						this.goimg.setAttribute("font-family", fontFamily);
						if (this.addGoimg==0) {
							svg.appendChild(this.goimg);
							this.addGoimg=1;

						}
					} else {
						if (this.addGoimg==1) {
							svg.removeChild(this.goimg);
							this.addGoimg=0;

						}

					}
				}

			}
		}
	}


}



// ************************
// Print the tree in an SVG frame, inside the species tree
function fdrawTree(taxaMargin,isRoot,drawTheEnd) {
	if (this.father!=null && this.father.color!=this.color) {
		this.isColoringRoot=1;
	}
	if (this.collapsed!="") {
		// The collapse case
		var text1 = document.createElementNS("http://www.w3.org/2000/svg", "text");
		if (displaytype=="ultra") {
			text1.setAttribute("x", width-margin-taxaMargin + 5);
		} else {
			text1.setAttribute("x", this.upx + 5);
		}
		text1.setAttribute("y", this.y+ (fontSize/3));
		text1.setAttribute("font-family", fontFamily);
		text1.setAttribute("font-size", fontSize);
		if (this.taxon.substring(4,0)=="LOSS") {
			text1.setAttribute("opacity",0.5);
		}
		text1.setAttribute("fill", fontColor);
		text1.appendChild(document.createTextNode(this.collapsed));
		text1.addEventListener("mouseover", textMouseOver, false);
		text1.addEventListener("mouseout", textMouseOut, false);
		
		text1.setAttribute("opacity", this.opacity);
		text1.setAttribute("fill-opacity", this.opacity);
		if (drawTheEnd==1) {
			this.addCol=1;
			this.col=text1;
			svg.appendChild(text1);
		}

		// display support case
		var textSupport = document.createElementNS("http://www.w3.org/2000/svg", "text");
		var sup= this.support;
		if (sup.indexOf("_",0)!=-1) {
			sup=sup.substring(sup.lastIndexOf("_")+1,sup.length);
		}
		if (sup.length>5) {
			sup=sup.substring(0,5);
		}
		textSupport.setAttribute("x", this.x - parseInt(supportSize)*sup.length/2.0 - 2);
		textSupport.setAttribute("y", this.y- parseInt(supportSize)/3 - 4);
		textSupport.setAttribute("font-family", fontFamily);
		textSupport.setAttribute("font-size", parseInt(supportSize));
		if (this.taxon.substring(4,0)=="LOSS") {
			textSupport.setAttribute("opacity",0.5);
		}
		textSupport.setAttribute("fill", fontColor);

		textSupport.setAttribute("opacity", this.opacity);
		textSupport.setAttribute("fill-opacity", this.opacity);
		
		textSupport.appendChild(document.createTextNode(sup));
		textSupport.addEventListener("mouseover", supportMouseOver, false);
		textSupport.addEventListener("mouseout", supportMouseOut, false);

		var clickedIndex=clickedTreeNodes.length;
		clickedTreeNodes[clickedIndex]=this;
		this.sup=textSupport;
		textSupport.setAttribute("indexNode", clickedIndex);
		textSupport.addEventListener("click",supMouseClick, false);

		if (drawTheEnd==1 && displaySupport!=0) {
			this.addSup=1;
			svg.appendChild(textSupport);

		}
		drawTheEnd=0;

	}// else {
		// Counting the number of sons
		var count = this.sons.length;
		if (count>0) {


			var lineLeft = document.createElementNS("http://www.w3.org/2000/svg", "line");
			if (this.sons[0].colored==1 && this.colored==1) {
				lineLeft.setAttribute("stroke", tagColor);
			} else if (this.sons[0].color!="") {
				lineLeft.setAttribute("stroke", this.sons[0].color);			
			} else {
				lineLeft.setAttribute("stroke", lineColor);
			}
			lineLeft.setAttribute("stroke-width", lineWidth);
			lineLeft.setAttribute("x1", this.x);
			lineLeft.setAttribute("y1", this.sons[0].y + roundray);
			lineLeft.setAttribute("opacity", this.sons[0].opacity);
			lineLeft.setAttribute("fill-opacity", this.sons[0].opacity);
			lineLeft.setAttribute("x2", this.x);
			lineLeft.setAttribute("y2", this.y);
			this.left1=lineLeft;
			if (drawTheEnd==1 && (this.sons[0].y+roundray)<this.y) {
				this.addLeft=1;
				svg.appendChild(lineLeft);
			}

			lineLeft = document.createElementNS("http://www.w3.org/2000/svg", "line");
			if (this.sons[count-1].colored==1 && this.colored==1) {
				lineLeft.setAttribute("stroke", tagColor);
			}  else if (this.sons[count-1].color!="") {
				lineLeft.setAttribute("stroke", this.sons[count-1].color);			
			} else {
				lineLeft.setAttribute("stroke", lineColor);
			}
			lineLeft.setAttribute("stroke-width", lineWidth);
			lineLeft.setAttribute("x1", this.x);
			lineLeft.setAttribute("y1", this.y);
			lineLeft.setAttribute("opacity", this.sons[count-1].opacity);
			lineLeft.setAttribute("fill-opacity", this.sons[count-1].opacity);
			lineLeft.setAttribute("x2", this.x);
			lineLeft.setAttribute("y2", this.sons[count-1].y - roundray);
			this.left2=lineLeft;
			if (drawTheEnd==1 && (this.sons[0].y+roundray)<this.y) {
				this.addLeft=1;
				svg.appendChild(lineLeft);
			}

			// round parts
			var path2 = document.createElementNS("http://www.w3.org/2000/svg", "path");
			if (this.sons[0].colored == 1) {
				path2.setAttribute("stroke", tagColor);
			}  else if (this.sons[0].color!="") {
				path2.setAttribute("stroke", this.sons[0].color);			
			} else {
				path2.setAttribute("stroke", lineColor);
			}
			path2.setAttribute("stroke-width", lineWidth);
			path2.setAttribute("fill", "none");

			var d2= "";
			if ((this.sons[0].y+roundray)<this.y) {
				if (this.sons[0].x< (this.x + roundray)) {
					d2="M " + (this.x) + " " + (this.sons[0].y + roundray) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.sons[0].x) + " " + (this.sons[0].y);
				} else {
					d2="M " + (this.x) + " " + (this.sons[0].y + roundray) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.x + roundray) + " " + (this.sons[0].y);
				}
			} else {
				if (this.sons[0].x< (this.x + roundray)) {
					d2="M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.sons[0].x) + " " + (this.sons[0].y);
				} else {
					d2="M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.x + roundray) + " " + (this.sons[0].y);
				}
			}
			path2.setAttribute("d", d2);
			path2.setAttribute("opacity", this.sons[0].opacity);
			path2.setAttribute("fill-opacity", this.sons[0].opacity);
			path2.addEventListener("mouseover", lineMouseOver, false);
			path2.addEventListener("mouseout", lineMouseOut, false);
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this.sons[0];
			path2.setAttribute("indexNode", clickedIndex);
			path2.addEventListener("click",lineMouseClick, false);
			this.sons[0].round=path2;
			if (drawTheEnd==1) {
				this.sons[0].addRound=1;
				svg.appendChild(path2);
			}

			var path3 = document.createElementNS("http://www.w3.org/2000/svg", "path");
			if (this.sons[count-1].colored == 1) {
				path3.setAttribute("stroke", tagColor);
			}  else if (this.sons[count-1].color!="") {
				path3.setAttribute("stroke", this.sons[count-1].color);			
			} else {
				path3.setAttribute("stroke", lineColor);
			}
			path3.setAttribute("stroke-width", lineWidth);
			path3.setAttribute("fill", "none");
			path3.setAttribute("opacity", this.sons[count-1].opacity);
			path3.setAttribute("fill-opacity", this.sons[count-1].opacity);
			var d3 = "";
			if ((this.sons[count-1].y-roundray)>this.y) {
				if (this.sons[count-1].x< (this.x + roundray)) {
					d3= "M " + (this.x) + " " + (this.sons[count-1].y - roundray) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.sons[count-1].x) + " " + (this.sons[count-1].y);
				} else {
					d3= "M " + (this.x) + " " + (this.sons[count-1].y - roundray) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.x + roundray) + " " + (this.sons[count-1].y);
				}
			} else {
				if (this.sons[count-1].x< (this.x + roundray)) {
					d3= "M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.sons[count-1].x) + " " + (this.sons[count-1].y);
				} else {
					d3= "M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.x + roundray) + " " + (this.sons[count-1].y);
				}
			}
			path3.setAttribute("d", d3);
			path3.addEventListener("mouseover", lineMouseOver, false);
			path3.addEventListener("mouseout", lineMouseOut, false);
			clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this.sons[count-1];
			path3.setAttribute("indexNode", clickedIndex);
			path3.addEventListener("click",lineMouseClick, false);
			this.sons[count-1].round=path3;
			if (drawTheEnd==1) {
				this.sons[count-1].addRound=1;
				svg.appendChild(path3);
			}

			// display duplication case
			if (this.support.indexOf("D_",0)!=-1) {
				var polyDup = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');
				polyDup.setAttribute('points', (this.x -2*lineWidth-lineWidth) + "," + (this.y -lineWidth) + " " + (this.x-2*lineWidth+lineWidth) + "," + (this.y - lineWidth) + " " + (this.x-2*lineWidth+lineWidth) + "," + (this.y + lineWidth) + " " + (this.x-2*lineWidth  - lineWidth) + "," + (this.y + lineWidth));
				if (this.color!="") {
					polyDup.setAttribute("stroke", this.color);			
				} else {
					polyDup.setAttribute('stroke', lineColor);
				}
				polyDup.setAttribute("stroke-width", 0);
				if (this.color!="") {
					polyDup.setAttribute("fill", this.color);			
				} else {
					polyDup.setAttribute('fill', lineColor);
				}
				polyDup.setAttribute("opacity", this.opacity);
				polyDup.setAttribute("fill-opacity", this.opacity);
				this.nodeType=polyDup;
				if (drawTheEnd==1) {
					this.addNodeType=1;
					svg.appendChild(polyDup);
				}
			}

			// display transfert case
			if (this.support.indexOf("T_",0)!=-1) {
				var polyDup = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');
				polyDup.setAttribute('points', (this.x -2*lineWidth-2*lineWidth) + "," + (this.y -2*lineWidth) + " " + (this.x-2*lineWidth+lineWidth) + "," + (this.y) + " " + (this.x-2*lineWidth-2*lineWidth) + "," + (this.y + 2*lineWidth));
				if (this.color!="") {
					polyDup.setAttribute("stroke", this.color);			
				} else {
					polyDup.setAttribute('stroke', lineColor);
				}
				polyDup.setAttribute("stroke-width", 0);
				if (this.color!="") {
					polyDup.setAttribute("fill", this.color);			
				} else {
					polyDup.setAttribute('fill', lineColor);
				}
				polyDup.setAttribute("opacity", this.opacity);
				polyDup.setAttribute("fill-opacity", this.opacity);
				this.nodeType=polyDup;
				if (drawTheEnd==1) {
					this.addNodeType=1;
					svg.appendChild(polyDup);
				}
			}
			// display support case
			var text1 = document.createElementNS("http://www.w3.org/2000/svg", "text");
			var sup= this.support;
			if (sup.indexOf("_",0)!=-1) {
				sup=sup.substring(sup.lastIndexOf("_")+1,sup.length);
			}
			if (sup.length>5) {
				sup=sup.substring(0,5);
			}
			text1.setAttribute("x", this.x - parseInt(supportSize)*sup.length/2.0 - 2);
			text1.setAttribute("y", this.y- parseInt(supportSize)/3 - 4);
			text1.setAttribute("font-family", fontFamily);
			text1.setAttribute("font-size", parseInt(supportSize));
			text1.setAttribute("opacity", this.opacity);
			text1.setAttribute("fill-opacity", this.opacity);
			if (this.support.indexOf("D") == 0) {
				text1.setAttribute("fill", tagColor);
			} else {
				text1.setAttribute("fill", fontColor);
			}
			text1.appendChild(document.createTextNode(sup));
			text1.addEventListener("mouseover", supportMouseOver, false);
			text1.addEventListener("mouseout", supportMouseOut, false);
			this.sup=text1;
			if (drawTheEnd==1 && displaySupport!=0) {
				this.addSup=1;
				svg.appendChild(text1);

			}
// It's a node
			var i=0;
			for (i = 0; i < count; i++) {



					var lineRight = document.createElementNS("http://www.w3.org/2000/svg", "line");
					if (this.sons[i].colored == 1) {
						lineRight.setAttribute("stroke", tagColor);
					}  else if (this.sons[i].color!="") {
						lineRight.setAttribute("stroke", this.sons[i].color);			
					} else {
						lineRight.setAttribute("stroke", lineColor);
					}
					lineRight.setAttribute("stroke-width", lineWidth);
					lineRight.setAttribute("x1", this.sons[i].x);
					lineRight.setAttribute("y1", this.sons[i].y);
					if (i==0 || i==(count-1)) {
						lineRight.setAttribute("x2", this.x + roundray);
					} else {
						lineRight.setAttribute("x2", this.x);
					}
					lineRight.setAttribute("y2", this.sons[i].y);
					lineRight.addEventListener("mouseover", lineMouseOver, false);
					lineRight.addEventListener("mouseout", lineMouseOut, false);
					lineRight.setAttribute("opacity", this.sons[i].opacity);
					lineRight.setAttribute("fill-opacity", this.sons[i].opacity);

					var clickedIndex=clickedTreeNodes.length;
					clickedTreeNodes[clickedIndex]=this.sons[i];
					this.sons[i].line=lineRight;
					lineRight.setAttribute("indexNode", clickedIndex);
					lineRight.addEventListener("click",lineMouseClick, false);
					if (drawTheEnd==1 && this.sons[i].x> (this.x + roundray)) {
						this.sons[i].addLine=1;

						svg.appendChild(lineRight);
					}

				if (this.sons[i].collapsed!="") {
					//collapse polygon
					var polyCol = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');

					if (displaytype=="ultra") {
						polyCol.setAttribute('points', (this.sons[i].x) + "," + (this.sons[i].y) + " " + (width-margin-taxaMargin) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + " " + (width-margin-taxaMargin) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))));
					} else {
						polyCol.setAttribute('points', (this.sons[i].x) + "," + (this.sons[i].y) + " " + (this.sons[i].upx) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + " " + (this.sons[i].upx) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))));
					}
					if (this.sons[i].color!="") {
						polyCol.setAttribute("stroke", this.sons[i].color);			
					} else {
						polyCol.setAttribute('stroke', lineColor);
					}
					polyCol.setAttribute("stroke-width", lineWidth);
					polyCol.setAttribute('fill', collapseColor);

					polyCol.addEventListener("mouseover", lineMouseOver, false);
					polyCol.addEventListener("mouseout", lineMouseOut, false);
					polyCol.setAttribute("opacity", this.sons[i].opacity);
					polyCol.setAttribute("fill-opacity", this.sons[i].opacity);

					var clickedIndex=clickedTreeNodes.length;
					clickedTreeNodes[clickedIndex]=this.sons[i];
					this.sons[i].poly=polyCol;
					polyCol.setAttribute("indexNode", clickedIndex);
					polyCol.addEventListener("click",lineMouseClick, false);


					if (drawTheEnd==1) {
						this.sons[i].addPoly=1;
						svg.appendChild(polyCol);
					}

				}


				this.sons[i].drawTree(taxaMargin,0,drawTheEnd);
			}
		} else {
			// The leaf case

			var text1 = document.createElementNS("http://www.w3.org/2000/svg", "text");
			text1.setAttribute("x", this.x + 5);
			text1.setAttribute("y", this.y+ (fontSize/3));
			text1.setAttribute("font-family", fontFamily);
			text1.setAttribute("font-size", fontSize);
			if (this.colored == 1) {
				text1.setAttribute("fill", tagColor);
			} else {
				text1.setAttribute("fill", fontColor);
			}
			text1.appendChild(document.createTextNode(this.taxon));
			text1.addEventListener("mouseover", textMouseOver, false);
			text1.addEventListener("mouseout", textMouseOut, false);
			text1.setAttribute("opacity", this.opacity);
			text1.setAttribute("fill-opacity", this.opacity);
			if (infos[this.taxon]!=null) {
				var clickedIndex=clickedTreeNodes.length;
				clickedTreeNodes[clickedIndex]=this;
				text1.setAttribute("indexNode", clickedIndex);
				text1.addEventListener("mousedown", leafLink , false);
			}
			this.text=text1;
			if (drawTheEnd==1) {
					this.addText=1;
					svg.appendChild(text1);
			}
			selection=text1;

			var z=0;
			for (z=0;z<annotArray.length;z++) {
				var localTag= annotArray[z];


				if (infos[this.taxon]!=null) {
					if (typeAnnot[localTag]=="plain" && infos[this.taxon][localTag]!=null && hideShowAnnot[localTag]!=null) {

						var xtext=document.createElementNS("http://www.w3.org/2000/svg","text");
						//alert(annotXArray[localTag]);
						if (annotXArray[localTag]!=null) {
							xtext.setAttribute ("x", width - margin - annotMargin + annotXArray[localTag]);
						} else {
							xtext.setAttribute ("x", width - margin - annotMargin);						
						}
						xtext.setAttribute ("y", this.y + (fontSize/3));
						xtext.setAttribute("font-family", fontFamily);
						xtext.setAttribute("font-size", fontSize);
						xtext.setAttribute("opacity", this.opacity);
						xtext.setAttribute("fill-opacity", this.opacity);
						xtext.appendChild(document.createTextNode(infos[this.taxon][localTag]));
						this.xtext[localTag]=xtext;
						if (drawTheEnd==1 && hideShowAnnot[localTag]!=0) {
							this.addXtext[localTag]=1;
							svg.appendChild(xtext);
						} else {
							this.addXtext[localTag]=0;

						}

					}

				}

			}

			//annotations
			if (infos[this.taxon]!=null) {
				if (infos[this.taxon]["PO"]!=null && hideShowAnnot["PO"]!=null) {




					var poimg=document.createElementNS("http://www.w3.org/2000/svg","text");

					poimg.setAttribute ("x", width - margin - annotMargin + annotXArray["PO"]);
					poimg.setAttribute ("y", this.y + (fontSize/3));
					poimg.setAttribute("font-family", fontFamily);
					poimg.setAttribute("font-size", fontSize);
					poimg.appendChild(document.createTextNode("PO"));
					poimg.setAttribute("annot", "PO");
					poimg.addEventListener("mouseover", annotMouseOver, false);
					poimg.addEventListener("mouseout", annotMouseOut, false);
					poimg.setAttribute("opacity", this.opacity);
					poimg.setAttribute("fill-opacity", this.opacity);
					var clickedIndex=clickedTreeNodes.length;
					clickedTreeNodes[clickedIndex]=this;
					poimg.setAttribute("indexNode", clickedIndex);
					this.poimg=poimg;
					if (drawTheEnd==1 && hideShowAnnot["PO"]!=0) {
						this.addPoimg=1;
						svg.appendChild(poimg);
					} else {
						this.addPoimg=0;

					}


				}

				if (infos[this.taxon]["GO"]!=null && hideShowAnnot["GO"]!=null) {


					var goimg=document.createElementNS("http://www.w3.org/2000/svg","text");

					goimg.setAttribute ("x", width - margin - annotMargin + annotXArray["GO"]);
					goimg.setAttribute ("y", this.y + (fontSize/3));
					goimg.setAttribute("font-family", fontFamily);
					goimg.setAttribute("font-size", fontSize);
					goimg.appendChild(document.createTextNode("GO"));
					goimg.setAttribute("annot", "GO");
					goimg.addEventListener("mouseover", annotMouseOver, false);
					goimg.addEventListener("mouseout", annotMouseOut, false);
					goimg.setAttribute("opacity", this.opacity);
					goimg.setAttribute("fill-opacity", this.opacity);
					var clickedIndex=clickedTreeNodes.length;
					clickedTreeNodes[clickedIndex]=this;
					goimg.setAttribute("indexNode", clickedIndex);
					this.goimg=goimg;
					if (drawTheEnd==1 && hideShowAnnot["GO"]!=0) {
						svg.appendChild(goimg);
						this.addGoimg=1;
					} else {
						this.addGoimg=0;
					}

				}
			}

		}
	//}



}

function ffillSplit(threshold,split,splitSens) {

	// Counting the number of sons
	var count = this.sons.length;
	if (this.collapsed!="") {
		split[split.length]=this;
		if (threshold>=this.maxDepth()) {
			splitSens[splitSens.length]=0;	
		} else {
			splitSens[splitSens.length]=1;	
		}
	} else {
		if (count>0) {
			if (threshold>(this.sons[0].maxDepth() + this.sons[1].maxDepth())) {
				split[split.length]=this;
				splitSens[splitSens.length]=0;	
			} else {
				// It's a node
				var i=0;
				for (i = 0; i < count; i++) {
					this.sons[i].fillSplit(threshold,split,splitSens);
				}
			}
		} else {
			// It's a leaf
			split[split.length]=this;
			splitSens[splitSens.length]=0;	
		}
	}

}

function ftoneDownUndocumented() {
	// Counting the number of sons

	var count = this.sons.length;
		//alert(count);
	if (count>0) {
		var i=0;
		var isTone=1;
		var father=-1;
		for (i = 0; i < count; i++) {
			var tempIsTone=this.sons[i].toneDownUndocumented();
			if (tempIsTone==0) {
				isTone=0;
			} else {
				father=i;
			}
		}
		if (father==0 && isTone==0) {	
			if (this.left1!=null) { 
				if (this.left1.getAttribute("opacity")==null || this.left1.getAttribute("opacity")==1.0) {
					this.left1.setAttribute("opacity",opacitydegree);
					this.left1.setAttribute("fill-opacity",opacitydegree);
				} else {
					this.left1.setAttribute("opacity",1.0);
					this.left1.setAttribute("fill-opacity",1.0);
				}
			}	
		
		}
		if (father==1 && isTone==0) {	
			if (this.left2!=null) { 
				if (this.left2.getAttribute("opacity")==null || this.left2.getAttribute("opacity")==1.0) {
					this.left2.setAttribute("opacity",opacitydegree);
					this.left2.setAttribute("fill-opacity",opacitydegree);
				} else {
					this.left2.setAttribute("opacity",1.0);
					this.left2.setAttribute("fill-opacity",1.0);
				}
			}	
		
		}
		if (isTone==1) {
			if (this.line!=null && (this.line.getAttribute("opacity")==null || this.line.getAttribute("opacity")==1.0)) {
				if (this.round!=null) { 
				this.round.setAttribute("opacity",opacitydegree);
				this.round.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.line!=null) { 
				this.line.setAttribute("opacity",opacitydegree);
				this.line.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.poly!=null) { 
				this.poly.setAttribute("opacity",opacitydegree);
				this.poly.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.col!=null) { 
				this.col.setAttribute("opacity",opacitydegree);
				this.col.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.sup!=null) { 
				this.sup.setAttribute("opacity",opacitydegree);
				this.sup.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.text!=null) { 
				this.text.setAttribute("opacity",opacitydegree);
				this.text.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.poimg!=null) { 
				this.poimg.setAttribute("opacity",opacitydegree);
				this.poimg.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.goimg!=null) { 
				this.goimg.setAttribute("opacity",opacitydegree);
				this.goimg.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.left1!=null) { 
				this.left1.setAttribute("opacity",opacitydegree);
				this.left1.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.left2!=null) { 
				this.left2.setAttribute("opacity",opacitydegree);
				this.left2.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.nodeType!=null) { 
				this.nodeType.setAttribute("opacity",opacitydegree);
				this.nodeType.setAttribute("fill-opacity",opacitydegree);
				}
			} else {
				if (this.round!=null) { 
				this.round.setAttribute("opacity",1.0);
				this.round.setAttribute("fill-opacity",1.0);
				}	
				if (this.line!=null) { 
				this.line.setAttribute("opacity",1.0);
				this.line.setAttribute("fill-opacity",1.0);
				}	
				if (this.poly!=null) { 
				this.poly.setAttribute("opacity",1.0);
				this.poly.setAttribute("fill-opacity",1.0);
				}	
				if (this.col!=null) { 
				this.col.setAttribute("opacity",1.0);
				this.col.setAttribute("fill-opacity",1.0);
				}	
				if (this.sup!=null) { 
				this.sup.setAttribute("opacity",1.0);
				this.sup.setAttribute("fill-opacity",1.0);
				}	
				if (this.text!=null) { 
				this.text.setAttribute("opacity",1.0);
				this.text.setAttribute("fill-opacity",1.0);
				}	
				if (this.poimg!=null) { 
				this.poimg.setAttribute("opacity",1.0);
				this.poimg.setAttribute("fill-opacity",1.0);
				}	
				if (this.goimg!=null) { 
				this.goimg.setAttribute("opacity",1.0);
				this.goimg.setAttribute("fill-opacity",1.0);
				}	
				if (this.left1!=null) { 
				this.left1.setAttribute("opacity",1.0);
				this.left1.setAttribute("fill-opacity",1.0);
				}	
				if (this.left2!=null) { 
				this.left2.setAttribute("opacity",1.0);
				this.left2.setAttribute("fill-opacity",1.0);
				}	
				if (this.nodeType!=null) { 
				this.nodeType.setAttribute("opacity",1.0);
				this.nodeType.setAttribute("fill-opacity",1.0);
				}	
			}	
			return 1;		
		} else {
			return 0;
		}
	} else {
		if (infos[this.taxon]==null || (infos[this.taxon]["PO"]==null && infos[this.taxon]["GO"]==null)) {
			
			if (this.line!=null && (this.line.getAttribute("opacity")==null || this.line.getAttribute("opacity")==1.0)) {
				if (this.round!=null) { 
				this.round.setAttribute("opacity",opacitydegree);
				this.round.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.line!=null) { 
				this.line.setAttribute("opacity",opacitydegree);
				this.line.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.poly!=null) { 
				this.poly.setAttribute("opacity",opacitydegree);
				this.poly.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.col!=null) { 
				this.col.setAttribute("opacity",opacitydegree);
				this.col.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.sup!=null) { 
				this.sup.setAttribute("opacity",opacitydegree);
				this.sup.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.text!=null) { 
				this.text.setAttribute("opacity",opacitydegree);
				this.text.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.poimg!=null) { 
				this.poimg.setAttribute("opacity",opacitydegree);
				this.poimg.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.goimg!=null) { 
				this.goimg.setAttribute("opacity",opacitydegree);
				this.goimg.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.left1!=null) { 
				this.left1.setAttribute("opacity",opacitydegree);
				this.left1.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.left2!=null) { 
				this.left2.setAttribute("opacity",opacitydegree);
				this.left2.setAttribute("fill-opacity",opacitydegree);
				}	
				if (this.nodeType!=null) { 
				this.nodeType.setAttribute("opacity",opacitydegree);
				this.nodeType.setAttribute("fill-opacity",opacitydegree);
				}
			} else {
				if (this.round!=null) { 
				this.round.setAttribute("opacity",1.0);
				this.round.setAttribute("fill-opacity",1.0);
				}	
				if (this.line!=null) { 
				this.line.setAttribute("opacity",1.0);
				this.line.setAttribute("fill-opacity",1.0);
				}	
				if (this.poly!=null) { 
				this.poly.setAttribute("opacity",1.0);
				this.poly.setAttribute("fill-opacity",1.0);
				}	
				if (this.col!=null) { 
				this.col.setAttribute("opacity",1.0);
				this.col.setAttribute("fill-opacity",1.0);
				}	
				if (this.sup!=null) { 
				this.sup.setAttribute("opacity",1.0);
				this.sup.setAttribute("fill-opacity",1.0);
				}	
				if (this.text!=null) { 
				this.text.setAttribute("opacity",1.0);
				this.text.setAttribute("fill-opacity",1.0);
				}	
				if (this.poimg!=null) { 
				this.poimg.setAttribute("opacity",1.0);
				this.poimg.setAttribute("fill-opacity",1.0);
				}	
				if (this.goimg!=null) { 
				this.goimg.setAttribute("opacity",1.0);
				this.goimg.setAttribute("fill-opacity",1.0);
				}	
				if (this.left1!=null) { 
				this.left1.setAttribute("opacity",1.0);
				this.left1.setAttribute("fill-opacity",1.0);
				}	
				if (this.left2!=null) { 
				this.left2.setAttribute("opacity",1.0);
				this.left2.setAttribute("fill-opacity",1.0);
				}	
				if (this.nodeType!=null) { 
				this.nodeType.setAttribute("opacity",1.0);
				this.nodeType.setAttribute("fill-opacity",1.0);
				}	
			}		
			//alert(taxon + " " + 1);	
			return 1;
		} else {
			//alert(taxon + " " + 2);	
			return 0;
		}
		 
		
	}
}

function ftoneDown() {
	// Counting the number of sons
	//alert(this.line.getAttribute("opacity"));
	if (this.line.getAttribute("opacity")==null || this.line.getAttribute("opacity")==1.0) {
		if (this.round!=null) { 
		this.round.setAttribute("opacity",opacitydegree);
		this.round.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.line!=null) { 
		this.line.setAttribute("opacity",opacitydegree);
		this.line.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.poly!=null) { 
		this.poly.setAttribute("opacity",opacitydegree);
		this.poly.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.col!=null) { 
		this.col.setAttribute("opacity",opacitydegree);
		this.col.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.sup!=null) { 
		this.sup.setAttribute("opacity",opacitydegree);
		this.sup.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.text!=null) { 
		this.text.setAttribute("opacity",opacitydegree);
		this.text.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.poimg!=null) { 
		this.poimg.setAttribute("opacity",opacitydegree);
		this.poimg.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.goimg!=null) { 
		this.goimg.setAttribute("opacity",opacitydegree);
		this.goimg.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.left1!=null) { 
		this.left1.setAttribute("opacity",opacitydegree);
		this.left1.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.left2!=null) { 
		this.left2.setAttribute("opacity",opacitydegree);
		this.left2.setAttribute("fill-opacity",opacitydegree);
		}	
		if (this.nodeType!=null) { 
		this.nodeType.setAttribute("opacity",opacitydegree);
		this.nodeType.setAttribute("fill-opacity",opacitydegree);
		}
	} else {
		if (this.round!=null) { 
		this.round.setAttribute("opacity",1.0);
		this.round.setAttribute("fill-opacity",1.0);
		}	
		if (this.line!=null) { 
		this.line.setAttribute("opacity",1.0);
		this.line.setAttribute("fill-opacity",1.0);
		}	
		if (this.poly!=null) { 
		this.poly.setAttribute("opacity",1.0);
		this.poly.setAttribute("fill-opacity",1.0);
		}	
		if (this.col!=null) { 
		this.col.setAttribute("opacity",1.0);
		this.col.setAttribute("fill-opacity",1.0);
		}	
		if (this.sup!=null) { 
		this.sup.setAttribute("opacity",1.0);
		this.sup.setAttribute("fill-opacity",1.0);
		}	
		if (this.text!=null) { 
		this.text.setAttribute("opacity",1.0);
		this.text.setAttribute("fill-opacity",1.0);
		}	
		if (this.poimg!=null) { 
		this.poimg.setAttribute("opacity",1.0);
		this.poimg.setAttribute("fill-opacity",1.0);
		}	
		if (this.goimg!=null) { 
		this.goimg.setAttribute("opacity",1.0);
		this.goimg.setAttribute("fill-opacity",1.0);
		}	
		if (this.left1!=null) { 
		this.left1.setAttribute("opacity",1.0);
		this.left1.setAttribute("fill-opacity",1.0);
		}	
		if (this.left2!=null) { 
		this.left2.setAttribute("opacity",1.0);
		this.left2.setAttribute("fill-opacity",1.0);
		}	
		if (this.nodeType!=null) { 
		this.nodeType.setAttribute("opacity",1.0);
		this.nodeType.setAttribute("fill-opacity",1.0);
		}	
	}
	var count = this.sons.length;
	if (count>0) {
		var i=0;
		for (i = 0; i < count; i++) {
			this.sons[i].toneDown();
		}
	} else {
		
		
	}
}

function fcolorizeSubtree(erasor) {
	if (this.isColoringRoot==0) {
		if (erasor==1) {
			this.color="";
		} else {	
			this.color=colorbranchannote;
		}	
		if (this.round!=null) { 
			if (erasor==1) {		
				this.round.setAttribute("stroke",lineColor);		
			} else {
				this.round.setAttribute("stroke",colorbranchannote);		
			}
		}	
	
		if (this.line!=null) { 
			if (erasor==1) {		
				this.line.setAttribute("stroke",lineColor);		
			} else {
				this.line.setAttribute("stroke",colorbranchannote);		
			}
		}	
	
		if (this.left1!=null) { 
			if (erasor==1) {		
				this.left1.setAttribute("stroke",lineColor);		
			} else {
				this.left1.setAttribute("stroke",colorbranchannote);		
			}
		}	
	
		if (this.left2!=null) { 
			if (erasor==1) {		
				this.left2.setAttribute("stroke",lineColor);		
			} else {
				this.left2.setAttribute("stroke",colorbranchannote);		
			}
		}	
	
		if (this.poly!=null) { 
			if (erasor==1) {		
				this.poly.setAttribute("stroke",lineColor);		
			} else {
				this.poly.setAttribute("stroke",colorbranchannote);		
			}
		}	

		if (this.nodeType!=null) { 
			if (erasor==1) {		
				this.nodeType.setAttribute("stroke",lineColor);			
				this.nodeType.setAttribute("fill",lineColor);	
			} else {
				this.nodeType.setAttribute("stroke",colorbranchannote);		
				this.nodeType.setAttribute("fill",colorbranchannote);		
			}
		}				
		var count = this.sons.length;
		if (count>0) {
			var i=0;
			for (i = 0; i < count; i++) {
				this.sons[i].colorizeSubtree(erasor);
			}
		} else {
		
		
		}
	}
}

// ************************
// Translate the gene tree
function fgetNewick() {
	var res="";
	var count = this.sons.length;
	if (count>0) {
		res=res+"(";
		res=res+this.sons[0].getNewick();
		var i=1;
		for (i = 1; i < count; i++) {
			res=res+","+this.sons[i].getNewick();
		}
		res=res+")";
		res=res + this.support+":"+this.length;
	} else {
		
		res=res + this.taxon+":"+this.length;
	}

	//if () {
		res=res + "[&&NHX:";
		if (this.color!="") {
			res=res+"C="+this.color+":";
		}
		if (this.collapsed!="") {
			res=res+"L="+this.collapsed+":";
		}
		if (this.line!=null && (this.line.getAttribute("opacity")!=null && this.line.getAttribute("opacity")!=1.0)) {
			res=res+"O="+this.line.getAttribute("opacity")+":";		
		}


		res=res + "]";

	//}

	return res;
}

Node.prototype.printTree = fprintTree;
Node.prototype.maxTaxaString = fmaxTaxaString;
Node.prototype.annoteValues=fannoteValues;
Node.prototype.colorizeArbitrarly=fcolorizeArbitrarly;
Node.prototype.colorizeByAnnotation=fcolorizeByAnnotation;
Node.prototype.resetAnnotationColors=fresetAnnotationColors;
Node.prototype.fillInternalTags=ffillInternalTags;
Node.prototype.resetSplitCounters=fresetSplitCounters;
Node.prototype.maxDepth = fmaxDepth;
Node.prototype.maxUltraDepth = fmaxUltraDepth;
Node.prototype.nbLeaves = fnbLeaves;
Node.prototype.fullNbLeaves = ffullNbLeaves;
Node.prototype.findAncestor=ffindAncestor;
Node.prototype.initCoordinates=finitCoordinates;
Node.prototype.initUltraCoordinates=finitUltraCoordinates;
//Node.prototype.refreshCollapse=frefreshCollapse;
Node.prototype.resizeTree=fresizeTree;
Node.prototype.drawTree=fdrawTree;
Node.prototype.fillSplit=ffillSplit;
Node.prototype.toneDownUndocumented=ftoneDownUndocumented;
Node.prototype.toneDown=ftoneDown;
Node.prototype.colorizeSubtree=fcolorizeSubtree;
Node.prototype.getNewick=fgetNewick;



function lineMouseOver(evt) {
    var target = evt.target;
	var clickedTreeNode= clickedTreeNodes[target.getAttribute("indexNode")];
	if (clickedTreeNode.poly!=null)
    	clickedTreeNode.poly.setAttributeNS(null, "stroke-width", (clickedTreeNode.poly.getAttribute("stroke-width")*2));
	if (clickedTreeNode.line!=null)
    	clickedTreeNode.line.setAttributeNS(null, "stroke-width", (clickedTreeNode.line.getAttribute("stroke-width")*2));
	if (clickedTreeNode.round!=null)
    	clickedTreeNode.round.setAttributeNS(null, "stroke-width", (clickedTreeNode.round.getAttribute("stroke-width")*2));
}

function lineMouseOut(evt) {
    var target = evt.target;
	var clickedTreeNode= clickedTreeNodes[target.getAttribute("indexNode")];
	if (clickedTreeNode.poly!=null && clickedTreeNode.poly.getAttribute("stroke-width")!=lineWidth) {
	    clickedTreeNode.poly.setAttributeNS(null, "stroke-width", lineWidth);
	}
	if (clickedTreeNode.line!=null && clickedTreeNode.line.getAttribute("stroke-width")!=lineWidth) {
	    clickedTreeNode.line.setAttributeNS(null, "stroke-width", lineWidth);
	}
	if (clickedTreeNode.round!=null && clickedTreeNode.round.getAttribute("stroke-width")!=lineWidth) {
	   	clickedTreeNode.round.setAttributeNS(null, "stroke-width", lineWidth);
	}

}

function textMouseOver(evt) {
	document.body.style.cursor='pointer';
    var target = evt.target;
    target.setAttributeNS(null, "text-decoration", "underline");
  }

function textMouseOut(evt) {
	document.body.style.cursor='default';
    var target = evt.target;
    target.setAttributeNS(null, "text-decoration", "none");
}



// annotation reactions
function annotMouseOver(evt) {
	//alert("1");
	var target = evt.target;
    var tag = target.getAttribute("annot")
	var clickedTreeNode= clickedTreeNodes[target.getAttribute("indexNode")];
	var xhr_object = null;
	var position = "popannot";
	   if(window.XMLHttpRequest)  xhr_object = new XMLHttpRequest();
	  else
	    if (window.ActiveXObject)  xhr_object = new ActiveXObject("Microsoft.XMLHTTP");

	// On ouvre la requete vers la page désirée
	xhr_object.open("GET", "popupmsdmind.php?id=" + clickedTreeNode.taxon + "&tag=" + tag, true);
	//alert("popupmsdmind.php?id=" + clickedTreeNode.taxon + "&tag=" + tag);
	xhr_object.onreadystatechange = function(){
	if ( xhr_object.readyState == 4 )
	{
			//alert(xhr_object.responseText);
		// j'affiche dans la DIV spécifiées le contenu retourné par le fichier
		document.getElementById(position).innerHTML = xhr_object.responseText;
	}
	}

	// dans le cas du get
	xhr_object.send(null);

	changeVisibilite2("popannot",1);
/*    var target = evt.target;
    var tag = target.getAttribute("annot")
	var clickedTreeNode= clickedTreeNodes[target.getAttribute("indexNode")];
    var local=annotations[clickedTreeNode.taxon][tag].split("\n");
    var i=0;
    var maxLength=(tag + " ANNOTATIONS FOR " + clickedTreeNode.taxon + ":").length+2;
    for (i = 0; i < local.length; i++) {
    	if (local[i].length>maxLength) {
    		maxLength=local[i].length;
    	}

    }


	annotationFrame = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');
	annotationFrame.setAttribute('points', (5) + "," + (clickedTreeNode.y - parseInt(fontSize) / 2) + " " + ( maxLength*parseInt(fontSize)/2 + 10) + "," + (clickedTreeNode.y - parseInt(fontSize) / 2) + " " + (maxLength*parseInt(fontSize)/2 + 10) + "," + (clickedTreeNode.y - parseInt(fontSize) / 2 + 10 + (local.length+1)*parseInt(fontSize)) + " " + ( 5) + "," + (clickedTreeNode.y - parseInt(fontSize) / 2 + 10 + (local.length+1)*parseInt(fontSize)));
	annotationFrame.setAttribute('stroke', lineColor);
	annotationFrame.setAttribute("stroke-width", lineWidth);
	annotationFrame.setAttribute('fill', backColor);
	svg.appendChild(annotationFrame);

	annotationTexts= new Array();
    i=0;
    var localy= (clickedTreeNode.y + parseInt(fontSize)  / 2);
    var localText=document.createElementNS("http://www.w3.org/2000/svg", "text");
	localText.setAttribute("x", 10);
	localText.setAttribute("y", localy);
	localText.setAttribute("font-family", fontFamily);
	localText.setAttribute("font-size", fontSize);
	localText.setAttribute("fill", fontColor);
	localText.appendChild(document.createTextNode(tag + " ANNOTATIONS FOR " + clickedTreeNode.taxon + ":"));
	svg.appendChild(localText);

	annotationTexts[0]=localText;

	localy+= parseInt(fontSize);
    for (i = 0; i < local.length; i++) {
    	var localText2=document.createElementNS("http://www.w3.org/2000/svg", "text");
		localText2.setAttribute("x", 10);
		localText2.setAttribute("y", localy);
		localText2.setAttribute("font-family", fontFamily);
		localText2.setAttribute("font-size", fontSize);
		localText2.setAttribute("fill", fontColor);
		localText2.appendChild(document.createTextNode(local[i]));
		svg.appendChild(localText2);

		annotationTexts[i+1]=localText2;

		localy+= parseInt(fontSize);

    }
    refreshSVG();*/
    //alert(annotations[clickedTreeNode.taxon]["GO"]);
}

function annotMouseOut(evt,tag) {
	changeVisibilite2("popannot",0);
    /*var target = evt.target;
    //target.setAttributeNS(null, "font-size", parseInt(fontSize));
    svg.removeChild(annotationFrame);
    var i=0;
	for (i = 0; i < annotationTexts.length; i++) {
		svg.removeChild(annotationTexts[i]);

	}*/
}

function supportMouseOver(evt) {
    var target = evt.target;
    if (parseInt(fontSize)>7) {
    	target.setAttributeNS(null, "font-size", (parseInt(supportSize)*1.5));
    } else {
    	target.setAttributeNS(null, "font-size", 10);
    }
}

function supportMouseOut(evt) {
    var target = evt.target;
    target.setAttributeNS(null, "font-size", parseInt(supportSize));
}

// Functions related to animations
function lineMouseClick(evt) {
    var target = evt.target;
	var clickedTreeNode= clickedTreeNodes[target.getAttribute("indexNode")];
	if (clickedTreeNode.poly!=null && clickedTreeNode.poly.getAttribute("stroke-width")!=lineWidth) {
	    clickedTreeNode.poly.setAttributeNS(null, "stroke-width", lineWidth);
	}
	if (clickedTreeNode.line!=null && clickedTreeNode.line.getAttribute("stroke-width")!=lineWidth) {
	    clickedTreeNode.line.setAttributeNS(null, "stroke-width", lineWidth);
	}
	if (clickedTreeNode.round!=null && clickedTreeNode.round.getAttribute("stroke-width")!=lineWidth) {
	   	clickedTreeNode.round.setAttributeNS(null, "stroke-width", lineWidth);
	}
	//alert(clickedTreeNode.nbLeaves());
	if (clickedTreeNode.sons.length > 0) {
		if (annotebranchestool=="collapse") {
			if (clickedTreeNode.collapsed=="") {
				clickedTreeNode.collapsed=clickedTreeNode.fullNbLeaves() + " LEAVES";
				if (clickedTreeNode.sons.length>0) {
					clickedTreeNode.tags= new Array();
					var i=0;
					for (i = 0; i < colorArray.length; i++) {
						clickedTreeNode.tags[i]=0;
					}
					clickedTreeNode.fillInternalTags(clickedTreeNode);
				}


			} else {
				clickedTreeNode.collapsed="";

			}
		} else if (annotebranchestool=="down") {
			clickedTreeNode.toneDown();
		} else {
			if (clickedTreeNode.line!=null && clickedTreeNode.line.getAttribute("stroke")==lineColor) {
				clickedTreeNode.colorizeSubtree(0);
				clickedTreeNode.isColoringRoot=1;
			} else if (clickedTreeNode.isColoringRoot==1) {
				clickedTreeNode.isColoringRoot=0;
				clickedTreeNode.colorizeSubtree(1);
			} else {
				clickedTreeNode.colorizeSubtree(0);	
				clickedTreeNode.isColoringRoot=1;	
			}
		}
		reinitCoordinateSVG();
		//clickedTreeNode.refreshCollapse(1);
		resizeSVG();
	}
}

// Hyperlink for leaves
function leafLink(evt) {
    var target = evt.target;
	var clickedTreeNode= clickedTreeNodes[target.getAttribute("indexNode")];
	var link= "http://msdmind.cirad.fr/msdmind/JSP/jmol.jsp?id=" + infos[clickedTreeNode.taxon]["nameMSDMin"];
	top.location.href = link;
}

function supMouseClick(evt) {
    var target = evt.target;
	var clickedTreeNode= clickedTreeNodes[target.getAttribute("indexNode")];
	alert(clickedTreeNode.support + " " + clickedTreeNode.length);
	removeAll();
	drawAll();
}

var collapsedArray= new Object();

function changeTreeType(type) {
	displaytype=type;
	reinitCoordinateSVG();
	resizeSVG();
}

function changeSupport() {
	if (displaySupport==0) {
		displaySupport=1;
	} else {
		displaySupport=0;
	}
	reinitCoordinateSVG();
	resizeSVG();

	//removeAll();
	//drawAll();

}

function collapseLabel(label) {
	if (!collapsedArray.hasOwnProperty(label)) {
		collapsedArray[label]=0;
	}

	var clickedTreeNode= tree.findAncestor(label);

	if (annotebranchestool=="collapse") {
		if (collapsedArray[label]==0) {
			var labelMaj=label;
			clickedTreeNode.collapsed=labelMaj.toUpperCase() + " (" + clickedTreeNode.fullNbLeaves() + ")";
			collapsedArray[label]=1;
		} else {
			clickedTreeNode.collapsed="";
			collapsedArray[label]=0;
		}
		if (clickedTreeNode.sons.length>0) {
			clickedTreeNode.tags= new Array();
				var i=0;
				for (i = 0; i < colorArray.length; i++) {
					clickedTreeNode.tags[i]=0;
				}
			clickedTreeNode.fillInternalTags(clickedTreeNode);
			/*var i=0;
			var s="";
			for (i = 0; i < clickedTreeNode.tags.length; i++) {
				s = s + " " + clickedTreeNode.tags[i];

			}
			alert(s);*/
		}
	} else if (annotebranchestool=="color") {
		if (clickedTreeNode.line!=null && clickedTreeNode.line.getAttribute("stroke")==lineColor) {
			clickedTreeNode.colorizeSubtree(0);
			clickedTreeNode.isColoringRoot=1;
		} else if (clickedTreeNode.isColoringRoot==1) {
			clickedTreeNode.isColoringRoot=0;
			clickedTreeNode.colorizeSubtree(1);
		} else {
			clickedTreeNode.colorizeSubtree(0);	
			clickedTreeNode.isColoringRoot=1;	
		}	
	} else {
		clickedTreeNode.toneDown();	
	}
	reinitCoordinateSVG();
	//clickedTreeNode.refreshCollapse(1);
	resizeSVG();
}

var splits=new Array();
var splitsValues=new Array();
var splitsPath=new Array();
var splitsSens=new Array();
function treeSplit(threshold) {
	splitsValues[splitsValues.length]=threshold;
	resizeSVG();
	var split= splits[splits.length-1];
	var i;
	for (i=0;i<split.length;i++) {
		if (i%2==0) {
			split[i].colorizeArbitrarly("#888888",0);
		} else {			
			split[i].colorizeArbitrarly("#FFFDD0",0);
		}
	}
	
}

function reinitSplits() {
	var i;
	for (i=0;i<splitsValues.length;i++) {
		var split= new Array();
		var splitSens= new Array();
		tree.fillSplit(splitsValues[i],split,splitSens);
		splits[i]=split;
		splitsSens[i]=splitSens;
	}
}

function drawSplits() {
	reinitSplits();
	tree.resetSplitCounters();
	var i=0;
	for (i = 0; i < splitsPath.length; i++) {
		svg.removeChild(splitsPath[i]);
	}
	splitsPath= new Array();
	
	i=0;
	for (i = 0; i < splits.length; i++) {
		var j=0;
		var split= splits[i];
		var splitSens= splitsSens[i];
		var pathString="";
		var pathSplit = document.createElementNS("http://www.w3.org/2000/svg", "path");
		var preCurrentX;
		for (j = 0; j < split.length; j++) {
			var currentX=split[j].x;
			if (splitSens[j]==1) {
				if (displaytype=="ultra") {
					currentX=width-margin-taxaMargin + 5;
				} else {
					currentX=split[j].upx;
				}
			}
			split[j].splitCounter++;
			if (j==0) {
				pathString="M";
				pathString=pathString + (parseInt(currentX)  - 1 * split[j].splitCounter);
				pathString=pathString + " " + parseInt(split[j].y);
			} else {			
				/*pathString=pathString + " Q";
				if (preCurrentX<currentX) {
					pathString=pathString + (parseInt(preCurrentX)  - 15 * split[j].splitCounter);	
					pathString=pathString + " " + parseInt(split[j].y);
					pathString=pathString + " " + (parseInt(currentX)  - 15 * split[j].splitCounter);
					pathString=pathString + " " + parseInt(split[j].y) + " ";
				} else {
					pathString=pathString + (parseInt(currentX)  - 8 * split[j].splitCounter);	
					pathString=pathString + " " + parseInt(split[j-1].y);
					pathString=pathString + " " + (parseInt(currentX)  - 15 * split[j].splitCounter);
					pathString=pathString + " " + parseInt(split[j].y) + " ";
					
				}*/
				
				pathString=pathString + " L";
				if (preCurrentX<currentX) {
					pathString=pathString + (parseInt(currentX)  - 1 * split[j].splitCounter);
					pathString=pathString + " " + parseInt(split[j].y) + " ";
				} else {
					pathString=pathString + (parseInt(currentX)  - 1 * split[j].splitCounter);
					pathString=pathString + " " + parseInt(split[j].y) + " ";				
				}
					
				
				
			}	
			preCurrentX=currentX;			
		}		
		pathSplit.setAttribute("stroke", tagColor);	
		pathSplit.setAttribute("stroke-width", 1);
		pathSplit.setAttribute("fill", "none");
		pathSplit.setAttribute("d", pathString);
		//alert(pathString);
		svg.appendChild(pathSplit);
		splitsPath[splitsPath.length]=pathSplit;
	}
	
}

function saveSVG() {

   function eventFire(el, etype){
        if (el.fireEvent) {
            (el.fireEvent('on' + etype));
        } else {
            var evObj = document.createEvent('Events');
            evObj.initEvent(etype, true, false);
            el.dispatchEvent(evObj);
        }
    }	
	 
	var s = new XMLSerializer();
	var json = s.serializeToString(svg);

	json='<\?xml version="1.0" standalone="yes"?><svg version="1.1" fill="none" stroke="none" stroke-linecap="square" stroke-miterlimit="10" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"' + json.substring(4,json.length);




var blob = new Blob([json], {type: "application/json"});
var url  = URL.createObjectURL(blob);
//window.open(url,"Download");

var link = document.createElement("a");
link.download = "tree.svg";
link.href = url;
    eventFire(link, "click");

//window.open(url, 'download_window', 'toolbar=0,location=no,directories=0,status=0,scrollbars=0,resizeable=0,width=1,height=1,top=0,left=0');
//window.focus(); 
}

</script>

