(ns amvi.email-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Email test vaildation"
       (fact "Email structure is correct"
             (let [validate-email (def-validation-inline [(email-validation)])]
               (validate-email "pera@gmail.com") => truthy))
       (fact "Email structure is not correct"
             (let [validate-email (def-validation-inline [(email-validation)])]
               (validate-email "pera") => falsey)))