(ns lipstick.dataflow.sources
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf :refer [debug]]
            [taoensso.timbre :as log]
            [clojure.string :as str]
            [lipstick.tools.utils :as u]))


(def default-db {::files [{:name "Petstore"
                           :src "swagger.yaml"
                           :loading? false
                           :content nil}]})

(rf/reg-sub ::content
  ; (rf/subscribe [::sources/content] [idx])
  ; where idx is subscription
  (fn [db _ [idx]]
    (get-in db [::files idx :content])))

(rf/reg-sub ::names
  (fn [db _]
    (map-indexed (fn [n f] {:idx n
                            :name (:name f)})
                 (::files db))))

(rf/reg-event-db ::set-files
  ;[debug]
  (fn [db [_ files]]
    (assoc db ::files files)))

(rf/reg-event-fx ::ensure-content
  ;[debug]
  (fn [{:keys [db]} [_ idx]]
    (let [{:keys [content loading? src]} (get-in db [::files idx])
          load? (or content (not loading?))]
      (if load?
        {:db (assoc-in db [::files idx :loading?] true)
         :load-file {:url src
                     :on-success [::set-content idx]
                     :on-failure [::set-content-fail idx]}}
        {}))))

(defn parse-content [filename body]
  (let [js-data (if (str/ends-with? filename ".yaml")
                  (u/parse-yaml body) body)
        spec-data (js->clj js-data :keywordize-keys true)]
    spec-data))

(rf/reg-event-db ::set-content
  [debug]
  (fn [db [_ idx body]]
    (let [src (get-in db [::files idx :src])]
      (-> db
          (assoc-in [::files idx :loading?] false)
          (assoc-in [::files idx :content] (parse-content src body))))))

(rf/reg-event-db ::set-content-fail
  (fn [db [_ idx]]
    (assoc-in db [::files idx :loading?] false)))
