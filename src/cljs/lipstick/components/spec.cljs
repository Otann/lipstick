(ns lipstick.components.spec
  (:require [markdown.core :refer [md->html]]
            [taoensso.timbre :as log]
            [lipstick.components.collapsible :refer [collapsible]]
            [lipstick.utils :refer [with-keys]]))

(defn markdown->div [data]
  [:div {:dangerouslySetInnerHTML
         {:__html (-> data md->html)}}])

(defn path [name method spec]
  [collapsible {:collapsed true}
   [:span.path-title
    [:span.method method] " "
    [:span.path-name name]]
   [:pre (str spec)]])

(defn array-contains? [arr val]
  (some #(= val %) arr))


(defn filter-paths [all-paths predicate]
  (->> (for [[path-name methods] all-paths]
         (for [[method-name path-spec] methods]
           (if (predicate path-spec)
             {:method method-name
              :name path-name
              :spec path-spec})))
       (flatten)
       (filter #(not= nil %))))


(defn tag [tag-data all-paths]
  (let [tag-name (get tag-data "name")
        by-tag-name #(array-contains? (get % "tags") tag-name)
        paths (filter-paths all-paths by-tag-name)]
    [collapsible {:ellipsis nil
                       :collapsed false
                       :class "no-arrow"}
     [:span.name tag-name]
     [:div.paths
      (doall (for [{:keys [name method spec]} paths]
               ^{:key (str name method)}
               [path name method spec]))]]))

(defn swagger-spec [spec-data]
  (let [title (get-in spec-data ["info" "title"])
        description (get-in spec-data ["info" "description"])
        all-paths (get spec-data "paths")]
    [:div.spec
     [:h1 title]
     [:div (markdown->div description)]
     [:h2 "By tags:"]
     (doall (for [data (get spec-data "tags")]
              ^{:key (get data "name")} [tag data all-paths]))
     (let [paths (filter-paths all-paths
                               #(empty? (get % "tags")))]
       (if (not-empty paths)
         [:h2 "No tags:" [tag {"name" "Without tags"} paths]]))]))


