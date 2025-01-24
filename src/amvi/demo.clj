(ns amvi.demo
  (:import [java.lang Double]
           [java.lang Integer])
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as spec]
            [amvi.core :refer :all]
            [decision-tree.core :as dt]
            [malli.core :as m]))


(defn load-csv [f]
  (with-open [reader (io/reader f)]
    (->> (csv/read-csv reader)
         (mapv vec))))

(def carseats-path "resources/Carseats.csv")

(def carseats (load-csv carseats-path))

;; set without header
(def data-set-without-header (vec (rest carseats)))


;; Removing strings due to limt of decision tree library, I know some vars can be converted like ShelveLoc for graduation of bad, medium, good(1 2 3 or 0 1 2 ...)
(defn parse-carseat [carseat]
  (let [sales (Double/parseDouble (nth carseat 0))
        comp-price (Integer/parseInt (nth carseat 1))
        income (Integer/parseInt (nth carseat 2))
        advertising (Integer/parseInt (nth carseat 3))
        population (Integer/parseInt (nth carseat 4))
        price (Integer/parseInt (nth carseat 5))
        shelve-loc (nth carseat 6)
        age (Integer/parseInt (nth carseat 7))
        education (Integer/parseInt (nth carseat 8))
        urban (nth carseat 9)
        us (nth carseat 10)]
    {:Sales sales
     :CompPrice comp-price
     :Income income
     :Advertising advertising
     :Population population
     :Price price
     :Age age
     :Education education}))

(def data-set (mapv parse-carseat data-set-without-header))

;; 400 observations summary
(count data-set)

;; -------------Usage of AMVI (DSL library) for data processing-----------------
(defn validate-carseat [carseat]
  (let [validate-price (def-validation-inline [(number-range-validation 1 95)])
        validate-age (def-validation-inline [(number-range-validation 0 120)])
        validate-urban (def-validation-inline [(regex-validation #"No")])
        price (:Price carseat)
        age (:Age carseat)]
    (and (validate-price price)
         (validate-age age))))

(def valid-carseats-amvi (filterv #(validate-carseat %) data-set))

;; After data processing of wanted validations left 118 observations
(count valid-carseats-amvi)
;; -------------------------------------------------------------


;; -------------Usage of clojure.spec (library) for data processing-----------------
(spec/def ::price (spec/and integer? #(<= 1 % 95)))

(spec/def ::age (spec/and integer? #(<= 0 % 120)))

(spec/def ::urban (spec/and string? #(contains? #{"No"} %)))


(defn validate-carseat [carseat]
  (let [price (:Price carseat)
        age (:Age carseat)
        urban (:Urban carseat)]
    (and (spec/valid? ::price price)
         (spec/valid? ::age age))))

(def valid-carseats-spec (filterv #(validate-carseat %) data-set))
;; ---------------------------------------------------------------------------


;; ---------------------Usage of clojure.malli (library) for data processing--------------------------
(def number-range-validation-malli
  (m/schema
   [:and [:int {:min 1, :max 95}]]))

(def length-validation-malli
  (m/schema [:string {:min 0, :max 120}]))

(def regex-validation-malli
  (m/schema [:string {:regex #"No"}]))

;; Composite validation schema with multiple mapped fields
(def carseat-validation-schema
  [:map
   [:Price number-range-validation]
   [:Age number-range-validation]])


(defn validate-carseat [carseat]
  (let [validate-price (m/validate number-range-validation-malli (:Price carseat))
        validate-age (m/validate number-range-validation-malli (:Age carseat))]
    (and validate-price validate-age)))

(def valid-carseats-malli (filterv #(validate-carseat %) data-set))
;; -------------------------------------------------------------------------

(count valid-carseats-malli)

;; Calculating total sum of input Sales variable, after that calculating mean value dividing over count
(defn total [vector]
  (apply + vector))

(defn mean [vector]
  (/ (total vector) (count vector)))

;; Using average value to manage decision variable
(def average (mean (mapv #(:Sales %) valid-carseats-spec)))

(defn add-target-variable [carseat]
  (let [sales (:Sales carseat)]
    (if (> sales average)
      (assoc carseat :HighSales "High")
      (assoc carseat :HighSales "Low"))))

;; Data set with decision variable
(def data-set-with-target (map add-target-variable valid-carseats-spec))

(defn split-data [data]
  (let [train-size (int (* 0.8 (count data)))]
    {:train (take train-size data)
     :test (drop train-size data)}))

(def data (split-data data-set-with-target))

;; Depth of tree to avoid overfitting od underfitting
(def max-depth-of-decision-tree 3)

(defn build-tree [train-data]
  (dt/make-decision-tree train-data max-depth-of-decision-tree :HighSales))

(def tree (build-tree (:train data)))

;; Predictions
(defn predict [tree test-data]
  (map #(dt/predict tree (dissoc % :HighSales)) test-data))

(def predictions (predict tree (:test data)))


;; -------------------By the creator of clojure-decision-tree library([mrcsce/decision-tree "0.1.0"] dependency) you can calculate result with this functions, but I didn't use that, maybe it works, I have used to make confusion matrix------------------------------
(def results
  (map #(= %1 %2)
       (->> (:test data)
            (map (partial dt/predict tree)))
       (->> (:test data)
            (map :Species))))

(defn calculate-correct-rate
  [results]
  (let [results (frequencies results)
        k (->> (keys results)
               (map str)
               (map keyword))
        v (vals results)
        r (zipmap k v)
        number-of-true (:true r)
        number-of-data (+ (:true r) (:false r))]
    (->> (/ number-of-true number-of-data)
         double)))

(calculate-correct-rate results)
;; -----------------------------------------------

(defn true-positive [actual-values predicted-values]
  (count (filter (fn [[actual predicted]] (and (= actual "High") (= predicted "High"))) (map vector actual-values predicted-values))))

(defn false-positive [actual-values predicted-values]
  (count (filter (fn [[actual predicted]] (and (= actual "Low") (= predicted "High"))) (map vector actual-values predicted-values))))

(defn false-negative [actual-values predicted-values]
  (count (filter (fn [[actual predicted]] (and (= actual "High") (= predicted "Low"))) (map vector actual-values predicted-values))))

(defn true-negative [actual-values predicted-values]
  (count (filter (fn [[actual predicted]] (and (= actual "Low") (= predicted "Low"))) (map vector actual-values predicted-values))))

(defn compute-eval-metrics [actual predicted]
  (let [fp (false-positive actual predicted)
        tp (true-positive actual predicted)
        fn (false-negative actual predicted)
        tn (true-negative actual predicted)
        accuracy (/ (+ tp tn) (+ tp tn fp fn))
        precision (if (zero? (+ tp fp)) 0 (/ tp (+ tp fp)))
        recall (if (zero? (+ tp fn)) 0 (/ tp (+ tp fn)))
        f1 (* 2 (/ (* precision recall) (+ precision recall)))]
    (println "Accuracy:" accuracy)
    (println "Precision:" precision)
    (println "Recall:" recall)
    (println "F1:" f1)))

(def actual (mapv #(:HighSales %) (:test data)))


(compute-eval-metrics actual predictions)
