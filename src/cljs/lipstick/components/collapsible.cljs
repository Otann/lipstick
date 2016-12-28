(ns lipstick.components.collapsible
  (:require [reagent.core :as r]
            [lipstick.utils :refer [with-keys join-classes]]))

(defn collapsible
  "Type2 Reagent component that can collapse
  it's content between two labels"
  [{:keys [collapsed class ellipsis tail arrow-class arrow-open arrow-collapsed]
    :or {collapsed true
         arrow-open "-"
         arrow-collapsed "+"}}
   _ _]
  (let [collapsed (r/atom collapsed)]
    (fn [_ open-label children]
      ; todo: consider using .no-arrow
      [:div.collapsible
       {:class class}
       [:div.collapsible-view_item
        {:on-click #(swap! collapsed not)}
        (when (not-empty children)
          [:div.collapsible-view_arrow
           {:class arrow-class}
           (str (if @collapsed arrow-collapsed arrow-open) " ")])
        [:span.open-label open-label]
        (when @collapsed [:span.close-label ellipsis tail])]
       [:div.collapsible-view_children
        {:class (when @collapsed "collapsible-view_children-collapsed")}
        ; Important!
        ; Do not remove condition to cover
        ; circular dependencies!
        (when-not @collapsed
          [:div.collapsible-view_children-content
           (if (-> children first seq?)
             (with-keys children)
             children)])
        [:div tail]]])))

(comment

  [collapsible {:collapsed false
                :class "schema"
                :ellipsis "..."
                :tail "}"
                :arrow-class "arrow"}
   "{"
   [:p "This paragraph is enclosed in {}"]]

  )