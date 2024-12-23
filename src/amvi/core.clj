(ns amvi.core)

;; To solve this problem of string interpolation, actually to cut the process of using str function but to use interpolation we introduce macro for that purpose
(println-str (str "Hello" " world"))

(defmacro interpolation [s & args]
  "Macro for formatting string s with args forwarded to s"
  `(format ~s ~@args))

;; Function made to show that evaluation without macro cannot do what evaluation with macro can
(defn interpolatio-fun [s & args]
  (format s args))

;; (interpolatio-fun "Hello, %s, %s" "world" "world") ;; cannot execute but with one parameter is good

(interpolation "Hello, %s, %s" "world" "world")


;; -----Defining marco which is supposed to make actual validation with the spcific rules------
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

;; This macro is supposed to be used for inline function calling without binding name for macro produced function
(defonce validation-cache (atom {}))
(set-validator! validation-cache #(<= (count %) 3))


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

;; For testing purpose in criterium to show difference in performance without caching
(defmacro def-validation-inline-without-cache [rules]
  "Macro for making functions with combination of rules without binding vars."
  (try
    `(fn [~'value]
       (Thread/sleep 100)
       (every? #(apply % [~'value]) ~rules))
    (catch Exception e (println (.getMessage e)))))


;; -----------------Length checking validator-----------------
(defmacro length-validation
  ([min-length]
   (if (number? min-length)
     `(fn [~'value]
        (->
         ~'value
         (str)
         (count)
         (>= ~min-length)))
     `(do
        (throw
         (IllegalArgumentException.
          (interpolation "Input parameters must be numbers in macro 'length-validation': (%s)" ~min-length))))))

  ([min-length max-length]
   (if (and (number? min-length) (number? max-length) (> max-length min-length))
     `(fn [~'value]
        (let [~'length (count (str ~'value))]
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


;; -----------------Number range checking validator---------------------
(defmacro number-range-validation
  ([min-value]
   (if (number? min-value)
     `(fn [~'value]
        (try (->
              ~'value
              (>= ~min-value))
             (catch Exception ~'e
               (throw (IllegalArgumentException.
                       "Testing value is not correct")))))
     `(do
        (throw
         (IllegalArgumentException.
          (interpolation "Input parameters must be numbers'number-range-validation': (%s)" ~min-value))))))
  ([min-value max-value]
   (if (and (number? min-value) (number? max-value) (> max-value min-value))
     `(fn [~'value]
        (try (and (<= ~'value ~max-value)
                  (>= ~'value ~min-value))
             (catch Exception ~'e
               (throw (IllegalArgumentException.
                       "Testing value is not correct")))))
     `(do
        (throw
         (IllegalArgumentException.
          (interpolation "Input parameters must be numbers and 'max-value' must be greater than 'min-value' in macro 'number-range-validation': (%s, %s)" ~min-value ~max-value))))))
  ([min-length max-length & _]
   `(do
      (throw
       (IllegalArgumentException.
        "You can provide only 1 or 2 arguments for 'number-range-validation'")))))


;; -----------------Nil checking validator---------------------
(defmacro nil-validation []
  `(fn [~'value]
     (nil? ~'value)))

(defmacro not-nil-validation []
  `(fn [~'value]
     (not (nil? ~'value))))


;; ----------------Regex validator--------------------
(defmacro regex-validation [pattern]
  (if (= (type pattern) java.util.regex.Pattern)
    `(fn [~'value]
       (not (nil? (re-matches ~pattern (str ~'value)))))
    `(do
       (throw
        (IllegalArgumentException.
         "Input parameter must be a regex expression in macro 'regex-validation'")))))


;; ------------------Unique making validator------------------
(defmacro unique-validation [coll]
  (if (coll? (eval coll))
    `(fn [~'value]
       (let [~'item (some #{~'value} ~coll)]
         (not (nil? ~'item))))
    `(do
       (throw
        (IllegalArgumentException.
         "Input parameter must be a collection in macro 'unique-validation'")))))


;; ------------------Type validator--------------
(defmacro type-validation [expected-type]
  (if (class? (resolve expected-type))
    `(fn [~'value]
       (try (instance? ~expected-type ~'value)
            (catch Exception ~'e
              (throw (IllegalArgumentException.
                      "Testing value is not correct")))))
    `(do
       (throw
        (IllegalArgumentException.
         "Input parameter must be a type(java class) in macro 'type-validation'")))))


;; ----------------------Email validator-------------
(defmacro email-validation []
  `(fn [~'value]
     (re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$" (str ~'value))))


;; ----------------------Date before validator--------------
(defmacro date-before-validation [before-date]
  `(fn [~'value]
     (try (let [~'parsed-date (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd")  (str ~'value))
                ~'parsed-before-date (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd") ~before-date)]
            (and ~'parsed-date
                 (.before ~'parsed-date ~'parsed-before-date)))
          (catch Exception ~'e
            (throw (IllegalArgumentException.
                    "Date has to be in format 'yyyy-MM-dd'"))))))


;; ----------------------Date after validator--------------
(defmacro date-after-validation [after-date]
  `(fn [~'value]
     (try (let [~'parsed-date (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd")  (str ~'value))
                ~'parsed-after-date (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd") ~after-date)]
            (and ~'parsed-date
                 (.after ~'parsed-date ~'parsed-after-date)))
          (catch Exception ~'e
            (throw (IllegalArgumentException.
                    "Date has to be in format 'yyyy-MM-dd'"))))))
