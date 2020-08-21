(ns leiningen.clj-ossindex
  (:require [leiningen.core.classpath :as cp]
            [leiningen.core.eval :as eval]
            [leiningen.deps :as d]
            [clojure.pprint :as pp])
  (:import [org.sonatype.ossindex.service.client
            OssindexClient
            OssindexClientConfiguration]
           [org.sonatype.ossindex.service.client.internal
            OssindexClientImpl
            Version]
           [org.sonatype.ossindex.service.client.transport
            HttpClientTransport
            UserAgentSupplier]
           [org.sonatype.ossindex.service.client.marshal GsonMarshaller]
           [org.sonatype.goodies.packageurl PackageUrl PackageUrl$Builder]))

(defn- create-client-version [] (Version. Version))

(defn- create-user-agent-supplier [] (proxy [UserAgentSupplier] [(create-client-version)]))

(defn- create-http-client-transport [] (HttpClientTransport. (create-user-agent-supplier)))

(defn- create-marshaller [] (GsonMarshaller.))

(defn- create-client-config [] (OssindexClientConfiguration.))

(defn- create-client [client-config] (OssindexClientImpl.
                                      client-config
                                      (create-http-client-transport)
                                      (create-marshaller)))

(defn- flatten-map [m]
  (->> [nil m]
    (tree-seq sequential? second)
    (drop 1)
    (map first)))

(defn- dependencies [project]
  (map #(take 2 %) (flatten-map (cp/managed-dependency-hierarchy :dependencies :managed-dependencies project)))
  )

(defn- make-purl [[full-name version]]
  (PackageUrl/parse (str "pkg:maven/" (name full-name))))

(defn clj-ossindex
  "I don't do a lot."
  [project & args]
  (let [classpath (->> project cp/get-classpath (filter #(.endsWith % ".jar")))
        client (create-client (create-client-config))
        deps (map make-purl (dependencies project))
        reports (.requestComponentReports client (take 1 deps))
        ]
    (pp/pprint deps)
    ))


(comment

  (proxy [UserAgentSupplier] [])


  nil)
