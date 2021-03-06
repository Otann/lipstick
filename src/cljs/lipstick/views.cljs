(ns lipstick.views
  (:require [re-frame.core :as rf]
            [lipstick.routes :refer [url-for]]
            [lipstick.tools.utils :refer [with-keys]]
            [lipstick.components.spec :refer [selected-spec]]
            [lipstick.components.schema :refer [schema]]
            [lipstick.components.source :refer [source]]
            [lipstick.components.auth :refer [auth-control]]
            [taoensso.timbre :as log]))


(defn home-page []
  [:div.container
   [:div.controls
    [auth-control]
    [source]]
   [selected-spec]])


(defn about-page []
  (let [spec-data (rf/subscribe [:spec])]
    (fn []
      [:div
       [:p [:a {:href (url-for :home-page)} "back to main page"]]
       [:div
        [:h1 "This is an visualization of collapsible schemas"]
        [:h2 "Definitions from /swagger.yaml"]
        (doall (for [[schema-name schema-data] (:definitions @spec-data)]
                 ^{:key schema-name}
                 [schema schema-name schema-data @spec-data]))]])))


(defn root-view []
  (let [active-page (rf/subscribe [:active-page])]
    (fn []
      (case @active-page
        :root       [home-page]
        :home-page  [home-page]
        :about-page [about-page]
        :auth       [:p "auth page"]
        [:div (str "404? - " @active-page)]))))
