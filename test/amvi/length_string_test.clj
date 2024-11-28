(ns amvi.length-string-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "String length test validation"
       ;;user wants to test validation function for string length between 5 and 10 chars
       (fact "String length between 5 and 10"
             (let [validate-string-length (def-validation-inline [(length-validation 5 10)])]
               (validate-string-length "Test123") => truthy))
       (fact "String length if first input parameter is string"
             (let [validate-string-length (def-validation-inline [(length-validation "asd" 10)])]
               (validate-string-length "Test123") => (throws IllegalArgumentException)))
       (fact "String length if second input parameter is string"
             (let [validate-string-length (def-validation-inline [(length-validation 10 "asd")])]
               (validate-string-length "Test123") => (throws IllegalArgumentException)))
       (fact "String length if both input parameters are string"
             (let [validate-string-length (def-validation-inline [(length-validation "acb" "asd")])]
               (validate-string-length "Test123") => (throws IllegalArgumentException)))
       (fact "String length if first input parameter is greater"
             (let [validate-string-length (def-validation-inline [(length-validation 20 10)])]
               (validate-string-length "Test123") => (throws IllegalArgumentException)))
       (fact "String length if input parameters are equal"
             (let [validate-string-length (def-validation-inline [(length-validation 20 20)])]
               (validate-string-length "Test123") => (throws IllegalArgumentException)))
       (fact "String length if input parameters are equal"
             (let [validate-string-length (def-validation-inline [(length-validation 20 20)])]
               (validate-string-length "Test123") => (throws IllegalArgumentException))))