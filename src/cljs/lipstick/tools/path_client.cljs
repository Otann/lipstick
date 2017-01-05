(ns lipstick.tools.path-client
  (:require [reagent.ratom :as r :include-macros true]
            [taoensso.timbre :as log :include-macros true]
            [clojure.string :as str]
            [lipstick.tools.utils :refer [deref-json]]))

(defn replace-path-params
  "Replaces {parameter} in path with current ratom
  :state of the corresponding parameter in the map"
  [path params]
  (if-let [param (first params)]
    (let [match (str "{" (:name param) "}")
          value @(:state param)]
      (replace-path-params (str/replace path match value)
                           (rest params)))
    path))


(defn perform-call
  "Callback to perform call to provided path"
  [method path-name full-spec params]
  (let [base (:basePath full-spec)
        param-groups (group-by :in params)
        path (replace-path-params (subs (str path-name) 1)
                                  (get param-groups "path"))
        query (for [param (get param-groups "query")]
                (let [value @(:state param)]
                  (if-not (str/blank? value)
                    [(:name param) value])))]
    (log/debug "Calling" method "-" (str base path) "with query" (into {} query))))


(defn make-client [method path-name path-spec full-spec]
  (let [{:keys [parameters responses]} path-spec
        params-with-state (for [param parameters]
                            (let [[_ schema] (deref-json param full-spec)]
                              (assoc schema :state (r/atom ""))))
        resp-atom (r/atom responses)
        state (r/atom :ready)
        callback #(perform-call method path-name full-spec params-with-state)]
    [params-with-state resp-atom state callback]))