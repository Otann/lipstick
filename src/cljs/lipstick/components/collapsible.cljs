(ns lipstick.components.collapsible
  (:require [reagent.core :as r]
            [lipstick.utils :refer [with-keys]]))

(defn collapsible
  "Type2 Reagent component that can collapse
  it's content between two labels"
  [{:keys [collapsed class ellipsis tail]
    :or {collapsed true}}
   _ _ _]
  (let [collapsed (r/atom collapsed)]
    (fn [_ open-label children]
      ; todo: consider using .no-arrow
      [:div.collapsible
       {:class class}
       [:div.collapsible-view_item
        {:on-click #(swap! collapsed not)}
        (when (not-empty children)
          [:div.collapsible-view_arrow
           {:class (when @collapsed "collapsible-view_arrow-collapsed")}])
        [:span.open-label open-label]
        (when @collapsed [:span.close-label ellipsis tail])]
       [:div.collapsible-view_children
        {:class (when @collapsed "collapsible-view_children-collapsed")}
        [:div.collapsible-view_children-content
         (if (-> children first seq?)
           (with-keys children)
           children)]
        [:div tail]]])))