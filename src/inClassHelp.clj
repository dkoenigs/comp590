(ns inClassHelp
    require '[clojure.java.io :as io])

(defrecord FileSystemEntry
           [type parent-path name contents])

(->FileSystemEntry :file "src" "inClassHelp.clj" "<contents>")

