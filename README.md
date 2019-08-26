# CUMULATE


## Compile Instructions

Cumulate uses Apache ANT to compile and package the files. You'll need to specify the `catalina_home` variable in the `build.xml` to point to the tomcat jar files. 

Once this is done you can run

```
ant compile
```

and 

```
ant dist
```