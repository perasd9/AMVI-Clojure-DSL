(ns amvi.length-string-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "String length"
       ;;user wants to test validation function for string length between 5 and 10 chars
       (fact "String length between 5 and 10"
             (let [validate-string-length (def-validation-inline (length-validation 5 10))]
               (validate-string-length "Test123") => truthy)))