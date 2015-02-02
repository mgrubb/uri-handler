;; ## Public Interface
(ns co.grubb.uri-handler
  (:require [clojure.tools.namespace.find :as nsf]
            [clojure.java.classpath :as cp]
            [clojure.string :as str]
            [co.grubb.uri :as uri]
            [co.grubb.uri-namespace :as urins])
  (:import [java.net URI]))

;; ### URI Handler Resolution
;; A URI handler is found by searching the classpath for namespaces 
;; that match a pattern formed from the scheme part of the URI.
;;
;; The default search pattern used, when no pattern is specified is
;; `#".*\.uri-handler\.SCHEME`.  Where `SCHEME` is the scheme part of
;; the URI.  For example, a URI such as _http://example.com_, the classpath
;; will be searched for a namespace that ends with _.uri-handler.http_.
;; Where a URI such as _conf:///example.conf_, a namespace ending with _.uri-handler.conf_
;; will be searched for.
;;
;; If this handler resolution does not suit your specific use case, you can specifiy
;; a different search resolution in several different ways.
;;
;; #### Predicate specification
;; A predicate may be specified as either a:
;;
;; * A string that specifies a namespace pattern:
;;   * `"."` matches any namespace ending in *.SCHEME*
;;   * `".scheme-prefix"` matches any namespace ending in **.scheme-prefix.**_SCHEME_
;;   * `"scheme-prefix."` matches the namespace named **scheme-prefix.**_SCHEME_
;;   * `".scheme-prefix."` matches any namespace that contains **.scheme-prefix.**_SCHEME_**.**
;;   * `"any.namespace.name" `looks for the namespace named **any.namespace.name**
;; * A function which takes two arguments the URI and a namespace, returns a truthy value
;;   the namespace should handle the URI.
;;   * _The namespace is passed as a string, and it has not been loaded yet, so function introspection won't work._
;; * A map whose keys are the keywordized version of the scheme, and values which can
;;   be a string or function as described above.  The map may be nested as well, though very deep nesting will
;;   certainly cause overflows.

;; #### Handler functions
;; A handler function takes a single argument, the URI to handle, there is no contract
;; for the return value of a handler function. URI handler functions should conform
;; to at least one of the following standards in order to be found by the search routines.
;;
;; 1. Have the `:uri-handler` meta-data key set:
;;   1. The value of the key may be the keywordized version of the scheme
;;   2. or a simple truthy value
;; 2. Be named `uri-handler`
;;
;; Functions are prefered by specificity and definition order.

(defn- resolve-handler-fn
  "Look for URI handler function in the given namespace for the given scheme.
  Namespace should already have been loaded."
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
  "Search for a namespace and function to handle the given `uri`.
  Uses co.grubb.uri-namespace/uri-ns-match to actually handle the predicate"
  ([p uri]
   (let [scheme (-> uri uri/scheme keyword)
         require-ns (fn [sym] (require sym) sym)
         loaded-ns (->> (all-ns) (map ns-name))
         found-ns (->> (cp/classpath) nsf/find-namespaces)]
     (when-let [handle-ns (->> (mapcat vec [found-ns loaded-ns])
                               distinct
                               (map name)
                               (filter (urins/uri-ns-match p uri))
                               first)]
       (-> handle-ns
           symbol
           require-ns
           (resolve-handler-fn scheme))))))


(defn handle-uri
   "Search for and execute, if found, a handler function for `uri`.

   The one argument form uses the default search routine.
   The two argument form takes a predicate specification and a URI."
  ([uri]
   (handle-uri nil uri))
  ([pred uri]
   (when-let [handler (find-uri-handler pred uri)]
     (handler uri))))
