(ns amvi.regex-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Regex test vaildation"
       ;;user wants to test validation function checking regex
       (fact "Regex abc"
             (let [validate-regex (def-validation-inline (regex-validation #"abc"))]
               (validate-regex "abc") => truthy)))