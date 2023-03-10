# sarif-java
SARIF java library to read and write SARIF

We will support different SARIF versions.

## About structure
Here some information how we will support different SARIF versions inside this repository

```
github.com/de-jcup/sarif-java
     /sarif-2.1.0-generator
               build.gradle	     
	    /gen
                      /sarif-2.1.0 (generated gradle project)
                       build.grade
                      /src/main/java
                      /src/test/java
                /src/main/resources
                        sarif.json
	 /sarif-3.0-generator
	    build.gradle
	    /gen
```

### Versioning

#### Libary version
We will have the library version containing the v version and the our semantic version contained inside:

`${sarif_version}-${ourMajor}.${ourMinor}`

So as an example: For sarif 2.1.0 in the first release of our library we will have library name: `2.1.0-1.0`.
If there are minor changes for this library necessary it will be `2.1.0-1.1`.
If there are major (breaking) changes for the next library, it will be `2.1.0-2.0`. 

#### Package names
The package names do contain the SARIF version inside so easy to differentiate
    
For example:
```	
	de.jcup.sarif_2_1_0.*
	de.jcup.sarif_3_0_0.*
```
		
### Usage
The projects will use the library as a normal maven/gradle dependency:

E.g. 
`groupId: de.jcup.sarif artifactId: sarif version:2.1.0-1.0 type:jar`

## Build
- We have no generated source checked into the repository!
- To build the complete library we have a `full-build.sh` script. This will generate sources, a custom gradle build file and build the 
  library parts afterwards.
- At release time we use github actions workflow which uses the build script and will upload to maven central by using the relase version tag.

## Deployment
- Deployment to maven central (for each sub module)
