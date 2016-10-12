(ns lipstick.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]))


(rf/reg-sub :spec
  (fn [db]
    (:swagger db)))

(rf/reg-sub :schemas
  (fn [db]
    (:schemas db)))

(rf/reg-sub :active-page
  (fn [db _]
    (:active-page db)))

(rf/reg-sub :config
  (fn [db _]
    (:config db)))
