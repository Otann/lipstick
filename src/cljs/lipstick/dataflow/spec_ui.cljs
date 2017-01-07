(ns lipstick.dataflow.spec-ui
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [lipstick.dataflow.sources :as sources]
            [lipstick.dataflow.source-selector :as selector]))

(def default-db {::specs {}})

(rf/reg-sub-raw ::spec
  (fn [_ _]
    (let [idx (rf/subscribe [::selector/selected])]
      (rf/subscribe [::sources/content] [idx]))))

(rf/reg-sub ::tag-collapsed
  (fn [db [_ tag-name]]
    (let [spec-id (selector/selected db)]
      (get-in db [::specs spec-id :tags tag-name :collapsed] false))))

(rf/reg-event-db ::toggle-tag
  (fn [db [_ tag-name]]
    (let [spec-id (selector/selected db)]
      (update-in db [::specs spec-id :tags tag-name :collapsed]
                 #(if (nil? %) true (not %))))))

(rf/reg-event-db ::toggle-path
  (fn [db [_ tag-name method name]]
    (let [spec-id (selector/selected db)]
      (update-in db [::specs spec-id :tags tag-name :paths [method name] :collapsed]
                 #(if (nil? %) false (not %))))))

(rf/reg-sub ::path-collapsed
  (fn [db [_ tag-name method path-name]]
    (let [spec-id (selector/selected db)]
      (get-in db [::specs spec-id :tags tag-name :paths [method path-name] :collapsed] true))))