(defproject co.grubb/uri-handler "1.1.1-SNAPSHOT"
  :description "A Library which will search the classpath to load URI handlers"
  :url "http://github.com/mgrubb/uri-handler"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.classpath "0.2.2"]
                 [org.clojure/tools.namespace "0.2.8"]
                 [co.grubb/clj-utils "0.3.1"]]
  :plugins [[lein-set-version "LATEST"]]
  :aliases {"lint" ["do"
                    ["bikeshed" "-m" "132" "-v"]
                    ["kibit"]
                    ["eastwood" "{:namespaces [:source-paths]}"]]}
  ;; Commented out for now due to bug with technomancy/leiningen #1821
  ; :pom-location "target/"
  :set-version {:updates [{:path "README.md" :no-snapshot true}]}
  :deploy-branches ["master"]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}})
