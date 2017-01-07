(ns lipstick.dataflow.active-page
  (:require [re-frame.core :as rf]))

(def default-db {::page :root})

(rf/reg-event-db ::set
  ; Changes pages for routing
  (fn [db [_ active-panel]]
    (assoc db ::page active-panel)))

(rf/reg-sub ::page
  (fn [db _]
    (::page db)))
