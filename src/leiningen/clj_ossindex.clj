(ns leiningen.clj-ossindex
  (:require [leiningen.core.classpath :as cp]
            [leiningen.core.eval :as eval]
            [leiningen.deps :as d]
            [clojure.pprint :refer [pprint]]
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

(defn- make-purl [ecosystem [full-name version]]
  (str "pkg:" ecosystem "/" full-name "@" version))

(defn- double-name [[name version]]
  [(str name "/" name) version])

(def make-purls 
  (juxt
    (partial make-purl "maven")
    (partial make-purl "clojars")
    #(make-purl "clojars" (double-name %))))

(defn clj-ossindex
  "I don't do a lot."
  [project & args]
  (let [deps (mapcat
               make-purls
               (dependencies project))
        response (client/post
                   "https://ossindex.sonatype.org/api/v3/component-report"
                   {:body (json/generate-string {:coordinates deps})
                    :content-type :json
                    :cookie-policy :none})
        ]
    (println "Evaluating:")
    (pprint deps)
    (println "Vulnerabilities:")
    (if (not= 200 (:status response))
      (do
        (println "FAILED")
        (pprint response))
      (pprint (remove #(empty? (:vulnerabilities %)) (json/parse-string (:body response) true))))
    )
  )


(comment

  nil)
