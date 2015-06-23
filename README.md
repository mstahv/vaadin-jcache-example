# A simple  JCache example

Small Vaadin application demonstrating the usage of JCache API.

Just check out or download this project and run the example in e.g. TomEE using following command from the project directory: 

    mvn package tomee:run

The demo application will be available at http://localhost:8080/vaadin-jcache-example-1.0-SNAPSHOT/

In UI you can e.g. try to search for "foo" first and notice the latency, then on a second try the cache will be hit and the search is fast.

The code contains both plain Java SE style usage (in VaadinUI class) and a managed bean + annotation example (in PhoneBookService, which is the one enabled by default).

