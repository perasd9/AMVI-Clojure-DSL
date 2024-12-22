(ns amvi.core-test
  (:require [clojure.test :refer :all]
            [amvi.core :refer :all]
            [midje.sweet :refer :all]
            [criterium.core :refer :all]))

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

