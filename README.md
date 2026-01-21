[![install with bioconda](https://img.shields.io/badge/install%20with-bioconda-brightgreen.svg?style=flat)](https://anaconda.org/bioconda/rapgreen)
[<img alt="docker_rapgreen" src="https://img.shields.io/badge/container-Docker-blue">](https://quay.io/repository/biocontainers/rapgreen)
[<img alt="singularity_rapgreen" src="https://img.shields.io/badge/container-Singularity-orange">](https://quay.io/repository/biocontainers/rapgreen)
[![Anaconda-Server Badge](https://anaconda.org/bioconda/rapgreen/badges/license.svg)](https://anaconda.org/bioconda/rapgreen)
[![Anaconda-Server Badge](https://anaconda.org/bioconda/rapgreen/badges/downloads.svg)](https://anaconda.org/bioconda/rapgreen)

RapGreen
=========================================

Manipulate and annotate phylogenetic trees  

---------------------------

## Table of Contents

   * [Installation](#installation)
       * [Using Docker](#using-docker)
       * [Using Singularity](#using-singularity)
       * [Using Bioconda](#using-bioconda)
   * [Documentation](#documentation)
   * [How to cite?](#how-to-cite)

## Installation

### Using Docker
   <details>
      <summary>See details</summary>
      
First you must have [Docker](https://docs.docker.com/get-docker/) installed and running.  
Secondly have look at the availabe rapgreen biocontainers at [quay.io](https://quay.io/repository/biocontainers/rapgreen?tab=tags).

Then:
  ```
# get the chosen rapgreen container version
docker pull quay.io/biocontainers/rapgreen:1.0--hdfd78af_0
# use an rapgreen
docker run quay.io/biocontainers/rapgreen:1.0--hdfd78af_0 rapgreen --help
  ```
   </details>
 
### Using Singularity
   <details>
      <summary>See details</summary>
      
First you must have [Singularity](https://sylabs.io/guides/3.5/user-guide/quick_start.html) installed and running.  
Secondly have look at the availabe rapgreen biocontainers at [quay.io](https://quay.io/repository/biocontainers/rapgreen?tab=tags).

Then:
```
# get the chosen rapgreen container version
singularity pull docker://quay.io/biocontainers/rapgreen:1.0--hdfd78af_0
# run the container
singularity run rapgreen:1.0--hdfd78af_0
```

You are now in the container. You can use an RapGreen.
  </details>

### Using Bioconda
   <details>
      <summary>See details</summary>
      
#### Install rapgreen

  ```
  conda install -c bioconda rapgreen
  ```

#### Update rapgreen

  ```
  conda update rapgreen
  ```

#### Uninstall rapgreen
  ```
  conda uninstall rapgreen  
  ```

   </details>

## Documentation

For a full documentation, please visit the RapGreen wiki:
https://github.com/SouthGreenPlatform/rap-green/wiki

You'll find there the command lines documentation of the two main entry points of the RapGreen Java package, and tutorials to install the main webservices : the tree pattern matching, and the tree viewer.

If you plan to:

* Use RapGreen tree reconciler, in order to annote duplication and losses on a phylogenetic tree, please follow [this tutorial](https://github.com/SouthGreenPlatform/rap-green/wiki/How-to-use-RapGreen-to-reconcile-phylogenetic-trees). You could also get at look at the [statistics](https://github.com/SouthGreenPlatform/rap-green/wiki/About-gene-pair-statistics) given by RapGreen for each pair of genes of the reconciled phylogenetic tree.

* Install the tree pattern matching service on your own data, please follow [this tutorial](https://github.com/SouthGreenPlatform/rap-green/wiki/How-to-install-a-phylogenetic-tree-pattern-matching-service).

* Install the tree display service InTreeGreat [this tutorial](https://github.com/SouthGreenPlatform/rap-green/wiki/How-to-install-the-tree-visualizator-InTreeGreat).

The general API Java documentation of the RapGreen package is available [here](http://southgreenplatform.github.io/rap-green/javadoc/index.html).

RapGreen is used by several systems and platforms. Some examples are available [here](https://github.com/SouthGreenPlatform/rap-green/wiki/Examples-of-installed-services).

# How to cite

RapGreen, an interactive software and web package to explore and analyze phylogenetic trees Jean-François Dufayard, Stéphanie Bocs, Valentin Guignon, Delphine Larivière, Alexandra Louis, Nicolas Oubda, Mathieu Rouard, Manuel Ruiz, Frédéric de Lamotte , NAR Genomics and Bioinformatics 3 (3), lqab088, https://doi.org/10.1093/nargab/lqab088
_Jean-François Dufayard, Laurent Duret, Simon Penel, Manolo Gouy, François Rechenmann, Guy Perrière_
Bioinformatics, Volume 21, Issue 11, , Pages 2596–2603, https://doi.org/10.1093/bioinformatics/bti325
