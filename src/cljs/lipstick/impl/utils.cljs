(ns lipstick.impl.utils)

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