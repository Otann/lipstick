(ns lipstick.components.tree-view
  (:require [taoensso.timbre :as log :include-macros true]
            [reagent.core :as r]))

(defn tree [label & children]
  (let [collapsed (r/atom false)]
    (fn []
      [:div.tree-view
       [:div.tree-view_item
        [:div.tree-view_arrow
         {:class (when @collapsed "tree-view_arrow-collapsed")
          :on-click #(swap! collapsed not)}]
        label (str " - " @collapsed)]
       [:div.tree-view_children
        {:class (when @collapsed "tree-view_children-collapsed")}
        (map-indexed #(with-meta %2 {:key %1})
                     children)]])))