(ns init
    (:require [clojure.java.io :as io])
    (:import (java.io File)))


;--------------------------------------------------| INIT


(defn run [curr-dir arg]
      (cond
        (and (= 1 (count arg)) (or (= "-h" (nth arg 0)) (= "--help" (nth arg 0)))) (println "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
        (< 0 (count arg)) (println "Error: init accepts no arguments")
        :else (if (.isDirectory (io/file (str curr-dir ".idiot")))
                (println "Error: .idiot directory already exists")
                (do (.mkdir (File. (str curr-dir ".idiot"))) (.mkdir (File. (str curr-dir ".idiot/objects"))) (println "Initialized empty Idiot repository in .idiot directory")))))