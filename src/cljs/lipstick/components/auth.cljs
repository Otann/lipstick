(ns lipstick.components.auth
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [taoensso.timbre :as log])
  (:require [reagent.ratom :as r :include-macros true]
            [taoensso.timbre :as log]
            [re-frame.core :as rf]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn test-auth [config]
  (log/debug "Testing" config)
  (go (let [data (<! (http/get "https://oberyn.mask.zalan.do/"
                               {:with-credentials? false
                                :headers {"Authorization" (str "Bearer " (:access_token config))}}))]
        (log/debug "received data:" data))))


(defn auth-control []
  (let [config (rf/subscribe [:config])
        auth (rf/subscribe [:auth])]
    (fn []
      [:div.auth
       (if @auth
         [:button.btn {:on-click #(test-auth @auth)} "Test"]
         (if-let [auth-config (:auth @config)]
           (if-let [oauth (:oauth auth-config)]
             [:a.btn {:href (str (:url oauth)
                                 "/?client_id=" (:client_id oauth)
                                 "&response_type=token"
                                 "&realm=%2Femployees"
                                 "&scope=uid")}
              "Authorize"])))])))