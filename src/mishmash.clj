(ns mishmash
  (:require [clojure.string :as str]))

;Calculates factorial of value >= 0
(defn factorial [value] (
                          if (= value 0)
                          1
                          (* value (factorial (- value 1)))
                          )
  )

;For each line of the row 'value' of pascal's triangle, calculates binomial coefficient
;Either adds a " " or a "\n" when at the end
(defn pascal [value] (
                       if (>= value 0)
                       (dotimes [r (+ value 1)]
                         (if (= r value)
                           (print (str (/ (factorial value) (* (factorial r) (factorial (- value r)))) "\n"))
                           (print (str (/ (factorial value) (* (factorial r) (factorial (- value r)))) " "))
                           )
                         )
                       (println "invalid input")
                       )
  )

;Storing Roman Numerals for each multiple of 10
(def m ["" "M" "MM" "MMM"])
(def c ["" "C" "CC" "CCC" "CD" "D" "DC" "DCC" "DCCC" "CM"])
(def x ["" "X" "XX" "XXX" "XL" "L" "LX" "LXX" "LXXX" "XC"])
(def i ["" "I" "II" "III" "IV" "V" "VI" "VII" "VIII" "IX"])
(def roman-letters "MCDXLIV")
(def read-m 0)
(def read-c 0)
(def read-x 0)
(def read-i 0)



(defn write-roman [value] (
                            if (and (>= value 1) (< value 4000))
                            (do
                              (def tho (get m (quot value 1000)))
                              (def hun (get c (quot (rem value 1000) 100)))
                              (def ten (get x (quot (rem value 100) 10)))
                              (def one (get i (rem value 10)))
                              (println (str tho (str hun (str ten one))))
                              )
                            (println "invalid input")
                            )
  )

(defn read-roman [value] (do

                           (doseq [i (range 1 4)]
                             (if (.contains value (get m i))
                               (def read-m i)
                               )
                             )
                           ;Ensure that we are not accidentally reading a subtraction
                           (if (and (= read-m 1) (.contains value "CM"))
                             (if (and (= read-m 1) (.contains value "MCM")) ;Check whether subtraction is intentional
                               ()
                               (def read-m 0)
                               )
                             )

                           (doseq [i (range 1 10)]
                             (if (.contains value (get c i))
                               (if (<= (count (get c read-c)) (count (get c i)))
                                 (def read-c i))
                               )
                             )
                           ;Ensure that we are not accidentally reading a subtraction
                           (if (and (= read-c 1) (.contains value "XC"))
                             (if (and (= read-c 1) (.contains value "CXC")) ;Check whether subtraction is intentional
                               ()
                               (def read-c 0)
                               )
                             )

                           (doseq [i (range 1 10)]
                             (if (.contains value (get x i))
                               (if (<= (count (get x read-x)) (count (get x i)))
                                 (def read-x i))
                               )
                             )
                           ;Ensure that we are not accidentally reading a subtraction
                           (if (and (= read-x 1) (.contains value "IX"))
                             (if (and (= read-x 1) (.contains value "XIX")) ;Check whether subtraction is intentional
                               ()
                               (def read-x 0)
                               )
                             )

                           (doseq [j (range 1 10)]
                             (if (.contains value (get i j))
                               (if (<= (count (get i read-i)) (count (get x j)))
                                 (def read-i j))
                               )
                             )

                           (println (+ (* 1000 read-m) (+ (* 100 read-c) (+ (* 10 read-x) read-i))))
                           )
  )



(defn -main [& value]
  (if (= 2 (count value))
    (cond
      (= (nth value 0) "pascal")  (try
                                    (pascal (Integer/parseInt (nth value 1)))
                                    (catch NumberFormatException e (println "invalid input"))
                                    )


      (= (nth value 0) "write-roman") (try
                                        (write-roman (Integer/parseInt (nth value 1)))
                                        (catch NumberFormatException e (println "invalid input"))
                                        )

      (= (nth value 0) "read-roman") (if (string? (nth value 1))
                                       (if (= true (boolean (re-find #"^[I|X|C|M|D|L|V]*$" (nth value 1)))) ; Verify Roman alphabet
                                         (if (= true (boolean (re-find #"(IIII+)|(XXXX+)|(CCCC+)|(MMMM+)|(DD+)|(LL+)|(VV+)" (nth value 1)))) ; Verify correct number of roman characters
                                           (println "invalid input")
                                           (if (= true (boolean (re-find #"IXXC|IXCM|XCCM" (nth value 1))))
                                             (println "invalid input")
                                             (read-roman (nth value 1))
                                             )
                                           )
                                         (println "invalid input")
                                       )
                                       (println "invalid input")
                                       )
      :else         (println "invalid input")
      )
    (println "invalid input")
    )
  )
