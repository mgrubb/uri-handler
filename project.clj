(defproject co.grubb/uri-handler "0.2.0-SNAPSHOT"
  :description "A Library which will search the classpath to load URI handlers"
  :url "http://github.com/mgrubb/uri-handler"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.classpath "0.2.2"]
                 [org.clojure/tools.namespace "0.2.8"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}})
