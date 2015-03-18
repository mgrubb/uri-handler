(ns co.grubb.test.uri-handler.test-handler
  (:require [co.grubb.uri :as uri]))

(defn handler1
  [uri]
  ((juxt uri/schemek uri/scheme-part) uri))

(defn handler2
  [uri]
  ((juxt uri/schemek uri/host) uri))
