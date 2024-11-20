(ns amvi.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


;; to solve this problem of string interpolation, actually to cut the process of using str function but to use interpolation we introduce macro for that purpose
(def word "Nice")

(println-str (str word " pera"))

;; defining macro with arguments of s and args like arguments list for all variables found in string
(defmacro interpolation [s & args]
  `(format ~s ~@args))

(interpolation "Hello, ~{}" word)


;; defining marco which is supposed to make actual validation with the spcific rules
(defmacro def-validation-type [name & rule-args]
  `(defn ~name [~'value]
     (every? #(apply % [~'value]) ~rule-args)))

;; defining macro for type length validation
(defmacro length-validation [min-length max-length]
  `(fn [~'value]
     (let [~'length (count ~'value)]
       (and (<= ~'length ~max-length)
            (>= ~'length ~min-length)))))



(defmacro length-validation [min-length max-length]
  (fn [value]
    (let [length (count value)]
      (and (<= length max-length)
           (>= length min-length)))))


;; problems to solve with symbol for def-validation-type or def-validation because cannot resolve symbol for name and cannot rechange text for def special form in def-validation or df-val-type for example
(defmacro def-validation [name & rules]
  `(def ~name
     (fn [~'value]
       (every? #(apply % [~'value]) ~rules))))



(defmacro def-validation [& rules]
  `(fn [~'value]
     (every? #(apply % [~'value]) ~rules)))


((def-validation validate-string-length (length-validation 5 10)))

((length-validation 5 10) "pera123")

(length-validation 5 10)

(validate-string-length "pera123")
