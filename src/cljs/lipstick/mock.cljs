(ns lipstick.mock)


(def Int {:name "Int"
          :type :primitive})

(def String {:name "String"
             :type :primitive})

(def Gender {:name "Gender"
             :type :enum
             :values ["Male", "Female"]})


(def SiblingEnum {:name "SiblingType"
                  :type :enum
                  :values ["Sister" "Brother"]})


(def Address {:name "Address"
              :type :object
              :fields [{:name "city" :schema String}
                       {:name "street" :schema String}
                       {:name "zip" :schema String}]})

(def Sibling {:name "Sibling"
              :type :object
              :fields [{:name "first_name" :schema String}
                       {:name "last_name" :schema String}
                       {:name "relation" :schema SiblingEnum}]})


(def Person {:name "Person"
             :type :object
             :fields [{:name "first_name" :schema :string}
                      {:name "last_name" :schema String}
                      {:name "gender" :schema Gender :optional true}
                      {:name "age" :schema String :optional true}
                      {:name "home" :schema Address}
                      {:name "sibs" :schema {:type :array :items Sibling} :optional true}]})
