(ns lipstick.dataflow.source-selector
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf :refer [debug]]
            [lipstick.dataflow.sources :as files]))

(def default-db {::selected 0})

(rf/reg-sub ::selected
  (fn [db _] (::selected db)))

(rf/reg-event-fx ::select
  ;[debug]
  (fn [{:keys [db]} [_ idx]]
    {:db (assoc db ::selected idx)
     :dispatch [::files/ensure-content idx]}))

