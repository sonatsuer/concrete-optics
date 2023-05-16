#!/bin/bash

touch doc/showcase.md
echo '# Showcase' > doc/showcase.md
echo '' >> doc/showcase.md
echo '```clojure' >> doc/showcase.md
cat test/concrete_optics/showcase.clj >> doc/showcase.md
echo '```' >> doc/showcase.md
echo '' >> doc/showcase.md
lein codox
