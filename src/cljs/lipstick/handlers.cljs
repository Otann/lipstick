(ns lipstick.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [re-frame.core :as rf]
            [lipstick.database :as db]
            [lipstick.impl.remote :as remote]
            [taoensso.timbre :as log]))

(rf/reg-event-db :initialize-db
  ; Initializes default db, supposed to be dispathced
  ; on start using (rf/dispatch-sync [:initialize-db])
  (fn [_ _]
    (go (let [config (or (<! (remote/get-yaml-file "lipstick.yaml"))
                         db/default-config)]
          (rf/dispatch [:set-config config])))
    db/default-db))


(rf/reg-event-db :set-active-page
  ; Changes pages for routing
  (fn [db [_ active-panel]]
    (assoc db :active-page active-panel)))


(rf/reg-event-db :set-config
  ; Processes config and initialises loading of a swagger spec
  (fn [db [_ config-data]]
    (let [{:keys [name src]} (-> config-data :files first)
          config (into config-data {:selected name})]
      (go (let [spec (<! (remote/get-spec-from src))]
            (rf/dispatch [:set-swager-spec spec])))
      (assoc db :config config))))

(rf/reg-event-db :load-swagger-spec
  (fn [db [_ url]]
    (go (let [spec (<! (remote/get-spec-from url))]
          (rf/dispatch [:set-swager-spec spec])))
    db))


(rf/reg-event-db :set-swager-spec
  (fn [db [_ spec]]
    (assoc db :spec spec)))





(rf/reg-event-db :set-auth
  (fn [db [_ auth]]
    (rf/dispatch [:set-active-page :home-page])
    (assoc db :auth auth)))
