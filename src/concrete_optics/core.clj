(ns concrete-optics.core
  (:require [concrete-optics.base :as base] 
            [concrete-optics.iso.structures :as iso]
            [concrete-optics.prism.structures :as prism]
            [concrete-optics.lens.structures :as lens]
            [concrete-optics.traversal.structures :as traversal]))

;; Reexports from base
(def preview base/preview)
(def put base/put)
(def view base/view)
(def over base/over)
(def to-list base/to-list)
(def review base/review)
(def traverse base/traverse)
(def compose base/compose)

;; Reexports from iso 
(def mk-iso iso/mk-iso)
(def invert-iso iso/invert-iso)
(def eq iso/eq)
(def curried iso/curried)

;; Reexports from prism
(def mk-prism prism/mk-prism)
(def mk-simple-prism prism/mk-simple-prism)
(def predicate-prism prism/predicate-prism)
(def cons-prism prism/cons-prism)

;; Reexports from lens
(def mk-lens lens/mk-lens)
(def field lens/field)

;; Reexports from traversal
(def mk-traversal traversal/mk-traversal)
(def vector-traversal traversal/vector-traversal)
(def ix traversal/ix)
