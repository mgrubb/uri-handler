(ns co.grubb.uri-handler
  (:require [clojure.tools.namespace.find :as nsf]
            [clojure.java.classpath :as cp]
            [clojure.string :as str]
            [co.grubb.uri :as uri]
            [co.grubb.uri-namespace :as urins])
  (:import [java.net URI]))

; Default Search -
;  Search classpath for namespaces that end with uri-handler.<uri scheme>
;
; Custom Search -
;   Given a uri and a search predicate find namespaces that match the predicate
;     If predicate is a string, search for namespaces that
;        match <string>.<uri scheme>
;     If predicate is a function, search for namespaces that
;       match (predicate uri ns) where predicate returns true if the
;       ns should handle the uri
;     If predicate is a map, the uri scheme is converted to a keyword and used
;       as the key to the map, the value of the key is treated as above

(defn- resolve-handler-fn
  [namesp scheme]
  (let [nsmap (ns-publics namesp)
        handlers (filter (comp :uri-handler meta val) nsmap)
        specs (filter (comp #(= scheme %) :uri-handler meta val) handlers)]
    (if (empty? handlers)
      (nsmap 'uri-handler)
      (let [fns (if-not (empty? specs) specs handlers)]
        (->> fns
             (sort-by (comp :line meta val))
             first
             val)))))

(defn- find-uri-handler
  ([uri] (find-uri-handler nil uri))
  ([p uri]
   (let [scheme (-> uri uri/scheme keyword)
         require-ns (fn [sym] (require sym) sym)
         loaded-ns (->> (all-ns) (map ns-name) vec)]
     (when-let [handle-ns (->> (cp/classpath)
                               nsf/find-namespaces
                               vec
                               (apply conj loaded-ns)
                               distinct
                               (map name)
                               (filter (urins/uri-ns-match p uri))
                               first)]
       (-> handle-ns
           symbol
           require-ns
           (resolve-handler-fn scheme))))))

(defn handle-uri
  ([uri]
   (when-let [handler (find-uri-handler nil uri)]
     (handler uri)))
  ([pred uri]
   (when-let [handler (find-uri-handler pred uri)]
     (handler uri))))
