(defproject amvi-compiler "0.1.0-SNAPSHOT"
  :description "DSL macros project"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [midje "1.10.10" :exclusions [org.clojure/clojure]]
                 [criterium "0.4.6"]
                 [org.clojure/data.csv "1.0.1"]
                 [mrcsce/decision-tree "0.1.0"]
                 [metosin/malli "0.10.0"]]
  :repl-options {:init-ns amvi.core}
  :main amvi.core)
