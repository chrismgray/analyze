(ns analyze.util
  (:require [clojure.pprint :as pp]))

(defn- dissoc-rec [obj & keys]
  (cond
   (map? obj) (into {} (for [[key val] (apply dissoc obj keys)]
                         [key (apply dissoc-rec val keys)]))
   (sequential? obj) (map #(apply dissoc-rec % keys) obj)
   :else obj))

(defn print-expr
  "Pretty-prints expr, excluding supplied keys.
  Example: (print-expr expr :children :env)"
  [expr & exclusions]
  (pp/pprint (apply dissoc-rec expr exclusions)))

(defn expr-seq
  "Given an expression, returns a lazy sequence of the expressions
  followed by its children (in a depth first manner)"
  [expr]
  (tree-seq #(-> % :children boolean)
            :children
            expr))

(comment
(use 'analyze.core)
(print-expr 
  (analyze-one {:ns {:name 'clojure.core} :context :eval} 
               '(defn a 
                  ([^bytes b] ^Integer b)
                  ([b c] c)))
  :children :Expr-obj :ObjMethod-obj :LocalBinding-obj :env :BindingInit-obj)
  )
