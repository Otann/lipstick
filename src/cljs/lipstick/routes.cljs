(ns lipstick.routes
  (:import goog.History)
  (:require-macros [secretary.core :refer [defroute]]
                   [taoensso.timbre :as log])
  (:require [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as rf]
            [clojure.string :refer [blank? starts-with?]]
            [clojure.test :refer [function?]]
            [bidi.bidi :as bidi]))


(def routes ["#/" {""      :home-page
                    "about" :about-page}])


(defn dispatch-path [dispatch-fn path]
  (let [handler (->> path
                     (bidi/match-route routes)
                     (:handler))]
    (dispatch-fn [:set-active-page handler])))


(defn navigate-hook [event]
  (let [raw-path (-> event .-token)
        path (if (blank? raw-path)
               "#/" (str "#" raw-path) )]
    (dispatch-path rf/dispatch path)))


(defn navigate-sync [window]
  (let [raw-path (-> window .-location .-hash)
        path (if (blank? raw-path)
               "#/" raw-path)]
    (dispatch-path rf/dispatch-sync path)))


(defn init-routes []
  (navigate-sync js/window)

  (log/debug "Hooking into browser navigation")
  (doto (History.)
    (events/listen EventType/NAVIGATE navigate-hook)
    (.setEnabled true)))


(def url-for (partial bidi/path-for routes))