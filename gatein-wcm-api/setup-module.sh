# Retrieving JBOSS_HOME
. $(pwd)/../setup.properties

src="target/gatein-wcm-api-0.0.1-SNAPSHOT.jar"
if [ -f "$src" ]; then
    dest="$JBOSS_HOME/modules/org/gatein/wcm/gatein-wcm-api/main"
    echo "Copying $src into $dest"
    mkdir -p "$dest"
    cp "$src" "$dest"
fi
