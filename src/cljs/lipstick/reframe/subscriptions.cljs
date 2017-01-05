(ns lipstick.reframe.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [taoensso.timbre :as log]))


(rf/reg-sub :active-page
  (fn [db _]
    (:active-page db)))

(rf/reg-sub :config
  (fn [db _]
    (:config db)))

(rf/reg-sub :auth
  (fn [db _]
    (:auth db)))


(rf/reg-sub :specs
  (fn [db _]
    (:specs db)))


(rf/reg-sub :selected-spec
  (fn [db _]
    (let [idx (-> db :ui :selected-spec-id)]
      (get-in db [:specs idx :data]))))


(rf/reg-sub :ui-state
  (fn [db event]
    (get-in db (into [:ui] (rest event)))))