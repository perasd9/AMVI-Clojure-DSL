(ns amvi.unique-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(def users (list "mika" "pera"))

(facts "Uniquity test vaildation"
       ;;user wants to test validation function checking value is unique
       (fact "Value is unique"
             (let [validate-unique (def-validation-inline [(unique-validation users)])]
               (validate-unique "pera") => truthy))
       (fact "Input parameter is string"
             (let [validate-unique (def-validation-inline [(unique-validation "abc")])]
               (validate-unique "pera") => (throws IllegalArgumentException))))