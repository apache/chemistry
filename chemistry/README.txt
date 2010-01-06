Apache Chemistry
================

Building
--------

Using maven, build with:

  mvn install

Note that the chemistry-ws bindings fetch a lot of WS-related maven artifacts,
so if you don't need them and want to speed up the build you can comment it
out from the root pom.xml.


Testing
-------

A small in-memory demo AtomPub server can then be launched with:

  java -jar chemistry-tests/target/chemistry-tests-0.5-SNAPSHOT-jar-with-dependencies.jar
