(ns co.grubb.test.multi-handler)

(defn ^{:uri-handler :test1}
  test1-handler
  [uri]
  :test1)

(defn ^{:uri-handler :test2}
  test2-handler
  [uri]
  :test2)
