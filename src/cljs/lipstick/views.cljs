(ns lipstick.views
    (:require [re-frame.core :as rf]
              [lipstick.routes :refer [url-for]]))


(defn home-page []
  (let [name (rf/subscribe [:name])]
    (fn []
      [:div (str "Hello from " @name ". "
                 "This is the Home Page.")
       [:div [:a {:href (url-for :about-page)}
              "go to About Page"]]])))


(defn about-page []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href (url-for :home-page)}
            "go to Home Page"]]]))


(defn show-page
  [panel-name]
  (case panel-name
    :home-page  [home-page]
    :about-page [about-page]
    [:div "404?"]))


(defn main-panel []
  (let [active-page (rf/subscribe [:active-page])]
    (fn [] [show-page @active-page])))
