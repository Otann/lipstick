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


(defn type->schema [type] type)


(defn swag->property [name data required-set]
  (merge (keywordize-keys data)
         {:required (contains? required-set name)
          :schema (type->schema (get data "type"))}))

(defn definition->schema [[name swag-def]]
  (let [required-set (-> swag-def
                         (get "required")
                         (set))
        properties (doall (for [[name data] (get swag-def "properties")]
                            [name (swag->property name data required-set)]))]
    {:name name
     :meta {:allOf (get swag-def "allOf")}
     :properties properties
     ; todo: make sure default is object
     :type (or (-> swag-def (get "type") keyword)
               :object)}))


(defn init-spec []
  (go (let [response (<! (http/get "/swagger.yaml"))
            spec (-> response
                        :body
                        parse-yaml
                        js->clj)
            definitions (get spec "definitions")
            schemas (map definition->schema definitions)]
        (js/setTimeout #(do
                         (log/debug "Definitions:" (get spec "definitions"))
                         (log/debug "Schemas:" schemas))
                       1)
        (log/debug "Loaded swagger spec:" spec)
        (rf/dispatch [:set-swager-spec {:original spec
                                        :schemas schemas}]))))
