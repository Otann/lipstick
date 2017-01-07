(ns lipstick.rfnext.auth
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [taoensso.timbre :as log]
            [clojure.string :as str]
            [lipstick.tools.utils :as u]))

(def default-db {::data nil})

(rf/reg-event-db ::set
  (fn [db [_ data]]
    (assoc db ::data data)))


