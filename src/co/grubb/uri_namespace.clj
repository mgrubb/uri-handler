;; ## Predicate Execution
(ns co.grubb.uri-namespace
  (:require [co.grubb.uri :as uri]))

;; The `URINamespacematcher/uri-ns-match` function returns a function that
;; takes a namespace, as a string, and returns a truthy value if the namespace
;; matches the predicate.

(defprotocol URINamespaceMatcher
  "The URINamespaceMatcher protocol defines the interface for executing the 
  URI to namespace searching predicates."
  (uri-ns-match [x uri] "Takes a predicate specification and a URI and returns a function suitable for `filter`"))

;; Default predicate implementations are provided for string, map, and function predicates.
;; The default search pattern is also specified by extending the protocol to nil.

(extend-protocol URINamespaceMatcher
  java.lang.String
  (uri-ns-match [s uri]
    (letfn [(escdot [s] (clojure.string/replace s "." "\\."))
            (mkpat [pat scheme]
              (fn [s] (->> s escdot (format pat scheme) re-pattern)))]
    (let [scheme (-> uri uri/scheme escdot)
          re (condp re-matches s
               #"\."      :>> (mkpat ".*\\.%s" scheme)
               #"\..+\."  :>> (mkpat ".*%2$s%1$s\\..*" scheme)
               #"\..+"    :>> (mkpat ".*%2$s\\.%1$s" scheme)
               #".+\."    :>> (mkpat "%2$s%1$s" scheme)
               (-> s escdot re-pattern))]
      (fn [ns]
        (string? (re-matches re ns))))))

  clojure.lang.IPersistentMap
  (uri-ns-match [m uri]
    (let [v (-> uri uri/scheme keyword m)]
      (uri-ns-match v uri)))

  clojure.lang.Fn
  (uri-ns-match [f uri]
    (partial f uri))

  nil
  (uri-ns-match [_ uri]
    (uri-ns-match ".uri-handler" uri)))
