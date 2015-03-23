(ns uri-handler.test.test-handler
  (:require [uri-handler.uri :as uri]))

(defn handler1
  [uri]
  ((juxt uri/schemek uri/scheme-part) uri))

(defn handler2
  [uri]
  ((juxt uri/schemek uri/host) uri))
