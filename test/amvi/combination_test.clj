(ns amvi.combination-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Combination tests"
       (fact "String length and regex matching"
             (let [validate (def-validation-inline [(length-validation 2 6) (regex-validation #"abc")])]
               (validate "abc") => truthy))
       (fact "Not nil value and number range"
             (let [validate (def-validation-inline [(nil-validation) (number-range-validation 10 100)])]
               (validate 50) => falsey)))
