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

(defn swag->schema [name swag-def]
  (log/debug "swag->schema" name swag-def)
  (if-let [reference (swag-def "$ref")]
    {:type :reference :name (second (re-find #"#/definitions/(.*)" reference))}

    (let [required-set (-> swag-def
                           (get "required")
                           (set))
          swag->property (fn [name data required-set]
                           (merge (keywordize-keys data)
                                  {:required (contains? required-set name)
                                   :schema (swag->schema nil data)}))

          properties (for [[name data] (get swag-def "properties")]
                       [name (swag->property name data required-set)])]
      {:name name
       :meta {:allOf (get swag-def "allOf")}
       :properties properties
       :type (-> swag-def (get "type") keyword)})))


(defn init-spec []
  (go (let [response (<! (http/get "swagger.yaml"))
            spec (-> response
                        :body
                        parse-yaml
                        js->clj)]
        (rf/dispatch [:set-swager-spec spec]))))
