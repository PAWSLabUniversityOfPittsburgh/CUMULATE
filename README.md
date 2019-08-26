# CUMULATE


## Compile Instructions

Cumulate uses Apache ANT to compile and package the files. You'll need to specify the `catalina_home` variable in the `build.xml` to point to the tomcat jar files. 

```
  <property name="catalina_home" value="/usr/local/tomcat"/>
```

Once this is done you can run.

```
ant compile
```

and 

```
ant dist
```

Note: you may also need to revise the syntax if on windows to use semicolons, not colons:

```
	<target name="compile" depends="init" description="compile the source" >
		<!-- Compile the java code from ${src} into ${build} -->
		<javac srcdir="${src}" destdir="${build}" debug="on"
			classpath="${catalina_home}/lib/servlet-api.jar:
				${catalina_home}/lib/jsp-api.jar:
				${paws-core-jar}:
				${web}/WEB-INF/lib/sedona-report.jar"
		/>
	</target>
```


## Docker

Docker can be used to build the project. To accomplish this run the following:

```
docker-compose build
```

```
docker-compose run tomcat ant compile
```

```
docker-compose run tomcat ant dist
```