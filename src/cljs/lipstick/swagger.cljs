(ns lipstick.swagger
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [taoensso.timbre :as log])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cljsjs.js-yaml]
            [re-frame.core :as rf]))


(defn parse-yaml [string]
  (.safeLoad js/jsyaml string))


(defn init-spec []
  (go (let [response (<! (http/get "/swagger.yaml"))
            swagger (-> response
                        :body
                        parse-yaml
                        (js->clj :keywordize-keys true))]
        (log/debug "Loaded swagger spec:" swagger)
        (rf/dispatch [:set-swager-spec swagger]))))
