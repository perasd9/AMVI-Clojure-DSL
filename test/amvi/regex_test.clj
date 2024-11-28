(ns amvi.regex-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Regex test vaildation"
       ;;user wants to test validation function checking regex
       (fact "Regex abc"
             (let [validate-regex (def-validation-inline [(regex-validation #"abc")])]
               (validate-regex "abc") => truthy))
       (fact "Regex abc false"
             (let [validate-regex (def-validation-inline [(regex-validation #"abc")])]
               (validate-regex "qqa") => falsey))
       (fact "Regex if input prameter is string"
             (let [validate-regex (def-validation-inline [(regex-validation "abc")])]
               (validate-regex "abc") => (throws IllegalArgumentException)))
       (fact "Regex if input prameter is number"
             (let [validate-regex (def-validation-inline [(regex-validation 25)])]
               (validate-regex "abc") => (throws IllegalArgumentException))))