(ns amvi.type-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Type test vaildation"
       (fact "Value is given type"
             (let [validate-type (def-validation-inline [(type-validation String)])]
               (validate-type "pera@gmail.com") => truthy))
       (fact "Value is not given type"
             (let [validate-type (def-validation-inline [(type-validation String)])]
               (validate-type 100) => falsey)))