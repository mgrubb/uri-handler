(ns co.grubb.t-uri
  (:require [midje.sweet :refer :all]
            [co.grubb.uri :as uri]
            [clojure.string :refer (join)]
            )
  (:import [java.net URI]))

(def scheme-part "http")
(def user-part ["user" "password"])
(def host-part "example.com")
(def port-part 80)
(def path-part "/this/path.html")
(def query-part "query=1")
(def frag-part "frag")

(def uri-str (str scheme-part
                  "://" (join ";" user-part) "@"
                  host-part ":" port-part path-part "?"
                  query-part "#" frag-part))

(def http-str uri-str)

(def http-map {:scheme scheme-part
               :user-info (join ";" user-part)
               :host host-part
               :port port-part
               :path path-part
               :query query-part
               :fragment frag-part})

(def http-uri (URI. uri-str))

(defn- try-part [f]
  [(f http-uri) (f http-str) (f http-map)])

(defn- expect-part [x]
  (vec (repeatedly 3 (constantly x))))

(facts "about `uri`"
  (fact "a string can be a uri"
    (uri/uri http-str) => http-uri)
  (fact "a map can be a uri"
    (uri/uri http-map) => http-uri))

(facts "about uri parts"
  (fact "scheme will return the scheme as string"
    (try-part #'uri/scheme) => (expect-part scheme-part))
  (fact "host will return the host part as string"
    (try-part #'uri/host) => (expect-part host-part))
  (fact "port will return the port part as a number"
    (try-part #'uri/port) => (expect-part port-part))
  (fact "user-info will return the user info as a string"
    (try-part #'uri/user-info) => (expect-part (join ";" user-part)))
  (fact "user-info-map will return the user info as a map"
    (try-part #'uri/user-info-map) => (expect-part {:username (user-part 0)
                                                    :password (user-part 1)}))
  (fact "path will return the path part as a string"
    (try-part #'uri/path) => (expect-part path-part))
  (fact "query will return the query part as a string"
    (try-part #'uri/query) => (expect-part query-part))
  (fact "fragment will return the fragment part as a string"
    (try-part #'uri/fragment) => (expect-part frag-part)))
