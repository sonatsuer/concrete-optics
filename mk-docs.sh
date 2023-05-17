#!/bin/bash

LOCATION=doc/showcase.md

touch $LOCATION
echo '# Showcase' > $LOCATION
echo '' >> $LOCATION
echo '```clojure' >> $LOCATION
cat test/concrete_optics/showcase.clj >> $LOCATION
echo '```' >> $LOCATION
echo '' >> $LOCATION
lein codox
