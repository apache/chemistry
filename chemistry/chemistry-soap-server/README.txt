This contains the code for SOAP server bindings to the Chemistry SPI.

In order to have it work correctly, the endpoints have to be registered
with the SOAP runtime. This is usually done through a WEB-INF/sun-jaxws.xml
file placed in a WAR. An example such file is available in the examples/
directory.

In this file, the url-pattern will have to be configured depending on your
application server.

Note also the <handler-class> directive, which is necessary to have
authentication be propagated from SOAP to the underlying Chemistry repository.
