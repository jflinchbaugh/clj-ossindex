(ns leiningen.clj-ossindex
  (:require [leiningen.core.classpath :as cp]
            [leiningen.core.eval :as eval]
            [leiningen.deps :as d]
            [clojure.pprint :as pp]
            [clj-http.client :as client]
            [cheshire.core :as json]))

(defn- flatten-map [m]
  (->> [nil m]
    (tree-seq sequential? second)
    (drop 1)
    (map first)))

(defn- dependencies [project]
  (map #(take 2 %) (flatten-map (cp/managed-dependency-hierarchy :dependencies :managed-dependencies project)))
  )

(defn- make-purl [[full-name version]]
  (str "pkg:maven/" (name full-name) "@" version))

(defn clj-ossindex
  "I don't do a lot."
  [project & args]
  (let [classpath (->> project cp/get-classpath (filter #(.endsWith % ".jar")))
        deps (map make-purl (dependencies project))
        response (client/post
                   "https://ossindex.sonatype.org/api/v3/component-report"
                   {:body (json/generate-string {:coordinates deps})
                    :content-type :json
                    :cookie-policy :none})
        ]
    (if (not= 200 (:status response))
      (do
        (println "FAILED")
        (pp/pprint response))
      (pp/pprint (json/parse-string (:body response) true)))
    ))


(comment

  nil)
