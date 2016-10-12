(ns lipstick.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [devtools.core :as devtools]
            [lipstick.routes :as routes]
            [lipstick.views :as views]
            [lipstick.config :as config]
            [lipstick.swagger :as swagger]
            [lipstick.handlers]
            [lipstick.subscriptions]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")
    (devtools/install! [:formatters :hints])))


(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))


(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (routes/init-routes)
  (swagger/init-spec-async)
  (dev-setup)
  (mount-root))
