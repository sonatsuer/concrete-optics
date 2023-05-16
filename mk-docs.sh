#!/bin/bash

touch doc/showcase.md
echo '# Showcase of Use Cases' > doc/showcase.md
echo '' >> doc/showcase.md
echo '```clojure' >> doc/showcase.md
cat test/concrete_optics/showcase.clj >> doc/showcase.md
echo '```' >> doc/showcase.md
echo '' >> doc/showcase.md
