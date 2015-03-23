;; ## URI manipulation and Utilities
(ns uri-handler.uri
  (:require [clojure.string :as str])
  (:import [java.net URI]))

(defn- split-user-info
  "Takes a raw user-info URI field and splits it to a map.
  The map contains the keys :username and :password. The username
  and password from the user-info may be separated with a `:` or `;`."
  [s]
  (when (seq s)
    (into {} (mapcat hash-map
                     [:username :password]
                     (str/split s #":|;" 2)))))
(defn map->URI
  "Takes a map, `m`, that contains URI fields and returns a `java.net.URI`
  object.  The map supports the keys: `:scheme`, `:user-info`, `:host`,
  `:port`, `:path`, `:query`, and `:fragment`."
  [m]
  (URI. (:scheme m)
        (:user-info m)
        (:host m)
        (or (:port m) -1)
        (:path m)
        (:query m)
        (:fragment m)))

(defprotocol URILike
  "A protocol for dealing with things that act like URIs."
  (uri [x] "Function to convert `x` to a `java.net.URI` object.")
  (scheme [uri] "Return the scheme part of the URI.")
  (host [uri] "Return the host part of the URI.")
  (port [uri] "Return the port part of the URI.")
  (path [uri] "Return the path part of the URI, sans the query and the fragment")
  (user-info [uri] "Return the unprocessed user information part of the URI.")
  (user-info-map [uri] "Return a map of `:username` and `:password` parsed from user-info string.
                       Username and Password are separated either by `:` or `;`.")
  (query [uri] "Return the query portion of the URI.")
  (fragment [uri] "Return the fragment portion of the URI.")
  (authority [uri] "Returns the URI's authority part (typically host and port for heirarchichal URIs.")
  (scheme-part [uri] "Returns the scheme specific portion (i.e. everything after the scheme) of the URI.")
  (absolute? [uri] "Returns true if the URI is an absolute URI")
  (opaque? [uri] "Returns true if the URI is opaque or false if it is hierarchical"))

(extend-protocol URILike
  java.net.URI
  (uri [u] u)
  (scheme [uri] (.getScheme uri))
  (host [uri] (.getHost uri))
  (port [uri] (.getPort uri))
  (path [uri] (.getPath uri))
  (user-info [uri] (.getUserInfo uri))
  (user-info-map [uri] (-> uri .getUserInfo split-user-info))
  (query [uri] (.getQuery uri))
  (fragment [uri] (.getFragment uri))
  (authority [uri] (.getAuthority uri))
  (scheme-part [uri] (.getSchemeSpecificPart uri))
  (absolute? [uri] (.isAbsolute uri))
  (opaque? [uri] (.isOpaque uri))

  clojure.lang.IPersistentMap
  (uri [m] (map->URI m))
  (scheme [uri] (:scheme uri))
  (host [uri] (:host uri))
  (port [uri] (:port uri))
  (path [uri] (:path uri))
  (user-info [uri] (:user-info uri))
  (user-info-map [uri] (-> uri user-info split-user-info))
  (query [uri] (:query uri))
  (fragment [uri] (:fragment uri))
  (authority [uri] (:authority uri))
  (scheme-part [uri] (:scheme-part uri))
  (absolute? [u] (absolute? (uri u)))
  (opaque? [u] (opaque? (uri u)))

  java.lang.String
  (uri [s] (URI. s))
  (scheme [uri] (scheme (URI. uri)))
  (host [uri] (host (URI. uri)))
  (port [uri] (port (URI. uri)))
  (path [uri] (path (URI. uri)))
  (user-info [uri] (user-info (URI. uri)))
  (user-info-map [uri] (user-info-map (URI. uri)))
  (query [uri] (query (URI. uri)))
  (fragment [uri] (fragment (URI. uri)))
  (authority [uri] (authority (URI. uri)))
  (scheme-part [uri] (scheme-part (URI. uri)))
  (absolute? [uri] (absolute? (URI. uri)))
  (opaque? [uri] (opaque? (URI. uri))))

(defn schemek
  "Returns the scheme part of a uri as a keyword"
  [uri] (keyword (scheme uri)))
