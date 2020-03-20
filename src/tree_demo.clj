(ns tree-demo
    (:require [clojure.java.io :as io]
      [clojure.pprint :refer [pprint]]
      [clojure.string :as str])
    (:import java.io.File))

(def dir "dir")

(defrecord FileSystemEntry [type parent-path name contents])

(defn ->FileEntry [parent-path name]
      (let [file (io/file parent-path name)]
           (->FileSystemEntry :file parent-path name (slurp file))))

(declare ->Entry)

(defn ->DirEntry [parent-path name]
      (let [file (io/file parent-path name)
            dir-path (str parent-path File/separator name)
            child->entry #(->Entry dir-path %)
            contents (->> file .list (mapv child->entry))]
           (->FileSystemEntry :dir parent-path name contents)))

(defn ->Entry [parent-path name]
      (let [file (io/file parent-path name)]
           (assert (.exists file))
           (if (.isDirectory file)
             (->DirEntry parent-path name)
             (->FileEntry parent-path name))))

(defn remove-subdir [entry subdir-name]
      (letfn [(filter-by-name [entries]
                              (filterv #(not= subdir-name (:name %)) entries))]
             (update entry :contents filter-by-name)))

(declare store-entry)

;; Note: each of the following 3 functions, when completed, is expected to
;; return the address of the thing that it saved.

(defn store-blob-entry [{:keys [contents]}]
      (prn 'store-blob-entry contents)
      (format "<addr(%s)>" contents))

(defn store-tree-entry [{:keys [type parent-path name contents]}]
      (let [entries+addresses (mapv (juxt identity store-entry) contents)
            entry->debug-str (fn [[{:keys [name]} addr]] (str name "@" addr))
            entries-str (as-> entries+addresses $
                              (map entry->debug-str $)
                              (apply str $)
                              (str/replace $ #"\n" "\\\\n"))
            dir-debug-str (format "[dir(%s): %s]" name entries-str)]
           (println 'store-tree-entry dir-debug-str)
           dir-debug-str))

(defn store-entry [{:keys [type] :as entry}]
      (if (= type :file)
        (store-blob-entry entry)
        (store-tree-entry entry)))

(defn runthis []
  (do
    (pprint (->Entry "." dir))
    ;(pprint (remove-subdir (->Entry "." dir) ".idiot"))
    (store-entry ".dir"))
  )
