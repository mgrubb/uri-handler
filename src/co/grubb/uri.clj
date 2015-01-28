(ns co.grubb.uri
  (:require [clojure.string :as str])
  (:import [java.net URI]))

(defn- split-user-info
  [s]
  (when-not (empty? s)
    (into {} (mapcat #(hash-map %1 %2)
                     [:username :password]
                     (str/split s #";" 2)))))

(defn map->URI
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
  (uri [x])
  (scheme [uri])
  (host [uri])
  (port [uri])
  (path [uri])
  (user-info [uri])
  (user-info-map [uri])
  (query [uri])
  (fragment [uri])
  (authority [uri]))

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
  (authority [uri] (authority (URI. uri))))
