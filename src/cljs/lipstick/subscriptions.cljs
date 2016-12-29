(ns lipstick.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [taoensso.timbre :as log]))


(rf/reg-sub :spec
  (fn [db]
    ;(log/debug "Getting :spec")
    (:spec db)))

(rf/reg-sub :active-page
  (fn [db _]
    ;(log/debug "Getting :active-page")
    (:active-page db)))

(rf/reg-sub :config
  (fn [db _]
    ;(log/debug "Getting :config")
    (:config db)))
