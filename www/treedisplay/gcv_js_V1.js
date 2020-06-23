var svg1,width,height ;
var widthSeq=30;
var tooltips;
var nbNeighbors=0;
var familleRefArray;
//largeur entre les genes
var widthGene=25;
var heigthGene=1;//redimensionnement des genes

var lien="http://phylogeny.southgreen.fr/patterngeco/resultsDisplay.php?databank=viridiplantae_27_V2.1&targetgene="; //lien d'indexation de la BD nécessaire pour le double clic et le changement de gène de reference

//distance entre l'élement du haut et le debut de la représentation du contexte
var distanceHaut=10;


geneP = [ { "x": 0,   "y": 0},  { "x": 15,  "y": 0},
          { "x": 20,  "y": 7}, { "x": 15,  "y": 12},
          { "x": 0,  "y": 12},{ "x": 0,  "y": 0}];
geneN = [ { "x": 0,"y": 7},  { "x": 5,  "y": 0},
          { "x": 20,  "y": 0}, { "x": 20,  "y": 12},
          { "x": 5,  "y": 12},{ "x": 0,  "y": 7}];


function gcv(divs,widths,height,stroke="#C0C0C0",fill="none"){
  div = divs;
  width = widths;
  height = height;
// suppression de tous les element du div avant l'affichage
  var divPart = document.getElementById("myidGCV");
  while (divPart.firstChild){
   divPart.removeChild(divPart.firstChild);
  }
  svg1 = d3.select("#myidGCV")
        .append("svg")
        .attr("width",width)
        .attr("height",height);
        


 tooltips = d3.select("body")
    .append("div")
    .style('class', 'tooltip')
    .style("position", "absolute")
    .style("z-index",1)
    .style("visibility", "hidden")
    .style("background-color","black")
    .style("opacity",0.7)
    .style("text-align", "center")
    .style("padding","4px")
    .style("font-weight","normal")
    .style("color","white");

  
}

  gcv.prototype.getDiv = function() {
    return this.div;
  };
  gcv.prototype.getWidth = function() {
  return this.width;
};
  gcv.prototype.getHeight = function() {

  return this.height;
};
  //Setters
  gcv.prototype.setDiv = function(div) {
   this.div = div;
};
  gcv.prototype.setWidth = function(width) {
   this.width = width;
};
  gcv.prototype.setHeight = function(height) {
   this.height = height;
};

//Fonction qui trace un rectangle pour marquer la famille de la referrence
function drawRef(nbvoisin,y2=100){
  svg1.append("rect").attr("fill-opacity",0.1).attr("fill","green")
    .attr("stroke","green").attr("stroke-width", 1).attr("rx",4)
    .attr("x",nbvoisin*widthGene-((widthGene+1)/10))
    .attr("y",parseInt(distanceHaut)-6)
    .attr("width",widthGene)
    .attr("height",y2+6);
}

lineFunction = d3.line()
  .x(function(d) { return d.x; })
  .y(function(d) { return d.y; });

var scale=1;

//Fonction de zoom
 gcv.prototype.zoom = function(){
  var s = d3.selectAll("svg");
  scale=scale+0.05;
  newScale="scale("+scale+")";
  s.attr("transform", newScale);
 };
 
 //Fonction de translate de svg
 gcv.prototype.translate = function(){
  var s = d3.select("svg");
  s.attr("transform", "translate(120,120");
 };
 
 //Fonction pour dezoomer
 gcv.prototype.dezoomer = function(){
  var s = d3.selectAll("svg");
  scale=scale-0.05;
  newScale="scale("+scale+")";
  s.attr("transform", newScale);
 };
 
 //Fonction pour representer un gene en fonction de sa direction(strand) et du strand du gene de la meme famille que le gene de reference("2, 2")
function drawGene(container,gene,numAffichage,voisin){
  container.append("path")
      .attr("gene_name",gene.gene_name)
      .attr("numAffichageIntreeGreat",numAffichage) 
      .attr("opacity",1).attr("stroke-width", 1).attr("stroke", function(){ if(gene.hasOwnProperty('Paralogue') && numAffichage!=nbNeighbors){return "white";}
                                                                                  else{return "black";}})// entoure de lanc si le gène a la propriété paralogue
      .attr("d", function(){
                         if(gene.strandRef=="1"){return (gene.strand=="1"?lineFunction(geneP):lineFunction(geneN));}
                         else{return (gene.strand=="1"?lineFunction(geneN):lineFunction(geneP));}}) //Represente le gène en fonction de son sens direct ou indirect
      .attr("transform",function(){
          if(gene.strandRef=="1"){
               if(numAffichage==0){return "translate("+((parseInt(gene.gene_position)-parseInt(gene.positionRef)+parseInt(voisin)+parseInt(1))*widthGene)+","+(parseFloat(distanceHaut))+") scale("+heigthGene+")";}
          else{return "translate("+((parseInt(gene.gene_position)-parseInt(gene.positionRef)+parseInt(voisin)+parseInt(1))*widthGene)+","+(parseFloat(distanceHaut)+(parseFloat(numAffichage))*widthSeq)+") scale("+heigthGene+")";}
          }
          else{if(numAffichage==0){
           return "translate("+((-1*(parseInt(gene.gene_position))+parseInt(gene.positionRef)+parseInt(voisin)+parseInt(1))*widthGene)+","+(parseFloat(distanceHaut))+") scale("+heigthGene+")";}
          else{
           return "translate("+((-1*(parseInt(gene.gene_position))+parseInt(gene.positionRef)+parseInt(voisin)+parseInt(1))*widthGene)+","+(parseFloat(distanceHaut)+(parseFloat(numAffichage))*widthSeq)+") scale("+heigthGene+")";}
          }
           })//représente le gène en fonction du sens du gène de la meme famille que la reference si ce gène est dans les sens indirect(- ou -1) l'ordre est inversé
     
      
      .on("mouseover", function() { d3.selectAll('g').attr("opacity",0.3); return  tooltips.style("visibility", "visible").text(this.getAttribute("gene_name"));}) //Affiche le nom du gène au survol
      .on("mousemove", function() {
          return tooltips.style("top", (event.pageY - 30) + "px")
          .style("left", event.pageX + "px").style("stroke-width", 2);
        })
      .on("mouseout", function() {d3.selectAll('g').attr("opacity",1);
          return tooltips.style("visibility", "hidden");
        })
      .on("dblclick",function(){
       //tooltips.style("visibility", "hidden");
       //si le gène double cliquer est un gène de la meme famille que la reference nous ne rechargeons pas la page car l'arbre reste le meme dans le cas contraire la page est rechargée
         if(familleRefArray.indexOf(this.getAttribute("gene_name"))!=-1){
            
            recuperation(this.getAttribute("gene_name"),nbNeighbors);
            
         }
        else{
           var geneName = this.getAttribute("gene_name");
           var boolParenthese = geneName.lastIndexOf("(");
           if (boolParenthese!=-1){
             geneName=geneName.split("(")[1].split(")")[0];
           }
           tooltips.style("visibility", "hidden");
       
           lien=lien+geneName;
          
           window.location.assign(lien);
        //window.location.assign("http://dev.phylogeny.southgreen.fr/treepattern/resultsDisplay.php?databank=BFF&targetgene=Traes_1BL_46A3D7BAD.2");
       }
        
      });
}



//Fonction de creation d'un contenaire pour les genes de meme famille
function containerFamille(i){

  var container = svg1.append("g")
    .attr("opacity",1).attr("stroke-width", 1).attr("stroke", "black").attr("fill",function(){ return d3.interpolateSinebow((i/(nbNeigbors*2+1)));})
    .on("mouseover", function() {   this.setAttribute("opacity",1); this.setAttribute("stroke-width", 1);})
    .on("mouseout", function() {  this.setAttribute("opacity",1); this.setAttribute("stroke-width", 1);});
  return container;
}



 //Fonction qui permet de representer le contexte génomique
 gcv.prototype.drawAll = function(genes,voisinRef,voisin, familleRef="",width_Gene=25,heigth_Gene=1){
   
   
   widthGene = width_Gene;
   nbNeigbors = voisin;
   heigthGene = heigth_Gene;
   familleRefArray = Object.keys(genes);
   distanceHaut = margin-6; //distance avec "margin (variable qui provient de intreegreat)" nécessaire pour le zoom en hauteur
   var familleRefArrayIntreegreat = familleRef; //Liste de Gène de la même famille que le gène de referrence
  
   widthSeq=(height-2*margin)/(familleRef.length-1);
 
   //Marquage de la famille de la reference par le rectangle en vert
   drawRef((parseInt(nbNeigbors)+parseInt(1)),(familleRef.length+parseInt(1))*widthSeq);
  
   var ligneAffichage=0;var t=[];
   //Representation des familles de chaque voisin de la reférence
   for (var i=0;i<voisinRef.length;i++){
     container =containerFamille(i);
     for (var j=0; j<familleRefArray.length;j++){
       for (var y = 0; y<familleRefArrayIntreegreat.length;y++){
          if(familleRefArray[j].indexOf(familleRefArrayIntreegreat[y])!=-1){
             ligneAffichage=y;
          }
       }
      if (ligneAffichage==0){
        t.push(familleRefArray[j]);
      }
      
      for (var k = 0; k<gene[familleRefArray[j]].length;k++){
         if(gene[familleRefArray[j]][k].hasOwnProperty("familyRef") && gene[familleRefArray[j]][k].familyRef==voisinRef[i].gene_name){
              drawGene(container,gene[familleRefArray[j]][k],ligneAffichage,voisin);
         }
      }
    }       
  }
  
  // creation du contenaire unique pour les genes qui n'ont pas de famille avec le gene de reference ainsi que ses voisins
   var containerAutreGene = svg1.append("g").attr("fill","blue")
    .attr("fill-opacity",0.07).attr("stroke-width", 0.8).attr("stroke-opacity", 0.7).attr("stroke","black" );
    
    //Représentation des gène sans famille avec la refenrence et ses voisins
   for (var l=0; l<familleRefArray.length;l++){
        for (var h = 0; h<familleRefArrayIntreegreat.length;h++){if(familleRefArray[l].indexOf(familleRefArrayIntreegreat[h])!=-1){ligneAffichage=h;} }

        for (var m = 0; m<gene[familleRefArray[l]].length;m++){
           if(gene[familleRefArray[l]][m].hasOwnProperty("familyRef") ==false){
       
              drawGene(containerAutreGene,gene[familleRefArray[l]][m],ligneAffichage,nbNeigbors);
           }
         
      }
   }       
  
   //Affichage des noms des especes
   var containerTexte = svg1.append("g").attr("fill","black")
     .attr("stroke-width", 0.4).attr("stroke-opacity", 0.5).attr("stroke","black" ).style("font-size", "13px");

  for(var n=0;n<familleRefArray.length;n++){
    for (var d = 0; d<familleRefArrayIntreegreat.length;d++){if(familleRefArray[n].indexOf(familleRefArrayIntreegreat[d])!=-1){ligneAffichage=d;} }
    var geneSpecies= genes[familleRefArray[n]][0];
   
    containerTexte.append("text")
                  .attr("x",  (widthGene*(parseInt(nbNeigbors)*2+2.5)))
                  .attr("y", (ligneAffichage*widthSeq+parseInt(distanceHaut+10)))
                  .text( (geneSpecies.species_name.substr(0,geneSpecies.species_name.length).toUpperCase())+" - "+geneSpecies.chromosome_name);
                        
  }
 
}

