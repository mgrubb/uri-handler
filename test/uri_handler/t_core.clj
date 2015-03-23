(ns uri-handler.t-core
  (:require [midje.sweet :refer :all]
            [uri-handler.core :refer :all]))

(facts "about `uri-handlers`"
  (fact "uri-handlers returns handler registry"
    (uri-handlers) => (contains {:global (contains {:test1 (contains (contains {:scheme "test1"}))})}))

  (fact "uri-handlers will filter by scope"
    (uri-handlers :test (contains {:test1 (contains (contains {:scheme "test1"}))})))

  (fact "uri-handlers will filter by scope and scheme"
    (uri-handlers :global :test2) => (contains (contains {:scheme "test2"}))))

(facts "about `handle-uri`"
  (fact "Uses the global handler for the `test1` scheme"
    (handle-uri "test1:example") => [:test1 "example"])

  (fact "Uses the proper handler for the `test2` scheme"
    (handle-uri "test2://example.com") => [:test2 "example.com"])

  (fact "Uses scoped handler when given a scope for `test`"
    (handle-uri "test1:scope-example" :test) => [:test1-scoped "scope-example"])

  (fact "unknown schemes are falsey"
    (handle-uri "test3:example") => falsey))
