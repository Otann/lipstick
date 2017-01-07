(ns lipstick.dataflow.dataflow
  (:require [re-frame.core :as rf :refer [debug]]
            [lipstick.tools.utils :as u]
            [lipstick.dataflow.sources :as sources]
            [lipstick.dataflow.auth :as auth]))

(def default-config
  {:files [{:name "Service Spec"
            :src "swagger.yaml"}]})

(def default-db
  (merge
    lipstick.dataflow.spec-ui/default-db
    lipstick.dataflow.sources/default-db
    lipstick.dataflow.active-page/default-db
    lipstick.dataflow.source-selector/default-db))

(rf/reg-event-fx ::init
  ; Initializes default db, supposed to be dispathced
  ; on start using (rf/dispatch-sync [:initialize])
  ;[debug]
  (fn [_ _]
    {:db default-db
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
                   default-config)]
      {:dispatch-n [[::auth/set-config (-> config :auth :oauth)]
                    [::sources/set-files (:files config)]
                    [::sources/ensure-content 0]]})))

