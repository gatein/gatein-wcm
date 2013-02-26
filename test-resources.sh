JPG=https://www.dropbox.com/sh/n933phi74s37oy6/0HDvWDf0G_/pdf/wcm-whiteboard.jpg
PDF1=https://www.dropbox.com/sh/n933phi74s37oy6/b25pmaTIl6/pdf/GateIn-UserGuide-v3.5.pdf
PDF2=https://www.dropbox.com/sh/n933phi74s37oy6/3I4Tmk4Pxg/pdf/jbossportletbridge.pdf
wget $JPG
cp "$(pwd)/wcm-whiteboard.jpg" "gatein-wcm-impl/src/test/resources"
mv "$(pwd)/wcm-whiteboard.jpg" "gatein-wcm-integration-tests/src/test/resources"

wget $PDF1
cp "$(pwd)/GateIn-UserGuide-v3.5.pdf" "gatein-wcm-impl/src/test/resources"
mv "$(pwd)/GateIn-UserGuide-v3.5.pdf" "gatein-wcm-integration-tests/src/test/resources"

wget $PDF2
cp "$(pwd)/jbossportletbridge.pdf" "gatein-wcm-impl/src/test/resources"
mv "$(pwd)/jbossportletbridge.pdf" "gatein-wcm-integration-tests/src/test/resources"

