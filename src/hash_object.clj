(ns hash-object
                (:require helper-functions
                  [clojure.java.io :as io])
                (:import (java.io File)))


;--------------------------------------------------| HASH-OBJECT


(defn run [curr-dir arg]
      (cond
        (and (= 1 (count arg)) (or (= "-h" (nth arg 0)) (= "--help" (nth arg 0)))) (println "idiot hash-object: compute address and maybe create blob from file\n\nUsage: idiot hash-object [-w] <file>\n\nArguments:\n   -h       print this message\n   -w       write the file to database as a blob object\n   <file>   the file")
        (not (.isDirectory (io/file (str curr-dir ".idiot")))) (println "Error: could not find database. (Did you run `idiot init`?)")
        (and (= 2 (count arg)) (= "-w" (nth arg 0))) (if (.isFile (io/file (str curr-dir (nth arg 1))))
                                                       (helper-functions/hash-object-helper curr-dir "-w" (nth arg 1))
                                                       (println "Error: that file isn't readable"))
        (and (= 1 (count arg)) (not (= "-w" (nth arg 0)))) (if (.isFile (io/file (str curr-dir (nth arg 0))))
                                                             (helper-functions/hash-object-helper curr-dir "" (nth arg 0))
                                                             (println "Error: that file isn't readable"))
        :else (println "Error: you must specify a file.")))
