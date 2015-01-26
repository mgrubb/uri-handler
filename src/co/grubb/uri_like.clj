(ns co.grubb.uri-like
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

(defprotocol URINamespaceMatcher
  (uri-ns-match [x uri]))

(extend-protocol URINamespaceMatcher
  java.lang.String
  (uri-ns-match [s uri]
    (letfn [(escdot [s] (clojure.string/replace s "." "\\."))]
    (let [scheme (-> uri scheme escdot)
          re (condp re-matches s
               #"\." (->> scheme (format ".*\\.%s") re-pattern)
               #"\..+\." (->> s escdot (format ".*%2$s%1$s\\..*" scheme) re-pattern)
               #"\..+" (->> s escdot (format ".*%2$s\\.%1$s" scheme) re-pattern)
               #".+\." (->> s escdot (format "%2$s%1$s" scheme) re-pattern)
               (-> s escdot re-pattern))]
      (fn [ns]
        (string? (re-matches re ns))))))

  clojure.lang.IPersistentMap
  (uri-ns-match [m uri]
    (let [v (-> uri scheme keyword m)]
      (uri-ns-match v uri)))

  clojure.lang.Fn
  (uri-ns-match [f uri]
    (partial f uri))

  nil
  (uri-ns-match [_ uri]
    (uri-ns-match ".uri-handler" uri)))
