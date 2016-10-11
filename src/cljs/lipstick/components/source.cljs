(ns lipstick.components.source
  (:require [reagent.ratom :as r]
            [taoensso.timbre :as log]
            [re-frame.core :as rf]))

(defn atom-input [atom meta]
  [:input (into meta
                {:type "text"
                 :value @atom
                 :on-change #(reset! atom (-> % .-target .-value))})])

(defn source []
  (let [url (r/atom "swagger.yaml")
        on-click #(do (rf/dispatch [:load-swagger-spec @url])
                      (.preventDefault %))]
    (fn []
      [:form
       [:div.input-group
        [atom-input url {:class "form-control"
                         :type "text"
                         :id "source"}]
        [:div.input-group-button
         [:button.btn {:type "submit"
                       :on-click on-click} "Load Spec"]]]])))