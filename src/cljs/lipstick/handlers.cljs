(ns lipstick.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [re-frame.core :as rf]
            [lipstick.database :as db]
            [lipstick.impl.remote :as remote]
            [taoensso.timbre :as log]))


(rf/reg-event-db :initialize-db
  (fn [_ _]
    (go (let [config (<! (remote/get-config))]
          (rf/dispatch [:set-config config])))
    db/default-db))


(rf/reg-event-db :set-active-page
  (fn [db [_ active-panel]]
    (assoc db :active-page active-panel)))


(rf/reg-event-db :load-swagger-spec
  (fn [db [_ url]]
    (go (let [spec (<! (remote/get-spec-from url))]
          (rf/dispatch [:set-swager-spec spec])))
    db))


(rf/reg-event-db :set-swager-spec
  (fn [db [_ spec]]
    (log/debug "Setting spec")
    (assoc db :spec spec)))


(rf/reg-event-db :set-config
  (fn [db [_ config-data]]
    (let [{:keys [name src]} (-> config-data :files first)
          config (into config-data {:selected name})]
      (go (let [spec (<! (remote/get-spec-from src))]
            (log/debug "Dispatching spec")
            (rf/dispatch [:set-swager-spec spec])))
      (assoc db :config config))))