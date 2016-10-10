(ns lipstick.components.spec
  (:require [markdown.core :refer [md->html]]
            [lipstick.components.collapsible :refer [collapsible]]
            [lipstick.utils :refer [with-keys]]
            [lipstick.swagger :as swag]
            [lipstick.components.schema :refer [schema]]
            [taoensso.timbre :as log]))

(defn markdown->div [data]
  [:div {:dangerouslySetInnerHTML
         {:__html (-> data md->html)}}])

(def location-icons {"path"     "/../"
                     "formData" "form"
                     "query"    "?&="
                     "body"     "{..}"})

(defn location-row [value]
  [:td.location.tooltipped
   [:span.tip "located in: " value]
   [:span.label (or (location-icons value) value)]])

(defn schema-element [data]
  (if-let [ref (data "$ref")]
    (let []
      [:span (str "This is reference to " ref)])
    [:span "This is root"]))

(defn parameters [parameters]
  [:div.parameters
   [:div.title "Parameters"]
   [:table
    [:tbody
     (for [{:strs [name in required] :as parameter} parameters]
       ^{:key name}
       [:tr.parameter
        (location-row in)
        [:td.required.tooltipped
         (when required [:span "*" [:span.tip "required"]])]
        [:td.name name]
        [:td.format
         (when-let [schema-data (parameter "schema")]
           [schema-element schema-data])
         (when-let [type (parameter "type")]
           [:code type])]])]]])

(defn path [name method spec]
  (let [params (get spec "parameters")]
    [collapsible {:collapsed false
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


(defn paths [paths-data]
  [:div.paths
   (doall (for [{:keys [name method spec]} paths-data]
            ^{:key (str name method)}
            [path name method spec]))])

(defn tag [tag-data all-paths all-schemas]
  (let [tag-name (get tag-data "name")
        description (get tag-data "description")
        by-tag-name #(array-contains? (get % "tags") tag-name)
        paths-data (filter-paths all-paths by-tag-name)]
    [collapsible {:ellipsis nil
                  :collapsed false
                  :class "tag"}
     [:span.tag-label
      [:span.name tag-name]
      (when description
        [:span.description " : " description])]
     [paths paths-data]]))

(defn swagger-spec [spec-data]
  (let [title (get-in spec-data ["info" "title"])
        description (get-in spec-data ["info" "description"])
        tags (get spec-data "tags")
        all-paths (get spec-data "paths")
        definitions (get spec-data "definitions")
        all-schemas (->> definitions
                         (map #(apply swag/swag->schema %))
                         (map #(vector (:name %) %))
                         (into {}))]
    [:div.spec
     [:h1 "\uD83D\uDC84 " title]
     [:div (markdown->div description)]

     (if (not-empty tags)
       [:div.tags
        (doall (for [tag-data tags]
                 ^{:key (get tag-data "name")}
                 [tag tag-data all-paths all-schemas]))
        ; Append paths that has no tags assigned
        (let [paths-data (filter-paths all-paths
                                       #(empty? (get % "tags")))]
          (if (not-empty paths-data)
            [tag {"name" "Without tags"} paths-data all-schemas]))]
       [:div.no-tags
        [paths (filter-paths all-paths #(do true))]])

     [:div.definitions
      [:h2.title "Definitions"]
      (log/debug all-schemas)
      (doall (for [[_ schema-data] all-schemas]
               ^{:key (:name schema-data)}
               [schema schema-data all-schemas]))]]))


