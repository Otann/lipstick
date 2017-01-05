(ns lipstick.reframe.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [taoensso.timbre :as log]))


(rf/reg-sub :spec
  (fn [db]
    (:spec db)))

(rf/reg-sub :active-page
  (fn [db _]
    (:active-page db)))

(rf/reg-sub :config
  (fn [db _]
    (:config db)))

(rf/reg-sub :auth
  (fn [db _]
    (:auth db)))
