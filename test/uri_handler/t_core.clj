(ns uri-handler.t-core
  (:require [midje.sweet :refer :all]
            [uri-handler.core :refer :all]))

(fact "test hash"
  {:a [{:scheme "a" :namespace "n"}]} => (contains {:a (contains (contains {:scheme anything}))}))

(fact "uri-handlers returns handler registry"
  (uri-handlers) => (contains {:test1 (contains (contains {:scheme "test1"
                                                           :namespace 'uri-handler.test.test-handler
                                                           :handler 'handler1}))})
  (uri-handlers) => (contains {:test2 (contains (contains {:scheme "test2"
                                                           :namespace 'uri-handler.test.test-handler
                                                           :handler 'handler2}))}))
(facts "about `handle-uri`"
  (fact "Uses the proper handler for the `test1` scheme"
    (handle-uri "test1:example") => [:test1 "example"])

  (fact "Uses the proper handler for the `test2` scheme"
    (handle-uri "test2://example.com") => [:test2 "example.com"])

  (fact "unknown schemes are falsey"
    (handle-uri "test3:example") => falsey))
