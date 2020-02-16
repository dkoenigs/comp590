(ns idiot (:import (java.io File))
    (:require [clojure.java.io :as io])
    (:import java.security.MessageDigest)
    (:import java.util.zip.DeflaterOutputStream
      (java.io ByteArrayInputStream ByteArrayOutputStream))
    (:import java.io.ByteArrayOutputStream
      java.util.zip.InflaterInputStream))



;--------------------------------------------------| PROVIDED HELPER FUNCTIONS
(defn sha1-hash-bytes [data]
      (.digest (MessageDigest/getInstance "sha1")
               (.getBytes data)))

(defn byte->hex-digits [byte]
      (format "%02x"
              (bit-and 0xff byte)))

(defn bytes->hex-string [bytes]
      (->> bytes
           (map byte->hex-digits)
           (apply str)))

(defn sha1-sum [header+blob]
      (bytes->hex-string (sha1-hash-bytes header+blob)))

(defn zip-str
      "Zip the given data with zlib. Return a ByteArrayInputStream of the zipped
      content."
      [data]
      (let [out (ByteArrayOutputStream.)
            zipper (DeflaterOutputStream. out)]
           (io/copy data zipper)
           (.close zipper)
           (ByteArrayInputStream. (.toByteArray out))))

(defn unzip
      "Unzip the given data with zlib. Pass an opened input stream as the arg. The
      caller should close the stream afterwards."
      [input-stream]
      (with-open [unzipper (InflaterInputStream. input-stream)
                  out (ByteArrayOutputStream.)]
                 (io/copy unzipper out)
                 (->> (.toByteArray out)
                      (map char)
                      (apply str))))

;--------------------------------------------------| HASH-OBJECT HELPER
(defn hash-object-helper [switch, file-name]
      (let [header-and-blob (str "blob " (count (slurp file-name)) "\000" (slurp file-name))
            address (sha1-sum header-and-blob)
            directory (subs address 0 2)
            file-name (subs address 2)]
           (if (= "-w" switch)
             (do
               (.mkdir (File. (str ".git/objects/" directory)))
               (io/copy (zip-str header-and-blob) (io/file (str ".git/objects/" directory "/" file-name)))
               (println address))
             (println address)
             )
           )
      )

;--------------------------------------------------| HELP
(defn help [arg]
      (cond
        (= 0 (count arg)) (println "idiot: the other stupid content tracker\n\nUsage: idiot <command> [<args>]\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
        (or  (= "help" (nth arg 0)) (= "--help" (nth arg 0)) (= "-h" (nth arg 0))) (println "idiot help: print help for a command\n\nUsage: idiot help <command>\n\nArguments:\n   <command>   the command to print help for\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
        (= "init" (nth arg 0)) (println "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
        (= "hash-object" (nth arg 0)) (println "idiot hash-object: compute address and maybe create blob from file\n\nUsage: idiot hash-object [-w] <file>\n\nArguments:\n   -h       print this message\n   -w       write the file to database as a blob object\n   <file>   the file")
        (= "cat-file" (nth arg 0)) (println "idiot cat-file: print information about an object\n\nUsage: idiot cat-file -p <address>\n\nArguments:\n   -h          print this message\n   -p          pretty-print contents based on object type\n   <address>   the SHA1-based address of the object")
        :else (println "Error: invalid command")
        )
      )

;--------------------------------------------------| INIT
(defn init [arg]
      (cond
        (and (= 1 (count arg)) (or  (= "-h" (nth arg 0)) (= "--help" (nth arg 0)))) (println "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
        (< 0 (count arg)) (println "Error: init accepts no arguments")
        :else (if (.isDirectory (io/file ".git"))
                (println "Error: .git directory already exists")
                (do (.mkdir (File. ".git")) (.mkdir (File. ".git/objects")) (println "Initialized empty Idiot repository in .git directory"))
                )
        )
      )

;--------------------------------------------------| HASH-OBJECT
(defn hash-object [arg]
      (cond
        (and (= 1 (count arg)) (or  (= "-h" (nth arg 0)) (= "--help" (nth arg 0)))) (println "idiot hash-object: compute address and maybe create blob from file\n\nUsage: idiot hash-object [-w] <file>\n\nArguments:\n   -h       print this message\n   -w       write the file to database as a blob object\n   <file>   the file")
        (not (.isDirectory (io/file ".git"))) (println "Error: could not find database. (Did you run `idiot init`?)")
        (and (= 2 (count arg)) (= "-w" (nth arg 0))) (
                                                       if (.isFile (io/file (nth arg 1)))
                                                       (hash-object-helper "-w" (nth arg 1))
                                                       (println "Error: that file isn't readable")
                                                       )
        (and (= 1 (count arg)) (not (= "-w" (nth arg 0)))) (
                                                             if (.isFile (io/file (nth arg 0)))
                                                             (hash-object-helper "" (nth arg 0))
                                                             (println "Error: that file isn't readable")
                                                             )
        :else (println "Error: you must specify a file.")
        )
      )

;--------------------------------------------------| CAT-FILE
(defn cat-file [arg]
      (cond
        (and (= 1 (count arg)) (or  (= "-h" (nth arg 0)) (= "--help" (nth arg 0)))) (println "idiot cat-file: print information about an object\n\nUsage: idiot cat-file -p <address>\n\nArguments:\n   -h          print this message\n   -p          pretty-print contents based on object type\n   <address>   the SHA1-based address of the object")
        (not (.isDirectory (io/file ".git"))) (println "Error: could not find database. (Did you run `idiot init`?)")
        (and (= 2 (count arg)) (= "-p" (nth arg 0))) (let [
                                                           blob-location (str ".git/objects/" (subs (nth arg 1) 0 2) "/" (subs (nth arg 1) 2))
                                                           ]
                                                          (if (.isFile (io/file blob-location))
                                                            (let [
                                                                  unzipped-blob (with-open [input (-> blob-location io/file io/input-stream)] (unzip input))
                                                                  start-of-blob (.indexOf unzipped-blob "\000")
                                                                  ]
                                                                 (print (subs unzipped-blob (+ start-of-blob 1))))
                                                            (println "Error: that address doesn't exist")
                                                               )
                                                          )
        (< 0 (count arg)) (println "Error: you must specify an address")
        :else (println "Error: the -p switch is required"))
      )




;--------------------------------------------------| MAIN
(defn -main [& value]
      (cond
        (or (= 0 (count value)) (= "-h" (nth value 0)) (= "--help" (nth value 0))) (help (drop 1 value))
        (= "help" (nth value 0)) (help (drop 1 value))
        (= "init" (nth value 0)) (init (drop 1 value))
        (= "hash-object" (nth value 0)) (hash-object (drop 1 value))
        (= "cat-file" (nth value 0)) (cat-file (drop 1 value))
        :else (println "Error: invalid command")
        )
  )
