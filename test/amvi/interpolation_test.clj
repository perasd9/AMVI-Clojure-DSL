(ns amvi.interpolation-test
  (:require [amvi.core :refer :all]
            [midje.sweet :refer :all]))

(facts "Interpolation word"
       ;user wants to make string without using str and concatenating 
       (fact "Word nice"
             (let [word "Nice"]
               (interpolation "Hello, ~{}" word)) => "Hello, ~{}"))