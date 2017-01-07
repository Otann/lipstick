(ns lipstick.views
  (:require [re-frame.core :as rf]
            [lipstick.routes :refer [url-for]]
            [lipstick.tools.utils :refer [with-keys]]
            [lipstick.components.spec :refer [spec]]
            [lipstick.components.schema :refer [schema]]
            [lipstick.components.source :refer [source]]
            [lipstick.components.auth :refer [auth-control]]
            [lipstick.dataflow.active-page :as active-page]))


(defn home-page []
  [:div.container
   [:div.controls
    [auth-control]
    [source]]
   [spec]])


(defn root-view []
  (let [active-page (rf/subscribe [::active-page/page])]
    (fn []
      (case @active-page
        :root       [home-page]
        :home-page  [home-page]
        :auth       [:p "auth page"]
        [:div (str "404? - " @active-page)]))))
