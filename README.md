# sarif-java
SARIF java library to read and write SARIF

We will support different SARIF versions.

## About structure
Here some information how we will support different sarif versions inside this repository

```
github.com/de-jcup/sarif-java
     /sarif-2.1.0-generator
               build.gradle	     
	    /gen
                      /sarif-210 (generated gradle project)
                       Build.grade
                      /src/main/java
                      /src/test/java
                /src/main/resources
                        sarif.json
	 /sarif-3.0
	    build.gradle
	    /gen
```

### Versioning

#### libary name
${sarif_version}-${ourMajor}.${ourMinor}	
#### Package names
The package names do contain the sarif version inside so easy to differentiate
    
For example:
```	
	de.jcup.sarif.sarif210.*
	de.jcup.sarif.sarif300.*
```
		
### Usage
The projects will use the library as a normal maven/gradle dependency:

E.g. 
`groupId: de.jcup.sarif artifactId: sarif-java version:2.1.0-1.0 type:jar`

## Build
- We have no generated sourced checked into the repository!
- At the build time, we start the generated, execute tests
- At release time we use github actions workflow which does execute the genertion of the dedicated sub project
  (e.g. "sarif-2.1.0" via ./gradlew :sarif-2.1.0:generate") and deploy the build jar to maven central.

## Deployment
- Deployment to maven central (for each sub module)
