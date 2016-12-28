(ns lipstick.components.spec
  (:require [markdown.core :refer [md->html]]
            [lipstick.components.collapsible :refer [collapsible]]
            [lipstick.utils :refer [with-keys containsv?]]
            [lipstick.components.schema :refer [schema deref-json]]
            [taoensso.timbre :as log]))

(defn markdown->div
  "Renders markdown string as reagent-compatible text"
  [data]
  [:div.markdown-body
   {:dangerouslySetInnerHTML
    {:__html (-> data md->html)}}])


(def location-icons
  "Symbolic replacements for common locations"
  {"path"     "/../"
   "formData" "form"
   "query"    "?&="
   "body"     "{..}"})


(defn location-row
  "Location parameter for parameters table"
  [value]
  [:td.location.tag
   [:span.label.tooltipped.tooltipped-n
    {:aria-label (str "located in: " value)}
    (or (location-icons value) value)]])


(defn responses
  "Renders list of responses"
  [responses full-spec]
  [:div.responses
   [:div.title "Responses"]
   [:table
    [:tbody
     (-> (for [[code data] responses]
           [:tr.response
            [:td.status.tag [:span.label {:class (str "code" (-> code name first) "xx")} code]]
            [:td.description (:description data)]
            [:td.format [schema "body" (:schema data) full-spec true]]])
         (doall)
         (with-keys))]]])


(defn parameter [{:keys [name in required] :as parameter} full-spec]
  [:tr.parameter
   (location-row in)
   [:td.required
    (when required [:span.tooltipped.tooltipped-n
                    {:aria-label "required"}  "*"])]
   [:td.name name]
   [:td.format
    (when-let [schema-data (:schema parameter)]
      [schema "body" schema-data full-spec true])
    (when-let [type (:type parameter)]
      [:code type])]])


(defn parameters
  "Parameters table for path"
  [parameters full-spec]
  [:div.parameters
   [:div.title "Parameters"]
   [:table
    [:tbody
     (for [param-raw parameters]
       (let [[_ param] (deref-json param-raw full-spec)]
         ^{:key (:name param)}
         [parameter param full-spec]))]]])


(defn path
  "Component that renders endpoint documentation"
  [path-name method spec full-spec]
  [collapsible {:collapsed true
                :class "path"}
   [:span.path-title
    [:code.method {:class method} method] " "
    [:span.path-name (rest (str path-name))]]
   [:div.content
    [:div.summary (:summary spec)]
    [:div.description (:description spec)]
    (when-let [params (:parameters spec)]
      (if (not-empty params)
        [parameters params full-spec]))
    (when-let [resps (:responses spec)]
      [responses resps full-spec])]])




(defn flatten-paths
  "Turns nested path-method structure to plain vector"
  ([all-paths] (flatten-paths all-paths nil))
  ([all-paths predicate]
   (->> (for [[path-name methods] all-paths]
          (for [[method-name path-spec] methods]
            (if (or (not predicate)
                    (predicate path-spec))
              {:method method-name
               :name path-name
               :spec path-spec})))
        (flatten)
        (filter #(not= nil %)))))


(defn paths
  "Renders paths, used in multiple places thus separate"
  [paths-data full-spec]
  [:div.paths
   (doall (for [{:keys [name method spec]} paths-data]
            ^{:key (str name method)}
            [path name method spec full-spec]))])


(defn path-tag
  "Renders multiple paths groupped by a tag"
  [tag-data all-paths full-spec]
  (let [tag-name (:name tag-data)
        paths-data (flatten-paths all-paths
                                  #(containsv? (:tags %) tag-name))]
    [collapsible {:collapsed false
                  :class "tag"}
     [:span.tag-label
      [:span.name tag-name]
      (when-let [description (:description tag-data)]
        [:span.description " : " description])]
     [paths paths-data full-spec]]))


(defn swagger-spec
  "Renders full swagger spec"
  [{:keys [info tags paths] :as spec-data}]
  [:div.spec
   [:h1.title
    [:a.lipstick-logo {:href "https://github.com/Otann/lipstick"} "\uD83D\uDC84 "]
    (:title info)]
   [:div.description (markdown->div (:description info))]
   [:div.meta
    [:ul
     (when-let [version (:version info)]
       [:li [:span.label "Version: "] [:code version]])
     (when-let [contact (:contact info)]
       [:li [:span.label "Contact: "] [:a {:href (str "mailto:" (:email contact))} (:email contact)]])
     (when-let [license (:license info)]
       [:li [:span.label "License: "] [:a {:href (:url license)} (:name license)]])
     (when-let [tos (:termsOfService info)]
       [:li [:span.label [:a {:href tos} "Terms of Service"]]])]]


   (if (not-empty tags)
     [:div.tags
      (doall (for [tag-data tags]
               ^{:key (:name tag-data)}
               [path-tag tag-data paths spec-data]))
      ; Append paths that has no tags assigned
      (let [paths-data (flatten-paths paths #(empty? (:tags %)))]
        (if (not-empty paths-data)
          [path-tag {:name "Without tags"} paths-data spec-data]))]

     [:div.no-tags
      [paths (flatten-paths paths) spec-data]])])


