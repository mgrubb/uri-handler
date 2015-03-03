;; ## Public Interface
(ns co.grubb.uri-handler
  (:require [clojure.tools.namespace.find :as nsf]
            [clojure.java.classpath :as cp]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [clj-utils.seq :refer (seq*)]
            [co.grubb.uri :as uri]
            [co.grubb.uri-namespace :as urins])
  (:import [java.net URI]))

(def ^:private uri-handler-registry (atom {}))

(defn- validate-handler
  [h]
  (let [{:keys [scheme handler]} h
        {handle-ns :namespace} h]
    (and scheme handle-ns handler)))

(defn- register-handler
  [url]
  (let [hs (-> url slurp edn/read-string seq*)]
    (doseq [{:keys [scheme] :as h} hs]
      (let [scheme (keyword scheme)
            regs (get @uri-handler-registry scheme)]
        (when (validate-handler h)
          (swap! uri-handler-registry
                 assoc
                 scheme
                 (conj regs (assoc h :url url))))))))

(defn- register-handlers
  []
  (swap! uri-handler-registry (constantly {}))
  (let [urls (enumeration-seq (.. Thread
                                   currentThread
                                   getContextClassLoader
                                   (getResources "uri_handler.edn")))]
    (doseq [url urls] (register-handler url))))

(defn- require-handler
  [{handler-ns :namespace handler :handler :as h}]
  (require handler-ns)
  (-> handler-ns
      find-ns
      (ns-resolve handler)))

(defn- find-uri-handlers [uri]
  (let [scheme (keyword (uri/scheme uri))
        handlers (get @uri-handler-registry scheme)]
    (map require-handler handlers)))

(defn handle-uri
  "Search for and execute, if found, a handler function for `uri`."
  [uri]
  (when-let [handlers (seq (find-uri-handlers uri))]
    ((first handlers) uri)))

(register-handlers)
