(ns app.core
  (:require
   [org.httpkit.client :as http]
   [clojure.string     :as string]
   [hickory.core       :as h]
   [hickory.select     :as hs]
   [clojure.data.csv   :as csv]
   [clojure.java.io    :as io]))

(def request-body  (comp :body deref http/get))
(def parse-html    (comp h/as-hickory h/parse))
(def select-first  (comp first hs/select))
(def f-c           (comp first :content))
(def remove-rn     (partial remove #{"\r\n  "}))

(defn get-pair
  [node]
  [(-> node f-c f-c f-c)
   (or (-> node :content second f-c f-c)
       (-> node :content second f-c))])

(defn remove-resource-name
  [path]
  (string/join "." (-> path (string/split #"\.") rest)))

(defn definition-table-content
  [table]
  (loop [elements table result [] resource nil]
    (if (seq elements)
      (let [e (first elements)]
        (if (= "structure" (-> e f-c :attrs :class))
          (recur (rest elements) result 
                 (-> e f-c f-c :attrs :name remove-resource-name))
          (recur (rest elements)
                 (if (and (contains? #{"Cardinality" "Type"} (-> e get-pair first))
                          (not-empty resource))
                   (conj result resource)
                   #_(assoc-in result
                             [(keyword resource) (-> e get-pair first keyword)]
                             (-> e get-pair second))
                   result)
                 resource)))
      result)))

(defonce resources
  (->>
   (request-body "http://www.hl7.org/fhir/resourcelist.html")
   (parse-html)
   (select-first (hs/id :tabs-2))
   (hs/select (hs/tag :li))
   (mapv (comp f-c f-c))))


(def definitions 
  (reduce
   (fn [accumulator resource]
     (->>
      (format "http://www.hl7.org/fhir/%s-definitions.html" resource)
      (request-body)
      (parse-html)
      (select-first (hs/and (hs/tag :table) (hs/class :dict)))
      (:content)
      (remove-rn)
      (first)
      (:content)
      (remove-rn)
      (definition-table-content)
      (assoc accumulator resource)))
   {} resources))

(defn csv-export
  []
  (with-open [writer (io/writer "output.csv")]
    (csv/write-csv
     writer
     (reduce
      (fn [acc resource]
        (concat acc 
                (into 
                 [[nil nil nil]
                  [(key resource) nil nil]]
                 (mapcat (fn [[k v]]
                           [[k (:Cardinality v) (:Type v)]])
                         (val resource)))))
      [] definitions))))

(comment 
  (csv-export))
