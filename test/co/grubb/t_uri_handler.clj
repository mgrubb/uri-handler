(ns co.grubb.t-uri-handler
  (:require [midje.sweet :refer :all]
            [co.grubb.uri-handler :refer :all]))

(facts "about `handle-uri`"
  (fact "default search finds correct handler"
    (handle-uri "default:example") => :default)

  (fact "dot searches schemes at end of namespace"
    (handle-uri "." "test:example") => :test
    (handle-uri "." "default:example") => :default)

  (fact "suffix search finds correct handler"
    (handle-uri ".test.handlers" "test:example") => :test)

  (fact "namespace literal handles all URIs"
    (handle-uri "co.grubb.test.multi-handler" "test1:example") => :test1
    (handle-uri "co.grubb.test.multi-handler" "test2:example") => :test2)

  (fact "unknown schemes are falsey"
    (handle-uri "test3:example") => falsey))
