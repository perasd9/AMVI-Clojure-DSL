(ns amvi.nil-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Nil test vaildation"
       ;;user wants to test validation function checking value is nil
       (fact "Value is not nil"
             (let [validate-nil (def-validation-inline [(nil-validation)])]
               (validate-nil 100) => falsey))
       (fact "Value is nil"
             (let [validate-nil (def-validation-inline [(nil-validation)])]
               (validate-nil nil) => truthy)))