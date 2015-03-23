# co.grubb/uri-handler

A Clojure library to find uri handlers in the classpath.

## Releases and Dependency Information

* Latest stable release is 0.2.1

[Leiningen](http://leiningen.org/) dependency information:

```clojure
[co.grubb/uri-handler "0.2.1"]
```

[Maven](http://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>co.grubb</groupId>
  <artifactId>uri-handler</artifactId>
  <version>0.2.1</version>
</dependency>
```
## Overview

This library doesn't actually provide URI handlers, instead it provides
a framework for finding URI handlers that are developed with this library
in mind.  While the public API for handling a URI is one function,
`handle-uri`, it does provide some URI utilities as well.

The underlying implementation relies on java.net.URI, however you can give the library
a URI in the form of a string, map, or java.net.URI object.  If you don't like those
options you can also extend the `uri-handler.uri.URILike` protocol.

## Usage

```clojure
(ns example
 (:require [uri-handler.core :refer (handle-uri)]))
(handle-uri "http://example.com")
(handle-uri "conf:///example/config")
(handle-uri (java.net.URI. "conf:///example/config"))
(handle-uri {:scheme "conf" :path "/example/config"})
```

## Writing URI Handlers

URI handler providers register their provider function by adding a file named *uri_handler.edn*
at the top of the classpath. *(A good place is in the resources directory)*

The contents of *uri_handler.edn* is either a map or a list of maps that describe the URI handler functions.
The supported keys are as follows:

Key           | Value
------------- | --------------------------------------------------------
:scheme       | A string that contains the type of URI this handler will process, (e.g. "http")
:description  | A string that contains a description of the URI handler
:namespace    | A symbol that gives the namespace that the handler function is defined in.
:handler      | A symbol that names the function to call in :namespace

## License

Copyright © 2015 Michael Grubb.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
