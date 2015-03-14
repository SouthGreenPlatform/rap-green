#Description of command line arguments

# NAME: #

> - RAP-Green v1.0 -

# SYNOPSIS: #

> java -jar RapGreen.jar command args

# OPTIONS: #

**-g** gene\_tree\_file
> The input gene tree file

**-invert**
> Activate this option if your taxa identifier is in front of the sequence identifier

**-start** starting\_index
> The starting index (0 default), if the gene tree input is a directory

**-end** ending\_index
> The ending exclusive index (directory size default), if the gene tree input is a directory

**-s** species\_tree\_file
> The input species tree file

**-og** gene\_tree\_file
> The output tree file (annotated with duplications)

**-rerooted** gene\_tree\_file
> The simple unannotated rerooted gene tree file

**-phyloxml** gene\_tree\_phyloxml\_file
> The output tree file (annotated with duplications) in phyloXML format

**-os** species\_tree\_file
> The output species tree file (limited to gene tree species)

**-or** reconciled\_tree\_file
> The output reconciled tree file (consensus tree, with reductions and losses)

**-stats** gene\_tree\_file
> The output scoring statistic file

**-outparalogous**
> Add outparalogous informations in stats file.

**-gt** gene\_threshold
> The support threshold for gene tree branch collapse (optional, default 80.0)

**-st** species\_threshold
> The length threshold for species tree branch collapse (optional, default 10.0)

**-pt** polymorphism\_threshold
> The length depth threshold to deduce to polymorphism, allelism ... (optional, default 0.05)