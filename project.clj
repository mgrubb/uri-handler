(defproject co.grubb/uri-handler "1.0.0"
  :description "A Library which will search the classpath to load URI handlers"
  :url "http://github.com/mgrubb/uri-handler"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.classpath "0.2.2"]
                 [org.clojure/tools.namespace "0.2.8"]
                 [co.grubb/clj-utils "0.3.1"]]
  :aliases {"lint" ["do"
                    ["bikeshed" "-m" "132" "-v"]
                    ["kibit"]
                    ["eastwood" "{:namespaces [:source-paths]}"]]}
  ;; Commented out for now due to bug with technomancy/leiningen #1821
  ; :pom-location "target/"
  :deploy-branches ["master"]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}})
