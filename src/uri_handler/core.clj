;; ## Public Interface
(ns uri-handler.core
  (:require [clojure.tools.namespace.find :as nsf]
            [clojure.java.classpath :as cp]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [clj-utils.seq :refer (seq*)]
            [uri-handler.uri :as uri])
  (:import [java.net URI]))

(def ^{:private true
       :doc "The map of schemes to handlers"}
  uri-handler-registry (atom {}))

(defn- validate-handler
  "Validate a handler entry found in uri_handler.edn file.
  This makes sure that the 3 required keys, :scheme, :handler,
  and :namespace are present."
  [h]
  (let [{:keys [scheme handler]} h
        {handle-ns :namespace} h]
    (and scheme handle-ns handler)))

(defn- register-handler
  "Reads the EDN from url and if it is a valid handler definition,
  then add it to the regisry"
  [url]
  (let [hs (-> url slurp edn/read-string seq*)]
    (doseq [{:keys [scheme] :as h} hs]
      (let [scheme (keyword scheme)
            scope (or (:scope h) :global)
            regs (get-in @uri-handler-registry [scope scheme])]
        (when (validate-handler h)
          (swap! uri-handler-registry
                 assoc-in
                 [scope scheme]
                 (conj regs (assoc h :url url))))))))

(defn- register-handlers
  "Locate URI handler definition files on the classpath,
  and call register-handler on them."
  []
  (swap! uri-handler-registry (constantly {}))
  (let [urls (enumeration-seq (.. Thread
                                   currentThread
                                   getContextClassLoader
                                   (getResources "uri_handler.edn")))]
    (doseq [url urls] (register-handler url))))

(defn- require-handler
  "Loads the handler namespace, and resolves the handler symbol"
  [{handler-ns :namespace handler :handler :as h}]
  (require handler-ns)
  (-> handler-ns
      find-ns
      (ns-resolve handler)))

(defn- find-uri-handlers
  "Looks for a handler for uri in the handler registry"
  [uri scope]
  (let [scheme (keyword (uri/scheme uri))
        handlers (get-in @uri-handler-registry [scope scheme])]
    (map require-handler handlers)))

(defn handle-uri
  "Search for and execute, if found, a handler function for `uri`."
  ([uri] (handle-uri uri :global))
  ([uri scope]
  (when-let [handlers (seq (find-uri-handlers uri scope))]
    ((first handlers) uri))))

(defn uri-handlers
  "Returns the registry of uri handlers, optionally limited by scope and scheme"
  ([] @uri-handler-registry)
  ([scope] (@uri-handler-registry scope))
  ([scope scheme] (get-in @uri-handler-registry [scope scheme])))

;; When loaded, go find the uri handlers and build the registry
(register-handlers)
