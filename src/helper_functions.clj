(ns helper-functions  (:require [clojure.java.io :as io])
                      (:import (java.security MessageDigest)
                                (java.io ByteArrayOutputStream ByteArrayInputStream File)
                                (java.util.zip DeflaterOutputStream InflaterInputStream)))


;--------------------------------------------------| HELPER FUNCTIONS
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

;--------------------------------------------------| HASH-OBJECT HELPER BLOBS (Returns straight to hexadecimal!)
(defn hash-object-helper [curr-dir, switch, file-name]
      (let [header-and-blob (str "blob " (count (slurp (str curr-dir file-name))) "\000" (slurp (str curr-dir file-name)))
            address (sha1-sum header-and-blob)
            directory (subs address 0 2)
            file-name (subs address 2)]
           (if (= "-w" switch)
             (do
               (.mkdir (File. (str curr-dir ".idiot/objects/" directory)))
               (io/copy (zip-str header-and-blob) (io/file (str curr-dir ".idiot/objects/" directory "/" file-name)))
               (println address))
             (if (= "dontPrint" switch)
               address                                      ;Return address
               (println address))
             )))

;--------------------------------------------------| HASH-OBJECT HELPER TREES
(defn sha-bytes [bytes]
      (.digest (MessageDigest/getInstance "sha1") bytes))

(defn to-hex-string
      "Convert the given byte array into a hex string, 2 characters per byte."
      [bytes]
      (letfn [(to-hex [byte]
                      (format "%02x" (bit-and 0xff byte)))]
             (->> bytes (map to-hex) (apply str))))


(defn hex-digits->byte [[dig1 dig2]]
      (let [
            i (Integer/parseInt (str dig1 dig2) 16)
            byte-ready-int (if (< Byte/MAX_VALUE i) (byte (- i 256)) i)
            ]
           (byte byte-ready-int))
      )
(defn from-hex-string [hex-str] (byte-array (map hex-digits->byte (partition 2 hex-str))))

(defn bytes->str [bytes] (->> bytes (map char) (apply str)))


;------ EXECUTING:

;BLOB
;(defn hash-blob-binary [file-name] (let [
;                                             blob-addr (sha-bytes (.getBytes (str "blob " (count (slurp file-name)) "\000" (slurp file-name))))
;                                             ]
;                                            (.getBytes blob-addr)
;                                            )
;      )
;
;(def blob-contents "file contents\n")
;(def blob-addr (sha-bytes (.getBytes (str "blob 14\000" blob-contents))))
;(to-hex-string blob-addr) ; d03e2425cf1c82616e12cb430c69aaa6cc08ff84


;TREE (from blob)
(defn hash-tree [entry-addr] (let [
                                    tree-contents (byte-array (concat (.getBytes (str "tree " (count entry-addr) "\000")) entry-addr))
                                    tree-addr (sha-bytes tree-contents)
                                    ]
                                   (to-hex-string tree-addr)
                                   )
      )

;Write object to database
(defn write-to-db [address header-and-content-hash] (let
                              [
                   directory (subs address 0 2)
                   file-name (subs address 2)
                   ]
      (if (not (.exists (io/file (str ".idiot/objects/" directory "/" file-name))))
        (do
          (.mkdir (File. (str ".idiot/objects/" directory)))
          (io/copy (zip-str header-and-content-hash) (io/file (str ".idiot/objects/" directory "/" file-name)))
          )
        )
                              )
      )