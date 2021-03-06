<SCRIPT LANGUAGE="JavaScript" SRC="raphael.js">
</SCRIPT>

<script type="text/javascript">

var alertDebug=0;

// Define primal dimensions and margins
var width=1800;
var height=600;
var margin=30;
var annotMargin=400;
var annotX=0;
var legendHeight=40;
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
var lineWidth= 3;
var lineColor= "#05357E";
var collapseColor="#EEEEEE";
var tagColor="#FF0000";
var roundray=20;
var collapseWidth=3.0;
var tagWidth=15;

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

// display or not branch support
var displaySupport=0;

// Reference clickable objects
var clickedTreeNodes;
var legendTexts;

// the SVG objects
var svg;
var legendSvg;
var back;
var legendBack;

function setAttributes(widthparam,displaytypeparam,lineParam,roundparam,fontSizeParam,supportSizeParam,fontFamilyParam,collapseWidthParam,backColorParam,lineColorParam,collapseColorParam,fontColorParam, tagColorParam) {
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
		supportSize=parseInt(supportSizeParam);
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
	reinitCoordinateSVG();
	resizeSVG();
}
function resetColors() {
	tree.resetAnnotationColors();
	colorArray= new Array();
	resizeSVG();

	// hide legend

	changeVisibilite2("legend",0);
	legendSvg.remove();
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
		changeVisibilite2("legend",1);
		// Build the main SVG object

		legendTexts= new Array();
		legendSvg = Raphael('legend', localWidth(), legendHeight);
		legendBack = legendSvg.rect(0,0,localWidth(),legendHeight);
		legendBack.attr({fill:backColor, stroke:"none"});

	}
	//add new element to legend

	var inner= legendSvg.rect(legendX + 20,10,20,parseInt(legendFontSize));
	inner.attr({fill:color, stroke:"none","stroke-width":lineWidth});
	legendX+=45;
	var text1= legendSvg.text(legendX+parseInt(legendFontSize)*word.length/4,10+parseInt(legendFontSize)/2,word);
	text1.attr({"font-family":fontFamily, "font-size":legendFontSize,fill:fontColor,"stroke":"none"});
	legendX+=parseInt(legendFontSize)*word.length/2;
	legendTexts[legendTexts.length]=text1;
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
	svg.setSize(width,height);
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
		back.attr({"fill": backColor});
		if (legendBack!=null) {
			legendBack.attr({"fill": backColor});
		}
		if (legendTexts!=null) {
			var i = 0;
			for (i = 0; i < legendTexts.length; i++) {
				legendTexts[i].attr({"fill": fontColor});
			}
		}

	   	tree.resizeTree(1);

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
		//alert("start");
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
		svg = Raphael('treePanel', width, height);
		back = svg.rect(0,0,width,height);
		back.attr({fill:backColor, stroke:"none"});
	   	clickedTreeNodes= new Array();
	   	tree.drawTree(taxaMargin,1);
		/*svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		svg.setAttribute('width', width);
		svg.setAttribute('height', height);

		// Build the image border
		var back = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
		back.setAttribute('x', 0);
		back.setAttribute('y', 0);
		back.setAttribute('width', width);
		back.setAttribute('height', height);
		back.setAttribute('stroke', "none");
		back.setAttribute('fill', backColor);
		svg.appendChild(back);

	   	clickedTreeNodes= new Array();
	   	tree.drawTree(taxaMargin,1);
		document.getElementsByName("treePanel")[0].appendChild(svg);*/
		//alert("end");
	}
}

function removeAll() {
	svg.remove();
	//document.getElementsByName("treePanel")[0].removeChild(svg);
}

function refreshSVG() {
	//document.getElementsByName("treePanel")[0].removeChild(svg);
	//document.getElementsByName("treePanel")[0].appendChild(svg);
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
	this.nodeType=null;
	this.addNodeType=0;
	this.tags= new Array();
	this.tagsOut= new Array();
	this.tagsIn= new Array();

	this.xtext= new Array();
	this.addXtext= new Array();


	// Coloration of pattern matching
	this.colored=0;

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
		// While there is still a son to parse
		while (newick.charAt(index)==",") {
			// Parse the current son
			sonIndex++;
			index++;
			son= new Node(newick);
			this.sons[sonIndex]= son;
		}
		index++;

		while (newick.charAt(index)!=":" && newick.charAt(index)!="," && newick.charAt(index)!=")" && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
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
			while (newick.charAt(index)!="," && newick.charAt(index)!=")"  && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
				this.length = this.length + newick.charAt(index);
				index++;
			}
			//echo this.length;
		}
	} else {

		// The leaf case
		while (newick.charAt(index)!=":" && newick.charAt(index)!="," && newick.charAt(index)!=")" && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
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
			while (newick.charAt(index)!="," && newick.charAt(index)!=")"  && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
				this.length = this.length + newick.charAt(index);
				index++;
			}
			//echo this.length;
		}
		if (newick.charAt(index)=="$") {
			this.colored=1;
			index++;
		}
		while (newick.charAt(index)!=":" && newick.charAt(index)!="," && newick.charAt(index)!=")"  && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
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

			//outer polygon
			var outer = svg.path('M'+ (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + up + "L" + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth) + tagWidth -1) + "," + up + "L" + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth) + tagWidth -1) + "," + down + "L" + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + down + "L" + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + up);

			outer.attr({'stroke': "none","stroke-width": 0,"fill": collapseColor});

			outer.mouseover(lineMouseOver);
			outer.mouseout(lineMouseOut);
			this.tagsOut[this.tagsOut.length]=outer;

			//inner polygon
			var inner = svg.path('M' +  (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + limit + "L" + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth) + tagWidth -1) + "," + limit + "L" + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth) + tagWidth -1) + "," + down + "L" + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + down + "L" + (width + annotX - margin - annotMargin + (this.tagsIn.length*tagWidth))+ "," + limit);

			inner.attr({'stroke': "none","stroke-width": 0,"fill": colorArray[colorArray.length-1]});

			inner.mouseover(lineMouseOver);
			inner.mouseout(lineMouseOut);
			this.tagsIn[this.tagsIn.length]=inner;

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
				this.tags[nbtags]= svg.path('M' + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y - (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y + (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y + (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2));
				//this.tags[nbtags].setAttribute('stroke', lineColor);

				this.tags[nbtags].attr({"stroke-width": 0,"fill": colorparam});

				if (this.addText==1 && hide==0) {
					this.tags[nbtags].show();
				} else {
					this.tags[nbtags].hide();
				}
				already=1;
				loc++;

			//alert(this.color);
			}
		}
		if (already==0) {

				var nbtags= this.tags.length;
				this.tags[nbtags]= svg.path('M' + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y - (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (this.y + (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y + (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2));
				//this.tags[nbtags].setAttribute('stroke', lineColor);

				this.tags[nbtags].attr({"stroke-width": 0,"fill": collapseColor});

				if (this.addText==1 && hide==0) {
					this.tags[nbtags].show();
				} else {
					this.tags[nbtags].hide();
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
				this.tagsIn[w].remove();
				this.tagsOut[w].remove();
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
				this.tags[i].remove();
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
			var c= this.tags[i].attr("fill");
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
					this.xtext[localTag].hide();
					this.addXtext[localTag]=0;

				}
			}


		}
		if (this.addLine==1) {
			this.line.hide();
			this.addLine=0;
		}
		if (this.addLeft==1) {
			this.left1.hide();
			this.left2.hide();
			this.addLeft=0;
		}
		if (this.addRound==1) {
			this.round.hide();
			this.addRound=0;
		}
		if (this.addText==1) {
			var w=0;
			for (w = 0; w < this.tags.length; w++) {
				this.tags[w].hide();
			}
			this.text.hide();
			this.addText=0;
		}
		if (this.addCol==1) {
			this.col.hide();
			this.addCol=0;
		}
		if (this.addPoly==1) {
			var w=0;
			for (w = 0; w < this.tagsIn.length; w++) {
				this.tagsIn[w].hide();
				this.tagsOut[w].hide();
			}
			this.poly.hide();
			this.addPoly=0;
		}
		if (this.addGoimg==1) {
			this.goimg.hide();
			this.addGoimg=0;
		}
		if (this.addPoimg==1) {
			this.poimg.hide();
			this.addPoimg=0;
		}
		if (this.addSup==1) {
			this.sup.hide();
			this.addSup=0;
		}

		if (this.addNodeType==1) {
			this.addNodeType=0;
			this.nodeType.hide();
		}


	} else {

		if (this.collapsed!="") {
			if (this.addCol==0) {
				// collapse text
				var text1;
				if (displaytype=="ultra") {
					text1= svg.text(width-margin-taxaMargin + 5 + parseInt(fontSize)*this.collapsed.length/4,this.y,this.collapsed);
				} else {
					text1= svg.text(this.upx + 5 + parseInt(fontSize)*this.collapsed.length/4,this.y,this.collapsed);
				}
				text1.attr({"font-family": fontFamily,"font-size": fontSize,"fill": fontColor});
				text1.mouseover(textMouseOver);
				text1.mouseout(textMouseOut);
				this.col=text1;
				text1.show();
				this.addCol=1;


				// clean this subtree from old topology
				if (this.addLeft==1) {
					this.addLeft=0;
					this.left1.hide();
					this.left2.hide();
				}
				if (this.addGoimg==1) {
					this.goimg.hide();
					this.addGoimg=0;
				}
				if (this.addNodeType==1) {
					this.nodeType.hide();
					this.addNodeType=0;
				}
				if (this.addPoimg==1) {
					this.poimg.hide();
					this.addPoimg=0;
				}
				if (this.addText==1) {
					this.text.hide();
					this.addText=0;
				}
				var i=0;
				for (i=0;i<this.sons.length;i++) {
					this.sons[i].resizeTree(0);
				}
			} else {
				// The already collapsed case
				if (displaytype=="ultra") {
					this.col.attr({"x": width-margin-taxaMargin + 5 + parseInt(fontSize)*this.collapsed.length/4});
				} else {
					this.col.attr({"x": this.upx + 5 + parseInt(fontSize)*this.collapsed.length/4});
				}
				this.col.attr({"y": this.y,"font-size": fontSize, "font-family": fontFamily, "fill": fontColor});
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
				this.sup.attr({"x": this.x - supportSize*sup.length/2.0 - 2,"y": this.y - supportSize/3 - 4});
				if (this.addSup==0) {
					this.addSup=1;
					this.sup.show();
				}
			} else {
				if (this.addSup==1) {
					this.addSup=0;
					this.sup.hide();

				}

			}

		} else {
			if (this.addCol==1) {
				this.addCol=0;
				this.col.hide();

			}
			// Counting the number of sons
			var count = this.sons.length;
			if (count>0) {

				if ((this.sons[0].y+roundray)<this.y) {
					if (this.addLeft==0) {
						this.addLeft=1;
						this.left1.show();
						this.left2.show();

					}
					if (this.sons[0].colored==1 && this.colored==1) {
						this.left1.attr({"path": "M" + this.x + " " + (this.sons[0].y + roundray) + " L" + this.x + " " + this.y, "stroke-width": lineWidth, "stroke": tagColor});
					} else {
						this.left1.attr({"path": "M" + this.x + " " + (this.sons[0].y + roundray) + " L" + this.x + " " + this.y, "stroke-width": lineWidth, "stroke": lineColor});

					}
					if (this.sons[count-1].colored==1 && this.colored==1) {
						this.left2.attr({"path": "M" + this.x + " " + this.y + " L" + this.x + " " + (this.sons[count-1].y - roundray), "stroke-width": lineWidth, "stroke": tagColor});
					} else {
						this.left2.attr({"path": "M" + this.x + " " + this.y + " L" + this.x + " " + (this.sons[count-1].y - roundray), "stroke-width": lineWidth, "stroke": lineColor});

					}
				} else {

					if (this.addLeft==1) {
						this.addLeft=0;
						this.left1.hide();
						this.left2.hide();

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
				if (this.sons[0].colored==1 && this.colored==1) {
					this.sons[0].round.attr({"path": d2, "stroke-width": lineWidth, "stroke": tagColor});
				} else {
					this.sons[0].round.attr({"path": d2, "stroke-width": lineWidth, "stroke": lineColor});

				}

				if (this.sons[0].addRound==0) {

					this.sons[0].addRound=1;
					this.sons[0].round.show();

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
				if (this.sons[count-1].colored==1 && this.colored==1) {
					this.sons[count-1].round.attr({"path": d3, "stroke-width": lineWidth, "stroke": tagColor});
				} else {
					this.sons[count-1].round.attr({"path": d3, "stroke-width": lineWidth, "stroke": lineColor});

				}
				if (this.sons[count-1].addRound==0) {

					this.sons[count-1].addRound=1;
					this.sons[count-1].round.show();

				}


				// duplication
				if (this.support.indexOf("D_",0)!=-1) {
					this.nodeType.attr({"x": this.x -2*lineWidth-lineWidth, "y": this.y -lineWidth, "width": 2*lineWidth, "height": 2*lineWidth, "stroke": lineColor, "fill": lineColor});
					if (this.addNodeType==0) {
						this.addNodeType=1;
						this.nodeType.show();
					}
				}
				// transfert
				if (this.support.indexOf("T_",0)!=-1) {
					this.nodeType.attr({"path":"M " + (this.x -2*lineWidth-2*lineWidth) + " " + (this.y -2*lineWidth) + " L " + (this.x -2*lineWidth+lineWidth) + " " + (this.y) + " L " + (this.x -2*lineWidth-2*lineWidth) + " " + (this.y +2*lineWidth) + " L " + (this.x -2*lineWidth-2*lineWidth) + " " + (this.y -2*lineWidth), "stroke": lineColor, "fill": lineColor});
					if (this.addNodeType==0) {
						this.addNodeType=1;
						this.nodeType.show();
					}
				}				

				// support
				if (displaySupport!=0) {
					// display support case
					var sup= this.sup.data("supportLength");
					this.sup.attr({"x": this.x - supportSize*sup/4 - 2,"y": this.y- supportSize/2 - 4});

					this.sup.attr({"font-size": supportSize, "font-family": fontFamily, "fill": fontColor});
					if (this.addSup==0) {
						this.addSup=1;
						this.sup.show();
					}
				} else {
					if (this.addSup==1) {
						this.addSup=0;
						this.sup.hide();

					}

				}
				// Its a node
				var i=0;
				for (i = 0; i < count; i++) {


					if (this.sons[i].x> (this.x + roundray)) {
						if (this.sons[i].addLine==0) {
							this.sons[i].addLine=1;
							this.sons[i].line.show();
							//alert("toto");

						}


						if (i==0 || i==(count-1)) {
							this.sons[i].line.attr({"path": "M" + this.sons[i].x + " " + this.sons[i].y + " L" + (this.x + roundray) + " " + this.sons[i].y});
						} else {
							this.sons[i].line.attr({"path": "M" + this.sons[i].x + " " + this.sons[i].y + " L" + this.x + " " + this.sons[i].y});
						}
						if (this.sons[i].colored==1 && this.colored==1) {
							this.sons[i].line.attr({"stroke-width": lineWidth, "stroke": tagColor});
						} else {
							this.sons[i].line.attr({"stroke-width": lineWidth, "stroke": lineColor});

						}

					} else {
						if (this.sons[i].addLine==1) {
							this.sons[i].addLine=0;
							this.sons[i].line.hide();

						}

					}

					if (this.sons[i].collapsed!="") {
						//collapse polygon

						if (this.sons[i].addPoly==0) {
							// collapse polygon
							var polyCol;

							if (displaytype=="ultra") {
								polyCol= svg.path('M'+ (this.sons[i].x) + "," + (this.sons[i].y) + "L" + (width-margin-taxaMargin) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + "L" + (width-margin-taxaMargin) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))) + "L" + (this.sons[i].x) + "," + (this.sons[i].y));
							} else {
								polyCol= svg.path('M'+ (this.sons[i].x) + "," + (this.sons[i].y) + "L" + (this.sons[i].upx) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + "L" + (this.sons[i].upx) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))), + "L" + (this.sons[i].x) + "," + (this.sons[i].y));
							}
							polyCol.attr({'stroke': lineColor,"stroke-width": lineWidth,"fill": collapseColor});

							polyCol.mouseover(lineMouseOver);
							polyCol.mouseout(lineMouseOut);

							var clickedIndex=clickedTreeNodes.length;
							clickedTreeNodes[clickedIndex]=this.sons[i];
							this.sons[i].poly=polyCol;
							polyCol.data("indexNode", clickedIndex);
							polyCol.click(lineMouseClick);
							this.sons[i].addPoly=1;


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
								var outer = svg.path('M'+ (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + up + "L" + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + up + "L" + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + down + "L" + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + down + "L" + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + up);

								outer.attr({'stroke': "none","stroke-width": 0,"fill": collapseColor});


								this.sons[i].tagsOut[w]=outer;

								//inner polygon
								var inner = svg.path('M' +  (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + limit + "L" + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + limit + "L" + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + down + "L" + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + down + "L" + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + limit);

								inner.attr({'stroke': "none","stroke-width": 0,"fill": colorArray[w]});


								this.sons[i].tagsIn[w]=inner;


							}



						} else {

							if (displaytype=="ultra") {
								this.sons[i].poly.attr({"path" : 'M'+ (this.sons[i].x) + "," + (this.sons[i].y) + "L" + (width-margin-taxaMargin) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + "L" + (width-margin-taxaMargin) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))) + "L" + (this.sons[i].x) + "," + (this.sons[i].y)});
							} else {
								this.sons[i].poly.attr({"path" : 'M'+ (this.sons[i].x) + "," + (this.sons[i].y) + "L" + (this.sons[i].upx) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + "L" + (this.sons[i].upx) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))) + "L" + (this.sons[i].x) + "," + (this.sons[i].y)});
							}

							this.sons[i].poly.attr({"stroke-width": lineWidth, "stroke": lineColor, "fill": collapseColor});

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
								//alert(loc);
								this.sons[i].tagsOut[w].attr({"path": 'M'+ (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + up + "L" + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + up + "L" + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + down + "L" + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + down + "L" + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + up,"fill": collapseColor});
								this.sons[i].tagsIn[w].attr({"path":'M' +  (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + limit + "L" + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + limit + "L" + (width + annotX - margin - annotMargin + (w*tagWidth) + tagWidth -1) + "," + down + "L" + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + down + "L" + (width + annotX - margin - annotMargin + (w*tagWidth))+ "," + limit});

							}




						}
					} else if (this.sons[i].addPoly==1) {
							this.sons[i].addPoly=0;
							this.sons[i].poly.hide();
							var w=0;
							for (w=0;w<this.sons[i].tags.length;w++) {
								this.sons[i].tagsIn[w].hide();
								this.sons[i].tagsOut[w].hide();

							}

					}


					this.sons[i].resizeTree(1);
				}
			} else {
				// The leaf case

				this.text.attr({"x": this.x + 5, "y": this.y, "font-size":fontSize, "font-family": fontFamily, "fill": fontColor, 'text-anchor': 'start'});
				if (this.addText==0) {
					this.text.show();
					this.addText=1;
					var w=0;
					for (w = 0; w < this.tags.length; w++) {
						this.tags[w].show();;
					}

				}


				//tags
				var nbtags= this.tags.length;
				var i = 0;
				for (i = 0; i < nbtags; i++) {
					this.tags[i].attr({"path": 'M'+ (width + annotX - margin - annotMargin + (i*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (i*tagWidth) + tagWidth - 1) + "," + (this.y - (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (i*tagWidth) + tagWidth - 1) + "," + (this.y + (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (i*tagWidth)) + "," + (this.y + (parseInt(fontSize))/2) + "L" + (width + annotX - margin - annotMargin + (i*tagWidth)) + "," + (this.y - (parseInt(fontSize))/2)});
					if (this.tags[i].attr("fill")!=colorArray[i]) {
						this.tags[i].attr({"fill": collapseColor})
					}
				}


				var z=0;
				for (z=0;z<annotArray.length;z++) {
					var localTag= annotArray[z];


					if (infos[this.taxon]!=null) {
						if (typeAnnot[localTag]=="plain" && infos[this.taxon][localTag]!=null && hideShowAnnot[localTag]!=null && hideShowAnnot[localTag]!=0) {
							this.xtext[localTag].attr({"x": width - margin - annotMargin + annotXArray[localTag]+parseInt(fontSize)*localTag.length/2, "y":this.y , "font-size": fontSize, "font-family": fontFamily, "fill": fontColor});

							if (this.addXtext[localTag]==0) {
								this.xtext[localTag].show();
								this.addXtext[localTag]=1;

							}

						} else {
							if (this.addXtext[localTag]==1) {
								this.xtext[localTag].hide();
								this.addXtext[localTag]=0;

							}

						}

					}

				}

				//annotations
				if (infos[this.taxon]!=null) {
					if (infos[this.taxon]["PO"]!=null && hideShowAnnot["PO"]!=null && hideShowAnnot["PO"]!=0) {
						this.poimg.attr({"x": width - margin - annotMargin + annotXArray["PO"]+parseInt(fontSize)*("PO").length/4, "y":this.y , "font-size": fontSize, "font-family": fontFamily, "fill": fontColor});
						if (this.addPoimg==0) {
							this.poimg.show();
							this.addPoimg=1;

						}

					} else {
						if (this.addPoimg==1) {
							this.poimg.hide();
							this.addPoimg=0;

						}

					}

					if (infos[this.taxon]["GO"]!=null && hideShowAnnot["GO"]!=null && hideShowAnnot["GO"]!=0) {
						this.goimg.attr({"x": width - margin - annotMargin + annotXArray["GO"]+parseInt(fontSize)*("GO").length/4, "y":this.y , "font-size": fontSize, "font-family": fontFamily, "fill": fontColor});
						if (this.addGoimg==0) {
							this.goimg.show();
							this.addGoimg=1;

						}
					} else {
						if (this.addGoimg==1) {
							this.goimg.hide();
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
function fdrawTree(taxaMargin,isRoot) {


	if (this.collapsed!="") {
		// *******The collapse case: display collapse text
		/*var text1 = document.createElementNS("http://www.w3.org/2000/svg", "text");
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
		this.col=text1;
		svg.appendChild(text1);*/

		// *******The collapse case: display support case
		/*var textSupport = document.createElementNS("http://www.w3.org/2000/svg", "text");
		var sup= this.support;
		if (sup.indexOf("_",0)!=-1) {
			sup=sup.substring(sup.lastIndexOf("_")+1,sup.length);
		}
		if (sup.length>5) {
			sup=sup.substring(0,5);
		}
		textSupport.setAttribute("x", this.x - supportSize*sup.length/2.0 - 2);
		textSupport.setAttribute("y", this.y- supportSize/3 - 4);
		textSupport.setAttribute("font-family", fontFamily);
		textSupport.setAttribute("font-size", supportSize);
		if (this.taxon.substring(4,0)=="LOSS") {
			textSupport.setAttribute("opacity",0.5);
		}
		textSupport.setAttribute("fill", fontColor);

		textSupport.appendChild(document.createTextNode(sup));
		textSupport.addEventListener("mouseover", supportMouseOver, false);
		textSupport.addEventListener("mouseout", supportMouseOut, false);

		var clickedIndex=clickedTreeNodes.length;
		clickedTreeNodes[clickedIndex]=this;
		this.sup=textSupport;
		textSupport.setAttribute("indexNode", clickedIndex);
		textSupport.addEventListener("click",supMouseClick, false);

		if (displaySupport!=0) {
			this.addSup=1;
			svg.appendChild(textSupport);

		}*/

	} else {
		// Counting the number of sons
		var count = this.sons.length;
		if (count>0) {

			// *******left line

			var lineLeft= svg.path("M" + this.x + " " + (this.sons[0].y + roundray) + " L" + this.x + " " + this.y);
			if (this.sons[0].colored==1 && this.colored==1) {
				lineLeft.attr({"stroke": tagColor});
			} else {
				lineLeft.attr({"stroke": lineColor});
			}
			lineLeft.attr({"stroke-width": lineWidth});

			this.left1=lineLeft;

			if ((this.sons[0].y+roundray)<this.y) {
				this.addLeft=1;
			} else {
				this.addLeft=0;
				lineLeft.hide();
			}


			lineLeft= svg.path("M" + this.x + " " + this.y + " L" + this.x + " " + (this.sons[count-1].y - roundray));
			if (this.sons[count-1].colored==1 && this.colored==1) {
				lineLeft.attr({"stroke": tagColor});
			} else {
				lineLeft.attr({"stroke": lineColor});
			}
			lineLeft.attr({"stroke-width": lineWidth});


			this.left2=lineLeft;

			if ((this.sons[0].y+roundray)<this.y) {
				this.addLeft=1;
			} else {
				this.addLeft=0;
				lineLeft.hide();
			}


			// *******round parts
			var path2;
			if ((this.sons[0].y+roundray)<this.y) {
				if (this.sons[0].x< (this.x + roundray)) {
					path2=svg.path("M " + (this.x) + " " + (this.sons[0].y + roundray) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.sons[0].x) + " " + (this.sons[0].y));
				} else {
					path2=svg.path("M " + (this.x) + " " + (this.sons[0].y + roundray) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.x + roundray) + " " + (this.sons[0].y));
				}
			} else {
				if (this.sons[0].x< (this.x + roundray)) {
					path2=svg.path("M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.sons[0].x) + " " + (this.sons[0].y));
				} else {
					path2=svg.path("M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.x + roundray) + " " + (this.sons[0].y));
				}
			}
			path2.attr({"stroke-width": lineWidth,"fill": "none"});
			if (this.sons[0].colored == 1) {
				path2.attr({"stroke": tagColor});
			} else {
				path2.attr({"stroke": lineColor});
			}

			this.sons[0].round=path2;
			this.sons[0].addRound=1;
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this.sons[0];
			path2.data("indexNode",clickedIndex);

			path2.mouseover(lineMouseOver);
			path2.mouseout(lineMouseOut);
			path2.click(lineMouseClick);

			var path3;
			if ((this.sons[count-1].y-roundray)>this.y) {
				if (this.sons[count-1].x< (this.x + roundray)) {
					path3=svg.path("M " + (this.x) + " " + (this.sons[count-1].y - roundray) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.sons[count-1].x) + " " + (this.sons[count-1].y));
				} else {
					path3=svg.path("M " + (this.x) + " " + (this.sons[count-1].y - roundray) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.x + roundray) + " " + (this.sons[count-1].y));
				}
			} else {
				if (this.sons[count-1].x< (this.x + roundray)) {
					path3=svg.path("M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.sons[count-1].x) + " " + (this.sons[count-1].y));
				} else {
					path3=svg.path("M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.x + roundray) + " " + (this.sons[count-1].y));
				}
			}
			path3.attr({"stroke-width": lineWidth,"fill": "none"});
			if (this.sons[count-1].colored == 1) {
				path3.attr({"stroke": tagColor});
			} else {
				path3.attr({"stroke": lineColor});
			}


			this.sons[count-1].round=path3;
			this.sons[count-1].addRound=1;
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this.sons[count-1];
			path3.data("indexNode",clickedIndex);


			path3.mouseover(lineMouseOver);
			path3.mouseout(lineMouseOut);
			path3.click(lineMouseClick);



			// *******display duplication case
			if (this.support.indexOf("D_",0)!=-1) {
				var polyDup = svg.rect((this.x -2*lineWidth-lineWidth),(this.y -lineWidth),2*lineWidth,2*lineWidth);
				polyDup.attr({"stroke":lineColor, "stroke-width":0, "fill": lineColor});
				this.nodeType=polyDup;
				this.addNodeType=1;

			}
			
			// *******display transfert case
			if (this.support.indexOf("T_",0)!=-1) {
				var polyDup = svg.path("M " + (this.x -2*lineWidth-2*lineWidth) + " " + (this.y -2*lineWidth) + " L " + (this.x -2*lineWidth+lineWidth) + " " + (this.y) + " L " + (this.x -2*lineWidth-2*lineWidth) + " " + (this.y +2*lineWidth) + " L " + (this.x -2*lineWidth-2*lineWidth) + " " + (this.y -2*lineWidth));
				polyDup.attr({"stroke":lineColor, "stroke-width":0, "fill": lineColor});
				this.nodeType=polyDup;
				this.addNodeType=1;

			}
			
			// *******display support case
			var sup= this.support;
			if (sup.indexOf("_",0)!=-1) {
				sup=sup.substring(sup.lastIndexOf("_")+1,sup.length);
			}
			if (sup.length>5) {
				sup=sup.substring(0,5);
			}

			var text1= svg.text(this.x - supportSize*sup.length/4.0 - 2,this.y- supportSize/2 - 4,sup);
			text1.attr({"font-family": fontFamily,"font-size": supportSize,"fill": fontColor});
			text1.data("supportLength",sup.length);
			this.sup=text1;
			text1.mouseover(supportMouseOver);
			text1.mouseout(supportMouseOut);

			if (displaySupport!=0) {
				this.addSup=1;
				text1.show();
			} else {
				text1.hide();
			}

			// It's a node
			var i=0;
			for (i = 0; i < count; i++) {


					// ******* horizontal line
					var lineRight;
					if (i==0 || i==(count-1)) {
						lineRight=svg.path("M" + this.sons[i].x + " " + this.sons[i].y + " L" + (this.x + roundray) + " " + this.sons[i].y);
					} else {
						lineRight=svg.path("M" + this.sons[i].x + " " + this.sons[i].y + " L" + this.x + " " + this.sons[i].y);
					}
					if (this.sons[i].colored == 1) {
						lineRight.attr({"stroke":tagColor});
					} else {
						lineRight.attr({"stroke":lineColor});
					}
					lineRight.attr({"stroke-width":lineWidth});

					this.sons[i].line=lineRight;
					var clickedIndex=clickedTreeNodes.length;
					clickedTreeNodes[clickedIndex]=this.sons[i];
					lineRight.data("indexNode",clickedIndex);


					if (this.sons[i].x> (this.x + roundray)) {
						this.sons[i].addLine=1;
					} else {
						lineRight.hide();
						this.sons[i].addLine=0;
					}

					lineRight.mouseover(lineMouseOver);
					lineRight.mouseout(lineMouseOut);
					lineRight.click(lineMouseClick);


				if (this.sons[i].collapsed!="") {
					// *******collapse polygon
					/*var polyCol = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');

					if (displaytype=="ultra") {
						polyCol.setAttribute('points', (this.sons[i].x) + "," + (this.sons[i].y) + " " + (width-margin-taxaMargin) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + " " + (width-margin-taxaMargin) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))));
					} else {
						polyCol.setAttribute('points', (this.sons[i].x) + "," + (this.sons[i].y) + " " + (this.sons[i].upx) + "," + ((this.sons[i].y - collapseWidth / 2.0 * (parseInt(fontSize)))) + " " + (this.sons[i].upx) + "," + ((this.sons[i].y + collapseWidth / 2.0 * (parseInt(fontSize)))));
					}
					polyCol.setAttribute('stroke', lineColor);
					polyCol.setAttribute("stroke-width", lineWidth);
					polyCol.setAttribute('fill', collapseColor);

					polyCol.addEventListener("mouseover", lineMouseOver, false);
					polyCol.addEventListener("mouseout", lineMouseOut, false);

					var clickedIndex=clickedTreeNodes.length;
					clickedTreeNodes[clickedIndex]=this.sons[i];
					this.sons[i].poly=polyCol;
					polyCol.setAttribute("indexNode", clickedIndex);
					polyCol.addEventListener("click",lineMouseClick, false);


					svg.appendChild(polyCol);	*/

				}


				this.sons[i].drawTree(taxaMargin,0);
			}
		} else {
			// The leaf case

			// *******Taxon
			var text1= svg.text(this.x + 5,this.y,this.taxon);
			text1.attr({"font-family": fontFamily,"font-size": fontSize, 'text-anchor': 'start'});
			if (this.colored == 1) {
				text1.attr({"fill": tagColor});
			} else {
				text1.attr({"fill": fontColor});
			}

			this.text=text1;
			this.addText=1;

			if (infos[this.taxon]!=null) {
				var clickedIndex=clickedTreeNodes.length;
				clickedTreeNodes[clickedIndex]=this;
				text1.data("indexNode", clickedIndex);
				text1.click(leafLink);
			}
			text1.mouseover(textMouseOver);
			text1.mouseout(textMouseOut);

			var z=0;
			for (z=0;z<annotArray.length;z++) {
				var localTag= annotArray[z];


				if (infos[this.taxon]!=null) {
					if (typeAnnot[localTag]=="plain" && infos[this.taxon][localTag]!=null && hideShowAnnot[localTag]!=null) {

						// *******standard label
						var xtext= svg.text(width - margin - annotMargin + annotXArray[localTag]+parseInt(fontSize)*localTag.length/2,this.y, infos[this.taxon][localTag]);
						xtext.attr({"font-family": fontFamily,"font-size": fontSize});
						this.xtext[localTag]=xtext;
						if (hideShowAnnot[localTag]!=0) {
							this.addXtext[localTag]=1;
							xtext.show();
						} else {
							this.addXtext[localTag]=0;
							xtext.hide();

						}

					}

				}

			}


			if (infos[this.taxon]!=null) {
				if (infos[this.taxon]["PO"]!=null && hideShowAnnot["PO"]!=null) {


					// *******annotations PO
					var poimg=svg.text(width - margin - annotMargin + annotXArray["PO"]+parseInt(fontSize)*("PO").length/4,this.y,"PO");
					poimg.attr({"font-family": fontFamily,"font-size": fontSize});
					poimg.data("annot", "PO");
					poimg.mouseover(annotMouseOver);
					poimg.mouseout(annotMouseOut);
					var clickedIndex=clickedTreeNodes.length;
					clickedTreeNodes[clickedIndex]=this;
					poimg.data("indexNode", clickedIndex);
					this.poimg=poimg;
					if (hideShowAnnot["PO"]!=0) {
						this.addPoimg=1;
						poimg.show();
					} else {
						this.addPoimg=0;
						poimg.hide();

					}


				}

				if (infos[this.taxon]["GO"]!=null && hideShowAnnot["GO"]!=null) {


					// *******annotations GO
					var goimg=svg.text(width - margin - annotMargin + annotXArray["GO"]+parseInt(fontSize)*("GO").length/4,this.y,"GO");
					goimg.attr({"font-family": fontFamily,"font-size": fontSize});
					goimg.data("annot", "GO");
					goimg.mouseover(annotMouseOver);
					goimg.mouseout(annotMouseOut);
					var clickedIndex=clickedTreeNodes.length;
					clickedTreeNodes[clickedIndex]=this;
					goimg.data("indexNode", clickedIndex);
					this.goimg=goimg;
					if (hideShowAnnot["GO"]!=0) {
						this.addGoimg=1;
						goimg.show();
					} else {
						this.addGoimg=0;
						goimg.hide();

					}

				}
			}

		}
	}



}

Node.prototype.printTree = fprintTree;
Node.prototype.maxTaxaString = fmaxTaxaString;
Node.prototype.colorizeByAnnotation=fcolorizeByAnnotation;
Node.prototype.resetAnnotationColors=fresetAnnotationColors;
Node.prototype.fillInternalTags=ffillInternalTags;
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



function lineMouseOver(evt) {
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	if (clickedTreeNode.poly!=null)
    	clickedTreeNode.poly.attr({"stroke-width": (lineWidth*2)});
	if (clickedTreeNode.line!=null)
    	clickedTreeNode.line.attr({"stroke-width": (lineWidth*2)});
	if (clickedTreeNode.round!=null)
    	clickedTreeNode.round.attr({"stroke-width": (lineWidth*2)});
}

function lineMouseOut(evt) {
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	if (clickedTreeNode.poly!=null) {
	    clickedTreeNode.poly.attr({"stroke-width": lineWidth});
	}
	if (clickedTreeNode.line!=null) {
	    clickedTreeNode.line.attr({"stroke-width": lineWidth});
	}
	if (clickedTreeNode.round!=null) {
	   	clickedTreeNode.round.attr({"stroke-width": lineWidth});
	}

}

function textMouseOver(evt) {
	document.body.style.cursor='pointer';
  }

function textMouseOut(evt) {
	document.body.style.cursor='default';
}



// annotation reactions
function annotMouseOver(evt) {
	var target = evt.target;
    var tag = this.data("annot");
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
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
	//alert("a");
	//alert(document.getElementById("popannot").style.display);

	changeVisibilite2("popannot",1);
	//alert(document.getElementById("popannot").style.display);
	//alert("b");
}

function annotMouseOut(evt) {
	changeVisibilite2("popannot",0);
}

function supportMouseOver(evt) {
    if (parseInt(fontSize)>7) {
    	this.attr({"font-size": (supportSize*1.5)});
    } else {
    	this.attr({"font-size": 10});
    }
}

function supportMouseOut(evt) {
    this.attr({"font-size": supportSize});
}

// Functions related to animations
function lineMouseClick(evt) {
    var target = evt.target;
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	if (clickedTreeNode.poly!=null) {
	    clickedTreeNode.poly.attr({"stroke-width": lineWidth});
	}
	if (clickedTreeNode.line!=null) {
	    clickedTreeNode.line.attr({"stroke-width": lineWidth});
	}
	if (clickedTreeNode.round!=null) {
	   	clickedTreeNode.round.attr({"stroke-width": lineWidth});
	}
	//alert(clickedTreeNode.nbLeaves());
	if (clickedTreeNode.sons.length > 0) {
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
		reinitCoordinateSVG();
		//clickedTreeNode.refreshCollapse(1);
		resizeSVG();
	}
}

function supMouseClick(evt) {
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	alert(clickedTreeNode.support + " " + clickedTreeNode.length);
}

var collapsedArray= new Object();

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

function changeTreeType(type) {
	displaytype=type;
	reinitCoordinateSVG();
	resizeSVG();
}

function collapseLabel(label) {
	if (!collapsedArray.hasOwnProperty(label)) {
		collapsedArray[label]=0;
	}

	var clickedTreeNode= tree.findAncestor(label);


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
	reinitCoordinateSVG();
	//clickedTreeNode.refreshCollapse(1);
	resizeSVG();
}

function leafLink(evt) {
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	var link= "http://msdmind.cirad.fr/msdmind/JSP/jmol.jsp?id=" + infos[clickedTreeNode.taxon]["nameMSDMin"];
	top.location.href = link;
}
</script>

