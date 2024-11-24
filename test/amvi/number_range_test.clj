(ns amvi.number-range-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Number range test vaildation"
       ;;user wants to test validation function for number range between 0 and 100
       (fact "Number range between 0 and 100"
             (let [validate-number-range (def-validation-inline (number-range-validation 0 100))]
               (validate-number-range 20) => truthy)))