<script type="text/javascript">


<?php if (isSet($_POST['expression'])) { ?>

// Parsing expression data
var brut_expression="<?php 
echo str_replace("\r\n","&",$_POST['expression']); 
?>";
//alert(brut_expression);
var rows= brut_expression.split("&");
var titlesExpress= new Array();
var expvalues= new Array();
var splited= rows[0].split("\t");
for (var i = 2; i < splited.length; i++) {
	titlesExpress[i-2]= splited[i];	
}
var sumExpress=0.0;
var nbExpress=0.0;
var sumNegExpress=0.0;
var nbNegExpress=0.0;
//alert("loc:" + titlesExpress.length);
for (var i = 1; i < rows.length; i++) {
	splited= rows[i].split("\t");
	//if (splited[1]!="UN") {
		expvalues[splited[0]]= new Array();
		for (var j = 2; j < splited.length; j++) {
			if (splited[j]!="NA") {
				//if (i==1) alert(splited.length);
				var local=parseFloat(splited[j]);
				/*if (maxExpress<local) {
					maxExpress=local;
				}*/
				//alert(splited[j] + "*" + local + "*");
				if (local<0) {
					sumNegExpress+=local*(-1);
					nbNegExpress++;
				} else {
					sumExpress+=local;
					nbExpress++;
				}
				
				expvalues[splited[0]][j-2]= local;
			} else {
				expvalues[splited[0]][j-2]= "NA";
			
			}
		}
	//}
}

<?php } ?>

<?php if (isSet($_POST['ideven'])) { ?>

// Parsing Ideven data, specific to GenFam project
var ideven= new Array();
var idevenGraphs= new Array();
var idevenNodes= new Array();
var idevenBranches= new Array();
var idevenSelected=null;
var idevenNoColor="#CCCCCC";
var idevenYesColor="#666666";
var brut_ideven="<?php 
echo str_replace("\r\n","&",$_POST['ideven']); 
?>";
rows= brut_ideven.split("&");
for (var i = 1; i < rows.length; i++) {
	splited= rows[i].split("\t");
	var tax1=splited[0]/*.substring(0,splited[0].lastIndexOf("_"))*/;
	var tax2=splited[1]/*.substring(0,splited[1].lastIndexOf("_"))*/;
	
	
	if (ideven[tax1]==null) {
		ideven[tax1]= new Array();
	}
	ideven[tax1][ideven[tax1].length]=tax2;
	ideven[tax1][ideven[tax1].length]=splited[2];
	ideven[tax1][ideven[tax1].length]=splited[3];
	ideven[tax1][ideven[tax1].length]=splited[4];
	ideven[tax1][ideven[tax1].length]=splited[5];
	
	
	if (ideven[tax2]==null) {
		ideven[tax2]= new Array();
	}
	ideven[tax2][ideven[tax2].length]=tax1;
	ideven[tax2][ideven[tax2].length]=splited[2];
	ideven[tax2][ideven[tax2].length]=splited[3];	
	ideven[tax2][ideven[tax2].length]=splited[4];	
	ideven[tax2][ideven[tax2].length]=splited[5];	
	
		
	//alert(tax1 + " " + tax2 + " " + splited[2]);
}



// ************************
// Create the Ideven column
function createIdeven(localTree,ideven) {

	// Counting the number of sons
	var count = localTree.sons.length;
	var loc=0;
	if (count>0) {
	
		// It's a node
		var i = 0;
		for (i = 0; i < count; i++) {
			if (localTree.collapsed=="") {
				createIdeven(localTree.sons[i],ideven);
			} else {
				createIdeven(localTree.sons[i],ideven);
				
			}
		}
	} else {	

		// It's a leaf

		//this.text.setAttributeNS(null, "fill",colorparam);

			var nbtags= localTree.tags.length;

			localTree.tags[nbtags]= document.createElementNS('http://www.w3.org/2000/svg', 'polygon');


			localTree.tags[nbtags].setAttribute('points', (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (localTree.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (localTree.y - (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth) + tagWidth - 1) + "," + (localTree.y + (parseInt(fontSize))/2) + " " + (width + annotX - margin - annotMargin + (nbtags*tagWidth)) + "," + (localTree.y + (parseInt(fontSize))/2));
			//this.tags[nbtags].setAttribute('stroke', lineColor);

			localTree.tags[nbtags].setAttribute("stroke-width", 0);
			localTree.tags[nbtags].setAttribute("x", (width + annotX  - margin - annotMargin + tagWidth));
			localTree.tags[nbtags].setAttribute("y", (localTree.y + (parseInt(fontSize))/2));
			

			
			var colorValue=backColor;
			//alert(localTree.taxon.substring(0,localTree.taxon.lastIndexOf("_")));
			if (ideven[localTree.taxon/*.substring(0,localTree.taxon.lastIndexOf("_"))*/]!=null) {
				colorValue=idevenYesColor;			
				localTree.tags[nbtags].addEventListener("mouseover", idevenMouseOver, false);
				localTree.tags[nbtags].addEventListener("mouseout", idevenMouseOut, false);
				localTree.tags[nbtags].addEventListener("click", idevenMouseClick, false);
				localTree.tags[nbtags].setAttribute("legend-pop",localTree.taxon);
				idevenNodes[idevenNodes.length]=localTree;
				localTree.tags[nbtags].setAttribute("leaf-att",idevenNodes.length-1);
			
			}
			
			localTree.tags[nbtags].setAttribute('fill', colorValue);
			idevenGraphs[idevenGraphs.length]=localTree.tags[nbtags];
			svg.appendChild(localTree.tags[nbtags]);
			//alert(localTree.taxon);
	}
}


function idevenMouseOver(evt) {
    var target = evt.target;
    if (target!=idevenSelected) {	
		target.setAttribute("stroke-width", 2);
		target.setAttribute("stroke", "red");
	}
	if (idevenSelected!=null && target!=idevenSelected) {
		var localTable= ideven[idevenSelected.getAttribute("legend-pop")];
		var i=0;
		var relation="No relationships";
		var score="N/A";
		var score2="N/A";
		var score3="N/A";
		
		/*var tracet="";
		for (i=0;i<localTable.length;i++) {
			//if (target.getAttribute("legend-pop")=="") {
			tracet=tracet+ " " + localTable[i];
			
		}		
		alert(tracet);*/
		
		
		for (i=0;i<localTable.length/* && relation=="No relationships"*/;i++) {
			if (localTable[i]==target.getAttribute("legend-pop") && relation=="No relationships") {
				relation=localTable[i+1];
				score=localTable[i+2];
				score2=localTable[i+3];
				score3=localTable[i+4];
			} else if (localTable[i]==target.getAttribute("legend-pop")) {
				relation=relation+";"+localTable[i+1];
				score=score+";"+localTable[i+2];	
				score2=score2+";"+localTable[i+3];	
				score3=score3+";"+localTable[i+4];			
			}
		}
		
		//alert(relation+" "+score);
		
		/*tagPopup = document.createElementNS("http://www.w3.org/2000/svg", "text");

		tagPopup.setAttribute("x", target.getAttribute("x"));
		tagPopup.setAttribute("y", target.getAttribute("y")-2);
		tagPopup.setAttribute("font-family", fontFamily);
		tagPopup.setAttribute("font-size", fontSize);
		tagPopup.setAttribute("fill", fontColor);
	
		tagPopup.appendChild(document.createTextNode(target.getAttribute("legend-pop")));
		svg.appendChild(tagPopup);	*/
		
		
		var xhr_object = null;
		var position = "popannot";
		   if(window.XMLHttpRequest)  xhr_object = new XMLHttpRequest();
		  else
			if (window.ActiveXObject)  xhr_object = new ActiveXObject("Microsoft.XMLHTTP");

		// On ouvre la requete vers la page désirée
		xhr_object.open("GET", "popupgenfam.php?tag1=" + target.getAttribute("legend-pop") + "&tag2=" + idevenSelected.getAttribute("legend-pop") + "&relation=" + relation + "&score=" + score+ "&score2=" + score2+ "&score3=" + score3, true);
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
		
			
		// highlight branchs
		var jogger= idevenNodes[target.getAttribute("leaf-att")];
		var lca= idevenNodes[idevenSelected.getAttribute("leaf-att")];
		//alert(jogger.taxon + " " +  target.getAttribute("leaf-att").taxon + " " + target.getAttribute("legend-pop"));
		var trace= new Array();
		while (jogger!=null) {
		//alert(jogger.length);
			trace[jogger.length + "*" + jogger.support+"*"+jogger.y]="1";
			jogger=jogger.father;
		}
		while (trace[lca.length + "*" + lca.support+"*"+lca.y]==null) {
			//alert(lca.length);
			lca=lca.father;
		}
		idevenBranches= new Array();
		//lca.line.setAttribute("stroke-width",10);
		var leaf1= idevenNodes[target.getAttribute("leaf-att")];
		var leaf2= idevenNodes[idevenSelected.getAttribute("leaf-att")];
		var ante=null;
		while (leaf1!=lca) {
			idevenBranches[idevenBranches.length]=leaf1;
			leaf1.line.setAttribute("stroke-width",(leaf1.line.getAttribute("stroke-width")*2));
			leaf1.round.setAttribute("stroke-width",(leaf1.round.getAttribute("stroke-width")*2));
			if (leaf1.sons[0]==ante) {
				if (leaf1.left1!=null)
				leaf1.left1.setAttribute("stroke-width",(leaf1.left1.getAttribute("stroke-width")*2));		
			} else {
				if (leaf1.left2!=null)
				leaf1.left2.setAttribute("stroke-width",(leaf1.left2.getAttribute("stroke-width")*2));		
			}
			ante=leaf1;
			leaf1=leaf1.father;
		}
		ante=null;
		while (leaf2!=lca) {
			idevenBranches[idevenBranches.length]=leaf2;
			leaf2.line.setAttribute("stroke-width",(leaf2.line.getAttribute("stroke-width")*2));	
			leaf2.round.setAttribute("stroke-width",(leaf2.round.getAttribute("stroke-width")*2));		
			if (leaf2.sons[0]==ante) {
				if (leaf2.left1!=null)
				leaf2.left1.setAttribute("stroke-width",(leaf2.left1.getAttribute("stroke-width")*2));		
			} else {
				if (leaf2.left2!=null)
				leaf2.left2.setAttribute("stroke-width",(leaf2.left2.getAttribute("stroke-width")*2));		
			}
			ante=leaf2;
			leaf2=leaf2.father;
		}
		idevenBranches[idevenBranches.length]=lca;
		if (lca.left2!=null)
		lca.left2.setAttribute("stroke-width",(lca.left2.getAttribute("stroke-width")*2));	
		if (lca.left1!=null)
		lca.left1.setAttribute("stroke-width",(lca.left1.getAttribute("stroke-width")*2));	
	}
	
	//target.setAttribute('fill', "red");
}

function idevenMouseOut(evt) {
    var target = evt.target;
    if (target!=idevenSelected) {
		target.setAttribute("stroke-width", 0);
	}
	//svg.removeChild(tagPopup);
	changeVisibilite2("popannot",0);	
	
	var i=0;
	for (i=0;i<idevenBranches.length;i++) {
		idevenBranches[i].line.setAttribute("stroke-width",lineWidth);
		idevenBranches[i].round.setAttribute("stroke-width",lineWidth);
		if (idevenBranches[i].left1!=null) {
			idevenBranches[i].left1.setAttribute("stroke-width",lineWidth);
		}
		if (idevenBranches[i].left2!=null) {
			idevenBranches[i].left2.setAttribute("stroke-width",lineWidth);
		}
	}	

}

function idevenMouseClick(evt) {
    var target = evt.target;
    if (idevenSelected!=null) {
    	idevenSelected.setAttribute("stroke-width", 0);
    }
	target.setAttribute("stroke-width", 2);
	
	idevenSelected=target;
	
	
	//Change color of related cells
	var checkin= new Array();
	var localTable= ideven[idevenSelected.getAttribute("legend-pop")];
	var i=0;
	for (i=0;i<localTable.length;i+=3) {
		checkin[localTable[i]]="1";
	}	
	for (i=0;i<idevenGraphs.length;i++) {
		if (idevenGraphs[i].getAttribute("fill")!=backColor) {
			if (checkin[idevenGraphs[i].getAttribute("legend-pop")]==null && idevenGraphs[i]!=idevenSelected) {
				idevenGraphs[i].setAttribute('fill', idevenNoColor);
			
			} else {
				idevenGraphs[i].setAttribute('fill', idevenYesColor);
			
			}
		
		}
	}	
	 

}







<?php } ?>
<?php if (isSet($_POST['annotations'])) { ?>
var brut_annotations="<?php 
echo str_replace("\r\n","&",$_POST['annotations']); 
?>";
var annotations_genfam= new Array();
rows= brut_annotations.split("&");
var annotEntete= rows[0].split("\t");

for (var i = 1; i < rows.length; i++) {
	splited= rows[i].split("\t");
	for (var j = 0; j < annotEntete.length; j++) {
		var taxon=splited[0];
		if (annotations_genfam[taxon]==null) {
			annotations_genfam[taxon]= new Array();
		}
		annotations_genfam[taxon][annotEntete[j]]=splited[j];
		/*var taxon=splited[0];
		var genecode=splited[4];
		var functional=splited[5];
		var reviewed=splited[6];
		var score=splited[7];
		if (annotations_genfam[taxon]==null) {
			annotations_genfam[taxon]= new Array();
		}
		annotations_genfam[taxon]["code"]=genecode;
		annotations_genfam[taxon]["function"]=functional;
		annotations_genfam[taxon]["reviewed"]=reviewed;
		annotations_genfam[taxon]["score"]=score;*/
		
	}
}

//alert(2);
function genfamGeneOver(evt) {
    var target = evt.target;
	var clickedTreeNode= clickedTreeNodes[target.getAttribute("indexNode")];
	
	if (annotations_genfam[clickedTreeNode.taxon]!=null) {

	var xhr_object = null;
	var position = "popannot";
	   if(window.XMLHttpRequest)  xhr_object = new XMLHttpRequest();
	  else
		if (window.ActiveXObject)  xhr_object = new ActiveXObject("Microsoft.XMLHTTP");

	// On ouvre la requete vers la page désirée
	var addr= "popupgenfam.php?gene=" + clickedTreeNode.taxon;
	for (var j = 0; j < annotEntete.length; j++) {
		addr=addr + "&a" + (j) + "=" + annotEntete[j] + "§" + annotations_genfam[clickedTreeNode.taxon][annotEntete[j]];	
	}
	//alert(addr);
	xhr_object.open("GET", addr, true);	
	
	//xhr_object.open("GET", "popupgenfam.php?gene=" + clickedTreeNode.taxon + "&code=" + annotations_genfam[clickedTreeNode.taxon]["code"] + "&function=" + annotations_genfam[clickedTreeNode.taxon]["function"] + "&reviewed=" + annotations_genfam[clickedTreeNode.taxon]["reviewed"] + "&score=" + annotations_genfam[clickedTreeNode.taxon]["score"], true);
	xhr_object.onreadystatechange = function(){
	if ( xhr_object.readyState == 4 )
	{
		// j'affiche dans la DIV spécifiées le contenu retourné par le fichier
		document.getElementById(position).innerHTML = xhr_object.responseText;
	}
	}

	// dans le cas du get
	xhr_object.send(null);

	changeVisibilite2("popannot",1);	
	}	
	
}

function matchAnnotation(localTaxon,matchingString) {
	if (annotations_genfam[localTaxon]!=null) {
		if (annotations_genfam[localTaxon]["code"].toUpperCase().indexOf(matchingString.toUpperCase())!=-1 || annotations_genfam[localTaxon]["function"].toUpperCase().indexOf(matchingString.toUpperCase())!=-1 || annotations_genfam[localTaxon]["score"].toUpperCase().indexOf(matchingString.toUpperCase())!=-1 || annotations_genfam[localTaxon]["reviewed"].toUpperCase().indexOf(matchingString.toUpperCase())!=-1) {
			return 1; 
		} else {
			return 0;
		}
	} else {
		return 0;
	}
}

function genfamGeneOut(evt) {
	changeVisibilite2("popannot",0);	
}

function linkGeneFamEvent(localTree) {

	// Counting the number of sons
	var count = localTree.sons.length;
	var loc=0;
	if (count>0) {
	
		// It's a node
		var i = 0;
		for (i = 0; i < count; i++) {
			if (localTree.collapsed=="") {
				linkGeneFamEvent(localTree.sons[i]);
			} else {
				linkGeneFamEvent(localTree.sons[i]);
				
			}
		}
	} else {
		
		localTree.text.addEventListener("mouseover", genfamGeneOver , false);
		localTree.text.addEventListener("mouseout", genfamGeneOut , false);
	}
}

<?php } ?>
</script>

