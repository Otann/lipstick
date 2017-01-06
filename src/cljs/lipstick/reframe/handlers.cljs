(ns lipstick.reframe.handlers
  "Handlers that modify database
  Grouped by prefixes:
  :ui-       actions initialized by UI elements
  :receive-  received data from network or from browser"
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [re-frame.core :as rf]
            [re-frame.core :refer [debug path]]
            [lipstick.reframe.database :as db]
            [taoensso.timbre :as log]
            [lipstick.tools.utils :as u]
            [clojure.string :as str]))


(rf/reg-event-fx :initialize
  ; Initializes default db, supposed to be dispathced
  ; on start using (rf/dispatch-sync [:initialize])
  (fn [_ _]
    {:db db/default-db
     :load-file {:url "lipstick.yaml"
                 :on-success [:receive-config]
                 :on-failure [:receive-config nil]}}))


(rf/reg-event-fx :receive-config
  ;[debug]
  (fn [{:keys [db]} [_ config-data]]
    (let [config (if config-data
                   (-> config-data
                       (u/parse-yaml)
                       (js->clj :keywordize-keys true))
                   db/default-config)
          specs (map-indexed (fn [idx itm] (assoc itm :id idx))
                             (:files config))]
      (log/debug "Specs" specs)
      {:db (-> db
               (assoc :config config)
               (assoc :specs (into [] specs)))
       :dispatch [:ui-selected-spec (-> specs first :id)]})))


(rf/reg-event-fx :ui-selected-spec
  ; Marks spec as selected in database and reloads data
  ;[debug]
  (fn [{:keys [db]} [_ spec-idx]]
    (let [{:keys [src data]} (get-in db [:specs spec-idx])]
      {:db (-> db
               (assoc-in [:ui :selected-spec-id] spec-idx)
               (assoc-in [:specs spec-idx :loading] true))
       :load-file (if (nil? data)
                    {:url src
                     :on-success [:receive-spec-data-ok spec-idx]
                     :on-failure [:receive-spec-data-ko spec-idx]})})))


(rf/reg-event-db :receive-spec-data-ok
  ; Sets spec data to the database
  ;[debug]
  (fn [db [_ spec-idx body]]
    (let [spec    (get-in db [:specs spec-idx])
          js-data (if (str/ends-with? (:src spec) ".yaml")
                    (u/parse-yaml body) body)
          spec-data (js->clj js-data :keywordize-keys true)]
      (-> db
          (assoc-in [:specs spec-idx :loading] false)
          (assoc-in [:specs spec-idx :data] spec-data)))))


(rf/reg-event-db :receive-spec-data-ko
  (fn [db [_ spec-idx body]]
    (log/error "Failed to load spec:" (get-in db [:specs spec-idx]) body)
    (assoc-in db [:specs spec-idx :loading] false)))


(rf/reg-event-db :set-auth
  (fn [db [_ auth]]
    ; TODO: should not do redirect to home here
    (-> db
        (assoc :auth auth)
        (assoc :active-page :home-page))))


(rf/reg-event-db :set-active-page
  ; Changes pages for routing
  (fn [db [_ active-panel]]
    (assoc db :active-page active-panel)))


(rf/reg-event-db :toggle-tag-collapsible
  (fn [db [_ spec-id tag-name]]
    (update-in db [:ui
                   :spec
                   spec-id
                   :tags
                   tag-name
                   :collapsed]
               not)))

(rf/reg-event-db :toggle-path-collapsible
  (fn [db [_ spec-id tag-name method name]]
    (update-in db [:ui
                   :spec
                   spec-id
                   :tags
                   tag-name
                   :paths
                   [method name]
                   :collapsed]
               not)))