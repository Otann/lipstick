(ns lipstick.handlers
  (:require [re-frame.core :as rf]
            [lipstick.database :as db]
            [taoensso.timbre :as log]))


(rf/reg-event-db :initialize-db
  (fn [_ _]
    db/default-db))

(rf/reg-event-db :set-active-page
  (fn [db [_ active-panel]]
    (assoc db :active-page active-panel)))

(rf/reg-event-db :set-swager-spec
  (fn [db [_ spec]]
    (assoc db :swagger spec)))

(rf/reg-event-db :set-schemas
  (fn [db [_ spec]]
    (assoc db :schemas spec)))
