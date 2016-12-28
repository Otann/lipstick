(ns lipstick.swagger
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [taoensso.timbre :as log])
  (:require [clojure.walk :refer [keywordize-keys]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cljsjs.js-yaml]
            [re-frame.core :as rf]))


(defn parse-yaml [string]
  (.safeLoad js/jsyaml string))


(defn get-file
  "Important! Must be run inside go-block"
  [url]
  ; TODO: handle error and return null
  (go (let [{:keys [status body]} (<! (http/get url))]
        (if (< status 300)
          (-> body
              (parse-yaml)
              (js->clj :keywordize-keys true))))))

(defn fetch-spec [url]
  (go (when-let [spec (<! (get-file url))]
        (log/debug "Received spec from" url ", dispatching event" spec)
        (rf/dispatch [:set-swager-spec spec]))))

(defn init-spec-async []
  (go (if-let [config (<! (get-file "lipstick.yaml"))]

        (let [{:keys [name src]} (-> config :files first)]
          (log/debug "Loaded config: " (js->clj config))
          (rf/dispatch [:set-config (assoc config :selected name)])
          (fetch-spec src))
        (do
          (log/info "No configuration lipstick.yaml was loaded, fallback to swagger.yaml source")
          (fetch-spec "swagger.yaml")))))

