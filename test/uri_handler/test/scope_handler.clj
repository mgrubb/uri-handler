(ns uri-handler.test.scope-handler
  (:require [uri-handler.uri :as uri]))

(defn handler1
  [uri]
  (let [scheme (-> uri
                   uri/scheme
                   (str "-scoped")
                   keyword)]
    [scheme (uri/scheme-part uri)]))
