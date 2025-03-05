(ns amvi.core-test
  (:require [clojure.test :refer :all]
            [amvi.core :refer :all]
            [midje.sweet :refer :all]
            [criterium.core :refer :all]
            [clojure.spec.alpha :as spec]
            [malli.core :as m]))

(def values [{:name "apple" :expiration "2025-02-14"} {:name "banana" :expiration "2025-03-11"} {:name "kiwi" :expiration "2025-10-23"}])

(facts "High order functions tests with collections"
       (fact "Date is before given input field of date and field of name is not nil and string length is between 5 and 10"
             (let [validate-date (def-validation-inline [(date-before-validation "2026-01-01")])
                   validate-length (def-validation-inline [(not-nil-validation) (length-validation 5 10)])]
               (filter #(and (validate-date (:expiration %)) (validate-length (:name %))) values)
               => '({:name "apple", :expiration "2025-02-14"} {:name "banana", :expiration "2025-03-11"}))))

;; here we can see that results of same validation value gives us better performance with caching that validation instead of every time validate
(let [validate-string-length (def-validation-inline [(length-validation 5 10)])]
  (quick-bench (validate-string-length "Test123")))

(let [validate-string-length (def-validation-inline-without-cache [(length-validation 5 10)])]
  (quick-bench (validate-string-length "Test123")))


;; Performance testing from demo
;; Just one sample from dataset is taken for the testing nature
(defn validate-carseat [carseat]
  (let [validate-price (def-validation-inline [(number-range-validation 1 95)])
        validate-age (def-validation-inline [(number-range-validation 0 120)])
        price (:Price carseat)
        age (:Age carseat)]
    (and (validate-price price)
         (validate-age age))))

(quick-bench (validate-carseat {:Sales 9.5, :CompPrice 138, :Income 73, :Advertising 11, :Population 276, :Price 120, :Age 42, :Education 17}))


(spec/def ::price (spec/and integer? #(<= 1 % 95)))
(spec/def ::age (spec/and integer? #(<= 0 % 120)))
(defn validate-carseat [carseat]
  (let [price (:Price carseat)
        age (:Age carseat)
        urban (:Urban carseat)]
    (and (spec/valid? ::price price)
         (spec/valid? ::age age))))

(quick-bench (validate-carseat {:Sales 9.5, :CompPrice 138, :Income 73, :Advertising 11, :Population 276, :Price 120, :Age 42, :Education 17}))


(def number-range-validation-malli
  (m/schema
   [:and [:int {:min 1, :max 95}]]))
(defn validate-carseat [carseat]
  (let [validate-price (m/validate number-range-validation-malli (:Price carseat))
        validate-age (m/validate number-range-validation-malli (:Age carseat))]
    (and validate-price validate-age)))

(quick-bench (validate-carseat {:Sales 9.5, :CompPrice 138, :Income 73, :Advertising 11, :Population 276, :Price 120, :Age 42, :Education 17}))