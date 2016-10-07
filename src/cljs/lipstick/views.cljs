(ns lipstick.views
  (:require [re-frame.core :as rf]
            [lipstick.routes :refer [url-for]]
            [lipstick.components.schema-view :refer [schema field]]
            [lipstick.mock :as m]))

(defn home-page []
  (let [spec (rf/subscribe [:spec])]
    (fn []
      [:div
       [:h1 "This is an example of collapsible schemas"]
       [:p [:a {:href (url-for :about-page)} "About Page"]]

       [:h3 "Object Example"]
       [schema m/Person]

       [:h3 "Array Example"]
       [schema {:name "Siblings" :type :array :items m/Sibling}]

       [:h3 "Enum Example"]
       [schema m/Gender]

       [:h3 "Primitive Examples"]
       [schema m/String]
       [schema m/Int]
       [schema :int64]

       [:h3 "p.s. This project can also parse yaml!"]
       [:code (str @spec)]])))


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
