(defproject sqls "0.1.0-SNAPSHOT"
  :description "SQLS"
  :url "https://bitbucket.org/mpietrzak/sqls"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.6.0-beta2"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [seesaw "1.4.4"]
                 ]
  ; :main ^:skip-aot sqls.core
  :main sqls.core
  :java-source-paths ["src"]
  :target-path "target/%s"
  :plugins [[codox "0.6.6"]]
  :profiles {:uberjar {:aot :all}})
