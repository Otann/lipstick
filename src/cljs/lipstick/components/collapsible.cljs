(ns lipstick.components.collapsible
  (:require [reagent.core :as r]
            [lipstick.tools.utils :refer [with-keys join-classes]]))

(defn collapsible
  "Type2 Reagent component that can collapse
  it's content between two labels"
  [{:keys [collapsed class ellipsis tail arrow-class arrow-open arrow-collapsed callback]
    :or {collapsed true
         arrow-open "-"
         arrow-collapsed "+"}}
   _ _]
  (let [collapsed (r/atom collapsed)]
    (fn [_ open-label children]
      ; todo: consider using .no-arrow
      [:div.collapsible
       {:class class}
       [:div.collapsible-label
        {:on-click #(do (swap! collapsed not)
                        (if callback (callback @collapsed)))}
        (when (not-empty children)
          [:span.collapsible-arrow
           {:class arrow-class}
           (str (if @collapsed arrow-collapsed arrow-open))])
        [:span.open-label open-label]
        (when @collapsed [:span.close-label ellipsis tail])]
       [:div.collapsible-children
        {:class (when @collapsed "collapsible-children-collapsed")}
        ; Important!
        ; Do not remove condition to cover
        ; circular dependencies!
        (when-not @collapsed
          [:div.collapsible-children-content
           (if (-> children first seq?)
             (with-keys children)
             children)])
        [:div tail]]])))

(comment
  ; Example of usage
  [collapsible {:collapsed false
                :class "schema"
                :ellipsis "..."
                :tail "}"
                :arrow-class "arrow"}
   "Collapsed paragraph: {"
   [:p "This paragraph is enclosed"]])