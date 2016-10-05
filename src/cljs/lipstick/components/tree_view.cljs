(ns lipstick.components.tree-view
  (:require [reagent.core :as r]
            [lipstick.utils :refer [with-keys]]))

(defn tree []
  (let [collapsed (r/atom false)]
    (fn [label & children]
      [:div.tree-view
       [:div.tree-view_item
        [:div.tree-view_arrow
         {:class (when @collapsed "tree-view_arrow-collapsed")
          :on-click #(swap! collapsed not)}]
        label]
       [:div.tree-view_children
        {:class (when @collapsed "tree-view_children-collapsed")}
        (with-keys children)]])))