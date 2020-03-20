(ns write-wtree
    (:require
      [clojure.java.io :as io]
      helper-functions
      tree-demo))

(defn return-blob [file-path]
      (let [
            blob-hex (helper-functions/hash-object-helper "" "dontPrint"  file-path)
            blob-byte (helper-functions/from-hex-string blob-hex)
            blob-entry (concat (.getBytes (str "100644 " (last (clojure.string/split file-path #"/")) "\000")) blob-byte)
            ]
           blob-entry
           )
      )


(defn tree-helper [current]

      (let
        [sorted-dir (sort (seq (.listFiles (io/file current))))
         nonEmpty-dir (filter #(or (.isDirectory %) (.isFile %)) sorted-dir)
         hashed-dir (map #(
                            if (not (.isDirectory %))
                            (return-blob (str current "/" (.getName %))) ; Get blob, store as blob entry
                            (concat (.getBytes (str "40000 " (.getName %) "\000")) (tree-helper (str current "/" (.getName %)))) ; Recur down into this directory, store value as tree entry
                            ) nonEmpty-dir)
         formatted-dir (reduce concat (seq []) hashed-dir)  ; Concat all entries
         hashed-tree (helper-functions/hash-tree formatted-dir) ; hash tree
         tree-byte (helper-functions/from-hex-string hashed-tree)
         ]
        tree-byte
        )
      )


(defn run [curr-dir arg]
      (cond
        (and (= 1 (count arg)) (or (= "-h" (nth arg 0)) (= "--help" (nth arg 0)))) (println "idiot write-wtree: write the working tree to the database\n\nUsage: idiot write-wtree\n\nArguments:\n   -h       print this message")
        (< 0 (count arg)) (println "Error: write-wtree accepts no arguments")
        :else (let
                [
                 root-tree-entry (tree-helper curr-dir)
                 ;hashed-root-tree (helper-functions/hash-tree root-tree-entry)
                 ]
                (do
                  ;(helper-functions/write-to-db hashed-root-tree (byte-array root-tree-entry))
                  (println (helper-functions/to-hex-string root-tree-entry))
                  )
                )
        )
      )

;(
;  let [
;       blob-Hash (helper-functions/hash-object-helper "dontPrint"  "dir/file")
;       blob-byte-seq (helper-functions/from-hex-string blob-Hash)
;       tree-Hash1 (helper-functions/hash-tree (concat (.getBytes "100644 file\000") blob-byte-seq))


;       tree-byte-seq (helper-functions/from-hex-string tree-Hash1)
;       tree-Hash2 (helper-functions/hash-tree (concat (.getBytes "40000 dir\000") tree-byte-seq))
;       ]
;      (println tree-Hash2)
;      )

;Get blob hash: (helper-functions/hash-object-helper "dontPrint"  "dir/file")

;Convert hex-string into bytes for further use: (helper-functions/from-hex-string blob-Hash)

;Get tree hash (blob): (helper-functions/hash-tree (concat (.getBytes "100644 file\000") blob-byte-seq))

;<or>  (concat (.getBytes "40000 dir\000") tree-byte-seq))