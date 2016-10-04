(ns lipstick.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]))


(rf/reg-sub :name
  (fn [db]
    (:name db)))


(rf/reg-sub :active-page
  (fn [db _]
    (:active-page db)))
