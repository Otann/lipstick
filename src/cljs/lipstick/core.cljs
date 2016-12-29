(ns lipstick.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [lipstick.routes :as routes]
            [lipstick.views :as views]
            [lipstick.handlers]
            [lipstick.subscriptions]
            [lipstick.impl.dev :as dev]
            [taoensso.timbre :as log]))


(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")
                  #(log/debug "Render callback")))


(defn ^:export init []
  (dev/init)
  (re-frame/dispatch-sync [:initialize-db])
  (routes/init-routes)
  (log/debug "Completed initialization, mounting root")
  (mount-root))
