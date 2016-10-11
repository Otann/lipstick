(ns lipstick.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [re-frame.core :as rf]
            [lipstick.database :as db]
            [lipstick.swagger :as swagger]
            [taoensso.timbre :as log]))


(rf/reg-event-db :initialize-db
  (fn [_ _]
    db/default-db))

(rf/reg-event-db :set-active-page
  (fn [db [_ active-panel]]
    (assoc db :active-page active-panel)))

(rf/reg-event-db :load-swagger-spec
  (fn [db [_ url]]
    (log/debug "Requesting to load spec from url" url)
    (go (swagger/fetch-spec url))
    db))

(rf/reg-event-db :set-swager-spec
  (fn [db [_ spec]]
    (assoc db :swagger spec)))

(rf/reg-event-db :set-schemas
  (fn [db [_ spec]]
    (assoc db :schemas spec)))
