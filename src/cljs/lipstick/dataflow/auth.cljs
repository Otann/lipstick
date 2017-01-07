(ns lipstick.dataflow.auth
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]))

(def default-db {::data nil
                 ::config nil})

(rf/reg-event-db ::set-config
  (fn [db [_ data]]
    (assoc db ::config data)))

(rf/reg-sub ::config
  (fn [db _]
    (::config db)))

(rf/reg-sub ::data
  (fn [db _]
    (::data db)))


