(ns lipstick.components.spec
  (:require [markdown.core :refer [md->html]]
            [lipstick.components.collapsible :refer [collapsible]]
            [lipstick.utils :refer [with-keys]]
            [lipstick.swagger :as swag]
            [lipstick.components.schema :refer [schema]]))

(defn markdown->div [data]
  [:div {:dangerouslySetInnerHTML
         {:__html (-> data md->html)}}])

(def location-icons {"path"     "/../"
                     "formData" "form"
                     "query"    "?&="
                     "body"     "{...}"})

(defn in-icons [value]
  [:span.label.tooltipped
   (or (location-icons value) value)
   [:span.tip "in: " value]])

(defn parameters [parameters]
  [:div.parameters
   [:div.title "Parameters"]
   [:table
    [:tbody
     (for [{:strs [name in required] :as parameter} parameters]
       ^{:key name}
       [:tr.parameter
        [:td.location (in-icons in)]
        [:td.required
         (when required [:span.tooltipped "*" [:span.tip "required"]])]
        [:td.name name]
        [:td.format
         (when-let [schema-data (parameter "schema")]
           (str schema-data))
         (when-let [type (parameter "type")]
           [:code type])]])]]])

(defn path [name method spec]
  (let [params (get spec "parameters")]
    [collapsible {:collapsed true
                  :class "path"}
     [:span.path-title
      [:code.method {:class method} method] " "
      [:span.path-name name]]
     [:div.content
      [:div.summary (get spec "summary")]
      [:div.description (get spec "description")]
      (when (not-empty params)
        [parameters params])]]))

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
        description (get tag-data "description")
        by-tag-name #(array-contains? (get % "tags") tag-name)
        paths (filter-paths all-paths by-tag-name)]
    [collapsible {:ellipsis nil
                  :collapsed false
                  :class "tag"}
     [:span.tag-label
      [:span.name tag-name]
      (when description
        [:span.description " : " description])]
     [:div.paths
      (doall (for [{:keys [name method spec]} paths]
               ^{:key (str name method)}
               [path name method spec]))]]))

(defn swagger-spec [spec-data]
  (let [title (get-in spec-data ["info" "title"])
        description (get-in spec-data ["info" "description"])
        tags (get spec-data "tags")
        all-paths (get spec-data "paths")
        definitions (get spec-data "definitions")
        schemas (map swag/definition->schema definitions)]
    [:div.spec
     [:h1 "\uD83D\uDC84 " title]
     [:div (markdown->div description)]

     (if (not-empty tags)
       [:div.tags
        #_[:h2 "By tags:"]
        (doall (for [tag-data tags]
                 ^{:key (get tag-data "name")}
                 [tag tag-data all-paths]))
        ; Append paths that has no tags assigned
        (let [paths (filter-paths all-paths
                                  #(empty? (get % "tags")))]
          (if (not-empty paths)
            [:h2 "No tags:" [tag {"name" "Without tags"} paths]]))]
       [:div.no-tags
        (doall (for [{:keys [name method spec]}
                     (filter-paths all-paths #(do true))]
                 ^{:key (str name method)}
                 [path name method spec]))])

     [:div.definitions
      [:h2.title "Definitions"]
      (doall (for [schema-data schemas]
               ^{:key (:name schema-data)}
               [schema schema-data]))]]))


