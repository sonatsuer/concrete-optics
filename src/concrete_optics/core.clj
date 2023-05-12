(ns concrete-optics.core
  (:require [concrete-optics.base :as base] 
            [concrete-optics.iso.structures :as iso]
            [concrete-optics.prism.structures :as prism]
            [concrete-optics.lens.structures :as lens]
            [concrete-optics.traversal.structures :as traversal]
            [concrete-optics.getter.structures :as getter]
            [concrete-optics.setter.structures :as setter]
            [concrete-optics.fold.structures :as fold]))

;; Reexports from core
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

;; Reexports from prism
(def mk-prism prism/mk-prism)
(def mk-simple-prism prism/mk-simple-prism)
(def predicate-prism prism/predicate-prism)

;; Reexports from lens
(def mk-lens lens/mk-lens)
(def field lens/field)

;; Reexports from traversal
(def mk-traversal traversal/mk-traversal)
(def vector-traversal traversal/vector-traversal)
(def ix traversal/ix)

;; Reexports from getter
(def mk-getter getter/mk-getter)

;; Reexports from setter
(def mk-setter setter/mk-setter)

;; Reexports from fold
(def mk-fold fold/mk-fold)
