(ns co.grubb.uri-namespace
  (:require [co.grubb.uri :as uri]))

(defprotocol URINamespaceMatcher
  (uri-ns-match [x uri]))

(extend-protocol URINamespaceMatcher
  java.lang.String
  (uri-ns-match [s uri]
    (letfn [(escdot [s] (clojure.string/replace s "." "\\."))
            (mkpat [pat scheme] (fn [s] (->> s escdot (format pat scheme) re-pattern)))]
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
