(ns lipstick.rfnext.handlers
  (:require [lipstick.reframe.database :as db]
            [re-frame.core :as rf :refer [debug]]
            [lipstick.tools.utils :as u]
            [lipstick.rfnext.sources :as sources]
            [lipstick.rfnext.source-selector :as selector]
            [lipstick.rfnext.auth :as auth]))

(rf/reg-event-fx ::init
  ; Initializes default db, supposed to be dispathced
  ; on start using (rf/dispatch-sync [:initialize])
  ;[debug]
  (fn [_ _]
    {:db db/default-db
     :load-file {:url "lipstick.yaml"
                 :on-success [::receive-config]
                 :on-failure [::receive-config nil]}}))

(defn parse-config [data]
  (-> data
      (u/parse-yaml)
      (js->clj :keywordize-keys true)))

(rf/reg-event-fx ::receive-config
  ;[debug]
  (fn [_ [_ config-data]]
    (let [config (if config-data
                   (parse-config config-data)
                   db/default-config)]
      {:dispatch-all [[::sources/set-files (:files config)]
                      [::auth/set (-> config :auth :oauth)]
                      [::selector/select 0]]})))