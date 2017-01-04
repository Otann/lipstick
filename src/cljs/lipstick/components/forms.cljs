(ns lipstick.components.forms
  (:require [taoensso.timbre :as log]))

(defn atom-input [atom meta]
  [:input (into meta
                {:type "text"
                 :value @atom
                 :on-change #(reset! atom (-> % .-target .-value))})])
