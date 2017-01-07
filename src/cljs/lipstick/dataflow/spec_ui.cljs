(ns lipstick.dataflow.spec-ui
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]))

(def default-db {::specs {}})

(rf/reg-sub ::tag-collapsed
  (fn [db [_ spec-id tag-name]]
    (get-in db [::specs spec-id :tags tag-name :collapsed] false)))

(rf/reg-event-db ::toggle-tag
  (fn [db [_ spec-id tag-name]]
    (update-in db [::specs spec-id :tags tag-name :collapsed]
               #(if (nil? %) true (not %)))))

(rf/reg-event-db ::toggle-path
  (fn [db [_ spec-id tag-name method name]]
    (update-in db [::specs spec-id :tags tag-name :paths [method name] :collapsed]
               #(if (nil? %) false (not %)))))

(rf/reg-sub ::path-collapsed
  (fn [db [_ spec-id tag-name method path-name]]
    (get-in db [::specs spec-id :tags tag-name :paths [method path-name] :collapsed] true)))