(ns lipstick.utils)

(defn with-keys
  "Adds :key metadata to each child in the list"
  [children]
  (map-indexed #(with-meta %2 {:key %1})
               children))