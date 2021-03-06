(ns lipstick.components.auth
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [taoensso.timbre :as log])
  (:require [reagent.ratom :as r :include-macros true]
            [taoensso.timbre :as log]
            [re-frame.core :as rf]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cemerick.url :refer [url-encode]]))

(defn test-auth [config]
  (log/debug "Testing" config)
  (go (let [data (<! (http/get "https://catalog-cors.mask.zalan.do/config-debug"
                               {:with-credentials? false
                                :headers {"Authorization" (str "Bearer " (:access_token config))}}))]
        (log/debug "received data:" data))))


(defn auth-control []
  (let [config (rf/subscribe [:config])
        auth (rf/subscribe [:auth])]
    (fn []
      [:div.auth
       (if @auth
         [:span.octicon.octicon-lock]
         (if-let [auth-config (:auth @config)]
           (if-let [oauth (:oauth auth-config)]
             [:a.btn {:href (str (:auth_url oauth)
                                 "?client_id=" (:client_id oauth)
                                 "&response_type=token"
                                 "&realm=" (url-encode (:realm oauth))
                                 "&redirect_uri=" (url-encode (:redirect_url oauth))
                                 "&scope=" (url-encode (:scope oauth))
                                 )}
              "Authorize"])))])))