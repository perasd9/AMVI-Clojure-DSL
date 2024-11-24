(ns amvi.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


;; to solve this problem of string interpolation, actually to cut the process of using str function but to use interpolation we introduce macro for that purpose
(def word "Nice")

(println-str (str word " pera"))

(defmacro interpolation [s & args]
  "Macro for formatting string s with args forwarded to s"
  `(format ~s ~@args))

(defn interpolatio-fun [s & args]
  (format s args))

(interpolatio-fun "Hello, %s you %s" word word)

(interpolation "Hello, %s you %s" word word)


;; -----defining marco which is supposed to make actual validation with the spcific rules------
(defmacro def-validation-type [name & rule-args]
  "Macro for making functions with combination of rules."
  `(defn ~name [~'value]
     (every? #(apply % [~'value]) ~rule-args)))

(defmacro def-validation [name rules]
  "Macro for making functions with combination of rules."
  `(def ~name
     (fn [~'value]
       (every? #(apply % [~'value]) [~rules]))))

;; this macro is supposed to be used for inline function calling without binding name for macro produced function
(defmacro def-validation-inline [rules]
  "Macro for making functions with combination of rules without binding vars."
  `(fn [~'value]
     (every? #(apply % [~'value]) [~rules])))

;; -----------------length checking validators-----------------
;; defining macro for type length validation
(defmacro length-validation [min-length max-length]
  `(fn [~'value]
     (let [~'length (count ~'value)]
       (and (<= ~'length ~max-length)
            (>= ~'length ~min-length)))))


;; (def-validation validate-string-length (length-validation 5 10))

;; (validate-string-length "pera")

;; -----------------number range checking validators---------------------
;; defining macro for number range validation
(defmacro number-range-validation [min-value max-value]
  `(fn [~'value]
     (and (<= ~'value ~max-value)
          (>= ~'value ~min-value))))

;; (def-validation validate-number-range (number-range-validation 10 100))

;; (validate-number-range 50)

;; -----------------nil checking validators---------------------
(defmacro nil-validation []
  `(fn [~'value]
     (nil? ~'value)))

;; (def-validation validate-nil (nil-validation))

;; (validate-nil 1)

;; ----------------regex validators--------------------
(defmacro regex-validation [pattern]
  `(fn [~'value]
     re-matches ~pattern ~'value))

;; (def-validation validate-regex (regex-validation #"abc"))

;; (validate-regex "zz")

;; ------------------unique making validators------------------
(defmacro unique-validation [coll]
  `(fn [~'value]
     (let [~'item (some #{~'value} ~coll)]
       (not (nil? ~'item)))))


;; (def users (list "mika" "pera"))

;; (def-validation validate-unique (unique-validation users))

;; (validate-unique "a")


