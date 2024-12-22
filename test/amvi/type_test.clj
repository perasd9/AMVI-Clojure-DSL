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

;; left to find a bug
(=  (type String) java.lang.Class)

((fn [value, types]
   (if (= (type types) java.lang.Class)
     (try (instance? types value)
          (catch Exception e
            (throw (IllegalArgumentException.
                    "Testing value is not correct"))))
     (do
       (throw
        (IllegalArgumentException.
         "Input parameter must be a type(java class) in macro 'type-validation'"))))) "pera" String)