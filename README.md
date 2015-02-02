# co.grubb.uri-handler

A Clojure library to find uri handlers in the classpath.

## Releases and Dependency Information

* Latest stable release is 0.1.1

[Leiningen](http://leiningen.org/) dependency information:

```clojure
[co.grubb/uri-handler "0.1.0"]
```

[Maven](http://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>co.grubb</groupId>
  <artifactId>uri-handler</artifactId>
  <version>0.1.0</version>
</dependency>
```
## Overview

This library doesn't actually provide URI handlers, instead it provides
a framework for finding URI handlers that are developed with this library
in mind.  While the public API for handling handling a URI is one function,
`handle-uri`, it does provide some URI utilities as well.

The underlying implementation relies on java.net.URI, however you can give the library
a URI in the form of a string, map, or java.net.URI object.  If you don't like those
options you can also extend the `co.grubb.URILike` protocol.

### Namespace Searching

The library uses the following as the default search algorthim for finding a
function to handle a given URI.

1. Get the scheme part of the URI
2. Iterate through all the namespaces on the classpath.
3. Take the first namespace that matches
```clojure
;; SCHEME is the scheme part from step 1.
#"^.*\.uri-handler\.SCHEME$"
```
4. Require the first matching namespace

Once a namespace has been found, it is searched for a handler function.
Handler functions may be found in the following ways, ordered by preference.

1. Functions that have the `:uri-handler` meta-data key set:
    1. The value of the key may be the keywordized version of the scheme
```clojure
(defn ^{:uri-handler :http} my-http-handler
 [uri]
 ...)
```

    2. Or a simple truthy value
```clojure
(defn ^:uri-handler my-uri-handler
 [uri]
 ...)
```

2. A function named `uri-handler`
```clojure
(defn uri-handler
 [uri]
 ...)
```
The namespace searching may be customized as discussed below.

## Usage

If the above search routine works for your use case, handling a URI is as simple
as calling `handle-uri` with a URI argument.

```clojure
;; Uses default search method
(handle-uri "http://example.com")
(handle-uri "conf:///example/config")
(handle-uri (java.net.URI. "conf:///example/config"))
(handle-uri {:scheme "conf" :path "/example/config"})
```

Otherwise `handle-uri` can take second argument to customize the search.
```clojure
(handle-uri p "http://example.com")
(handle-uri p "conf:///example/config")
```

Where `p` can be:
* A string that specifies a different pattern to use for searching (e.g.):
    * `"."` matches any namespace ending in *.SCHEME*
    * `".scheme-prefix"` matches any namespace ending in **.scheme-prefix.**_SCHEME_
    * `"scheme-prefix."` matches the namespace named **scheme-prefix.**_SCHEME_
    * `".scheme-prefix."` matches any namespace that contains **.scheme-prefix.**_SCHEME_**.**
    * `"any.namespace.name"` looks for the namespace named **any.namespace.name**
* A function which takes two arguments the uri and a namespace and returns a truthy value if the namespace should handle the uri.
    * _The namespace is passed as a string, and it has not been loaded yet, so function introspection won't work._
```clojure
(defn uri-ns-pred
 [uri ns]
 (.startsWith ns (scheme uri)))
```

* A map whose keys are the keywordized version of the scheme, and values which can be a string or a function as described above.
  The map may be nested as well, though very deep nesting will certainly cause overflows.

```clojure
{:http "some.specific.namespace"                ;; Looks only in some.specific.namespace for handler functions
 :conf ".conf.uri-handler"                       ;; Looks for any namespace ending in .conf.uri-handler.conf for handler functions
 :abc (fn [uri ns] (.contains ns (scheme uri)))} ;; Looks for namespaces that contain "abc"
```
**-or-**
```clojure
{:abc {:abc (fn [uri ns] (.contains ns (scheme uri)))}} ;; Nested maps (same as map above)
```

## License

Copyright © 2015 Michael Grubb.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
