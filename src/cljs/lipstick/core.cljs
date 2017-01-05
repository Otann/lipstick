(ns lipstick.core
  (:require [taoensso.timbre :as log]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [lipstick.reframe.handlers]
            [lipstick.reframe.effects]
            [lipstick.reframe.subscriptions]
            [lipstick.routes :as routes]
            [lipstick.views :as views]
            [lipstick.tools.devenv :as dev]))


(defn mount-root []
  (reagent/render [views/root-view]
                  (.getElementById js/document "app")
                  #(log/debug "Render callback")))


(defn ^:export init []
  (dev/init)
  (rf/dispatch-sync [:initialize])
  (routes/init-routes)
  (log/debug "Completed initialization, mounting root")
  (mount-root))
