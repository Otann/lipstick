(ns lipstick.tools.utils
  (:require [clojure.string :as str]))

(defn after-now [seconds]
  (let [now (js/Date.)
        sec (+ seconds (.getSeconds now))]
    (.setSeconds now sec)))


(defn join-classes [& classes]
  (-> classes
      (filter #(not (nil? %)))
      (clojure.string/join " ")))


(defn containsv? [arr val]
  (some #(= val %) arr))


(defn with-keys
  "Adds :key metadata to each child in the list"
  [children]
  (map-indexed #(if (string? %2) %2 (with-meta %2 {:key %1}))
               children))

(defn deref-json
  "Resoves actual content from $ref objects"
  [schema swag-root]
  (if-let [ref (:$ref schema)]
    (let [parts (str/split ref "/")
          name (last parts)
          path (rest parts)]
      [name (get-in swag-root (map keyword path))])
    [nil schema]))
