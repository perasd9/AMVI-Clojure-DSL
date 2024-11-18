(ns amvi.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


;to solve this problem of string interpolation, actually to cut the process of using str function but to use interpolation we introduce macro for that purpose
(def word "Nice")

(println-str (str word " pera"))

;defining macro with arguments of s and args like arguments list for all variables found in string
(defmacro interpolation [s & args]
  `(format ~s ~@args))

(interpolation "Hello, ~{}" word)


