(defproject concrete-optics "0.1.0-SNAPSHOT"
  :description "Dead simple optics in Clojure"
  :url "https://github.com/sonatsuer/concrete-optics"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/test.check "1.1.0"]]
  :plugins [[lein-codox "0.10.8"]]
  :codox
  {:output-path "docs"
   :source-uri "https://github.com/sonatsuer/concrete-optics/blob/main/{filepath}#L{line}"})
