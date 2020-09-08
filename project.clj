(defproject clj-ossindex "0.1.0-SNAPSHOT"
  :description "Leiningen plugin to scan dependencies against vulnerabilities at OSS Index"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[clj-http "3.10.1"]
                 [cheshire "5.10.0"]
                 [hiccup "2.0.0-alpha1"]
                 [com.fasterxml.jackson.core/jackson-databind "2.9.10.5"]]
  :eval-in-leiningen true)
