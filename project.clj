(defproject sqls "0.1.11"
  :description "SQLS"
  :url "https://github.com/mpietrzak/sqls"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.fifesoft/rsyntaxtextarea "3.1.3"]
                 [com.taoensso/timbre "5.1.2"]
                 [fipp "0.6.23"]
                 [io.aviso/pretty "1.1"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/data.json "2.3.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.clojure/tools.cli "1.0.206"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.xerial/sqlite-jdbc "3.34.0"]
                 [seesaw "1.5.0"]]
  :main sqls.core
  :java-source-paths ["src"]
  :javac-options ["-target" "11" "-source" "11"]
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
  :target-path "target/%s"
  :plugins [[lein-codox "0.10.7"]]
  :codox {:output-dir "doc/codox"}
  :profiles {:uberjar {:aot :all}
             :repl {:dependencies [[org.clojure/tools.namespace "1.1.0"]]}
             :dev {:dependencies [[org.clojure/tools.namespace "1.1.0"]]
                   :global-vars {*warn-on-reflection* true}
                   :jvm-opts ["-Xms4M"
                              "-Xmx4G"
                              "-XX:+UseG1GC"
                              "-XX:MaxGCPauseMillis=10"]}})
