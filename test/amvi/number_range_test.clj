(ns amvi.number-range-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Number range test vaildation"
       ;;user wants to test validation function for number range between 0 and 100
       (fact "Number range between 0 and 100"
             (let [validate-number-range (def-validation-inline [(number-range-validation 0 100)])]
               (validate-number-range 20) => truthy))
       (fact "Number range if first input parameter is string"
             (let [validate-number-range (def-validation-inline [(number-range-validation "asd" 100)])]
               (validate-number-range 20) => (throws IllegalArgumentException)))
       (fact "Number range if second input parameter is string"
             (let [validate-number-range (def-validation-inline [(number-range-validation 100 "asd")])]
               (validate-number-range 20) => (throws IllegalArgumentException)))
       (fact "Number range if both input parameters are string"
             (let [validate-number-range (def-validation-inline [(number-range-validation "abc" "asd")])]
               (validate-number-range 20) => (throws IllegalArgumentException)))
       (fact "Number range if first input parameter is greater"
             (let [validate-number-range (def-validation-inline [(number-range-validation 100 10)])]
               (validate-number-range 20) => (throws IllegalArgumentException)))
       (fact "Number range if input parameters are equal"
             (let [validate-number-range (def-validation-inline [(number-range-validation 100 100)])]
               (validate-number-range 20) => (throws IllegalArgumentException))))