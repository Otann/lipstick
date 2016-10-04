(ns lipstick.views
  (:require [re-frame.core :as rf]
            [lipstick.routes :refer [url-for]]
            [taoensso.timbre :as log]))


(defn home-page []
  (let [spec (rf/subscribe [:spec])]
    (fn []
      [:div (str "This is the Home Page.")
       [:div [:a {:href (url-for :about-page)}
              "go to About Page"]]])))


(defn about-page []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href (url-for :home-page)}
            "go to Home Page"]]]))


(defn show-page
  [page-name]
  (case page-name
    :root       [home-page]
    :home-page  [home-page]
    :about-page [about-page]
    [:div (str "404? - " page-name)]))


(defn main-panel []
  (let [active-page (rf/subscribe [:active-page])]
    (fn [] [show-page @active-page])))
