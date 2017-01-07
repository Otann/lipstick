(ns lipstick.core
  (:require [taoensso.timbre :as log]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [lipstick.routes :as routes]
            [lipstick.views :as views]
            [lipstick.dataflow.effects]
            [lipstick.dataflow.dataflow :as dataflow]
            [lipstick.tools.devenv :as dev]))


(defn mount-root []
  (reagent/render [views/root-view]
                  (.getElementById js/document "app")
                  #(log/debug "Render callback")))


(defn ^:export init []
  (dev/init)
  (rf/dispatch-sync [::dataflow/init])
  (routes/init-routes)
  (log/debug "Completed initialization, mounting root")
  (mount-root))
