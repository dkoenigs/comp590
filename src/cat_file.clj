(ns cat-file
              (:require helper-functions
                [clojure.java.io :as io])
    (:import (java.io File)))


;--------------------------------------------------| CAT-FILE


(defn run [curr-dir arg]
      (cond
        (and (= 1 (count arg)) (or (= "-h" (nth arg 0)) (= "--help" (nth arg 0)))) (println "idiot cat-file: print information about an object\n\nUsage: idiot cat-file -p <address>\n\nArguments:\n   -h          print this message\n   -p          pretty-print contents based on object type\n   <address>   the SHA1-based address of the object")
        (not (.isDirectory (io/file (str curr-dir ".idiot")))) (println "Error: could not find database. (Did you run `idiot init`?)")
        (and (= 2 (count arg)) (= "-p" (nth arg 0))) (let [blob-location (str curr-dir ".idiot/objects/" (subs (nth arg 1) 0 2) "/" (subs (nth arg 1) 2))]
                                                          (if (.isFile (io/file blob-location))
                                                            (let [unzipped-blob (with-open [input (-> blob-location io/file io/input-stream)] (helper-functions/unzip input))
                                                                  start-of-blob (.indexOf unzipped-blob "\000")]
                                                                 (print (subs unzipped-blob (+ start-of-blob 1))))
                                                            (println "Error: that address doesn't exist")))

        (> 0 (count arg)) (println "Error: you must specify an address")
        :else (println "Error: the -p switch is required")))
