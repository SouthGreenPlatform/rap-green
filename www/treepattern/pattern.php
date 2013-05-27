<SCRIPT LANGUAGE="JavaScript" SRC="raphael.js">
</SCRIPT>

<script type="text/javascript">

var alertDebug=0;

// Define primal dimensions and margins
var width=900;
var height=400;
var margin=40;
// Computed regarding data
var taxaMargin=0;

// Define the esthetic parameters of the tree displaying
var fontFamily="Candara";
var fontSize=14;
var smallFontSize=11;
var fontColor="black";
var backColor="white";
var undefColor="white";
var specColor="#6aa84f";
var targetColor="#DDDDDD";
var dupColor="red";

var lineWidth= 2;
var lightLineWidth= 1;
var spotRadius= 10;
var lineColor= /*"black";*/"#05357E";
var roundray=100;
var constraintDelta=10;
var constraintLine=0;
var constraintNoneColor="#FBD099";

// Reference clickable objects
var clickedTreeNodes;

var selectedNode="";
var rightSelection=1;

// the SVG objects
var svg;
var back;
var targetPath;


function drawAll() {
	if (tree!="undef") {
		//alert("start");
		// Compute the private parameters, from the primal dimensions and the tree
		var maxDepth=0.0;
		maxDepth=tree.maxDepth();
		var nbLeaves= tree.nbLeaves();
		var maxTaxaString= tree.maxTaxaString();
		taxaMargin= maxTaxaString * fontSize * 0.6;

		// Initialize the coordinates of each node and leaf of the tree

		tree.initCoordinates(0.0,taxaMargin,maxDepth,nbLeaves,0);


		// Build the main SVG object
		svg = Raphael('treePanel', width, height);
		back = svg.rect(0,0,width,height);
		back.attr({fill:backColor, stroke:"none"});
		targetPath= svg.path("M 0 0");
		targetPath.hide();
	   	clickedTreeNodes= new Array();
	   	tree.drawTree(taxaMargin,1);
	}
}

function refreshAll() {
	var maxDepth=0.0;
	maxDepth=tree.maxDepth();
	var nbLeaves= tree.nbLeaves();
	var maxTaxaString= tree.maxTaxaString();
	taxaMargin= maxTaxaString * fontSize * 0.5;

	// Initialize the coordinates of each node and leaf of the tree

	tree.initCoordinates(0.0,taxaMargin,maxDepth,nbLeaves,0);


	// Build the main SVG object
   	tree.drawTree(taxaMargin,1);
   	if (selectedNode!="") {
   		if (rightSelection==1) {
   			//alert(0);
			targetPath.attr({"path": "M " + (selectedNode.x) + " " + (selectedNode.y) + " L " + width + " " + "0" + " L " + width + " "  + height + " L " + (selectedNode.x) + " " + (selectedNode.y), "stroke-width" : "0", "fill" : targetColor});
   		} else {
   			if (selectedNode.father!="") {
   				//alert(1);
   				targetPath.attr({"path": "M " + (selectedNode.father.x + roundray) + " " + (selectedNode.y) + " L " + width + " " + "0" + " L " + width + " "  + height + " L " + (selectedNode.father.x + roundray) + " " + (selectedNode.y), "stroke-width" : "0", "fill" : targetColor});
   			} else {
   				//alert(2);
   				targetPath.attr({"path": "M " + (margin) + " " + (selectedNode.y) + " L " + width + " " + "0" + " L " + width + " "  + height + " L " + (margin) + " " + (selectedNode.y), "stroke-width" : "0", "fill" : targetColor});
   			}
   		}
   	}
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

	// Sons are an array of SpeciesNode
	this.sons= new Array();

	// Initialize the taxon string
	this.label="";
	
	// Transfer type
	this.transfer=2;
	
	// Taxonomical informations
	this.leftConstraint= new Array();
	this.rightConstraint= new Array();

	// The newick extented label
	this.nhx="";

	// the branch length
	this.length=-1.0;

	// the depth of the tree
	this.depth=0.0;

	// the depth of the tree
	this.father="";

	// Graphical elements
	this.vline1="";
	this.vline2="";
	this.hline="";
	this.round="";
	this.textLabel="";
	this.leftTextLabel="";
	this.spot="";
	this.branchConstraint="";

	// Node or leaf conditionnal
	if (newick.charAt(index)=="(") {
		//document.writeln("A0");
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
			son.father=this;
			this.sons[sonIndex]= son;
		}
		index++;
		//alert(this.label);
		while (newick.charAt(index)!="[" && newick.charAt(index)!=":" && newick.charAt(index)!="," && newick.charAt(index)!=")" && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
			this.label = this.label + newick.charAt(index);
			index++;
		}
		//alert(this.label);
		if (newick.charAt(index)==":") {
			index++;
			var localLength="";
			while (newick.charAt(index)!="[" && newick.charAt(index)!="," && newick.charAt(index)!=")"  && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
				localLength = localLength + newick.charAt(index);
				index++;
			}
			this.length=parseFloat(localLength);
			//echo this.length;
		}
		if (newick.charAt(index)=="[") {
			index++;
			while (newick.charAt(index)!="]") {
				this.nhx = this.nhx + newick.charAt(index);
				index++;
			}
			index++;
			//echo this.length;
		}
	} else {
		//document.writeln("B1");

		// The leaf case
		while (newick.charAt(index)!="[" && newick.charAt(index)!=":" && newick.charAt(index)!="," && newick.charAt(index)!=")" && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
			this.label = this.label + newick.charAt(index);
			index++;
		}
		if (newick.charAt(index)==":") {
			index++;
			var localLength="";
			while (newick.charAt(index)!="[" && newick.charAt(index)!="," && newick.charAt(index)!=")"  && newick.charAt(index)!="(" && newick.charAt(index)!=";") {
				localLength = localLength + newick.charAt(index);
				index++;
			}
			this.length=parseFloat(localLength);
			//echo this.length;
		}
		if (newick.charAt(index)=="[") {
			index++;
			while (newick.charAt(index)!="]") {
				this.nhx = this.nhx + newick.charAt(index);
				index++;
			}
			index++;
			//echo this.length;
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
		document.writeln(this.label + ";" + this.length + " " + this.nhx);
		document.writeln("&nbsp;" + this.x);
		document.writeln("&nbsp;" + this.y);
		document.writeln("<br>");
		i=0;
		for (i = 0; i < count; i++) {
			this.sons[i].printTree(quote+1);
		}
	} else {
		document.writeln(this.label + ";" + this.length + " " + this.nhx);
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
		if (this.rightConstraint.length==0) {
			return 0;
		} else {
			var max=0;
			var i = 0;
			for (i = 0; i < this.rightConstraint.length; i++) {
				// Compute the max depth of the sons
				if (this.rightConstraint[i].length>max) {
					max=this.rightConstraint[i].length;
				}
			}

			return max;
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
		this.depth=max+ 1.0;
		return max+ 1.0;
		//return max+ 1.0;
	} else {
		// It's a leaf
		this.depth=1.0;
		return 1.0;
		//return 1.0;
	}
}
// ************************
// Return the number of leaves
function fnbLeaves() {
	// Counting the number of sons

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
		// Its a leaf
		return 1.0;
	}

}
// ************************
// Return the number of leaves
function fdeleteSubparts() {
	// Counting the number of sons
	if (this.vline1!="") {
		this.vline1.remove();
		this.vline1="";
	}
	if (this.vline2!="") {
		this.vline2.remove();
		this.vline2="";
	}
	if (this.hline!="") {
		this.hline.remove();
		this.hline="";
	}
	if (this.round!="") {
		this.round.remove();
		this.round="";
	}
	if (this.textLabel!="") {
		this.textLabel.remove();
		this.textLabel="";
	}
	if (this.leftTextLabel!="") {
		this.leftTextLabel.remove();
		this.leftTextLabel="";
	}
	if (this.spot!="") {
		this.spot.remove();
		this.spot="";
	}
	if (this.branchConstraint!="") {
		this.branchConstraint.remove();
		this.branchConstraint="";
	}

	var count = this.sons.length;
	if (count>0) {
		// It's a node
		var i=0;
		for (i = 0; i < count; i++) {
			// Compute the number of leaves of the sons
			this.sons[i].deleteSubparts();
		}
	}

}

// ************************
// Initialize the (x,y) coordinates
function finitCoordinates(depth,taxaMargin,maxDepth,nbLeaves,level) {
	//alert(maxDepth);
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
		this.x= minX-(width-2*margin-taxaMargin)/(maxDepth);
		//this.x= margin + (depth + parseFloat(this.length))/maxDepth*(width-2*margin-taxaMargin)
		this.y= sumY/count;

		return newLevel;

	} else {
		// It's a leaf
		this.x=width-margin-taxaMargin;
		this.y=(level+1) * height/(nbLeaves+1);
		return 1;

	}

}


// ************************
// Print the tree in an SVG frame, inside the species tree
function fdrawTree(taxaMargin,isRoot) {

	// Counting the number of sons
	var count = this.sons.length;
	// ******* horizontal line
	var lineRight;
	if (this.hline!="") {
		lineRight=this.hline;
		if (this.father!=""/* || count!=0*/) {
			this.hline.attr({"path" : "M" + this.x + " " + this.y + " L" + (this.father.x + roundray -1) + " " + this.y});
		} else {
			//alert(4);
			this.hline.attr({"path" : "M" + this.x + " " + this.y + " L" + margin + " " + this.y});
		}
	} else {
		if (this.father!=""/* || count!=0*/) {
			lineRight=svg.path("M" + this.x + " " + this.y + " L" + (this.father.x + roundray -1) + " " + this.y);
		} else {
			lineRight=svg.path("M" + this.x + " " + this.y + " L" + margin + " " + this.y);
		}
		this.hline=lineRight;
		var clickedIndex=clickedTreeNodes.length;
		clickedTreeNodes[clickedIndex]=this;
		lineRight.data("indexNode",clickedIndex);
		lineRight.mouseover(lineMouseOver);
		lineRight.mouseout(lineMouseOut);
		lineRight.click(lineMouseClick);
	lineRight.attr({"stroke-width":lineWidth});
	}
	lineRight.attr({"stroke":lineColor});




	if (this.father!="" && this.x> (this.father.x + roundray)) {
		lineRight.show();
	} else {
		if (this.father!="") {
			//alert(this.father.label + " " + (this.father.x + roundray) + " " + this.x);
			lineRight.hide();
		}
	}

	var isTop=0;
	if (this.father!="") {
		if (this.father.sons[0]==this) {
			isTop=1;
		}
	}

	// ******* branch constraint
	lineRight;
	var limitUp=this.y+roundray - constraintDelta;
	if (limitUp> this.father.y-constraintDelta) {
		limitUp=this.father.y-constraintDelta;
	}
	var limitDown=this.y-roundray + constraintDelta;
	if (limitDown< this.father.y+constraintDelta) {
		limitDown=this.father.y+constraintDelta;
	}
	if (this.branchConstraint!="") {
		lineRight=this.branchConstraint;
		if (this.father!=""/* || count!=0*/) {
			if (isTop) {
				this.branchConstraint.attr({"path" : "M" + (this.x - 2*spotRadius) + " " + (this.y-constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y-2*constraintDelta) + " L" + (this.father.x+roundray) + " " + (this.y-2*constraintDelta) + " Q "  + (this.father.x) + " " + (this.y-constraintDelta) + " " + (this.father.x - constraintDelta) + " "+ (limitUp) + " Q "  + (this.father.x) + " " + (this.y -constraintDelta) + " " + (this.father.x+ roundray) + " "+ (this.y - constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y-constraintDelta)});


			} else {
				this.branchConstraint.attr({"path" : "M" + (this.x - 2*spotRadius) + " " + (this.y+constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y+2*constraintDelta) + " L" + (this.father.x+roundray) + " " + (this.y+2*constraintDelta) + " Q "  + (this.father.x) + " " + (this.y+constraintDelta) + " " + (this.father.x - constraintDelta) + " "+ (limitDown) + " Q "  + (this.father.x) + " " + (this.y + constraintDelta) + " " + (this.father.x+ roundray) + " "+ (this.y + constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y+constraintDelta)});
			}
		} else {
				this.branchConstraint.attr({"path" : "M" + (this.x - 2*spotRadius) + " " + (this.y-constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y-2*constraintDelta) + " L" + (margin+roundray) + " " + (this.y-2*constraintDelta) + " L" + (margin) + " " + (this.y-constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y-constraintDelta)});


		}
	} else {
		if (this.father!=""/* || count!=0*/) {
			if (isTop) {
				lineRight=svg.path("M" + (this.x - 2*spotRadius) + " " + (this.y-constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y-2*constraintDelta) + " L" + (this.father.x+roundray) + " " + (this.y-2*constraintDelta) + " Q "  + (this.father.x) + " " + (this.y-constraintDelta) + " " + (this.father.x - constraintDelta) + " "+ (limitUp) + " Q "  + (this.father.x) + " " + (this.y -constraintDelta) + " " + (this.father.x+ roundray) + " "+ (this.y - constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y-constraintDelta));


			} else {
				lineRight=svg.path("M" + (this.x - 2*spotRadius) + " " + (this.y+constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y+2*constraintDelta) + " L" + (this.father.x+roundray) + " " + (this.y+2*constraintDelta) + " Q "  + (this.father.x) + " " + (this.y+constraintDelta) + " " + (this.father.x - constraintDelta) + " "+ (limitDown) + " Q "  + (this.father.x) + " " + (this.y + constraintDelta) + " " + (this.father.x+ roundray) + " "+ (this.y + constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y+constraintDelta));
			}
		} else {
				lineRight=svg.path("M" + (this.x - 2*spotRadius) + " " + (this.y-constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y-2*constraintDelta) + " L" + (margin+roundray) + " " + (this.y-2*constraintDelta) + " L" + (margin) + " " + (this.y-constraintDelta) + " L" + (this.x - 2*spotRadius) + " " + (this.y-constraintDelta));

		}
		this.branchConstraint=lineRight;
		var clickedIndex=clickedTreeNodes.length;
		clickedTreeNodes[clickedIndex]=this;
		lineRight.data("indexNode",clickedIndex);
		lineRight.mouseover(lineMouseOver);
		lineRight.mouseout(lineMouseOut);
		lineRight.click(lineMouseClick);
		lineRight.attr({"fill":constraintNoneColor});
		lineRight.attr({"stroke-width":constraintLine});
	}
	lineRight.attr({"stroke":lineColor});

	lineRight.show();

	if (count>0) {

		// *******left line

		var lineLeft;
		if (this.vline1!="") {
			lineLeft= this.vline1;
			lineLeft.attr({"path":"M" + this.x + " " + (this.sons[0].y + roundray) + " L" + this.x + " " + this.y});
		} else {
			lineLeft= svg.path("M" + this.x + " " + (this.sons[0].y + roundray) + " L" + this.x + " " + this.y);
			this.vline1=lineLeft;
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this.sons[0];
			lineLeft.data("indexNode",clickedIndex);

			lineLeft.mouseover(lineMouseOver);
			lineLeft.mouseout(lineMouseOut);
			lineLeft.click(lineMouseClick);
		}

		lineLeft.attr({"stroke": lineColor});

		lineLeft.attr({"stroke-width": lineWidth});


		if ((this.sons[0].y+roundray)<this.y) {
			lineLeft.show();
		} else {
			lineLeft.hide();
		}


		if (this.vline2!="") {
			lineLeft=this.vline2;
			lineLeft.attr({"path":"M" + this.x + " " + this.y + " L" + this.x + " " + (this.sons[count-1].y - roundray)});
		} else {
			lineLeft= svg.path("M" + this.x + " " + this.y + " L" + this.x + " " + (this.sons[count-1].y - roundray));
			this.vline2=lineLeft;
			lineLeft.attr({"stroke": lineColor});
		}



		lineLeft.attr({"stroke-width": lineWidth});



		if ((this.sons[0].y+roundray)<this.y) {
			lineLeft.show();
		} else {
			lineLeft.hide();
		}


		// *******round parts
		var path2;
		if (this.sons[0].round=="") {
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
			this.sons[0].round=path2;
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this.sons[0];
			path2.data("indexNode",clickedIndex);
			path2.attr({"stroke-width": lineWidth});

			path2.mouseover(lineMouseOver);
			path2.mouseout(lineMouseOut);
			path2.click(lineMouseClick);
		} else {
			path2=this.sons[0].round;
			if ((this.sons[0].y+roundray)<this.y) {
				if (this.sons[0].x< (this.x + roundray)) {
					path2.attr({"path":"M " + (this.x) + " " + (this.sons[0].y + roundray) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.sons[0].x) + " " + (this.sons[0].y)});
				} else {
					path2.attr({"path":"M " + (this.x) + " " + (this.sons[0].y + roundray) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.x + roundray) + " " + (this.sons[0].y)});
				}
			} else {
				if (this.sons[0].x< (this.x + roundray)) {
					path2.attr({"path":"M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.sons[0].x) + " " + (this.sons[0].y)});
				} else {
					path2.attr({"path":"M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[0].y) + " " + (this.x + roundray) + " " + (this.sons[0].y)});
				}
			}

		}
		path2.attr({"fill": "none"});
		path2.attr({"stroke": lineColor});




		var path3;
		if (this.sons[count-1].round=="") {
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
			this.sons[count-1].round=path3;
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this.sons[count-1];
			path3.data("indexNode",clickedIndex);

			path3.attr({"stroke-width": lineWidth});
			path3.mouseover(lineMouseOver);
			path3.mouseout(lineMouseOut);
			path3.click(lineMouseClick);
		} else {
			path3=this.sons[count-1].round;
			if ((this.sons[count-1].y-roundray)>this.y) {
				if (this.sons[count-1].x< (this.x + roundray)) {
					path3.attr({"path":"M " + (this.x) + " " + (this.sons[count-1].y - roundray) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.sons[count-1].x) + " " + (this.sons[count-1].y)});
				} else {
					path3.attr({"path":"M " + (this.x) + " " + (this.sons[count-1].y - roundray) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.x + roundray) + " " + (this.sons[count-1].y)});
				}
			} else {
				if (this.sons[count-1].x< (this.x + roundray)) {
					path3.attr({"path":"M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.sons[count-1].x) + " " + (this.sons[count-1].y)});
				} else {
					path3.attr({"path":"M " + (this.x) + " " + (this.y) + " Q " + (this.x) + " " + (this.sons[count-1].y) + " " + (this.x + roundray) + " " + (this.sons[count-1].y)});
				}
			}

		}
		path3.attr({"fill": "none"});
		path3.attr({"stroke": lineColor});

		// right constraints
		if (this.rightConstraint.length>0) {
			// *******Taxon
			var text1;
			var constraintLabel=this.rightConstraint[0];
			var z=1;
			for (z=1;z<this.rightConstraint.length;z++) {
				constraintLabel=constraintLabel + "\n" + this.rightConstraint[z];
			}
			if (this.textLabel=="") {
				text1= svg.text(this.x  + spotRadius +5 ,this.y,constraintLabel);

				this.textLabel=text1;
				var clickedIndex=clickedTreeNodes.length;
				clickedTreeNodes[clickedIndex]=this;
				text1.data("indexNode",clickedIndex);
				text1.mouseover(nodeMouseOver);
				text1.mouseout(nodeMouseOut);
				text1.click(nodeMouseClick);
			} else {
				text1= this.textLabel;
				text1.attr({"x":this.x + spotRadius +5 ,"y":this.y,"text":constraintLabel});

			}
			text1.attr({"font-size": fontSize,"font-family": fontFamily, 'text-anchor': 'start'});
			text1.attr({"fill": fontColor});

		} else {
			if (this.textLabel!="") {
				this.textLabel.hide();
			}
		}

		// left constraints
		if (this.leftConstraint.length>0) {
			// *******Taxon
			var text1;
			var constraintLabel=this.leftConstraint[0];
			var z=1;
			for (z=1;z<this.leftConstraint.length;z++) {
				constraintLabel=constraintLabel + "\n" + this.leftConstraint[z];
			}
			var calcY=this.y;
			if (this.father!="" && this.father.sons[0]==this) {
				calcY=calcY-z*smallFontSize;
			} else if (this.father!="") {
				calcY=calcY+z*smallFontSize;
			}
			if (this.leftTextLabel=="") {
				if (this.father=="") {
					text1= svg.text(margin  + spotRadius,calcY,constraintLabel);

				} else {
					text1= svg.text(this.father.x  + spotRadius ,calcY,constraintLabel);
				}

				this.leftTextLabel=text1;
				var clickedIndex=clickedTreeNodes.length;
				clickedTreeNodes[clickedIndex]=this;
				text1.data("indexNode",clickedIndex);
				text1.mouseover(lineMouseOver);
				text1.mouseout(lineMouseOut);
				text1.click(lineMouseClick);
			} else {
				text1= this.leftTextLabel;
				if (this.father=="") {
					text1.attr({"font-size": smallFontSize,"x":margin + spotRadius ,"y":calcY,"text":constraintLabel});
				} else {
					text1.attr({"font-size": smallFontSize,"x":this.father.x + spotRadius ,"y":calcY,"text":constraintLabel});

				}

			}
			text1.attr({"font-family": fontFamily, 'text-anchor': 'start'});
			text1.attr({"fill": fontColor});

		} else {
			if (this.leftTextLabel!="") {
				this.leftTextLabel.hide();
			}
		}

		// Its a node
		var i=0;
		for (i = 0; i < count; i++) {
			this.sons[i].drawTree(taxaMargin,0);
		}
	} else {
		//alert(this.label + " : " + this.father);
		// The leaf case
		if (this.rightConstraint.length>0) {
			// *******Taxon
			var text1;
			var constraintLabel=this.rightConstraint[0];
			var z=1;
			for (z=1;z<this.rightConstraint.length;z++) {
				constraintLabel=constraintLabel + "\n" + this.rightConstraint[z];
			}
			if (this.textLabel=="") {
				text1= svg.text(this.x + 5,this.y,constraintLabel);

				this.textLabel=text1;
				var clickedIndex=clickedTreeNodes.length;
				clickedTreeNodes[clickedIndex]=this;
				text1.data("indexNode",clickedIndex);
				text1.mouseover(nodeMouseOver);
				text1.mouseout(nodeMouseOut);
				text1.click(nodeMouseClick);
			} else {
				text1= this.textLabel;
				text1.attr({"x":this.x + 5,"y":this.y,"text":constraintLabel});

			}
			text1.attr({"font-size": fontSize,"font-family": fontFamily, 'text-anchor': 'start'});
			text1.attr({"fill": fontColor});

		} else {
			if (this.textLabel!="") {
				this.textLabel.hide();
			}
		}
		if (this.spot!="") {
			this.spot.hide();
		}

		// left constraints
		if (this.leftConstraint.length>0) {
			// *******Taxon
			var text1;
			var constraintLabel=this.leftConstraint[0];
			var z=1;
			for (z=1;z<this.leftConstraint.length;z++) {
				constraintLabel=constraintLabel + "\n" + this.leftConstraint[z];
			}
			var calcY=this.y;
			if (this.father!="" && this.father.sons[0]==this) {
				calcY=calcY-z*smallFontSize;
			} else if (this.father!="") {
				calcY=calcY+z*smallFontSize;
			}
			if (this.leftTextLabel=="") {
				if (this.father=="") {
					text1= svg.text(margin  + spotRadius,calcY,constraintLabel);

				} else {
					text1= svg.text(this.father.x  + spotRadius ,calcY,constraintLabel);
				}

				this.leftTextLabel=text1;
				var clickedIndex=clickedTreeNodes.length;
				clickedTreeNodes[clickedIndex]=this;
				text1.data("indexNode",clickedIndex);
				text1.mouseover(lineMouseOver);
				text1.mouseout(lineMouseOut);
				text1.click(lineMouseClick);
			} else {
				text1= this.leftTextLabel;
				if (this.father=="") {
					text1.attr({"font-size": smallFontSize,"x":margin + spotRadius ,"y":calcY,"text":constraintLabel});
				} else {
					text1.attr({"font-size": smallFontSize,"x":this.father.x + spotRadius ,"y":calcY,"text":constraintLabel});

				}

			}
			text1.attr({"font-family": fontFamily, 'text-anchor': 'start'});
			text1.attr({"fill": fontColor});

		} else {
			if (this.leftTextLabel!="") {
				this.leftTextLabel.hide();
			}
		}

	}
	if (this.label=="speciation") {
		// undefined leaf
		var undefinedLeaf;
		if (this.spot!="" && this.spot.type=="path") {
			//alert(this.spot.type);
			undefinedLeaf= this.spot;
			undefinedLeaf.attr({"path":"M" + (this.x - spotRadius) + " " + this.y + " L" + this.x + " " + (this.y - spotRadius) + " L" + (this.x + spotRadius) + " " + this.y + " L" + this.x + " " + (this.y + spotRadius) + "z"});
			undefinedLeaf.toFront();
		} else {
			if (this.spot!="") {
				this.spot.remove();
			}
			undefinedLeaf= svg.path("M" + (this.x - spotRadius) + " " + this.y + " L" + this.x + " " + (this.y - spotRadius) + " L" + (this.x + spotRadius) + " " + this.y + " L" + this.x + " " + (this.y + spotRadius) + "z");
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this;
			undefinedLeaf.data("indexNode",clickedIndex);
			undefinedLeaf.attr({"stroke-width": lightLineWidth});
			this.spot=undefinedLeaf;
			undefinedLeaf.mouseover(nodeMouseOver);
			undefinedLeaf.mouseout(nodeMouseOut);
			undefinedLeaf.click(nodeMouseClick);
		}

		undefinedLeaf.attr({"stroke": lineColor});


		undefinedLeaf.attr({"fill": specColor});



		undefinedLeaf.show();
		/*if (this.textLabel!="") {
			this.textLabel.hide();
		}*/

	} else if (this.label=="transfert") {
		//alert(this.transfer);
		// undefined leaf
		var undefinedLeaf;
		if (this.spot!="" && this.spot.type=="path") {
			//alert(this.spot.type);
			undefinedLeaf= this.spot;
			if (this.transfer==0) {
				undefinedLeaf.attr({"path":"M" + (this.x) + " " + (this.y - spotRadius) + " L" + (this.x + spotRadius) + " " + (this.y + spotRadius) + " L" + (this.x - spotRadius) + " " + (this.y + spotRadius) + "z"});
			} else if (this.transfer==1) {
				undefinedLeaf.attr({"path":"M" + (this.x) + " " + (this.y + spotRadius) + " L" + (this.x + spotRadius) + " " + (this.y - spotRadius) + " L" + (this.x - spotRadius) + " " + (this.y - spotRadius) + "z"});		
			} else {
				undefinedLeaf.attr({"path":"M" + (this.x - spotRadius) + " " + (this.y - spotRadius) + " L" + (this.x + spotRadius) + " " + (this.y) + " L" + (this.x - spotRadius) + " " + (this.y + spotRadius) + "z"});		
			}
			undefinedLeaf.toFront();
		} else {
			if (this.spot!="") {
				this.spot.remove();
			}
			undefinedLeaf= svg.path("M" + (this.x - spotRadius) + " " + (this.y - spotRadius) + " L" + (this.x + spotRadius) + " " + (this.y) + " L" + (this.x - spotRadius) + " " + (this.y + spotRadius) + "z");
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this;
			undefinedLeaf.data("indexNode",clickedIndex);
			this.spot=undefinedLeaf;
			undefinedLeaf.mouseover(nodeMouseOver);
			undefinedLeaf.mouseout(nodeMouseOut);
			undefinedLeaf.click(nodeMouseClick);
			undefinedLeaf.attr({"stroke-width": lightLineWidth});
		}

		undefinedLeaf.attr({"stroke": lineColor});


		undefinedLeaf.attr({"fill": dupColor});



		undefinedLeaf.show();
		/*if (this.textLabel!="") {
			this.textLabel.hide();
		}*/

	} else if (this.label=="duplication") {
		// undefined leaf
		var undefinedLeaf;
		if (this.spot!="" && this.spot.type=="path") {
			//alert(this.spot.type);
			undefinedLeaf= this.spot;
			undefinedLeaf.attr({"path":"M" + (this.x - spotRadius) + " " + (this.y - spotRadius) + " L" + (this.x + spotRadius) + " " + (this.y - spotRadius) + " L" + (this.x + spotRadius) + " " + (this.y + spotRadius) + " L" + (this.x - spotRadius) + " " + (this.y + spotRadius) + "z"});
			undefinedLeaf.toFront();
		} else {
			if (this.spot!="") {
				this.spot.remove();
			}
			undefinedLeaf= svg.path("M" + (this.x - spotRadius) + " " + (this.y - spotRadius) + " L" + (this.x + spotRadius) + " " + (this.y - spotRadius) + " L" + (this.x + spotRadius) + " " + (this.y + spotRadius) + " L" + (this.x - spotRadius) + " " + (this.y + spotRadius) + "z");
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this;
			undefinedLeaf.data("indexNode",clickedIndex);
			this.spot=undefinedLeaf;
			undefinedLeaf.mouseover(nodeMouseOver);
			undefinedLeaf.mouseout(nodeMouseOut);
			undefinedLeaf.click(nodeMouseClick);
			undefinedLeaf.attr({"stroke-width": lightLineWidth});
		}

		undefinedLeaf.attr({"stroke": lineColor});


		undefinedLeaf.attr({"fill": dupColor});



		undefinedLeaf.show();
		/*if (this.textLabel!="") {
			this.textLabel.hide();
		}*/

	} else if (this.rightConstraint.length==0) {
		// undefined leaf
		var undefinedLeaf;
		if (this.spot!="" && this.spot.type=="circle") {
			undefinedLeaf= this.spot;
			undefinedLeaf.attr({"cx":this.x,"cy":this.y,"r":spotRadius});
		} else {
			undefinedLeaf= svg.circle(this.x ,this.y, spotRadius);
			var clickedIndex=clickedTreeNodes.length;
			clickedTreeNodes[clickedIndex]=this;
			undefinedLeaf.data("indexNode",clickedIndex);
			this.spot=undefinedLeaf;
			undefinedLeaf.mouseover(nodeMouseOver);
			undefinedLeaf.mouseout(nodeMouseOut);
			undefinedLeaf.click(nodeMouseClick);
			undefinedLeaf.attr({"stroke-width": lightLineWidth});
		}

		undefinedLeaf.attr({"stroke": lineColor,"stroke-dasharray": "."});


		undefinedLeaf.attr({"fill": undefColor});



		undefinedLeaf.show();
		if (this.textLabel!="" && this.rightConstraint.length>0) {
			this.textLabel.hide();
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
		if (this.label=="speciation") {
			res=res+"S";
		} else if (this.label=="duplication") {
			res=res+"D";
		} else {
			res=res+"T";
		}
		res=res+":"+this.length;
		/*if (this.noDuplication==1 || this.noTransfert==1) {
			res=res+":";
		}
		if (this.noDuplication==1 && this.noTransfert==1) {
			res=res+"3.0";
		} else if (this.noDuplication==1) {
			res=res+"1.0";
		} else if (this.noTransfert==1) {
			res=res+"2.0";
		} 		*/
	} else {
		if (this.type==3) {
			res=res+"LOSS";
			var speciesList= new Array();
			this.speciesNode.getSpecies(speciesList);
			count = speciesList.length;
			var i=0;
			for (i = 0; i < count; i++) {
				res=res+"|";
				res=res+speciesList[i];
			}
		} else {
			//alert(this.label);
			res=res+dico[this.rightConstraint[0]];
		}
		res=res+":"+this.length;
		/*if (this.noDuplication==1 || this.noTransfert==1) {
			res=res+":";
		}
		if (this.noDuplication==1 && this.noTransfert==1) {
			res=res+"3.0";
		} else if (this.noDuplication==1) {
			res=res+"1.0";
		} else if (this.noTransfert==1) {
			res=res+"2.0";
		} */
	}

	if (this.leftConstraint.length>0 || this.rightConstraint.length>0 || this.label=="transfert") {
		res=res + "[";
		if (this.leftConstraint.length>0) {
			if (this.leftConstraint[0].indexOf("Not ")==0) {
				res=res + "<L>Not "+ dico[this.leftConstraint[0].substring(4,this.leftConstraint[0].length)];
			} else {

				res=res + "<L>"+ dico[this.leftConstraint[0]];
			}
			for (var i=1;i<this.leftConstraint.length;i++) {
				if (this.leftConstraint[i].indexOf("Not ")==0) {
					res=res + ";Not "+ dico[this.leftConstraint[i].substring(4,this.leftConstraint[i].length)];
				} else {

					res=res + ";"+ dico[this.leftConstraint[i]];
				}
			}
			res=res + "</L>";
		}
		if (this.rightConstraint.length>0) {
			if (this.rightConstraint[0].indexOf("Not ")==0) {
				res=res + "<R>Not "+ dico[this.rightConstraint[0].substring(4,this.rightConstraint[0].length)];
			} else {

				res=res + "<R>"+ dico[this.rightConstraint[0]];
			}
			for (var i=1;i<this.rightConstraint.length;i++) {
				if (this.rightConstraint[i].indexOf("Not ")==0) {
					res=res + ";Not "+ dico[this.rightConstraint[i].substring(4,this.rightConstraint[i].length)];
				} else {

					res=res + ";"+ dico[this.rightConstraint[i]];
				}
			}
			res=res + "</R>";
		}
		if (this.label=="transfert") {
			res=res + "<T>" + this.transfer + "</T>"
		}



		res=res + "]";

	}

	return res;
}

Node.prototype.printTree = fprintTree;
Node.prototype.maxTaxaString = fmaxTaxaString;
Node.prototype.maxDepth = fmaxDepth;
Node.prototype.nbLeaves = fnbLeaves;
Node.prototype.deleteSubparts = fdeleteSubparts;
Node.prototype.initCoordinates=finitCoordinates;
Node.prototype.drawTree=fdrawTree;
Node.prototype.getNewick=fgetNewick;

function lineMouseOver(evt) {
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	if (clickedTreeNode.hline!="")
    	clickedTreeNode.hline.attr({"stroke-width": (lineWidth*2)});
	if (clickedTreeNode.round!="")
    	clickedTreeNode.round.attr({"stroke-width": (lineWidth*2)});
	if (clickedTreeNode.branchConstraint!="")
    	clickedTreeNode.branchConstraint.attr({"stroke-width": (lightLineWidth)});

    if (clickedTreeNode.leftTextLabel!="") {
    	clickedTreeNode.leftTextLabel.attr({"font-size": smallFontSize+4});
    }
}

function lineMouseOut(evt) {
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	if (clickedTreeNode!=selectedNode || rightSelection==1) {
		if (clickedTreeNode.hline!="") {
			clickedTreeNode.hline.attr({"stroke-width": lineWidth});
		}
		if (clickedTreeNode.round!="") {
			clickedTreeNode.round.attr({"stroke-width": lineWidth});
		}
		if (clickedTreeNode.branchConstraint!="") {
			clickedTreeNode.branchConstraint.attr({"stroke-width": 0});
		}
	}
		if (clickedTreeNode.leftTextLabel!="") {
			clickedTreeNode.leftTextLabel.attr({"font-size": smallFontSize});
		}

}

// Functions related to animations
function lineMouseClick(evt) {
    var target = evt.target;
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	if (clickedTreeNode.hline!="") {
		clickedTreeNode.hline.attr({"stroke-width": lineWidth});
	}
	if (clickedTreeNode.round!="") {
		clickedTreeNode.round.attr({"stroke-width": lineWidth});
	}
	if (clickedTreeNode.leftTextLabel!="") {
		clickedTreeNode.leftTextLabel.attr({"font-size": smallFontSize});
	}

	if (tool=="noSpeciations") {
		clickedTreeNode.branchConstraint.attr({"fill": dupColor});
		clickedTreeNode.length=4.0;
	} else if (tool=="noConstraints") {
		clickedTreeNode.branchConstraint.attr({"fill": constraintNoneColor});
		clickedTreeNode.length=-1.0;
	} else if (tool=="noDuplications") {
		clickedTreeNode.branchConstraint.attr({"fill": specColor});
		clickedTreeNode.length=1.0;
	} else if (tool=="noDuplicationsOrTransfers") {
		clickedTreeNode.branchConstraint.attr({"fill": specColor});
		clickedTreeNode.length=3.0;
	} else if (tool=="taxa") {
		changeVisibilite2("poptaxa",1);
		if (selectedNode!="" && selectedNode.hline!="")
			selectedNode.hline.attr({"stroke-width": (lineWidth)});
		if (selectedNode!="" && selectedNode.round!="")
			selectedNode.round.attr({"stroke-width": (lineWidth)});
		if (selectedNode!="" && selectedNode.branchConstraint!="")
			selectedNode.branchConstraint.attr({"stroke-width": 0});


		if (selectedNode!="" && selectedNode.spot!="") {
			if (selectedNode.rightConstraint.length==0) {
				selectedNode.spot.attr({"stroke-width": (lightLineWidth)});

			} else {

				selectedNode.spot.attr({"stroke-width": (lineWidth)});
			}
		}
		if (selectedNode!="" && selectedNode.textLabel!="") {
			selectedNode.textLabel.attr({"font-size": fontSize});
		}
		selectedNode=clickedTreeNode;

		if (selectedNode!="" && selectedNode.hline!="")
			selectedNode.hline.attr({"stroke-width": (lineWidth*2)});
		if (selectedNode!="" && selectedNode.round!="")
			selectedNode.round.attr({"stroke-width": (lineWidth*2)});
		if (selectedNode!="" && selectedNode.branchConstraint!="")
			selectedNode.branchConstraint.attr({"stroke-width": (lightLineWidth)});


		rightSelection=0;
		fillConstraintBoard(selectedNode.leftConstraint);
		targetPath.show();

	}
	refreshAll();
	//alert(clickedTreeNode.nbLeaves());
}

function nodeMouseOver(evt) {
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	if (clickedTreeNode!=undefined && clickedTreeNode.spot!="")
    	clickedTreeNode.spot.attr({"stroke-width": (lineWidth*2)});
    if (clickedTreeNode!=undefined && clickedTreeNode.textLabel!="") {
    	clickedTreeNode.textLabel.attr({"font-size": fontSize+4});
    }
}

function nodeMouseOut(evt) {
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	if (clickedTreeNode!=selectedNode || rightSelection==0) {
		if (clickedTreeNode!=undefined && clickedTreeNode.spot!="") {
			clickedTreeNode.spot.attr({"stroke-width": lightLineWidth});
		}
	}
		if (clickedTreeNode!=undefined && clickedTreeNode.textLabel!="") {
			clickedTreeNode.textLabel.attr({"font-size": fontSize});
		}

}
// Functions related to animations
function nodeMouseClick(evt) {
    var target = evt.target;
	var clickedTreeNode= clickedTreeNodes[this.data("indexNode")];
	if (clickedTreeNode!=undefined && clickedTreeNode.spot!="") {
	    clickedTreeNode.spot.attr({"stroke-width": lightLineWidth});
	}
	if (clickedTreeNode.textLabel!="") {
    	clickedTreeNode.textLabel.attr({"font-size": fontSize});
    }

	if (tool=="speciation") {
		clickedTreeNode.label="speciation";

		var count = clickedTreeNode.sons.length;
		if (count==0) {
			clickedTreeNode.sons[0]= new Node(";");
			clickedTreeNode.sons[0].father=clickedTreeNode;
			clickedTreeNode.sons[1]= new Node(";");
			clickedTreeNode.sons[1].father=clickedTreeNode;
		}
	} else if (tool=="duplication") {
		clickedTreeNode.label="duplication";

		var count = clickedTreeNode.sons.length;
		if (count==0) {
			clickedTreeNode.sons[0]= new Node(";");
			clickedTreeNode.sons[0].father=clickedTreeNode;
			clickedTreeNode.sons[1]= new Node(";");
			clickedTreeNode.sons[1].father=clickedTreeNode;
		}

	} else if (tool=="transfert") {
		if (clickedTreeNode.label=="transfert") {
			if (clickedTreeNode.transfer==0) {
				clickedTreeNode.transfer=1;
			} else if (clickedTreeNode.transfer==1) {
				clickedTreeNode.transfer=2;				
			} else {
				clickedTreeNode.transfer=0;
			}
			//alert("change");
			
		} else {
			clickedTreeNode.label="transfert";
	
			var count = clickedTreeNode.sons.length;
			if (count==0) {
				clickedTreeNode.sons[0]= new Node(";");
				clickedTreeNode.sons[0].father=clickedTreeNode;
				clickedTreeNode.sons[1]= new Node(";");
				clickedTreeNode.sons[1].father=clickedTreeNode;
			}
			
		}

	} else if (tool=="delete") {
		clickedTreeNode.label="";
		clickedTreeNode.rightConstraint= new Array();
		clickedTreeNode.leftConstraint= new Array();
		clickedTreeNode.deleteSubparts();
		clickedTreeNode.sons= new Array();
	} else if (tool=="noSpeciations") {
		clickedTreeNode.branchConstraint.attr({"fill": dupColor});
		clickedTreeNode.length=4.0;
	} else if (tool=="noConstraints") {
		clickedTreeNode.branchConstraint.attr({"fill": constraintNoneColor});
		clickedTreeNode.length=-1.0;
	} else if (tool=="noDuplications") {
		clickedTreeNode.branchConstraint.attr({"fill": specColor});
		clickedTreeNode.length=1.0;
	} else if (tool=="taxa") {
		changeVisibilite2("poptaxa",1);


		if (selectedNode!="" && selectedNode.hline!="")
			selectedNode.hline.attr({"stroke-width": (lineWidth)});
		if (selectedNode!="" && selectedNode.round!="")
			selectedNode.round.attr({"stroke-width": (lineWidth)});
		if (selectedNode!="" && selectedNode.branchConstraint!="")
			selectedNode.branchConstraint.attr({"stroke-width": 0});

		if (selectedNode!="" && selectedNode.spot!="") {
			if (selectedNode.rightConstraint.length==0) {
				selectedNode.spot.attr({"stroke-width": (lightLineWidth)});

			} else {

				selectedNode.spot.attr({"stroke-width": (lineWidth)});
			}
		}
		if (selectedNode!="" && selectedNode.textLabel!="") {
			selectedNode.textLabel.attr({"font-size": fontSize});
		}
		selectedNode=clickedTreeNode;


		if (selectedNode!="" && selectedNode.spot!="")
			selectedNode.spot.attr({"stroke-width": (lineWidth*2)});

		rightSelection=1;
		fillConstraintBoard(selectedNode.rightConstraint);
		targetPath.show();



	}
	refreshAll();
	//tree.printTree(0);
	//alert(clickedTreeNode.nbLeaves());
}

function fillConstraintBoard(constraint) {
	var theSel= document.getElementById("speciesselector");
	while (theSel.length>0) {
		theSel.options[theSel.length - 1] = null;
	}
	var i=0;
	for (i = 0; i < constraint.length; i++) {
		theSel.options[theSel.length] = new Option(constraint[i], constraint[i]);
	}


}

function removeAllTaxon() {
	var theSel= document.getElementById("speciesselector");
	while (theSel.length>0) {
		theSel.options[theSel.length - 1] = null;
	}
	if (rightSelection==1) {
		selectedNode.rightConstraint= new Array();
		selectedNode.textLabel.remove();
		selectedNode.textLabel="";
	} else {
		selectedNode.leftConstraint= new Array();
		selectedNode.leftTextLabel.remove();
		selectedNode.leftTextLabel="";
	}
	refreshAll();

}

function addTaxon(tag,not) {
	//selectedNode.label=tag;
	if (rightSelection==1) {
		if (not==1) {
			selectedNode.rightConstraint[selectedNode.rightConstraint.length]="Not " + tag;
		} else {
			selectedNode.rightConstraint[selectedNode.rightConstraint.length]=tag;
		}
	} else {
		if (not==1) {
			selectedNode.leftConstraint[selectedNode.leftConstraint.length]="Not " + tag;
		} else {
			selectedNode.leftConstraint[selectedNode.leftConstraint.length]=tag;
		}
	}
	var theSel= document.getElementById("speciesselector");
	if (not==1) {
		theSel.options[theSel.length] = new Option("Not " + tag,"Not " + tag);
	} else {
		theSel.options[theSel.length] = new Option(tag,tag);
	}
	refreshAll();
}

function refreshTaxaList(e,tag) {
	if (e.keyCode == 13) {
		alert("Enter");
	} else {
		var xhr_object = null;
		var position = "poplist";
		   if(window.XMLHttpRequest)  xhr_object = new XMLHttpRequest();
		  else
		    if (window.ActiveXObject)  xhr_object = new ActiveXObject("Microsoft.XMLHTTP");

		// On ouvre la requete vers la page désirée
		xhr_object.open("GET", "taxalist.php?tag=" + tag + "&databank=" + database, true);
		xhr_object.onreadystatechange = function(){
		if ( xhr_object.readyState == 4 )
		{
			// j'affiche dans la DIV spécifiées le contenu retourné par le fichier
			document.getElementById(position).innerHTML = xhr_object.responseText;
		}
		}

		// dans le cas du get
		xhr_object.send(null);
	}
}

function step1() {
	var position = "popresults";



	var xhr_object = null;
	   if(window.XMLHttpRequest)  xhr_object = new XMLHttpRequest();
	  else
	    if (window.ActiveXObject)  xhr_object = new ActiveXObject("Microsoft.XMLHTTP");

	// On ouvre la requete vers la page désirée
	xhr_object.open("GET", "wait.php", true);
	xhr_object.onreadystatechange = function(){
	if ( xhr_object.readyState == 4 )
	{
		document.getElementById(position).innerHTML = xhr_object.responseText;
	}
	}

	// dans le cas du get
	xhr_object.send(null);

	changeVisibilite2("popresults",1);
	return 1;
}

function step2() {
	var position = "popresults";
	window.open("resultsList.php?database=" + database + "&pattern=" + tree.getNewick() + ";");
	changeVisibilite2("popresults",0);
	return 1;
}

function displayResults() {
	var nomat=step1();
	nomat=step2();

}

function eraseTarget() {
	targetPath.hide();
	if (selectedNode!="" && selectedNode.hline!="")
		selectedNode.hline.attr({"stroke-width": (lineWidth)});
	if (selectedNode!="" && selectedNode.round!="")
		selectedNode.round.attr({"stroke-width": (lineWidth)});
	if (selectedNode!="" && selectedNode.branchConstraint!="")
		selectedNode.branchConstraint.attr({"stroke-width": 0});

	if (selectedNode!="" && selectedNode.spot!="") {
		if (selectedNode.rightConstraint.length==0) {
			selectedNode.spot.attr({"stroke-width": (lightLineWidth)});

		} else {

			selectedNode.spot.attr({"stroke-width": (lineWidth)});
		}
	}
	if (selectedNode!="" && selectedNode.textLabel!="") {
		selectedNode.textLabel.attr({"font-size": fontSize});
	}
	selectedNode="";

}

</script>

