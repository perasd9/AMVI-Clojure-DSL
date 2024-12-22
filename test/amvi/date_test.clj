(ns amvi.date-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Date tests"
       (fact "Date is before given input"
             (let [validate-date (def-validation-inline [(date-before-validation "2025-01-01")])]
               (validate-date "2024-01-31") => truthy))
       (fact "Date is after given input"
             (let [validate-date (def-validation-inline [(date-after-validation "2025-01-01")])]
               (validate-date "2025-02-02") => truthy)))
