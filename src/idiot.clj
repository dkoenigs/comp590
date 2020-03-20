(ns idiot
    (:require help
              init
              hash-object
              cat-file
              helper-functions
              write-wtree))



;--------------------------------------------------| MAIN


(defn -main [& value]
  (let
    [
     args (if
                (and (< 0 (count value)) (= "-r" (nth value 0)))
                (if (< 1 (count value)) (drop 1 value) (do (println "Error: the -r switch needs an argument") (System/exit 0)))
                (conj value "")
                )
     ]
    (cond
      (or (= 1 (count args)) (= "-h" (nth args 1)) (= "--help" (nth args 1))) (help/run (drop 2 args))
      (= "help" (nth args 1)) (help/run (drop 2 args))
      (= "init" (nth args 1)) (init/run (if (not (= "" (nth args 0))) (str (nth args 0) "/")) (drop 2 args))
      (= "hash-object" (nth args 1)) (hash-object/run (if (not (= "" (nth args 0))) (str (nth args 0) "/")) (drop 2 args))
      (= "cat-file" (nth args 1)) (cat-file/run (if (not (= "" (nth args 0))) (str (nth args 0) "/") "") (drop 2 args))
      (= "write-wtree" (nth args 1)) (write-wtree/run (if (not (= "" (nth args 0))) (str (nth args 0) "/") "") (drop 2 args))
      :else (println "Error: invalid command"))
    )
      )