(ns lipstick.impl.remote
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [taoensso.timbre :as log])
  (:require [clojure.walk :refer [keywordize-keys]]
            [clojure.string :as str]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cljsjs.js-yaml]
            [re-frame.core :as rf]
            [lipstick.database :as db]))


(defn parse-yaml [string]
  (.safeLoad js/jsyaml string))


(defn get-body [url]
  (go (let [{:keys [status body]} (<! (http/get url))]
        (if (< status 300) body))))


(defn get-yaml-file
  "Important! Must be run inside go-block"
  [url]
  (go (let [{:keys [status body]} (<! (http/get url))]
        (if (< status 300)
          (-> body
              (parse-yaml)
              (js->clj :keywordize-keys true))))))


(defn get-spec-from
  "Returns true if spec was received and dispatched or false otherwise"
  [url]
  (log/debug "Getting spec from" url)
  (go (if-let [body (<! (get-body url))]
        (let [data (if (str/ends-with? url ".yaml")
                     (parse-yaml body)
                     body)
              spec (js->clj data :keywordize-keys true)]
          (log/debug "Received spec from" url "spec:" spec)
          spec)
        (do
          (log/debug "Failed to receive spec from" url)
          nil))))

