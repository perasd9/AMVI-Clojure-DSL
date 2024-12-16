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

;; (interpolatio-fun "Hello, %s, %s" word word)

(interpolation "Hello, %s, %s" word word)


;; -----defining marco which is supposed to make actual validation with the spcific rules------
(defmacro def-validation [name rules]
  "Macro for making functions with combination of rules."
  (try
    (let [rule-key (str rules)]
      `(def ~name
         (fn [~'value]
           (let [~'item (get @~'validation-cache (str ~'value "-" ~rule-key))]
             (if (not (nil? ~'item))
               ~'item
               (let [~'result (every? #(apply % [~'value]) ~rules)]
                 (if (= (count @~'validation-cache) 3)
                   (swap! ~'validation-cache dissoc ((first @~'validation-cache) 0)))
                 (do
                   (Thread/sleep 100)
                   (swap! ~'validation-cache assoc-in [(str ~'value "-" ~rule-key)] ~'result)
                   (get @~'validation-cache (str ~'value "-" ~rule-key)))))))))
    (catch Exception e (println (.getMessage e)))))

;; this macro is supposed to be used for inline function calling without binding name for macro produced function
(def validation-cache (atom {}))


(defmacro def-validation-inline [rules]
  "Macro for making functions with combination of rules without binding vars."
  (try
    (let [rule-key (str rules)]
      `(fn [~'value]
         (let [~'item (get @~'validation-cache (str ~'value "-" ~rule-key))]
           (if (not (nil? ~'item))
             ~'item
             (let [~'result (every? #(apply % [~'value]) ~rules)]
               (if (= (count @~'validation-cache) 3)
                 (swap! ~'validation-cache dissoc ((first @~'validation-cache) 0)))
               (do
                 (Thread/sleep 100)
                 (swap! ~'validation-cache assoc-in [(str ~'value "-" ~rule-key)] ~'result)
                 (get @~'validation-cache (str ~'value "-" ~rule-key))))))))
    (catch Exception e (println (.getMessage e)))))

;; for testing purpose in criterium
(defmacro def-validation-inline-without-cache [rules]
  "Macro for making functions with combination of rules without binding vars."
  (try
    `(fn [~'value]
       (Thread/sleep 100)
       (every? #(apply % [~'value]) ~rules))
    (catch Exception e (println (.getMessage e)))))


;; -----------------length checking validator-----------------
(defmacro length-validation
  ([min-length]
   (if (number? min-length)
     `(fn [~'value]
        (->
         ~'value
         (count)
         (>= ~min-length)))
     `(do
        (throw
         (IllegalArgumentException.
          (interpolation "Input parameters must be numbers in macro 'length-validation': (%s)" ~min-length))))))

  ([min-length max-length]
   (if (and (number? min-length) (number? max-length) (> max-length min-length))
     `(fn [~'value]
        (let [~'length (count ~'value)]
          (and (<= ~'length ~max-length)
               (>= ~'length ~min-length))))
     `(do
        (throw
         (IllegalArgumentException.
          (interpolation "Input parameters must be numbers and 'max-length' must be greater than 'min-length' in macro 'length-validation': (%s, %s)" ~min-length ~max-length))))))

  ([min-length max-length & _]
   `(do
      (throw
       (IllegalArgumentException.
        "You can provide only 1 or 2 arguments for 'length-validation'")))))


;; -----------------number range checking validator---------------------
(defmacro number-range-validation
  ([min-value]
   (if (number? min-value)
     `(fn [~'value]
        (->
         ~'value
         (>= ~min-value)))
     `(do
        (throw
         (IllegalArgumentException.
          (interpolation "Input parameters must be numbers'number-range-validation': (%s)" ~min-value))))))
  ([min-value max-value]
   (if (and (number? min-value) (number? max-value) (> max-value min-value))
     `(fn [~'value]
        (and (<= ~'value ~max-value)
             (>= ~'value ~min-value)))
     `(do
        (throw
         (IllegalArgumentException.
          (interpolation "Input parameters must be numbers and 'max-value' must be greater than 'min-value' in macro 'number-range-validation': (%s, %s)" ~min-value ~max-value))))))
  ([min-length max-length & _]
   `(do
      (throw
       (IllegalArgumentException.
        "You can provide only 1 or 2 arguments for 'number-range-validation'")))))


;; -----------------nil checking validator---------------------
(defmacro nil-validation []
  `(fn [~'value]
     (nil? ~'value)))


;; ----------------regex validator--------------------
(defmacro regex-validation [pattern]
  (if (= (type pattern) java.util.regex.Pattern)
    `(fn [~'value]
       (not (nil? (re-matches ~pattern ~'value))))
    `(do
       (throw
        (IllegalArgumentException.
         "Input parameter must be a regex expression in macro 'regex-validation'")))))


;; ------------------unique making validator------------------
(defmacro unique-validation [coll]
  (if (coll? (eval coll))
    `(fn [~'value]
       (let [~'item (some #{~'value} ~coll)]
         (not (nil? ~'item))))
    `(do
       (throw
        (IllegalArgumentException.
         "Input parameter must be a collection in macro 'unique-validation'")))))


;; ------------------type validator--------------
(defmacro type-validation [expected-type]
  (if (= (type expected-type) java.lang.Class)
    `(fn [~'value]
       (instance? ~expected-type ~'value))
    `(do
       (throw
        (IllegalArgumentException.
         "Input parameter must be a type(java class) in macro 'type-validation'")))))


;; ----------------------email validator-------------
(defmacro email-validation []
  `(fn [~'value]
     (re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$" ~'value)))



(defmacro date-validation [before-date]
  `(fn [~'value]
     (let [parsed-date (try (java.text.SimpleDateFormat. "yyyy-MM-dd") .parse ~'value (catch Exception _ nil))]
       (and parsed-date (.before parsed-date ~before-date)))))

(def before-date ((java.text.SimpleDateFormat. "yyyy-MM-dd") .parse "2025-01-01"))
((date-validation before-date) "2024-12-31") ;; true



